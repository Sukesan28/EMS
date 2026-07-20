import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink } from '@angular/router';
import { EmployeeService } from '../../services/employee.service';
import { LeaveService } from '../../services/leave.service';
import { AttendanceService } from '../../services/attendance.service';
import { AuthService } from '../../services/auth.service';
import { GreetingComponent } from '../../components/greeting/greeting.component';
import { StatisticCardComponent } from '../../components/statistic-card/statistic-card.component';
import { EmployeeResponseDTO } from '../../models/user.model';
import { LeaveResponseDTO } from '../../models/leave.model';

@Component({
  selector: 'app-manager-dashboard',
  standalone: true,
  imports: [CommonModule, RouterLink, GreetingComponent, StatisticCardComponent],
  templateUrl: './manager-dashboard.component.html',
  styleUrl: './manager-dashboard.component.css'
})
export class ManagerDashboardComponent implements OnInit {
  fullName = '';
  
  // Dashboard statistics
  teamCount = signal(0);
  presentCount = signal(0);
  absentCount = signal(0);
  pendingLeavesCount = signal(0);
  
  teamMembers = signal<EmployeeResponseDTO[]>([]);
  pendingLeaves = signal<LeaveResponseDTO[]>([]);
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
    private leaveService: LeaveService,
    private attendanceService: AttendanceService
  ) {
    const user = this.authService.currentUser();
    this.fullName = user ? user.fullName : 'Manager';
  }

  ngOnInit() {
    this.loadDashboardData();
  }

  private loadDashboardData() {
    this.isLoading.set(true);
    const managerUser = this.authService.currentUser();
    
    // Fetch all employees to find the team members
    this.employeeService.getAllEmployees().subscribe({
      next: (employees) => {
        const team = employees.filter(emp => emp.managerName === managerUser?.fullName);
        this.teamMembers.set(team);
        this.teamCount.set(team.length);
        
        this.attendanceService.getTeamAttendance().subscribe({
          next: (attendanceLogs) => {
            const todayStr = new Date().toISOString().split('T')[0];
            const todayPresentTeam = attendanceLogs.filter(log => 
              log.attendanceDate === todayStr && 
              log.attendanceStatus === 'PRESENT'
            );
            this.presentCount.set(todayPresentTeam.length);
            
            // Assume rest is absent or on leave
            const absentCalculated = this.teamCount() - this.presentCount();
            this.absentCount.set(absentCalculated > 0 ? absentCalculated : 0);
          },
          error: () => {}
        });
      },
      error: () => {}
    });

    // Fetch pending leaves
    this.leaveService.getPendingLeaves().subscribe({
      next: (leaves) => {
        this.pendingLeaves.set(leaves);
        this.pendingLeavesCount.set(leaves.length);
        this.isLoading.set(false);
      },
      error: () => {
        this.isLoading.set(false);
      }
    });
  }
}
