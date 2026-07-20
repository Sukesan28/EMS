package com.example.ems.dto.request;

public class LeaveApprovalRequestDTO {

    private String managerComments;

    public LeaveApprovalRequestDTO() {
    }

    public LeaveApprovalRequestDTO(String managerComments) {
        this.managerComments = managerComments;
    }

    public String getManagerComments() {
        return managerComments;
    }

    public void setManagerComments(String managerComments) {
        this.managerComments = managerComments;
    }
}
