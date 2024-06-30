package com.example.automatedtimetablegenerationsystem;

public class signupClass {
    String name,email,password;
    String matrixNumber;
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

    public signupClass(String name, String email, String password, String matrixNumber) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.matrixNumber = matrixNumber;
    }

}
