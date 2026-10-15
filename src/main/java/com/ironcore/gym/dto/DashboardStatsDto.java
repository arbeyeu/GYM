package com.ironcore.gym.dto;

import com.ironcore.gym.entity.Attendance;
import com.ironcore.gym.entity.Member;
import com.ironcore.gym.entity.Payment;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class DashboardStatsDto {
    private long totalActiveMembers;
    private long newRegistrationsThisMonth;
    private long todayAttendanceCount;
    private long expiringMembershipsCount;
    private BigDecimal monthlyRevenue = BigDecimal.ZERO;
    private long pendingPaymentsCount;
    private BigDecimal pendingPaymentsAmount = BigDecimal.ZERO;

    private List<String> memberGrowthLabels = new ArrayList<>();
    private List<Long> memberGrowthData = new ArrayList<>();

    private List<String> revenueLabels = new ArrayList<>();
    private List<BigDecimal> revenueData = new ArrayList<>();

    private List<Member> recentMembers = new ArrayList<>();
    private List<Payment> recentPayments = new ArrayList<>();
    private List<Attendance> recentAttendance = new ArrayList<>();

    public DashboardStatsDto() {}

    public long getTotalActiveMembers() {
        return totalActiveMembers;
    }

    public void setTotalActiveMembers(long totalActiveMembers) {
        this.totalActiveMembers = totalActiveMembers;
    }

    public long getNewRegistrationsThisMonth() {
        return newRegistrationsThisMonth;
    }

    public void setNewRegistrationsThisMonth(long newRegistrationsThisMonth) {
        this.newRegistrationsThisMonth = newRegistrationsThisMonth;
    }

    public long getTodayAttendanceCount() {
        return todayAttendanceCount;
    }

    public void setTodayAttendanceCount(long todayAttendanceCount) {
        this.todayAttendanceCount = todayAttendanceCount;
    }

    public long getExpiringMembershipsCount() {
        return expiringMembershipsCount;
    }

    public void setExpiringMembershipsCount(long expiringMembershipsCount) {
        this.expiringMembershipsCount = expiringMembershipsCount;
    }

    public BigDecimal getMonthlyRevenue() {
        return monthlyRevenue;
    }

    public void setMonthlyRevenue(BigDecimal monthlyRevenue) {
        this.monthlyRevenue = monthlyRevenue;
    }

    public long getPendingPaymentsCount() {
        return pendingPaymentsCount;
    }

    public void setPendingPaymentsCount(long pendingPaymentsCount) {
        this.pendingPaymentsCount = pendingPaymentsCount;
    }

    public BigDecimal getPendingPaymentsAmount() {
        return pendingPaymentsAmount;
    }

    public void setPendingPaymentsAmount(BigDecimal pendingPaymentsAmount) {
        this.pendingPaymentsAmount = pendingPaymentsAmount;
    }

    public List<String> getMemberGrowthLabels() {
        return memberGrowthLabels;
    }

    public void setMemberGrowthLabels(List<String> memberGrowthLabels) {
        this.memberGrowthLabels = memberGrowthLabels;
    }

    public List<Long> getMemberGrowthData() {
        return memberGrowthData;
    }

    public void setMemberGrowthData(List<Long> memberGrowthData) {
        this.memberGrowthData = memberGrowthData;
    }

    public List<String> getRevenueLabels() {
        return revenueLabels;
    }

    public void setRevenueLabels(List<String> revenueLabels) {
        this.revenueLabels = revenueLabels;
    }

    public List<BigDecimal> getRevenueData() {
        return revenueData;
    }

    public void setRevenueData(List<BigDecimal> revenueData) {
        this.revenueData = revenueData;
    }

    public List<Member> getRecentMembers() {
        return recentMembers;
    }

    public void setRecentMembers(List<Member> recentMembers) {
        this.recentMembers = recentMembers;
    }

    public List<Payment> getRecentPayments() {
        return recentPayments;
    }

    public void setRecentPayments(List<Payment> recentPayments) {
        this.recentPayments = recentPayments;
    }

    public List<Attendance> getRecentAttendance() {
        return recentAttendance;
    }

    public void setRecentAttendance(List<Attendance> recentAttendance) {
        this.recentAttendance = recentAttendance;
    }
}
