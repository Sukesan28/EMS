import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmployeeService } from '../../services/employee.service';
import { AttendanceService } from '../../services/attendance.service';
import { LeaveService } from '../../services/leave.service';
import { AuthService } from '../../services/auth.service';
import { GreetingComponent } from '../../components/greeting/greeting.component';
import { StatisticCardComponent } from '../../components/statistic-card/statistic-card.component';
import { EmployeeResponseDTO } from '../../models/user.model';
import { AttendanceResponseDTO } from '../../models/attendance.model';

@Component({
  selector: 'app-employee-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, GreetingComponent, StatisticCardComponent],
  templateUrl: './employee-dashboard.component.html',
  styleUrl: './employee-dashboard.component.css'
})
export class EmployeeDashboardComponent implements OnInit {
  fullName = '';
  
  myAttendanceCount = signal('0 Days');
  leaveBalance = signal(20);
  departmentName = signal('N/A');
  managerName = signal('N/A');
  
  profile = signal<EmployeeResponseDTO | null>(null);
  recentAttendance = signal<AttendanceResponseDTO[]>([]);
  isLoading = signal(true);
  
  upcomingHolidays = [
    { name: 'Independence Day', date: 'August 15, 2026' },
    { name: 'Ganesh Chaturthi', date: 'September 15, 2026' },
    { name: 'Gandhi Jayanti', date: 'October 02, 2026' },
    { name: 'Diwali', date: 'November 08, 2026' }
  ];

  constructor(
    private authService: AuthService,
    private employeeService: EmployeeService,
    private attendanceService: AttendanceService,
    private leaveService: LeaveService
  ) {
    const user = this.authService.currentUser();
    this.fullName = user ? user.fullName : 'Employee';
  }

  ngOnInit() {
    this.loadDashboardData();
  }

  private loadDashboardData() {
    this.isLoading.set(true);

    // Fetch user profile
    this.employeeService.getProfile().subscribe({
      next: (prof) => {
        this.profile.set(prof);
        this.departmentName.set(prof.departmentName || 'None');
        this.managerName.set(prof.managerName || 'None');
        
        // Fetch personal attendance logs
        this.attendanceService.getMyAttendance().subscribe({
          next: (logs) => {
            const presentLogs = logs.filter(log => log.attendanceStatus === 'PRESENT');
            this.myAttendanceCount.set(`${presentLogs.length} Days`);
            
            // Sort logs by date descending and slice first 5
            const sortedLogs = [...logs].sort((a, b) => new Date(b.attendanceDate).getTime() - new Date(a.attendanceDate).getTime());
            this.recentAttendance.set(sortedLogs.slice(0, 5));
          },
          error: () => {}
        });

        // Fetch leaves to compute balance (Total 20 - approved leave days)
        this.leaveService.getMyLeaves().subscribe({
          next: (leaves) => {
            const approvedLeaves = leaves.filter(l => l.status === 'APPROVED');
            const totalDays = approvedLeaves.reduce((sum, leave) => sum + leave.totalDays, 0);
            this.leaveBalance.set(20 - totalDays);
            this.isLoading.set(false);
          },
          error: () => {
            this.isLoading.set(false);
          }
        });
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }
}
