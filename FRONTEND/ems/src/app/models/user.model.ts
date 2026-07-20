export type Role = 'HR' | 'MANAGER' | 'EMPLOYEE' | 'IT_SUPPORT';
export type Gender = 'MALE' | 'FEMALE' | 'OTHER';
export type EmploymentStatus = 'ACTIVE' | 'INACTIVE' | 'ON_PROBATION' | 'RESIGNED' | 'TERMINATED' | 'SUSPENDED';

export interface LoginRequestDTO {
  employeeCode: string;
  password?: string;
}

export interface LoginResponseDTO {
  employeeCode: string;
  fullName: string;
  role: Role;
  firstLogin: boolean;
  message: string;
  token?: string;
}

export interface ChangePasswordRequestDTO {
  oldPassword?: string;
  newPassword?: string;
  confirmPassword?: string;
}

export interface EmployeeRequestDTO {
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  gender: Gender;
  dateOfBirth: string; 
  joiningDate: string; 
  designation: string;
  salary: number;
  address: string;
  employmentStatus: EmploymentStatus;
  role: Role;
  departmentId: number;
  managerId?: number | null;
}

export interface EmployeeSelfUpdateDTO {
  email: string;
  phone: string;
  address: string;
}

export interface EmployeeResponseDTO {
  id: number;
  employeeCode: string;
  firstName: string;
  lastName: string;
  email: string;
  phone: string;
  gender: Gender;
  dateOfBirth: string;
  joiningDate: string;
  designation: string;
  salary: number;
  address: string;
  employmentStatus: EmploymentStatus;
  role: Role;
  departmentName: string;
  managerName: string;
  accountEnabled: boolean;
  accountLocked?: boolean;
  firstLogin?: boolean;
  createdAt?: string;
  updatedAt?: string;
}
