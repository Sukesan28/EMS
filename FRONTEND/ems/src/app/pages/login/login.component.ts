import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './login.component.html',
  styleUrl: './login.component.css'
})
export class LoginComponent {
  employeeCode = signal('');
  password = signal('');
  rememberMe = signal(true);
  isInitMode = signal(false); // Toggle to allow initialization of the HR account
  
  isLoading = signal(false);
  errorMessage = signal('');
  successMessage = signal('');

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  onSubmit() {
    if (!this.employeeCode() || !this.password()) {
      this.errorMessage.set('Employee code and password are required');
      return;
    }

    this.isLoading.set(true);
    this.errorMessage.set('');
    this.successMessage.set('');

    const credentials = {
      employeeCode: this.employeeCode(),
      password: this.password()
    };

    if (this.isInitMode()) {
      this.authService.initializeAdmin(credentials).subscribe({
        next: (res) => {
          this.isLoading.set(false);
          this.successMessage.set('HR Admin account initialized successfully! You can now log in using the credentials you created.');
          this.isInitMode.set(false);
        },
        error: (err) => {
          this.isLoading.set(false);
          this.errorMessage.set(err.error?.message || 'System initialization failed. The database may already be initialized.');
        }
      });
    } else {
      this.authService.login(credentials).subscribe({
        next: (res) => {
          this.isLoading.set(false);
          
          if (res.firstLogin) {
            this.router.navigate(['/change-password']);
            return;
          }
          
          // Redirect based on role
          if (res.role === 'HR') {
            this.router.navigate(['/hr-dashboard']);
          } else if (res.role === 'MANAGER') {
            this.router.navigate(['/manager-dashboard']);
          } else if (res.role === 'IT_SUPPORT') {
            this.router.navigate(['/it-support-dashboard']);
          } else {
            this.router.navigate(['/employee-dashboard']);
          }
        },
        error: (err) => {
          this.isLoading.set(false);
          this.errorMessage.set(err.error?.message || 'Login failed. Please verify your credentials.');
        }
      });
    }
  }

  toggleInitMode() {
    this.isInitMode.update(val => !val);
    this.errorMessage.set('');
    this.successMessage.set('');
  }
}
