package com.example.automatedtimetablegenerationsystem;

public class classModel {
    String classId,className;
    public classModel() {
    }

    public String getClassId() {
        return classId;
    }

    public void setClassId(String classId) {
        this.classId = classId;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public classModel(String classId, String className) {
        this.classId = classId;
        this.className = className;
    }
}
