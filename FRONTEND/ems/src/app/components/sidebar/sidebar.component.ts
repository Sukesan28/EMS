import { Component, Input, computed } from '@angular/core';
import { CommonModule } from '@angular/common';
import { RouterLink, RouterLinkActive } from '@angular/router';
import { AuthService } from '../../services/auth.service';

interface MenuItem {
  path: string;
  label: string;
  icon: string;
  roles: string[];
}

@Component({
  selector: 'app-sidebar',
  standalone: true,
  imports: [CommonModule, RouterLink, RouterLinkActive],
  templateUrl: './sidebar.component.html',
  styleUrl: './sidebar.component.css'
})
export class SidebarComponent {
  @Input() isCollapsed = false;

  private allMenuItems: MenuItem[] = [
    // Dashboards
    { path: '/hr-dashboard', label: 'Dashboard', icon: '', roles: ['HR'] },
    { path: '/manager-dashboard', label: 'Dashboard', icon: '', roles: ['MANAGER'] },
    { path: '/employee-dashboard', label: 'Dashboard', icon: '', roles: ['EMPLOYEE'] },
    { path: '/it-support-dashboard', label: 'IT Support', icon: '', roles: ['IT_SUPPORT'] },
    { path: '/it-support/reset-password', label: 'Reset Password', icon: '', roles: ['IT_SUPPORT'] },
    { path: '/it-support/suspend-employee', label: 'Suspend Employee', icon: '', roles: ['IT_SUPPORT'] },
    
    // Modules
    { path: '/employees', label: 'Employees', icon: '', roles: ['HR'] },
    { path: '/departments', label: 'Departments', icon: '', roles: ['HR'] },
    
    // Attendance
    { path: '/attendance', label: 'Attendance', icon: '', roles: ['HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT'] },
    
    // Leave
    { path: '/leave', label: 'Leaves', icon: '', roles: ['HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT'] },
    
    // Tasks
    { path: '/tasks', label: 'Tasks', icon: '', roles: ['HR'] },
    { path: '/tasks', label: 'Team Tasks', icon: '', roles: ['MANAGER'] },
    { path: '/tasks', label: 'My Tasks', icon: '', roles: ['EMPLOYEE'] },
    
    // Performance
    { path: '/performance', label: 'Performance', icon: '', roles: ['HR'] },
    { path: '/performance', label: 'Team Reviews', icon: '', roles: ['MANAGER'] },
    { path: '/performance', label: 'Performance', icon: '', roles: ['EMPLOYEE'] },
    
    // Profile
    { path: '/profile', label: 'My Profile', icon: '', roles: ['HR', 'MANAGER', 'EMPLOYEE', 'IT_SUPPORT'] },
    
    // Reports
    { path: '/reports', label: 'Reports', icon: '', roles: ['HR'] }
  ];

  filteredMenuItems = computed(() => {
    const user = this.authService.currentUser();
    const role = user ? user.role : null;
    if (!role) return [];
    
    const items: MenuItem[] = [];
    for (let i = 0; i < this.allMenuItems.length; i++) {
      const item = this.allMenuItems[i];
      if (item.roles.indexOf(role) !== -1) {
        items.push(item);
      }
    }
    return items;
  });

  constructor(private authService: AuthService) {}

  logout() {
    this.authService.logout();
  }
}
