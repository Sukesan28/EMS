import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { EmployeeService } from '../../services/employee.service';
import { DepartmentService } from '../../services/department.service';
import { SearchBarComponent } from '../../components/search-bar/search-bar.component';
import { TableComponent } from '../../components/table/table.component';
import { ConfirmDialogComponent } from '../../components/confirm-dialog/confirm-dialog.component';
import { EmployeeResponseDTO, EmployeeRequestDTO, Gender, EmploymentStatus, Role } from '../../models/user.model';
import { DepartmentResponseDTO } from '../../models/department.model';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-employees',
  standalone: true,
  imports: [CommonModule, FormsModule, SearchBarComponent, TableComponent, ConfirmDialogComponent],
  templateUrl: './employees.component.html',
  styleUrl: './employees.component.css'
})
export class EmployeesComponent implements OnInit {
  employees = signal<EmployeeResponseDTO[]>([]);
  departments = signal<DepartmentResponseDTO[]>([]);
  managers = signal<EmployeeResponseDTO[]>([]);
  userRole = '';
  currentUserCode = '';
  
  // Table state
  isLoading = signal(true);
  isEmpty = signal(false);
  
  // Search state
  searchQuery = '';
  
  // Modal state
  isModalOpen = false;
  isViewModalOpen = false;
  isEditMode = false;
  selectedEmployeeId: number | null = null;
  selectedEmployee: EmployeeResponseDTO | null = null;

  // Excel upload state
  isUploadModalOpen = false;
  selectedFile: File | null = null;
  isUploading = false;
  uploadFinished = false;
  uploadSuccessCount = 0;
  uploadFailureCount = 0;
  uploadErrors: string[] = [];

  // Confirm dialog state
  isConfirmDialogOpen = false;
  employeeToDelete: number | null = null;

  // Form Model
  formModel: EmployeeRequestDTO = this.getEmptyFormModel();

  constructor(
    private authService: AuthService,
    private employeeService: EmployeeService,
    private departmentService: DepartmentService,
    private route: ActivatedRoute
  ) {
    const user = this.authService.currentUser();
    this.userRole = user ? user.role : 'EMPLOYEE';
    this.currentUserCode = user ? user.employeeCode : '';
  }

  ngOnInit() {
    this.loadInitialData();
    
    // Check if query params ask to open create modal directly
    this.route.queryParams.subscribe(params => {
      if (params['action'] === 'create') {
        this.openAddModal();
      }
    });
  }

  loadInitialData() {
    this.isLoading.set(true);
    
    // Fetch departments
    this.departmentService.getAllDepartments().subscribe({
      next: (depts) => this.departments.set(depts),
      error: () => {}
    });

    // Fetch employees
    this.loadEmployees();
  }

  get filteredManagers(): EmployeeResponseDTO[] {
    const role = this.formModel.role;
    const deptId = Number(this.formModel.departmentId);
    
    if (role === 'EMPLOYEE' || role === 'IT_SUPPORT') {
      return this.employees().filter(e => e.role === 'MANAGER' && this.getDepartmentIdByName(e.departmentName) === deptId);
    } else if (role === 'MANAGER') {
      return this.employees().filter(e => e.role === 'HR');
    } else {
      return [];
    }
  }

  getDepartmentIdByName(name: string): number {
    const dept = this.departments().find(d => d.departmentName === name);
    return dept ? dept.id : 0;
  }

  onDepartmentOrRoleChange() {
    const currentFiltered = this.filteredManagers;
    if (currentFiltered.length === 1) {
      this.formModel.managerId = currentFiltered[0].id;
    } else {
      this.formModel.managerId = null;
    }
  }

  loadEmployees(filters?: any) {
    this.isLoading.set(true);
    this.employeeService.getAllEmployees(filters).subscribe({
      next: (data) => {
        this.employees.set(data);
        this.managers.set(data.filter(e => e.role === 'MANAGER' || e.role === 'HR'));
        this.isEmpty.set(data.length === 0);
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
    const filters = query ? { code: query } : undefined; // simple search by code/name
    this.loadEmployees(filters);
  }

  openAddModal() {
    this.isEditMode = false;
    this.selectedEmployeeId = null;
    this.formModel = this.getEmptyFormModel();
    this.isModalOpen = true;
  }

  openEditModal(emp: EmployeeResponseDTO) {
    this.isEditMode = true;
    this.selectedEmployeeId = emp.id;
    
    // Find department ID
    const dept = this.departments().find(d => d.departmentName === emp.departmentName);
    const deptId = dept ? dept.id : 0;
    
    // Find manager ID
    const manager = this.managers().find(m => `${m.firstName} ${m.lastName}` === emp.managerName);
    const managerId = manager ? manager.id : null;

    this.formModel = {
      firstName: emp.firstName,
      lastName: emp.lastName,
      email: emp.email,
      phone: emp.phone,
      gender: emp.gender,
      dateOfBirth: emp.dateOfBirth,
      joiningDate: emp.joiningDate,
      designation: emp.designation,
      salary: emp.salary,
      address: emp.address,
      employmentStatus: emp.employmentStatus,
      role: emp.role,
      departmentId: deptId,
      managerId: managerId
    };
    this.isModalOpen = true;
  }

  openViewModal(emp: EmployeeResponseDTO) {
    this.selectedEmployee = emp;
    this.isViewModalOpen = true;
  }

  closeModal() {
    this.isModalOpen = false;
    this.isViewModalOpen = false;
  }

  onSubmitForm() {
    if (this.isEditMode && this.selectedEmployeeId !== null) {
      this.employeeService.updateEmployee(this.selectedEmployeeId, this.formModel).subscribe({
        next: () => {
          this.closeModal();
          this.loadEmployees();
        },
        error: () => {}
      });
    } else {
      this.employeeService.createEmployee(this.formModel).subscribe({
        next: () => {
          this.closeModal();
          this.loadEmployees();
        },
        error: () => {}
      });
    }
  }

  triggerDelete(id: number) {
    const emp = this.employees().find(e => e.id === id);
    if (emp && emp.employeeCode === this.currentUserCode) {
      alert('You cannot delete your own profile.');
      return;
    }
    this.employeeToDelete = id;
    this.isConfirmDialogOpen = true;
  }

  onConfirmDelete() {
    if (this.employeeToDelete !== null) {
      this.employeeService.deleteEmployee(this.employeeToDelete).subscribe({
        next: () => {
          this.isConfirmDialogOpen = false;
          this.employeeToDelete = null;
          this.loadEmployees();
        },
        error: (err) => {
          this.isConfirmDialogOpen = false;
          this.employeeToDelete = null;
          alert(err.error?.message || 'Failed to delete employee.');
        }
      });
    }
  }

  onCancelDelete() {
    this.isConfirmDialogOpen = false;
    this.employeeToDelete = null;
  }

  private getEmptyFormModel(): EmployeeRequestDTO {
    return {
      firstName: '',
      lastName: '',
      email: '',
      phone: '',
      gender: 'OTHER' as Gender,
      dateOfBirth: '',
      joiningDate: '',
      designation: '',
      salary: 0,
      address: '',
      employmentStatus: 'ACTIVE' as EmploymentStatus,
      role: 'EMPLOYEE' as Role,
      departmentId: 0,
      managerId: null
    };
  }

  openUploadModal() {
    this.isUploadModalOpen = true;
    this.selectedFile = null;
    this.uploadFinished = false;
    this.uploadErrors = [];
    this.uploadSuccessCount = 0;
    this.uploadFailureCount = 0;
  }

  closeUploadModal() {
    this.isUploadModalOpen = false;
    this.selectedFile = null;
    this.uploadFinished = false;
  }
}
