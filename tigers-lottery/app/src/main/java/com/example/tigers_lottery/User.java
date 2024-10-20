package com.example.tigers_lottery;

public class User {
    private String email;
    private String password;
    private String number;
    public User (String email, String password){
        this.email=email;
        this.password=password;
    }

    public void setEmail(String email){

    }

    public void setPassword(String password){

    }

    public void setNumber(String number){

    }

    public String getEmail(){
        return this.email;
    }

    public String getPassword(){
        return this.password;
    }

    public String getNumber(){
        return this.number;
    }
}
