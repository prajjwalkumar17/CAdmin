package com.thundersharp.cadmin.core.globalmodels;

public class UserData {
    public String bio, dob, email, image_uri, name, phone_no, uid, uri_ref;

    public UserData() {
    }

    public UserData(String bio, String dob, String email, String image_uri, String name, String phone_no, String uid, String uri_ref) {
        this.bio = bio;
        this.dob = dob;
        this.email = email;
        this.image_uri = image_uri;
        this.name = name;
        this.phone_no = phone_no;
        this.uid = uid;
        this.uri_ref = uri_ref;
    }

    public String getBio() {
        return bio;
    }

    public String getDob() {
        return dob;
    }

    public String getEmail() {
        return email;
    }

    public String getImage_uri() {
        return image_uri;
    }

    public String getName() {
        return name;
    }

    public String getPhone_no() {
        return phone_no;
    }

    public String getUid() {
        return uid;
    }

    public String getUri_ref() {
        return uri_ref;
    }
}
