import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { LeaveRequestDTO, LeaveApprovalRequestDTO, LeaveResponseDTO } from '../models/leave.model';

@Injectable({
  providedIn: 'root'
})
export class LeaveService {
  private readonly apiUrl = 'http://localhost:8081/api/leaves';

  constructor(private http: HttpClient) {}

  applyLeave(dto: LeaveRequestDTO): Observable<LeaveResponseDTO> {
    return this.http.post<LeaveResponseDTO>(this.apiUrl, dto);
  }

  cancelLeave(id: number): Observable<LeaveResponseDTO> {
    return this.http.put<LeaveResponseDTO>(`${this.apiUrl}/${id}/cancel`, {});
  }

  getMyLeaves(): Observable<LeaveResponseDTO[]> {
    return this.http.get<LeaveResponseDTO[]>(`${this.apiUrl}/my-leaves`);
  }

  getPendingLeaves(): Observable<LeaveResponseDTO[]> {
    return this.http.get<LeaveResponseDTO[]>(`${this.apiUrl}/pending`);
  }

  approveLeave(id: number, dto: LeaveApprovalRequestDTO): Observable<LeaveResponseDTO> {
    return this.http.put<LeaveResponseDTO>(`${this.apiUrl}/${id}/approve`, dto);
  }

  rejectLeave(id: number, dto: LeaveApprovalRequestDTO): Observable<LeaveResponseDTO> {
    return this.http.put<LeaveResponseDTO>(`${this.apiUrl}/${id}/reject`, dto);
  }

  getAllLeaves(): Observable<LeaveResponseDTO[]> {
    return this.http.get<LeaveResponseDTO[]>(this.apiUrl);
  }
}
