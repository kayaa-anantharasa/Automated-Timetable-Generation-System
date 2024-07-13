package com.example.automatedtimetablegenerationsystem;

public class subjectModel {
    String Id,subjectName,subjectcode;
    public subjectModel() {
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    public String getSubjectName() {
        return subjectName;
    }

    public void setSubjectName(String subjectName) {
        this.subjectName = subjectName;
    }

    public String getSubjectcode() {
        return subjectcode;
    }

    public void setSubjectcode(String subjectcode) {
        this.subjectcode = subjectcode;
    }

    public subjectModel(String id, String subjectName, String subjectcode) {
        Id = id;
        this.subjectName = subjectName;
        this.subjectcode = subjectcode;
    }
}
