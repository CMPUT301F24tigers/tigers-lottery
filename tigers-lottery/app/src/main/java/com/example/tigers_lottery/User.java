package com.example.tigers_lottery;

public class User {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String userName;
    private String number;

    public User (String email, String firstName, String lastName, String userName){
        setEmail(email);

        this.firstName=firstName;
        this.lastName=lastName;
        this.userName=userName;
    }

    public void setEmail(String email){

    }


    public void setFirstName(String firstName){
        this.firstName=firstName;
    }

    public void setLastName(String lastName){
        this.lastName=lastName;
    }

    public void setUserName(String userName){
        this.userName=userName;
    }

    public void setNumber(String number){
    }

    public String getFirstName(){
        return this.firstName;
    }

    public String getLastName(){
        return this.lastName;
    }

    public String getUserName(){
        return this.userName;
    }

    public String getEmail(){
        return this.email;
    }


    public String getNumber(){
        return this.number;
    }
}
