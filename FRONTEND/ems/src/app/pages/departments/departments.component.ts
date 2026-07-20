import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { DepartmentService } from '../../services/department.service';
import { EmployeeService } from '../../services/employee.service';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { TableComponent } from '../../components/table/table.component';
import { DepartmentResponseDTO, DepartmentRequestDTO, DepartmentStatus, DepartmentManagerHistoryResponseDTO } from '../../models/department.model';
import { EmployeeResponseDTO } from '../../models/user.model';

@Component({
  selector: 'app-departments',
  standalone: true,
  imports: [CommonModule, FormsModule, SearchBarComponent, TableComponent],
  templateUrl: './departments.component.html',
  styleUrl: './departments.component.css'
})
export class DepartmentsComponent implements OnInit {
  departments = signal<DepartmentResponseDTO[]>([]);
  filteredDepartments = signal<DepartmentResponseDTO[]>([]);
  
  // Table state
  isLoading = signal(true);
  isEmpty = signal(false);
  
  // Search state
  searchQuery = '';
  
  // Modal state
  isModalOpen = false;
  isEditMode = false;
  selectedDepartmentId: number | null = null;

  // Change Manager Modal State
  isChangeManagerOpen = false;
  selectedDept: DepartmentResponseDTO | null = null;
  deptEmployees = signal<EmployeeResponseDTO[]>([]);
  selectedNewManagerId: number = 0;
  managerHistory = signal<DepartmentManagerHistoryResponseDTO[]>([]);

  // Form Model
  formModel: DepartmentRequestDTO = this.getEmptyFormModel();

  constructor(
    private departmentService: DepartmentService,
    private employeeService: EmployeeService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.loadDepartments();
    
    // Check if query params ask to open create modal directly
    this.route.queryParams.subscribe(params => {
      if (params['action'] === 'create') {
        this.openAddModal();
      }
    });
  }

  loadDepartments() {
    this.isLoading.set(true);
    this.departmentService.getAllDepartments().subscribe({
      next: (data) => {
        this.departments.set(data);
        this.applyFilter();
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
        this.isEmpty.set(true);
      }
    });
  }

  onSearch(query: string) {
    this.searchQuery = query;
    this.applyFilter();
  }

  applyFilter() {
    const list = this.departments();
    if (!this.searchQuery.trim()) {
      this.filteredDepartments.set(list);
      this.isEmpty.set(list.length === 0);
      return;
    }

    const filtered = list.filter(dept => 
      dept.departmentCode.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      dept.departmentName.toLowerCase().includes(this.searchQuery.toLowerCase()) ||
      (dept.departmentHead && dept.departmentHead.toLowerCase().includes(this.searchQuery.toLowerCase()))
    );

    this.filteredDepartments.set(filtered);
    this.isEmpty.set(filtered.length === 0);
  }

  openAddModal() {
    this.isEditMode = false;
    this.selectedDepartmentId = null;
    this.formModel = this.getEmptyFormModel();
    this.isModalOpen = true;
  }

  openEditModal(dept: DepartmentResponseDTO) {
    this.isEditMode = true;
    this.selectedDepartmentId = dept.id;
    this.formModel = {
      departmentName: dept.departmentName,
      description: dept.description,
      departmentHead: dept.departmentHead,
      status: dept.status
    };
    this.isModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
  }

  onSubmitForm() {
    if (this.isEditMode && this.selectedDepartmentId !== null) {
      this.departmentService.updateDepartment(this.selectedDepartmentId, this.formModel).subscribe({
        next: () => {
          this.closeModal();
          this.loadDepartments();
        },
        error: () => {}
      });
    } else {
      this.departmentService.createDepartment(this.formModel).subscribe({
        next: () => {
          this.closeModal();
          this.loadDepartments();
        },
        error: () => {}
      });
    }
  }

  // Change Manager Methods
  openChangeManagerModal(dept: DepartmentResponseDTO) {
    this.selectedDept = dept;
    this.selectedNewManagerId = 0;
    this.deptEmployees.set([]);
    this.managerHistory.set([]);
    
    // Load ALL employees (any department) so HR can pick anyone to promote
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        // Only EMPLOYEE role can be promoted to manager
        const filtered = data.filter(e => e.role === 'EMPLOYEE');
        this.deptEmployees.set(filtered);
        if (filtered.length > 0) {
          this.selectedNewManagerId = filtered[0].id;
        }
      }
    });

    // Load manager change history
    this.departmentService.getManagerHistory(dept.id).subscribe({
      next: (history) => {
        this.managerHistory.set(history);
      }
    });

    this.isChangeManagerOpen = true;
  }

  closeChangeManagerModal() {
    this.isChangeManagerOpen = false;
    this.selectedDept = null;
  }

  onChangeManagerSubmit() {
    if (!this.selectedDept || !this.selectedNewManagerId) return;

    this.departmentService.changeManager(this.selectedDept.id, this.selectedNewManagerId).subscribe({
      next: () => {
        this.closeChangeManagerModal();
        this.loadDepartments();
      },
      error: (err) => {
        alert(err.error?.message || 'Failed to change manager');
      }
    });
  }

  private getEmptyFormModel(): DepartmentRequestDTO {
    return {
      departmentName: '',
      description: '',
      departmentHead: '',
      status: 'ACTIVE' as DepartmentStatus
    };
  }
}
