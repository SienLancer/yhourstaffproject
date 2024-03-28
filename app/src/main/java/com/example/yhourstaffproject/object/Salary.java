package com.example.yhourstaffproject.object;

public class Salary {
    private String id;
    private Integer currentSalary;
    private String status;
    private String startDate;
    private String payDay;

    public Salary(String id, Integer currentSalary, String status, String startDate, String payDay) {
        this.id = id;
        this.currentSalary = currentSalary;
        this.status = status;
        this.startDate = startDate;
        this.payDay = payDay;
    }

    public Salary(Integer currentSalary, String status, String startDate, String payDay) {
        this.currentSalary = currentSalary;
        this.status = status;
        this.startDate = startDate;
        this.payDay = payDay;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getCurrentSalary() {
        return currentSalary;
    }

    public void setCurrentSalary(Integer currentSalary) {
        this.currentSalary = currentSalary;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getPayDay() {
        return payDay;
    }

    public void setPayDay(String payDay) {
        this.payDay = payDay;
    }
}
