package com.ironcore.gym.dto;

import jakarta.validation.constraints.NotBlank;

public class AttendanceDto {

    @NotBlank(message = "Member code is required")
    private String memberCode;

    private String notes;

    public AttendanceDto() {}

    public AttendanceDto(String memberCode, String notes) {
        this.memberCode = memberCode;
        this.notes = notes;
    }

    public String getMemberCode() {
        return memberCode;
    }

    public void setMemberCode(String memberCode) {
        this.memberCode = memberCode;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
