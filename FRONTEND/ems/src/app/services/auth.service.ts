import { Injectable, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { LoginRequestDTO, LoginResponseDTO, ChangePasswordRequestDTO } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private readonly apiUrl = 'http://localhost:8081/api/auth';
  
  currentUser = signal<LoginResponseDTO | null>(null);

  constructor(
    private http: HttpClient,
    private router: Router
  ) {
    this.loadSession();
  }

  initializeAdmin(request: LoginRequestDTO): Observable<LoginResponseDTO> {
    return this.http.post<LoginResponseDTO>(`${this.apiUrl}/bootstrap`, request).pipe(
      tap(response => {
        if (response && response.employeeCode && response.token) {
          this.setSession(response, 'Bearer ' + response.token);
        }
      })
    );
  }

  login(request: LoginRequestDTO): Observable<LoginResponseDTO> {
    return this.http.post<LoginResponseDTO>(`${this.apiUrl}/login`, request).pipe(
      tap(response => {
        if (response && response.employeeCode) {
          const authString = response.token 
            ? 'Bearer ' + response.token 
            : 'Basic ' + btoa(`${request.employeeCode}:${request.password}`);
          this.setSession(response, authString);
        }
      })
    );
  }

  private setSession(user: LoginResponseDTO, authHeader: string): void {
    this.currentUser.set(user);
    localStorage.setItem('currentUser', JSON.stringify(user));
    localStorage.setItem('authHeader', authHeader);
  }

  changePassword(request: ChangePasswordRequestDTO): Observable<any> {
    return this.http.put(`${this.apiUrl}/change-password`, request).pipe(
      tap(() => {
        const user = this.currentUser();
        if (user) {
          user.firstLogin = false;
          this.currentUser.set({ ...user });
          localStorage.setItem('currentUser', JSON.stringify(user));
        }
      })
    );
  }

  logout(): void {
    localStorage.removeItem('currentUser');
    localStorage.removeItem('authHeader');
    this.currentUser.set(null);
    this.router.navigate(['/login']);
  }

  isAuthenticated(): boolean {
    return this.currentUser() !== null;
  }

  getRole(): string | null {
    const user = this.currentUser();
    return user ? user.role : null;
  }

  getAuthHeader(): string | null {
    return localStorage.getItem('authHeader');
  }

  private loadSession(): void {
    const localUser = localStorage.getItem('currentUser');
    const header = this.getAuthHeader();
    
    if (header && !header.startsWith('Bearer ')) {
      this.logout();
      return;
    }
    
    if (localUser) {
      this.currentUser.set(JSON.parse(localUser));
    }
  }
}
