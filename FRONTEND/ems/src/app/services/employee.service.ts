import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { EmployeeRequestDTO, EmployeeResponseDTO, EmployeeSelfUpdateDTO } from '../models/user.model';

@Injectable({
  providedIn: 'root'
})
export class EmployeeService {
  private readonly apiUrl = 'http://localhost:8081/api/employees';

  constructor(private http: HttpClient) {}

  createEmployee(dto: EmployeeRequestDTO): Observable<EmployeeResponseDTO> {
    return this.http.post<EmployeeResponseDTO>(this.apiUrl, dto);
  }

  getAllEmployees(filters?: {
    code?: string;
    email?: string;
    department?: string;
    role?: string;
  }): Observable<EmployeeResponseDTO[]> {
    let params = new HttpParams();
    if (filters) {
      if (filters.code) params = params.set('code', filters.code);
      if (filters.email) params = params.set('email', filters.email);
      if (filters.department) params = params.set('department', filters.department);
      if (filters.role) params = params.set('role', filters.role);
    }
    return this.http.get<EmployeeResponseDTO[]>(this.apiUrl, { params });
  }

  getProfile(): Observable<EmployeeResponseDTO> {
    return this.http.get<EmployeeResponseDTO>(`${this.apiUrl}/profile`);
  }

  selfUpdate(dto: EmployeeSelfUpdateDTO): Observable<EmployeeResponseDTO> {
    return this.http.put<EmployeeResponseDTO>(`${this.apiUrl}/profile`, dto);
  }

  getEmployeeById(id: number): Observable<EmployeeResponseDTO> {
    return this.http.get<EmployeeResponseDTO>(`${this.apiUrl}/emp/${id}`);
  }

  updateEmployee(id: number, dto: EmployeeRequestDTO): Observable<EmployeeResponseDTO> {
    return this.http.put<EmployeeResponseDTO>(`${this.apiUrl}/${id}`, dto);
  }

  deleteEmployee(id: number): Observable<{ message: string }> {
    return this.http.delete<{ message: string }>(`${this.apiUrl}/${id}`);
  }

  getEmployeeProfileDetails(id: number): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/${id}/profile-details`);
  }

  getMyProfileDetails(): Observable<any> {
    return this.http.get<any>(`${this.apiUrl}/my-profile-details`);
  }
}
