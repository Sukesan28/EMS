package com.example.ems.dto.request;

import java.time.LocalDate;

public class ClockInRequestDTO {

    private LocalDate attendanceDate;

    public ClockInRequestDTO() {
    }

    public ClockInRequestDTO(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }

    public LocalDate getAttendanceDate() {
        return attendanceDate;
    }

    public void setAttendanceDate(LocalDate attendanceDate) {
        this.attendanceDate = attendanceDate;
    }
}
