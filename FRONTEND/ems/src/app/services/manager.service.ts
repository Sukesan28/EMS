import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { map } from 'rxjs/operators';
import { EmployeeResponseDTO } from '../models/employee-response.model';

@Injectable({
  providedIn: 'root'
})
export class ManagerService {
  private readonly apiUrl = 'http://localhost:8081/api/employees';

  constructor(private http: HttpClient) {}

  getMyEmployees(): Observable<EmployeeResponseDTO[]> {
    return this.http.get<any[]>(this.apiUrl).pipe(
      map(data => data.map(emp => ({
        employeeId: emp.id,
        employeeCode: emp.employeeCode,
        fullName: emp.firstName + ' ' + emp.lastName,
        email: emp.email,
        phone: emp.phone,
        designation: emp.designation,
        departmentName: emp.departmentName,
        employmentStatus: emp.employmentStatus
      })))
    );
  }
}
