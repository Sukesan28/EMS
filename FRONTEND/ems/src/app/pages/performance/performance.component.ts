import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { PerformanceService } from '../../services/performance.service';
import { ManagerService } from '../../services/manager.service';
import { AuthService } from '../../services/auth.service';
import { EmployeeService } from '../../services/employee.service';
import { TableComponent } from '../../components/table/table.component';
import { PerformanceResponseDTO, PerformanceRequestDTO } from '../../models/performance.model';
import { EmployeeResponseDTO as ModelEmployeeResponseDTO } from '../../models/user.model';
import { EmployeeResponseDTO } from '../../models/employee-response.model';

@Component({
  selector: 'app-performance',
  standalone: true,
  imports: [CommonModule, FormsModule, TableComponent],
  templateUrl: './performance.component.html',
  styleUrl: './performance.component.css'
})
export class PerformanceComponent implements OnInit {
  userRole = '';
  reviews = signal<PerformanceResponseDTO[]>([]);
  teamMembers = signal<EmployeeResponseDTO[]>([]);
  
  // Table states
  isLoading = signal(true);
  isEmpty = signal(false);
  
  // Modal states
  isModalOpen = false;
  isEditMode = false;
  selectedReviewId: number | null = null;
  selectedEmployeeId = 0;
  isViewModalOpen = false;
  selectedEmployee = signal<ModelEmployeeResponseDTO | null>(null);
  
  // Form model
  formModel: PerformanceRequestDTO = this.getEmptyFormModel();

  activeTab: 'my-reviews' | 'team-reviews' | 'all-reviews' = 'my-reviews';

  constructor(
    private authService: AuthService,
    private performanceService: PerformanceService,
    private managerService: ManagerService,
    private employeeService: EmployeeService,
    private router: Router
  ) {
    const user = this.authService.currentUser();
    this.userRole = user ? user.role : 'EMPLOYEE';
    
    if (this.userRole === 'HR') {
      this.activeTab = 'all-reviews';
    } else if (this.userRole === 'MANAGER') {
      this.activeTab = 'team-reviews';
    } else {
      this.activeTab = 'my-reviews';
    }
  }

  ngOnInit() {
    this.loadReviews();
    if (this.userRole === 'MANAGER') {
      this.loadTeamMembers();
    }
  }

  loadReviews() {
    this.isLoading.set(true);
    if (this.activeTab === 'my-reviews') {
      this.performanceService.getMyReviews().subscribe({
        next: (data) => this.setReviewsData(data),
        error: () => this.setReviewsData([])
      });
    } else if (this.activeTab === 'team-reviews') {
      this.performanceService.getTeamReviews().subscribe({
        next: (data) => this.setReviewsData(data),
        error: () => this.setReviewsData([])
      });
    } else if (this.activeTab === 'all-reviews') {
      this.performanceService.getAllReviews().subscribe({
        next: (data) => this.setReviewsData(data),
        error: () => this.setReviewsData([])
      });
    }
  }

  private setReviewsData(data: PerformanceResponseDTO[]) {
    this.reviews.set(data);
    this.isEmpty.set(data.length === 0);
    this.isLoading.set(false);
  }

  loadTeamMembers() {
    this.managerService.getMyEmployees().subscribe({
      next: (data) => {
        this.teamMembers.set(data);
        if (data.length > 0) {
          this.selectedEmployeeId = data[0].employeeId;
        }
      },
      error: () => {}
    });
  }

  switchTab(tab: 'my-reviews' | 'team-reviews' | 'all-reviews') {
    this.activeTab = tab;
    this.loadReviews();
  }

  getHeaders(): string[] {
    if (this.userRole === 'HR') {
      return ['Department Name', 'Employee Name', 'Employee Code', 'Review Period', 'Feedback', 'Rating', 'Review Date', 'Actions'];
    } else if (this.activeTab === 'my-reviews') {
      return ['Review Period', 'Performance Goal', 'Rating', 'Review Date', 'Actions'];
    } else {
      return ['Employee', 'Review Period', 'Performance Goal', 'Rating', 'Review Date', 'Actions'];
    }
  }

  openCreateModal() {
    this.isEditMode = false;
    this.selectedReviewId = null;
    this.selectedEmployeeId = this.teamMembers().length > 0 ? this.teamMembers()[0].employeeId : 0;
    this.formModel = this.getEmptyFormModel();
    this.isModalOpen = true;
  }

  openEditModal(review: PerformanceResponseDTO) {
    this.isEditMode = true;
    this.selectedReviewId = review.id;
    
    // Find employee ID from direct reports
    const member = this.teamMembers().find(m => `${m.fullName}` === review.employeeName);
    this.selectedEmployeeId = member ? member.employeeId : 0;

    this.formModel = {
      reviewPeriod: review.reviewPeriod,
      performanceGoal: review.performanceGoal,
      achievement: review.achievement,
      strength: review.strength,
      improvementArea: review.improvementArea,
      rating: review.rating,
      feedback: review.feedback,
      reviewDate: review.reviewDate
    };
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  openViewModal(review: PerformanceResponseDTO) {
    if (review.employeeId) {
      this.isLoading.set(true);
      this.employeeService.getEmployeeById(review.employeeId).subscribe({
        next: (emp) => {
          this.selectedEmployee.set(emp);
          this.isViewModalOpen = true;
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
          alert('Failed to load employee details.');
        }
      });
    }
  }

  closeViewModal() {
    this.selectedEmployee.set(null);
    this.isViewModalOpen = false;
  }

  navigateToProfile(review: PerformanceResponseDTO) {
    if (review.employeeId) {
      this.router.navigate(['/profile'], { queryParams: { employeeId: review.employeeId } });
    }
  }

  onSubmit() {
    this.isLoading.set(true);
    if (this.isEditMode && this.selectedReviewId !== null) {
      this.performanceService.updateReview(this.selectedReviewId, this.formModel).subscribe({
        next: () => {
          this.closeModal();
          this.loadReviews();
        },
        error: (err) => {
          this.isLoading.set(false);
          alert(err.error?.message || 'Failed to update review');
        }
      });
    } else {
      if (!this.selectedEmployeeId) {
        this.isLoading.set(false);
        alert('Please select an employee');
        return;
      }
      this.performanceService.createReview(this.selectedEmployeeId, this.formModel).subscribe({
        next: () => {
          this.closeModal();
          this.loadReviews();
        },
        error: (err) => {
          this.isLoading.set(false);
          alert(err.error?.message || 'Failed to create review');
        }
      });
    }
  }

  private getEmptyFormModel(): PerformanceRequestDTO {
    return {
      reviewPeriod: '',
      performanceGoal: '',
      achievement: '',
      strength: '',
      improvementArea: '',
      rating: 5,
      feedback: '',
      reviewDate: new Date().toISOString().split('T')[0]
    };
  }
}
