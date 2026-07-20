export interface PerformanceRequestDTO {
  reviewPeriod: string;
  performanceGoal: string;
  achievement: string;
  strength: string;
  improvementArea: string;
  rating: number;
  feedback: string;
  reviewDate: string; 
}

export interface PerformanceResponseDTO {
  id: number;
  employeeId?: number;
  employeeCode: string;
  employeeName: string;
  reviewPeriod: string;
  performanceGoal: string;
  achievement: string;
  strength: string;
  improvementArea: string;
  rating: number;
  feedback: string;
  reviewDate: string;
  departmentName?: string;
}
