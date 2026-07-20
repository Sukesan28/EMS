import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { DepartmentRequestDTO, DepartmentResponseDTO, DepartmentManagerHistoryResponseDTO } from '../models/department.model';

@Injectable({
  providedIn: 'root'
})
export class DepartmentService {
  private readonly apiUrl = 'http://localhost:8081/api/departments';

  constructor(private http: HttpClient) {}

  createDepartment(dto: DepartmentRequestDTO): Observable<DepartmentResponseDTO> {
    return this.http.post<DepartmentResponseDTO>(this.apiUrl, dto);
  }

  getAllDepartments(): Observable<DepartmentResponseDTO[]> {
    return this.http.get<DepartmentResponseDTO[]>(this.apiUrl);
  }

  getDepartmentById(id: number): Observable<DepartmentResponseDTO> {
    return this.http.get<DepartmentResponseDTO>(`${this.apiUrl}/${id}`);
  }

  updateDepartment(id: number, dto: DepartmentRequestDTO): Observable<DepartmentResponseDTO> {
    return this.http.put<DepartmentResponseDTO>(`${this.apiUrl}/${id}`, dto);
  }

  changeManager(id: number, newManagerId: number): Observable<DepartmentResponseDTO> {
    return this.http.put<DepartmentResponseDTO>(`${this.apiUrl}/${id}/change-manager?newManagerId=${newManagerId}`, {});
  }

  getManagerHistory(id: number): Observable<DepartmentManagerHistoryResponseDTO[]> {
    return this.http.get<DepartmentManagerHistoryResponseDTO[]>(`${this.apiUrl}/${id}/manager-history`);
  }
}
