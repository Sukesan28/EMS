import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmployeeResponseDTO } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class ITSupportService {
  private readonly apiUrl = 'http://localhost:8081/api/it-support';

  constructor(private http: HttpClient) {}

  resetPassword(employeeId: number): Observable<EmployeeResponseDTO> {
    return this.http.put<EmployeeResponseDTO>(`${this.apiUrl}/reset-password/${employeeId}`, {});
  }

  unlockAccount(employeeId: number): Observable<EmployeeResponseDTO> {
    return this.http.put<EmployeeResponseDTO>(`${this.apiUrl}/unlock-account/${employeeId}`, {});
  }

  enableAccount(employeeId: number): Observable<EmployeeResponseDTO> {
    return this.http.put<EmployeeResponseDTO>(`${this.apiUrl}/enable-account/${employeeId}`, {});
  }

  disableAccount(employeeId: number): Observable<EmployeeResponseDTO> {
    return this.http.put<EmployeeResponseDTO>(`${this.apiUrl}/disable-account/${employeeId}`, {});
  }
}
