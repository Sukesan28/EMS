export type AttendanceStatus = 'PRESENT' | 'ABSENT' | 'HALF_DAY' | 'ON_LEAVE' | 'LATE';

export interface ClockInRequestDTO {
  attendanceDate: string; 
}

export interface AttendanceResponseDTO {
  attendanceDate: string; 
  clockInTime: string; 
  clockOutTime: string; 
  workingHours: number;
  overtimeHours: number;
  attendanceStatus: AttendanceStatus;
  employeeCode?: string;
  employeeName?: string;
}
