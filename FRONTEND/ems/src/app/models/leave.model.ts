export type LeaveType = 'CASUAL' | 'SICK' | 'ANNUAL' | 'MATERNITY' | 'PATERNITY';
export type LeaveStatus = 'PENDING' | 'APPROVED' | 'REJECTED' | 'CANCELLED';

export interface LeaveRequestDTO {
  leaveType: LeaveType;
  startDate: string; 
  endDate: string; 
  reason: string;
}

export interface LeaveApprovalRequestDTO {
  managerComments: string;
}

export interface LeaveResponseDTO {
  id: number;
  employeeCode: string;
  employeeName: string;
  leaveType: LeaveType;
  startDate: string;
  endDate: string;
  totalDays: number;
  reason: string;
  status: LeaveStatus;
  managerComments: string;
  approvedDate?: string;
  employeeRole: string;
}
