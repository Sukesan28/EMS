import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { TaskService } from '../../services/task.service';
import { ManagerService } from '../../services/manager.service';
import { AuthService } from '../../services/auth.service';
import { EmployeeService } from '../../services/employee.service';
import { TableComponent } from '../../components/table/table.component';
import { TaskResponseDTO, TaskRequestDTO } from '../../models/task.model';
import { EmployeeResponseDTO } from '../../models/employee-response.model';

@Component({
  selector: 'app-tasks',
  standalone: true,
  imports: [CommonModule, FormsModule, TableComponent],
  templateUrl: './tasks.component.html',
  styleUrl: './tasks.component.css'
})
export class TasksComponent implements OnInit {
  userRole = '';
  tasks = signal<TaskResponseDTO[]>([]);
  teamMembers = signal<EmployeeResponseDTO[]>([]);
  
  // Table states
  isLoading = signal(true);
  isEmpty = signal(false);
  
  // Modal states
  isAssignModalOpen = false;
  
  // Assign task form model
  assignModel: TaskRequestDTO = {
    title: '',
    description: '',
    assigneeId: 0,
    status: 'NOT_STARTED',
    dueDate: ''
  };

  activeTab: 'my-tasks' | 'team-tasks' = 'my-tasks';

  constructor(
    private authService: AuthService,
    private taskService: TaskService,
    private managerService: ManagerService,
    private employeeService: EmployeeService
  ) {
    const user = this.authService.currentUser();
    this.userRole = user ? user.role : 'EMPLOYEE';
    
    if (this.userRole === 'HR' || this.userRole === 'MANAGER') {
      this.activeTab = 'team-tasks';
    } else {
      this.activeTab = 'my-tasks';
    }
  }

  ngOnInit() {
    this.loadTasks();
    if (this.userRole === 'MANAGER') {
      this.loadTeamMembers();
    } else if (this.userRole === 'HR') {
      this.loadAllEmployeesAsAssignees();
    }
  }

  loadTasks() {
    this.isLoading.set(true);
    if (this.activeTab === 'my-tasks') {
      this.taskService.getMyTasks().subscribe({
        next: (data) => this.setTasksData(data),
        error: () => this.setTasksData([])
      });
    } else {
      this.taskService.getTeamTasks().subscribe({
        next: (data) => this.setTasksData(data),
        error: () => this.setTasksData([])
      });
    }
  }

  private setTasksData(data: TaskResponseDTO[]) {
    this.tasks.set(data);
    this.isEmpty.set(data.length === 0);
    this.isLoading.set(false);
  }

  loadTeamMembers() {
    this.managerService.getMyEmployees().subscribe({
      next: (data) => {
        this.teamMembers.set(data);
        if (data.length > 0) {
          this.assignModel.assigneeId = data[0].employeeId;
        }
      },
      error: () => {}
    });
  }

  loadAllEmployeesAsAssignees() {
    this.employeeService.getAllEmployees().subscribe({
      next: (data) => {
        const mapped = data.map(e => ({
          employeeId: e.id,
          employeeCode: e.employeeCode,
          fullName: `${e.firstName} ${e.lastName}`,
          email: e.email,
          phone: e.phone,
          designation: e.designation,
          departmentName: e.departmentName,
          employmentStatus: e.employmentStatus
        }));
        this.teamMembers.set(mapped);
        if (mapped.length > 0) {
          this.assignModel.assigneeId = mapped[0].employeeId;
        }
      },
      error: () => {}
    });
  }

  getHeaders(): string[] {
    if (this.userRole === 'HR') {
      return ['Department Name', 'Employee Code', 'Employee Name', 'Task Title', 'Description', 'Status', 'Due Date'];
    } else if (this.userRole === 'MANAGER') {
      return ['Employee Code', 'Employee Name', 'Task Title', 'Description', 'Status', 'Due Date'];
    } else {
      // EMPLOYEE - only their own tasks, no need for employee info
      return ['Task Title', 'Description', 'Status', 'Due Date'];
    }
  }

  switchTab(tab: 'my-tasks' | 'team-tasks') {
    this.activeTab = tab;
    this.loadTasks();
  }

  openAssignModal() {
    this.assignModel = {
      title: '',
      description: '',
      assigneeId: this.teamMembers().length > 0 ? this.teamMembers()[0].employeeId : 0,
      status: 'NOT_STARTED',
      dueDate: ''
    };
    this.isAssignModalOpen = true;
  }

  closeAssignModal() {
    this.isAssignModalOpen = false;
  }

  onAssignTask() {
    if (!this.assignModel.title || !this.assignModel.assigneeId) return;
    this.isLoading.set(true);

    const payload = { ...this.assignModel };
    if (!payload.dueDate) {
      delete payload.dueDate;
    }

    this.taskService.assignTask(payload).subscribe({
      next: () => {
        this.closeAssignModal();
        this.loadTasks();
      },
      error: (err) => {
        this.isLoading.set(false);
        alert(err.error?.message || 'Failed to assign task');
      }
    });
  }
}
