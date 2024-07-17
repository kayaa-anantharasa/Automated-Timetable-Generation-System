package com.example.automatedtimetablegenerationsystem;

public class signupClass {
    String name,email,password;
    String matrixNumber,time;
    public signupClass() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getMatrixNumber() {
        return matrixNumber;
    }

    public void setMatrixNumber(String matrixNumber) {
        this.matrixNumber = matrixNumber;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public signupClass(String email, String name, String password, String matrixNumber, String time) {
        this.email = email;
        this.name = name;
        this.password = password;
        this.matrixNumber = matrixNumber;
        this.time = time;
    }
}
