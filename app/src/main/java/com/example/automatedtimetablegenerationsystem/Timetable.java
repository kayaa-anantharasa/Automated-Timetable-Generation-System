package com.example.automatedtimetablegenerationsystem;

public class Timetable {
    private String subjectCode;
    private String subjectName;
    private String lecturer;
    private String days;
    private String time;
    private String semi;
    public Timetable() {
    }
    public Timetable(String subjectCode, String subjectName, String lecturer, String days, String time,String semi) {
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.lecturer = lecturer;
        this.days = days;
        this.time = time;
        this.semi = semi;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    public String getSemi() {
        return semi;
    }

    public void setSemi(String semi) {
        this.semi = semi;
    }
}
