import { Component, OnInit } from '@angular/core';
import { Routes, Router } from '@angular/router';
import { AuthService } from './services/auth.service';
import { authGuard } from './guards/auth.guard';
import { roleGuard } from './guards/role.guard';

// Layouts
import { AuthLayoutComponent } from './layouts/auth-layout/auth-layout.component';
import { AdminLayoutComponent } from './layouts/admin-layout/admin-layout.component';

// Pages
import { LoginComponent } from './pages/login/login.component';
import { ResetPasswordComponent } from './pages/reset-password/reset-password.component';
import { HrDashboardComponent } from './pages/hr-dashboard/hr-dashboard.component';
import { ManagerDashboardComponent } from './pages/manager-dashboard/manager-dashboard.component';
import { EmployeeDashboardComponent } from './pages/employee-dashboard/employee-dashboard.component';
import { ItSupportDashboardComponent } from './pages/it-support-dashboard/it-support-dashboard.component';
import { EmployeesComponent } from './pages/employees/employees.component';
import { ManagerEmployeesComponent } from './pages/manager-employees/manager-employees.component';
import { DepartmentsComponent } from './pages/departments/departments.component';
import { AttendanceComponent } from './pages/attendance/attendance.component';
import { LeaveComponent } from './pages/leave/leave.component';
import { TasksComponent } from './pages/tasks/tasks.component';
import { PerformanceComponent } from './pages/performance/performance.component';
import { ProfileComponent } from './pages/profile/profile.component';
import { ReportsComponent } from './pages/reports/reports.component';
import { ChangePasswordComponent } from './pages/change-password/change-password.component';
import { UnauthorizedComponent } from './pages/unauthorized/unauthorized.component';
import { NotfoundComponent } from './pages/notfound/notfound.component';
import { ItSupportResetPasswordComponent } from './pages/it-support-reset-password/it-support-reset-password.component';
import { ItSupportSuspendComponent } from './pages/it-support-suspend/it-support-suspend.component';

@Component({
  selector: 'app-dashboard-redirect',
  standalone: true,
  template: ''
})
export class DashboardRedirectComponent implements OnInit {
  constructor(private authService: AuthService, private router: Router) {}
  ngOnInit() {
    const role = this.authService.getRole();
    if (role === 'HR') {
      this.router.navigate(['/hr-dashboard']);
    } else if (role === 'MANAGER') {
      this.router.navigate(['/manager-dashboard']);
    } else if (role === 'IT_SUPPORT') {
      this.router.navigate(['/it-suppo. rt-dashboard']);
    } else if (role === 'EMPLOYEE') {
      this.router.navigate(['/employee-dashboard']);
    } else {
      // Default fallback
      this.router.navigate(['/login']);
    }
  }
}

export const routes: Routes = [
  // Open Redirect
  {
    path: '',
    pathMatch: 'full',
    redirectTo: 'login'
  },
  
  // Auth Layout wrapper
  {
    path: '',
    component: AuthLayoutComponent,
    children: [
      { path: 'login', component: LoginComponent },
      { path: 'reset-password', component: ResetPasswordComponent }
    ]
  },
  
  // Admin/Dashboard Layout wrapper (Security guards active)
  {
    path: '',
    component: AdminLayoutComponent,
    canActivate: [authGuard],
    children: [
      // Redirect helper
      { path: 'dashboard', component: DashboardRedirectComponent },
      
      // Role Dashboards
      { 
        path: 'hr-dashboard', 
        component: HrDashboardComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR'] }
      },
      { 
        path: 'manager-dashboard', 
        component: ManagerDashboardComponent,
        canActivate: [roleGuard],
        data: { roles: ['MANAGER'] }
      },
      { 
        path: 'employee-dashboard', 
        component: EmployeeDashboardComponent,
        canActivate: [roleGuard],
        data: { roles: ['EMPLOYEE'] }
      },
      { 
        path: 'it-support-dashboard', 
        component: ItSupportDashboardComponent,
        canActivate: [roleGuard],
        data: { roles: ['IT_SUPPORT'] }
      },
      { 
        path: 'it-support/reset-password', 
        component: ItSupportResetPasswordComponent,
        canActivate: [roleGuard],
        data: { roles: ['IT_SUPPORT'] }
      },
      { 
        path: 'it-support/suspend-employee', 
        component: ItSupportSuspendComponent,
        canActivate: [roleGuard],
        data: { roles: ['IT_SUPPORT'] }
      },
      
      // Modules
      { 
        path: 'employees', 
        component: EmployeesComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR'] }
      },
      { 
        path: 'manager/employees', 
        component: ManagerEmployeesComponent,
        canActivate: [roleGuard],
        data: { roles: ['MANAGER'] }
      },
      { 
        path: 'departments', 
        component: DepartmentsComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR'] }
      },
      { 
        path: 'attendance', 
        component: AttendanceComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT'] }
      },
      { 
        path: 'leave', 
        component: LeaveComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT'] }
      },
      { 
        path: 'tasks', 
        component: TasksComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR', 'MANAGER', 'EMPLOYEE'] }
      },
      { 
        path: 'performance', 
        component: PerformanceComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT'] }
      },
      { 
        path: 'profile', 
        component: ProfileComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT'] }
      },
      { 
        path: 'reports', 
        component: ReportsComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR'] }
      },
      { 
        path: 'change-password', 
        component: ChangePasswordComponent,
        canActivate: [roleGuard],
        data: { roles: ['HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT'] }
      }
    ]
  },
  
  // Error Pages
  { path: 'unauthorized', component: UnauthorizedComponent },
  { path: '**', component: NotfoundComponent }
];
