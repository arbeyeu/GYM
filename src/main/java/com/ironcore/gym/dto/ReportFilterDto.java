package com.ironcore.gym.dto;

import java.time.LocalDate;

public class ReportFilterDto {
    private LocalDate startDate = LocalDate.now().minusDays(30);
    private LocalDate endDate = LocalDate.now();
    private String reportType = "DAILY_ATTENDANCE"; // DAILY_ATTENDANCE, MONTHLY_REVENUE, MEMBERSHIP_EXPIRY, MEMBER_GROWTH, PAYMENTS
    private String status;
    private String format = "html"; // html, csv

    public ReportFilterDto() {}

    public LocalDate getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public String getReportType() {
        return reportType;
    }

    public void setReportType(String reportType) {
        this.reportType = reportType;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
