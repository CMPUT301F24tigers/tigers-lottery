package com.example.tigers_lottery;
import org.apache.commons.validator.routines.EmailValidator;

public class User {
    private String email;
    private String password;
    private String firstName;
    private String lastName;
    private String userName;
    private String number;

    public User (String email, String password, String firstName, String lastName, String userName){
        setEmail(email);
        setPassword(password);
        this.firstName=firstName;
        this.lastName=lastName;
        this.userName=userName;
    }

    public void setEmail(String email){
        EmailValidator validator = EmailValidator.getInstance();
        if(validator.isValid(email)){
            this.email=email;
        }
    }

    public void setPassword(String password){
        PasswordStrengthCheck passwordStrength = new PasswordStrengthCheck();
        if(passwordStrength.passwordIsSafe(password)){
            this.password=password;
        };
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

    public String getPassword(){
        return this.password;
    }

    public String getNumber(){
        return this.number;
    }
}
