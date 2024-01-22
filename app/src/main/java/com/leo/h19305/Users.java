package com.leo.h19305;

public class Users {

    static String firstName, lastName, email, password;

    public Users(String firstName, String lastName, String email, String password) {
        Users.firstName = firstName;
        Users.lastName = lastName;
        Users.email = email;
        Users.password = password;
    }

    public Users() {
    }


    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        Users.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        Users.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        Users.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        Users.password = password;
    }

}
