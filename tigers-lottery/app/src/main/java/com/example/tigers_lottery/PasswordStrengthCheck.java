package com.example.tigers_lottery;

public class PasswordStrengthCheck {
    private String password;
    public boolean passwordIsSafe(String password){
        if(password.length() <8){
            return false;
        }
        boolean upperCase=false;
        boolean lowerCase=false;
        boolean digit=false;
        boolean specialChar=false;

        for(char c : password.toCharArray()){
            if(Character.isUpperCase(c)) {
                upperCase = true;
            }else if (Character.isLowerCase(c)){
                lowerCase=true;
            } else if (Character.isDigit(c)){
                digit=true;
            } else if (isSpecialChar(c)){
                specialChar=true;
            }
        }
        if(upperCase && lowerCase && digit && specialChar){
            return true;
        } return false;
    }
    public boolean isSpecialChar(char c){
        String specialCharacters = "!@#$%^&*()-_=+[]{}|/.?<>,";
        return specialCharacters.indexOf(c) >=0;
    }
}
