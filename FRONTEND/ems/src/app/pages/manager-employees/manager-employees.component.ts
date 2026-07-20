import { Component, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { ManagerService } from '../../services/manager.service';
import { EmployeeService } from '../../services/employee.service';
import { EmployeeResponseDTO as ManagerEmployeeDTO } from '../../models/employee-response.model';
import { EmployeeResponseDTO } from '../../models/user.model';

@Component({
  selector: 'app-manager-employees',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './manager-employees.component.html',
  styleUrl: './manager-employees.component.css'
})
export class ManagerEmployeesComponent implements OnInit {
  employees: ManagerEmployeeDTO[] = [];
  isLoading = true;
  errorMessage = '';

  isViewModalOpen = false;
  selectedEmployee: EmployeeResponseDTO | null = null;
  isLoadingProfile = false;

  constructor(
    private managerService: ManagerService,
    private employeeService: EmployeeService
  ) {}

  ngOnInit() {
    this.loadEmployees();
  }

  loadEmployees() {
    this.isLoading = true;
    this.errorMessage = '';
    
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        this.employees = data.map(emp => ({
          employeeId: emp.id,
          employeeCode: emp.employeeCode,
          fullName: emp.firstName + ' ' + emp.lastName,
          email: emp.email,
          phone: emp.phone,
          designation: emp.designation,
          departmentName: emp.departmentName,
          employmentStatus: emp.employmentStatus
        }));
        this.isLoading = false;
      },
      error: (err) => {
        this.isLoading = false;
        this.errorMessage = err.error?.message || 'Failed to load employee records.';
      }
    });
  }

  openViewModal(emp: ManagerEmployeeDTO) {
    this.isLoadingProfile = true;
    this.isViewModalOpen = true;
    this.selectedEmployee = null;

    this.employeeService.getEmployeeById(emp.employeeId).subscribe({
      next: (fullEmp) => {
        this.selectedEmployee = fullEmp;
        this.isLoadingProfile = false;
      },
      error: (err) => {
        this.isLoadingProfile = false;
        alert(err.error?.message || 'Failed to fetch employee details.');
        this.closeModal();
      }
    });
  }

  closeModal() {
    this.isViewModalOpen = false;
    this.selectedEmployee = null;
    this.isLoadingProfile = false;
  }
}
