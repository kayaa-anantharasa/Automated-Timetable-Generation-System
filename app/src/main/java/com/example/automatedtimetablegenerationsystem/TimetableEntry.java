package com.example.automatedtimetablegenerationsystem;

public class TimetableEntry {
    private String selectedClass;
    private String selectedSubject;
    private String selectedProgram;
    private String selectedTime;
    private String selectedCurrentSemester;
    private String selectedFailedSemester;

    public String getSelectedClass() {
        return selectedClass;
    }

    public void setSelectedClass(String selectedClass) {
        this.selectedClass = selectedClass;
    }

    public String getSelectedSubject() {
        return selectedSubject;
    }

    public void setSelectedSubject(String selectedSubject) {
        this.selectedSubject = selectedSubject;
    }

    public String getSelectedProgram() {
        return selectedProgram;
    }

    public void setSelectedProgram(String selectedProgram) {
        this.selectedProgram = selectedProgram;
    }

    public String getSelectedTime() {
        return selectedTime;
    }

    public void setSelectedTime(String selectedTime) {
        this.selectedTime = selectedTime;
    }

    public String getSelectedCurrentSemester() {
        return selectedCurrentSemester;
    }

    public void setSelectedCurrentSemester(String selectedCurrentSemester) {
        this.selectedCurrentSemester = selectedCurrentSemester;
    }

    public String getSelectedFailedSemester() {
        return selectedFailedSemester;
    }

    public void setSelectedFailedSemester(String selectedFailedSemester) {
        this.selectedFailedSemester = selectedFailedSemester;
    }

    public TimetableEntry(String selectedClass, String selectedSubject, String selectedProgram, String selectedTime, String selectedCurrentSemester, String selectedFailedSemester) {
        this.selectedClass = selectedClass;
        this.selectedSubject = selectedSubject;
        this.selectedProgram = selectedProgram;
        this.selectedTime = selectedTime;
        this.selectedCurrentSemester = selectedCurrentSemester;
        this.selectedFailedSemester = selectedFailedSemester;
    }


}
