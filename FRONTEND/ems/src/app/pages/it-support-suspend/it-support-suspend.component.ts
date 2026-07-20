import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ITSupportService } from '../../services/it-support.service';
import { EmployeeService } from '../../services/employee.service';
import { EmployeeResponseDTO } from '../../models/user.model';

@Component({
  selector: 'app-it-support-suspend',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './it-support-suspend.component.html',
  styleUrl: './it-support-suspend.component.css'
})
export class ItSupportSuspendComponent implements OnInit {
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

  onActivate() {
    const employee = this.selectedEmployee();
    if (!employee) return;

    if (employee.role === 'HR') {
      this.errorMessage.set('IT Support cannot modify HR accounts.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.itSupportService.enableAccount(employee.id).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.successMessage.set(`Account enabled successfully for ${res.firstName} ${res.lastName} (${res.employeeCode}).`);
        // Set full updated response from backend
        this.selectedEmployee.set(res);
        this.loadEmployees(); // Refresh cached list
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to enable account.');
      }
    });
  }

  onSuspend() {
    const employee = this.selectedEmployee();
    if (!employee) return;

    if (employee.role === 'HR') {
      this.errorMessage.set('IT Support cannot modify HR accounts.');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    this.itSupportService.disableAccount(employee.id).subscribe({
      next: (res) => {
        this.isLoading.set(false);
        this.successMessage.set(`Account suspended successfully for ${res.firstName} ${res.lastName} (${res.employeeCode}).`);
        // Set full updated response from backend
        this.selectedEmployee.set(res);
        this.loadEmployees(); // Refresh cached list
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to suspend account.');
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
