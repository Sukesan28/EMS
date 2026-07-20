export interface TaskRequestDTO {
  title: string;
  description: string;
  assigneeId: number;
  status?: string;
  dueDate?: string;
}

export interface TaskResponseDTO {
  id: number;
  title: string;
  description: string;
  assigneeId: number;
  assigneeName: string;
  assigneeCode: string;
  departmentName: string;
  assignerId: number;
  assignerName: string;
  createdAt: string;
  updatedAt: string;
  status: string;
  dueDate?: string;
}
