export type DepartmentStatus = 'ACTIVE' | 'INACTIVE';

export interface DepartmentRequestDTO {
  departmentName: string;
  description: string;
  departmentHead: string;
  status: DepartmentStatus;
}

export interface DepartmentResponseDTO {
  id: number;
  departmentCode: string;
  departmentName: string;
  description: string;
  departmentHead: string;
  status: DepartmentStatus;
  totalEmployees: number;
}

export interface DepartmentManagerHistoryResponseDTO {
  id: number;
  departmentId: number;
  previousManagerName: string;
  newManagerName: string;
  changedAt: string;
}
