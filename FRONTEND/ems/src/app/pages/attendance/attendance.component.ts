import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { AttendanceService } from '../../services/attendance.service';
import { AuthService } from '../../services/auth.service';
import { TableComponent } from '../../components/table/table.component';
import { AttendanceResponseDTO } from '../../models/attendance.model';

@Component({
  selector: 'app-attendance',
  standalone: true,
  imports: [CommonModule, FormsModule, TableComponent],
  templateUrl: './attendance.component.html',
  styleUrl: './attendance.component.css'
})
export class AttendanceComponent implements OnInit {
  userRole = '';
  logs = signal<AttendanceResponseDTO[]>([]);
  
  // States
  isLoading = signal(true);
  isEmpty = signal(false);
  
  // Clock state for employees
  hasClockedInToday = signal(false);
  hasClockedOutToday = signal(false);
  todayLog = signal<AttendanceResponseDTO | null>(null);

  // Tab State
  activeTab: 'my-attendance' | 'team-attendance' | 'all-attendance' = 'my-attendance';

  constructor(
    private authService: AuthService,
    private attendanceService: AttendanceService
  ) {
    const user = this.authService.currentUser();
    this.userRole = user ? user.role : 'EMPLOYEE';
    
    // Set default active tab based on role
    if (this.userRole === 'HR') {
      this.activeTab = 'all-attendance';
    } else if (this.userRole === 'MANAGER') {
      this.activeTab = 'team-attendance';
    } else {
      this.activeTab = 'my-attendance';
    }
  }

  ngOnInit() {
    this.loadLogs();
  }

  loadLogs() {
    this.isLoading.set(true);
    
    if (this.activeTab === 'my-attendance') {
      this.attendanceService.getMyAttendance().subscribe({
        next: (data) => {
          this.logs.set(data);
          this.isEmpty.set(data.length === 0);
          this.checkTodayStatus(data);
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
          this.isEmpty.set(true);
        }
      });
    } else if (this.activeTab === 'team-attendance') {
      this.attendanceService.getTeamAttendance().subscribe({
        next: (data) => {
          this.logs.set(data);
          this.isEmpty.set(data.length === 0);
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
          this.isEmpty.set(true);
        }
      });
    } else if (this.activeTab === 'all-attendance') {
      this.attendanceService.getAllAttendance().subscribe({
        next: (data) => {
          this.logs.set(data);
          this.isEmpty.set(data.length === 0);
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
          this.isEmpty.set(true);
        }
      });
    }
  }

  private checkTodayStatus(data: AttendanceResponseDTO[]) {
    const todayStr = new Date().toISOString().split('T')[0];
    const log = data.find(l => l.attendanceDate === todayStr);
    
    if (log) {
      this.todayLog.set(log);
      this.hasClockedInToday.set(log.clockInTime !== null);
      this.hasClockedOutToday.set(log.clockOutTime !== null);
    } else {
      this.todayLog.set(null);
      this.hasClockedInToday.set(false);
      this.hasClockedOutToday.set(false);
    }
  }

  switchTab(tab: 'my-attendance' | 'team-attendance' | 'all-attendance') {
    this.activeTab = tab;
    this.loadLogs();
  }

  clockIn() {
    this.isLoading.set(true);
    const todayStr = new Date().toISOString().split('T')[0];
    this.attendanceService.clockIn({ attendanceDate: todayStr }).subscribe({
      next: () => {
        this.loadLogs();
      },
      error: (err) => {
        this.isLoading.set(false);
        alert(err.error?.message || 'Failed to clock in.');
      }
    });
  }

  clockOut() {
    this.isLoading.set(true);
    this.attendanceService.clockOut().subscribe({
      next: () => {
        this.loadLogs();
      },
      error: (err) => {
        this.isLoading.set(false);
        alert(err.error?.message || 'Failed to clock out.');
      }
    });
  }

  getHeaders(): string[] {
    if (this.activeTab === 'my-attendance') {
      return ['Attendance Date', 'Check In Time', 'Check Out Time', 'Working Hours', 'Overtime Hours', 'Status'];
    } else {
      return ['Employee Code', 'Employee Name', 'Attendance Date', 'Check In Time', 'Check Out Time', 'Working Hours', 'Overtime Hours', 'Status'];
    }
  }
}
