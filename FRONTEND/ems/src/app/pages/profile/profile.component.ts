import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { EmployeeService } from '../../services/employee.service';
import { AuthService } from '../../services/auth.service';
import { EmployeeResponseDTO, EmployeeSelfUpdateDTO } from '../../models/user.model';

@Component({
  selector: 'app-profile',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './profile.component.html',
  styleUrl: './profile.component.css'
})
export class ProfileComponent implements OnInit {
  basicDetails = signal<EmployeeResponseDTO | null>(null);
  performanceSummary = signal<any[]>([]);
  attendanceSummary = signal<any[]>([]);
  leaveSummary = signal<any[]>([]);
  
  // States
  isLoading = signal(true);
  isEditMode = signal(false);
  successMessage = signal('');
  errorMessage = signal('');
  isOwnProfile = signal(false);

  // Form Model
  formModel: EmployeeSelfUpdateDTO = {
    email: '',
    phone: '',
    address: ''
  };

  constructor(
    private employeeService: EmployeeService,
    private authService: AuthService,
    private route: ActivatedRoute
  ) {}

  ngOnInit() {
    this.route.queryParams.subscribe(params => {
      const employeeId = params['employeeId'];
      if (employeeId) {
        this.loadProfileDetails(Number(employeeId));
      } else {
        this.loadMyProfileDetails();
      }
    });
  }

  loadProfileDetails(employeeId: number) {
    this.isLoading.set(true);
    this.employeeService.getEmployeeProfileDetails(employeeId).subscribe({
      next: (data) => {
        this.basicDetails.set(data.employeeDetails);
        this.performanceSummary.set(data.performanceSummary);
        this.attendanceSummary.set(data.attendanceSummary);
        this.leaveSummary.set(data.leaveSummary);
        
        const currentUser = this.authService.currentUser();
        const details = this.basicDetails();
        this.isOwnProfile.set(currentUser !== null && details !== null && currentUser.employeeCode === details.employeeCode);
        
        if (details) {
          this.formModel = {
            email: details.email,
            phone: details.phone,
            address: details.address
          };
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load employee profile details.');
      }
    });
  }

  loadMyProfileDetails() {
    this.isLoading.set(true);
    this.employeeService.getMyProfileDetails().subscribe({
      next: (data) => {
        this.basicDetails.set(data.employeeDetails);
        this.performanceSummary.set(data.performanceSummary);
        this.attendanceSummary.set(data.attendanceSummary);
        this.leaveSummary.set(data.leaveSummary);
        this.isOwnProfile.set(true);
        
        const details = this.basicDetails();
        if (details) {
          this.formModel = {
            email: details.email,
            phone: details.phone,
            address: details.address
          };
        }
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to load profile details.');
      }
    });
  }

  toggleEditMode() {
    this.isEditMode.update(val => !val);
    this.successMessage.set('');
    this.errorMessage.set('');
  }

  onSaveProfile() {
    this.isLoading.set(true);
    this.successMessage.set('');
    this.errorMessage.set('');

    this.employeeService.selfUpdate(this.formModel).subscribe({
      next: (res) => {
        this.basicDetails.set(res);
        this.isEditMode.set(false);
        this.successMessage.set('Profile updated successfully!');
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to update profile. Check form inputs.');
      }
    });
  }
}
