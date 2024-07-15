package com.example.automatedtimetablegenerationsystem;

public class subjectModel {
    String Id,subjectName,subjectcode,semester,prerequisiteSubject;
    public subjectModel() {
    }

    public subjectModel(String id, String subjectName, String subjectcode, String semester, String prerequisiteSubject) {
        Id = id;
        this.subjectName = subjectName;
        this.subjectcode = subjectcode;
        this.semester = semester;
        this.prerequisiteSubject = prerequisiteSubject;
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

    public String getSemester() {
        return semester;
    }

    public void setSemester(String semester) {
        this.semester = semester;
    }

    public String getPrerequisiteSubject() {
        return prerequisiteSubject;
    }

    public void setPrerequisiteSubject(String prerequisiteSubject) {
        this.prerequisiteSubject = prerequisiteSubject;
    }
}
