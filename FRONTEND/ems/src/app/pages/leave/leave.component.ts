import { Component, OnInit, signal } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute } from '@angular/router';
import { LeaveService } from '../../services/leave.service';
import { AuthService } from '../../services/auth.service';
import { TableComponent } from '../../components/table/table.component';
import { LeaveResponseDTO, LeaveRequestDTO, LeaveType } from '../../models/leave.model';

@Component({
  selector: 'app-leave',
  standalone: true,
  imports: [CommonModule, FormsModule, TableComponent],
  templateUrl: './leave.component.html',
  styleUrl: './leave.component.css'
})
export class LeaveComponent implements OnInit {
  userRole = '';
  leaves = signal<LeaveResponseDTO[]>([]);
  
  // Table state
  isLoading = signal(true);
  isEmpty = signal(false);
  
  // Modal states
  isApplyModalOpen = false;
  isApprovalModalOpen = false;
  isApprovalActionApprove = true; // true = approve, false = reject
  
  selectedLeaveId: number | null = null;
  managerComments = '';

  // Apply form model
  applyModel: LeaveRequestDTO = {
    leaveType: 'CASUAL' as LeaveType,
    startDate: '',
    endDate: '',
    reason: ''
  };

  // Tabs for Manager/HR
  activeTab: 'my-leaves' | 'pending-approvals' | 'all-leaves' = 'my-leaves';

  constructor(
    private authService: AuthService,
    private leaveService: LeaveService,
    private route: ActivatedRoute
  ) {
    const user = this.authService.currentUser();
    this.userRole = user ? user.role : 'EMPLOYEE';
    
    // Set default active tab based on role
    if (this.userRole === 'MANAGER') {
      this.activeTab = 'pending-approvals';
    } else if (this.userRole === 'HR') {
      this.activeTab = 'all-leaves';
    } else {
      this.activeTab = 'my-leaves';
    }
  }

  ngOnInit() {
    this.loadLeaves();
    
    // Check query parameters to open apply modal
    this.route.queryParams.subscribe(params => {
      if (params['action'] === 'apply') {
        this.openApplyModal();
      }
    });
  }

  loadLeaves() {
    this.isLoading.set(true);
    
    if (this.activeTab === 'my-leaves') {
      this.leaveService.getMyLeaves().subscribe({
        next: (data) => this.setLeavesData(data),
        error: () => this.setLeavesData([])
      });
    } else if (this.activeTab === 'pending-approvals') {
      this.leaveService.getPendingLeaves().subscribe({
        next: (data) => this.setLeavesData(data),
        error: () => this.setLeavesData([])
      });
    } else if (this.activeTab === 'all-leaves') {
      this.leaveService.getAllLeaves().subscribe({
        next: (data) => this.setLeavesData(data),
        error: () => this.setLeavesData([])
      });
    }
  }

  private setLeavesData(data: LeaveResponseDTO[]) {
    this.leaves.set(data);
    this.isEmpty.set(data.length === 0);
    this.isLoading.set(false);
  }

  switchTab(tab: 'my-leaves' | 'pending-approvals' | 'all-leaves') {
    this.activeTab = tab;
    this.loadLeaves();
  }

  openApplyModal() {
    this.applyModel = {
      leaveType: 'CASUAL',
      startDate: '',
      endDate: '',
      reason: ''
    };
    this.isApplyModalOpen = true;
  }

  closeApplyModal() {
    this.isApplyModalOpen = false;
  }

  onApplyLeave() {
    this.isLoading.set(true);
    this.leaveService.applyLeave(this.applyModel).subscribe({
      next: () => {
        this.closeApplyModal();
        this.loadLeaves();
      },
      error: (err) => {
        this.isLoading.set(false);
        alert(err.error?.message || 'Failed to submit leave request');
      }
    });
  }

  cancelLeave(id: number) {
    if (confirm('Are you sure you want to cancel this leave request?')) {
      this.isLoading.set(true);
      this.leaveService.cancelLeave(id).subscribe({
        next: () => this.loadLeaves(),
        error: () => this.isLoading.set(false)
      });
    }
  }

  // Manager Approval Actions
  openApprovalModal(id: number, approve: boolean) {
    this.selectedLeaveId = id;
    this.isApprovalActionApprove = approve;
    this.managerComments = '';
    this.isApprovalModalOpen = true;
  }

  closeApprovalModal() {
    this.isApprovalModalOpen = false;
    this.selectedLeaveId = null;
  }

  submitApproval() {
    if (this.selectedLeaveId === null) return;
    this.isLoading.set(true);
    const dto = { managerComments: this.managerComments };

    if (this.isApprovalActionApprove) {
      this.leaveService.approveLeave(this.selectedLeaveId, dto).subscribe({
        next: () => {
          this.closeApprovalModal();
          this.loadLeaves();
        },
        error: () => this.isLoading.set(false)
      });
    } else {
      this.leaveService.rejectLeave(this.selectedLeaveId, dto).subscribe({
        next: () => {
          this.closeApprovalModal();
          this.loadLeaves();
        },
        error: () => this.isLoading.set(false)
      });
    }
  }

  getHeaders(): string[] {
    if (this.activeTab === 'my-leaves') {
      return ['Leave Type', 'Start Date', 'End Date', 'Total Days', 'Status', 'Reason', 'Actions'];
    } else {
      return ['Employee', 'Leave Type', 'Start Date', 'End Date', 'Total Days', 'Status', 'Reason', 'Actions'];
    }
  }
}
