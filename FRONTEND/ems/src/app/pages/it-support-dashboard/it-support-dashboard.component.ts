import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ITSupportService } from '../../services/it-support.service';
import { EmployeeService } from '../../services/employee.service';
import { AuthService } from '../../services/auth.service';
import { GreetingComponent } from '../../components/greeting/greeting.component';
import { StatisticCardComponent } from '../../components/statistic-card/statistic-card.component';
import { EmployeeResponseDTO } from '../../models/user.model';

interface SupportTicket {
  id: string;
  employeeId: number;
  employeeCode: string;
  employeeName: string;
  issueType: 'LOCKED_ACCOUNT' | 'PASSWORD_RESET';
  details: string;
  status: 'OPEN' | 'RESOLVED';
  createdAt: string;
}

@Component({
  selector: 'app-it-support-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule, GreetingComponent, StatisticCardComponent],
  templateUrl: './it-support-dashboard.component.html',
  styleUrl: './it-support-dashboard.component.css'
})
export class ItSupportDashboardComponent implements OnInit {
  fullName = '';
  
  // Dashboard statistics
  lockedAccountsCount = signal(0);
  resetRequestsCount = signal(0);
  openTicketsCount = signal(0);
  inactiveAccountsCount = signal(0);

  isLoading = signal(true);
  errorMessage = signal('');
  successMessage = signal('');

  // Lists
  tickets = signal<SupportTicket[]>([]);
  employeesList = signal<EmployeeResponseDTO[]>([]);

  constructor(
    private authService: AuthService,
    private itSupportService: ITSupportService,
    private employeeService: EmployeeService
  ) {
    const user = this.authService.currentUser();
    this.fullName = user ? user.fullName : 'IT Support Specialist';
  }

  ngOnInit() {
    this.loadDashboardData();
  }

  loadDashboardData() {
    this.isLoading.set(true);
    this.errorMessage.set('');
    
    this.employeeService.getAllEmployees().subscribe({
      next: (employees) => {
        this.employeesList.set(employees);
        
        // Generate tickets queue dynamically based on real database conditions
        const queue: SupportTicket[] = [];
        let ticketIdSeq = 101;
        
        employees.forEach((emp) => {
          if (emp.accountLocked) {
            queue.push({
              id: `TKT-LCK${ticketIdSeq++}`,
              employeeId: emp.id,
              employeeCode: emp.employeeCode,
              employeeName: `${emp.firstName} ${emp.lastName}`,
              issueType: 'LOCKED_ACCOUNT',
              details: 'User account locked automatically following failed security access attempts.',
              status: 'OPEN',
              createdAt: emp.updatedAt || new Date().toISOString()
            });
          }
          if (emp.firstLogin && emp.accountEnabled && !emp.accountLocked) {
            queue.push({
              id: `TKT-RST${ticketIdSeq++}`,
              employeeId: emp.id,
              employeeCode: emp.employeeCode,
              employeeName: `${emp.firstName} ${emp.lastName}`,
              issueType: 'PASSWORD_RESET',
              details: 'Requires default password change or requested a secure credential update.',
              status: 'OPEN',
              createdAt: emp.updatedAt || new Date().toISOString()
            });
          }
        });

        this.tickets.set(queue);
        this.openTicketsCount.set(queue.length);
        this.lockedAccountsCount.set(employees.filter(e => e.accountLocked).length);
        this.inactiveAccountsCount.set(employees.filter(e => !e.accountEnabled).length);
        
        // Count password resets (defined as active users needing their first login/reset)
        const resets = employees.filter(e => e.firstLogin && e.accountEnabled && !e.accountLocked).length;
        this.resetRequestsCount.set(resets);
        
        this.isLoading.set(false);
      },
      error: (err) => {
        this.isLoading.set(false);
        this.errorMessage.set(err.error?.message || 'Failed to fetch employee directories from the server.');
      }
    });
  }

  // Resolve Ticket Quick Trigger
  resolveTicket(ticket: SupportTicket) {
    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    if (ticket.issueType === 'LOCKED_ACCOUNT') {
      this.itSupportService.unlockAccount(ticket.employeeId).subscribe({
        next: (res) => {
          this.successMessage.set(`Successfully resolved ticket: unlocked account access for ${res.firstName} ${res.lastName} (${res.employeeCode})`);
          this.loadDashboardData();
        },
        error: (err) => {
          this.isLoading.set(false);
          this.errorMessage.set(err.error?.message || 'Failed to unlock user account.');
        }
      });
    } else {
      this.itSupportService.resetPassword(ticket.employeeId).subscribe({
        next: (res) => {
          this.successMessage.set(`Successfully resolved ticket: password reset for ${res.firstName} ${res.lastName} (${res.employeeCode}). New credentials emailed.`);
          this.loadDashboardData();
        },
        error: (err) => {
          this.isLoading.set(false);
          this.errorMessage.set(err.error?.message || 'Failed to reset user password.');
        }
      });
    }
  }
}
