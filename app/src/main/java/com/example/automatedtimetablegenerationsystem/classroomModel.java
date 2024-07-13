package com.example.automatedtimetablegenerationsystem;

public class classroomModel {
    String classroomId,classroomName;
    public classroomModel() {
    }

    public String getClassroomId() {
        return classroomId;
    }

    public void setClassroomId(String classroomId) {
        this.classroomId = classroomId;
    }

    public String getClassroomName() {
        return classroomName;
    }

    public void setClassroomName(String classroomName) {
        this.classroomName = classroomName;
    }

    public classroomModel(String classroomId, String classroomName) {
        this.classroomId = classroomId;
        this.classroomName = classroomName;
    }
}
