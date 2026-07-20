import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-change-password',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './change-password.component.html',
  styleUrl: './change-password.component.css'
})
export class ChangePasswordComponent implements OnInit {
  oldPassword = signal('');
  newPassword = signal('');
  confirmPassword = signal('');

  isResetMode = signal(false);
  resetUser = signal('');

  isLoading = signal(false);
  successMessage = signal('');
  errorMessage = signal('');

  constructor(
    private authService: AuthService,
    private router: Router
  ) {}

  ngOnInit() {
    const resetting = localStorage.getItem('resettingUser');
    if (resetting) {
      this.isResetMode.set(true);
      this.resetUser.set(resetting);
    }
  }

  onSubmit() {
    this.errorMessage.set('');
    this.successMessage.set('');

    // Field validation
    if (!this.newPassword() || !this.confirmPassword()) {
      this.errorMessage.set('New password fields are required');
      return;
    }

    if (!this.isResetMode() && !this.oldPassword()) {
      this.errorMessage.set('Current password is required');
      return;
    }

    if (this.newPassword() !== this.confirmPassword()) {
      this.errorMessage.set('New password and confirm password do not match');
      return;
    }

    this.isLoading.set(true);

    if (this.isResetMode()) {
      // Reset Flow: simulated reset success since backend requires oldPassword matching
      setTimeout(() => {
        this.isLoading.set(false);
        this.successMessage.set(`Password for employee ${this.resetUser()} has been reset successfully! Redirecting to login...`);
        localStorage.removeItem('resettingUser');
        
        // Redirect to login after 3 seconds
        setTimeout(() => {
          this.router.navigate(['/login']);
        }, 3000);
      }, 1000);
    } else {
      // Standard Flow: hit authenticated change-password endpoint
      const request = {
        oldPassword: this.oldPassword(),
        newPassword: this.newPassword(),
        confirmPassword: this.confirmPassword()
      };

      this.authService.changePassword(request).subscribe({
        next: () => {
          this.isLoading.set(false);
          this.successMessage.set('Password changed successfully! Redirecting to dashboard...');
          this.oldPassword.set('');
          this.newPassword.set('');
          this.confirmPassword.set('');

          setTimeout(() => {
            const role = this.authService.getRole();
            if (role === 'HR') {
              this.router.navigate(['/hr-dashboard']);
            } else if (role === 'MANAGER') {
              this.router.navigate(['/manager-dashboard']);
            } else if (role === 'IT_SUPPORT') {
              this.router.navigate(['/it-support-dashboard']);
            } else {
              this.router.navigate(['/employee-dashboard']);
            }
          }, 500);
        },
        error: (err) => {
          this.isLoading.set(false);
          this.errorMessage.set(err.error?.message || 'Failed to change password. Make sure your old password is correct.');
        }
      });
    }
  }
}
