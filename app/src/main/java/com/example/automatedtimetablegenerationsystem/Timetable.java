package com.example.automatedtimetablegenerationsystem;

public class Timetable {
    private String subjectCode;
    private String subjectName;
    private String lecturer;
    private String days;
    private String time;
    private String semi;
    private String program;
    private String classname;
    private String classroom;
    private String Prerequisite;
    private String key;
    public Timetable(String subjectName, String subjectCode, String lecturer, String days, String time, String semi, String program, String classname,String classroom,String Prerequisite) {
        this.subjectName = subjectName;
        this.subjectCode = subjectCode;
        this.lecturer = lecturer;
        this.days = days;
        this.time = time;
        this.semi = semi;
        this.program = program;
        this.classname = classname;
        this.classroom = classroom;
        this.Prerequisite = Prerequisite;

    }

    public String getClassroom() {
        return classroom;
    }

    public void setClassroom(String classroom) {
        this.classroom = classroom;
    }

    public String getPrerequisite() {
        return Prerequisite;
    }

    public void setPrerequisite(String prerequisite) {
        Prerequisite = prerequisite;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
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

    public String getProgram() {
        return program;
    }

    public void setProgram(String program) {
        this.program = program;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }

    public Timetable() {
    }

}
