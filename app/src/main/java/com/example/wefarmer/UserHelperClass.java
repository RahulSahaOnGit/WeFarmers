package com.example.wefarmer;

public class UserHelperClass {
    String name, user_id, typeofperson, state, district, locality, latitude, password, longitude, phone;

    public UserHelperClass() {

    }

    public UserHelperClass(String name, String user_id, String typeofperson,
                           String state, String district, String locality, String latitude,
                           String password, String longitude, String phone ) {
        this.name=name;
        this.user_id=user_id;
        this.typeofperson=typeofperson;
        this.state=state;
        this.district=district;
        this.locality=locality;
        this.latitude=latitude;
        this.password=password;
        this.longitude=longitude;
        this.phone=phone;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getTypeofperson() {
        return typeofperson;
    }

    public void setTypeofperson(String typeofperson) {
        this.typeofperson = typeofperson;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getDistrict() {
        return district;
    }

    public void setDistrict(String district) {
        this.district = district;
    }

    public String getLocality() {
        return locality;
    }

    public void setLocality(String locality) {
        this.locality = locality;
    }

    public String getLatitude() {
        return latitude;
    }

    public void setLatitude(String latitude) {
        this.latitude = latitude;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getLongitude() {
        return longitude;
    }

    public void setLongitude(String longitude) {
        this.longitude = longitude;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
