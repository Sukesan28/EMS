package com.example.ems.dto.response;

public class ManagerNameResponseDTO {

    private long id;
    private String ManagerName;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getManagerName() {
        return ManagerName;
    }

    public void setManagerName(String managerName) {
        ManagerName = managerName;
    }

    public ManagerNameResponseDTO(long id, String managerName) {
        this.id = id;
        ManagerName = managerName;
    }

    public ManagerNameResponseDTO() {
    }
}
