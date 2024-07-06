package com.example.automatedtimetablegenerationsystem;

public class TimetableEntry {
    private String classname;
    private String days;
    private String lecturer;
    private String program;
    private String semi;
    private String subjectCode;
    private String subjectName;
    private String time;

    public  TimetableEntry() {

    }


    public TimetableEntry(String classname, String days, String lecturer, String semi, String program, String subjectCode, String subjectName, String time) {
        this.classname = classname;
        this.days = days;
        this.lecturer = lecturer;
        this.semi = semi;
        this.program = program;
        this.subjectCode = subjectCode;
        this.subjectName = subjectName;
        this.time = time;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public String getDays() {
        return days;
    }

    public void setDays(String days) {
        this.days = days;
    }

    public String getLecturer() {
        return lecturer;
    }

    public void setLecturer(String lecturer) {
        this.lecturer = lecturer;
    }

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getSemi() {
        return semi;
    }

    public void setSemi(String semi) {
        this.semi = semi;
    }

    public String getSubjectCode() {
        return subjectCode;
    }

    public void setSubjectCode(String subjectCode) {
        this.subjectCode = subjectCode;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }
}
