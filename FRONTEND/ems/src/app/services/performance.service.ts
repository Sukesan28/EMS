import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PerformanceRequestDTO, PerformanceResponseDTO } from '../models/performance.model';

@Injectable({
  providedIn: 'root'
})
export class PerformanceService {
  private readonly apiUrl = 'http://localhost:8081/api/performance';

  constructor(private http: HttpClient) {}

  createReview(employeeId: number, dto: PerformanceRequestDTO): Observable<PerformanceResponseDTO> {
    return this.http.post<PerformanceResponseDTO>(`${this.apiUrl}/${employeeId}`, dto);
  }

  updateReview(reviewId: number, dto: PerformanceRequestDTO): Observable<PerformanceResponseDTO> {
    return this.http.put<PerformanceResponseDTO>(`${this.apiUrl}/${reviewId}`, dto);
  }

  getMyReviews(): Observable<PerformanceResponseDTO[]> {
    return this.http.get<PerformanceResponseDTO[]>(`${this.apiUrl}/my-reviews`);
  }

  getTeamReviews(): Observable<PerformanceResponseDTO[]> {
    return this.http.get<PerformanceResponseDTO[]>(`${this.apiUrl}/team`);
  }

  getAllReviews(): Observable<PerformanceResponseDTO[]> {
    return this.http.get<PerformanceResponseDTO[]>(this.apiUrl);
  }
}
