package com.example.wefarmer;

public class UserHelperClass {
    String name, email, password, typeofperson,phone;

    public UserHelperClass() {

    }

    public UserHelperClass(String name, String email, String password,String typeofperson,String phone ) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.typeofperson = typeofperson;
        this.phone = phone;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getTypeofperson() {
        return typeofperson;
    }

    public void setTypeofperson(String typeofperson) {
        this.typeofperson = typeofperson;
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


}
