import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { EmployeeService } from '../../services/employee.service';
import { LeaveService } from '../../services/leave.service';
import { AttendanceService } from '../../services/attendance.service';
import { TableComponent } from '../../components/table/table.component';

@Component({
  selector: 'app-reports',
  standalone: true,
  imports: [CommonModule, FormsModule, TableComponent],
  templateUrl: './reports.component.html',
  styleUrl: './reports.component.css'
})
export class ReportsComponent implements OnInit {
  reportType: 'EMPLOYEES' | 'LEAVES' | 'ATTENDANCE' = 'EMPLOYEES';
  
  employeesList = signal<any[]>([]);
  leavesList = signal<any[]>([]);
  attendanceList = signal<any[]>([]);
  
  todayDate = new Date();
  isLoading = signal(false);
  isEmpty = signal(true);

  constructor(
    private employeeService: EmployeeService,
    private leaveService: LeaveService,
    private attendanceService: AttendanceService
  ) {}

  ngOnInit() {
    this.generateReport();
  }

  generateReport() {
    this.isLoading.set(true);
    this.isEmpty.set(true);

    if (this.reportType === 'EMPLOYEES') {
      this.employeeService.getAllEmployees().subscribe({
        next: (data) => {
          this.employeesList.set(data);
          this.isEmpty.set(data.length === 0);
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
        }
      });
    } else if (this.reportType === 'LEAVES') {
      this.leaveService.getAllLeaves().subscribe({
        next: (data) => {
          this.leavesList.set(data);
          this.isEmpty.set(data.length === 0);
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
        }
      });
    } else if (this.reportType === 'ATTENDANCE') {
      this.attendanceService.getAllAttendance().subscribe({
        next: (data) => {
          this.attendanceList.set(data);
          this.isEmpty.set(data.length === 0);
          this.isLoading.set(false);
        },
        error: () => {
          this.isLoading.set(false);
        }
      });
    }
  }
}
