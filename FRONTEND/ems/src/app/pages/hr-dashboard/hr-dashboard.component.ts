import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmployeeService } from '../../services/employee.service';
import { DepartmentService } from '../../services/department.service';
import { AttendanceService } from '../../services/attendance.service';
import { LeaveService } from '../../services/leave.service';
import { AuthService } from '../../services/auth.service';
import { GreetingComponent } from '../../components/greeting/greeting.component';
import { StatisticCardComponent } from '../../components/statistic-card/statistic-card.component';
import { EmployeeResponseDTO } from '../../models/user.model';

@Component({
  selector: 'app-hr-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, GreetingComponent, StatisticCardComponent],
  templateUrl: './hr-dashboard.component.html',
  styleUrl: './hr-dashboard.component.css'
})
export class HrDashboardComponent implements OnInit {
  fullName = '';
  
  // Dashboard statistics
  totalEmployees = signal(0);
  totalDepartments = signal(0);
  totalManagers = signal(0);
  presentCount = signal(0);
  absentCount = signal(0);
  onLeaveCount = signal(0);
  
  recentEmployees = signal<EmployeeResponseDTO[]>([]);
  isLoading = signal(true);

  constructor(
    private authService: AuthService,
    private employeeService: EmployeeService,
    private departmentService: DepartmentService,
    private attendanceService: AttendanceService,
    private leaveService: LeaveService
  ) {
    const user = this.authService.currentUser();
    this.fullName = user ? user.fullName : 'HR Admin';
  }

  ngOnInit() {
    this.loadDashboardData();
  }

  private loadDashboardData() {
    this.isLoading.set(true);
    
    // Fetch all employees
    this.employeeService.getAllEmployees().subscribe({
      next: (employees) => {
        this.totalEmployees.set(employees.length);
        this.totalManagers.set(employees.filter(e => e.role === 'MANAGER').length);
        
        // Sort employees by ID descending to get recent hires
        const sorted = [...employees].sort((a, b) => b.id - a.id);
        this.recentEmployees.set(sorted.slice(0, 5));
        
        // Let's assume some absents for mock or count inactive
        const inactiveCount = employees.filter(e => !e.accountEnabled).length;
        this.absentCount.set(inactiveCount);
      },
      error: () => {}
    });

    // Fetch all departments
    this.departmentService.getAllDepartments().subscribe({
      next: (depts) => {
        this.totalDepartments.set(depts.length);
      },
      error: () => {}
    });

    // Fetch all attendance for today
    const todayStr = new Date().toISOString().split('T')[0];
    this.attendanceService.getAllAttendance().subscribe({
      next: (logs) => {
        const todayLogs = logs.filter(log => log.attendanceDate === todayStr);
        this.presentCount.set(todayLogs.filter(log => log.attendanceStatus === 'PRESENT').length);
      },
      error: () => {}
    });

    // Fetch leaves to see who is on leave today
    this.leaveService.getAllLeaves().subscribe({
      next: (leaves) => {
        const today = new Date();
        const activeLeaves = leaves.filter(leave => {
          if (leave.status !== 'APPROVED') return false;
          const start = new Date(leave.startDate);
          const end = new Date(leave.endDate);
          return today >= start && today <= end;
        });
        this.onLeaveCount.set(activeLeaves.length);
        
        // If absentCount wasn't set, compute based on total - present - onLeave
        if (this.absentCount() === 0) {
          const calculatedAbsent = this.totalEmployees() - this.presentCount() - this.onLeaveCount();
          this.absentCount.set(calculatedAbsent > 0 ? calculatedAbsent : 0);
        }
        
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }
}
