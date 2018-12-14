package com.example.mikita.ppo_lab.storage;


import com.google.firebase.database.IgnoreExtraProperties;


@IgnoreExtraProperties
public class UserDM {
    public int getID() {
        return ID;
    }

    public void setID(int ID) {
        this.ID = ID;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getSurname() {
        return Surname;
    }

    public void setSurname(String surname) {
        Surname = surname;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    private int ID;
    private String Name;
    private String Surname;
    private String Email;
    private String Phone;

    public UserDM() {}

    public UserDM(int id, String name, String surname, String email) {
        ID = id;
        Name = name;
        Surname = surname;
        Email = email;
    }

    public String getPhone() {
        return Phone;
    }

    public void setPhone(String phone) {
        Phone = phone;
    }
}


