import { Component, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { RouterLink } from '@angular/router';

@Component({
  selector: 'app-reset-password',
  standalone: true,
  imports: [CommonModule, FormsModule, RouterLink],
  templateUrl: './reset-password.component.html',
  styleUrl: './reset-password.component.css'
})
export class ResetPasswordComponent {
  employeeCode = signal('');
  newPassword = signal('');
  confirmPassword = signal('');
  
  isSubmitted = signal(false);
  errorMessage = signal('');

  onSubmit() {
    if (!this.employeeCode() || !this.newPassword() || !this.confirmPassword()) {
      this.errorMessage.set('All fields are required');
      return;
    }

    if (this.newPassword() !== this.confirmPassword()) {
      this.errorMessage.set('Passwords do not match');
      return;
    }
    
    this.errorMessage.set('');
    this.isSubmitted.set(true);
  }
}
