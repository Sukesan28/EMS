import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ITSupportService } from '../../services/it-support.service';
import { EmployeeService } from '../../services/employee.service';
import { EmployeeResponseDTO } from '../../models/user.model';

@Component({
  selector: 'app-it-support-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './it-support-reset-password.component.html',
  styleUrl: './it-support-reset-password.component.css'
})
export class ItSupportResetPasswordComponent implements OnInit {
  employeeCodeInput = signal('');
  employeesList = signal<EmployeeResponseDTO[]>([]);
  selectedEmployee = signal<EmployeeResponseDTO | null>(null);
  
  isLoading = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  constructor(
    private itSupportService: ITSupportService,
    private employeeService: EmployeeService
  ) {}

  ngOnInit() {
    this.loadEmployees();
  }

  loadEmployees() {
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        this.employeesList.set(data);
      },
      error: () => {}
    });
  }

  onLookup() {
    this.errorMessage.set('');
    this.successMessage.set('');
    this.selectedEmployee.set(null);

    const code = this.employeeCodeInput().trim();
    if (!code) {
      this.errorMessage.set('Please enter an employee code.');
      return;
    }

    const found = this.employeesList().find(
      e => e.employeeCode.toLowerCase() === code.toLowerCase()
    );

    if (found) {
      this.selectedEmployee.set(found);
    } else {
      this.errorMessage.set(`No employee found with code: ${code}`);
    }
  }

  onResetPassword() {
    const employee = this.selectedEmployee();
    if (!employee) return;

    if (employee.role === 'HR') {
      this.errorMessage.set('IT Support cannot modify HR accounts.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.itSupportService.resetPassword(employee.id).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.successMessage.set(`Credentials reset successfully! A new random temporary password has been generated and emailed to ${res.firstName} ${res.lastName} (${res.email}).`);
        // Refresh local lookup copy
        const current = this.selectedEmployee();
        if (current) {
          current.firstLogin = true;
          this.selectedEmployee.set({ ...current });
        }
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to reset user password.');
      }
    });
  }

  clearSelection() {
    this.selectedEmployee.set(null);
    this.employeeCodeInput.set('');
    this.errorMessage.set('');
    this.successMessage.set('');
  }
}
