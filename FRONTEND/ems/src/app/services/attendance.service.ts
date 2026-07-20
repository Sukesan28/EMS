import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ClockInRequestDTO, AttendanceResponseDTO } from '../models/attendance.model';

@Injectable({
  providedIn: 'root'
})
export class AttendanceService {
  private readonly apiUrl = 'http://localhost:8081/api/attendance';

  constructor(private http: HttpClient) {}

  clockIn(dto: ClockInRequestDTO): Observable<AttendanceResponseDTO> {
    return this.http.post<AttendanceResponseDTO>(`${this.apiUrl}/clock-in`, dto);
  }

  clockOut(): Observable<AttendanceResponseDTO> {
    return this.http.put<AttendanceResponseDTO>(`${this.apiUrl}/clock-out`, {});
  }

  getMyAttendance(): Observable<AttendanceResponseDTO[]> {
    return this.http.get<AttendanceResponseDTO[]>(`${this.apiUrl}/my-attendance`);
  }

  getTeamAttendance(): Observable<AttendanceResponseDTO[]> {
    return this.http.get<AttendanceResponseDTO[]>(`${this.apiUrl}/team`);
  }

  getAllAttendance(): Observable<AttendanceResponseDTO[]> {
    return this.http.get<AttendanceResponseDTO[]>(this.apiUrl);
  }
}
