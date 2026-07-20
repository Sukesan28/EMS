import { Component, EventEmitter, Output } from '@angular/core';
import { CommonModule } from '@angular/common';
import { AuthService } from '../../services/auth.service';

@Component({
  selector: 'app-header',
  standalone: true,
  imports: [CommonModule],
  templateUrl: './header.component.html',
  styleUrl: './header.component.css'
})
export class HeaderComponent {
  @Output() sidebarToggle = new EventEmitter<void>();
  
  constructor(private authService: AuthService) {}

  getCurrentUser() {
    return this.authService.currentUser();
  }

  toggleSidebar() {
    this.sidebarToggle.emit();
  }



  logout() {
    this.authService.logout();
  }
}
