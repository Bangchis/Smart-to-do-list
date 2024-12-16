package com.smarttodo.utils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegistrationUtils {

    // Validate email format using regex
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[a-zA-Z0-9_+&*-]+(?:\\.[a-zA-Z0-9_+&*-]+)*@(?:[a-zA-Z0-9-]+\\.)+[a-zA-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    // Validate password length
    public static boolean isValidPassword(String password) {
        return password.length() >= 8;
    }

    // Validate gender value (must be 1 or 2)
    public static boolean isValidGender(int gender) {
        return gender == 1 || gender == 2;
    }

    // Validate birthday format (YYYY-MM-DD)
    public static boolean isValidBirthday(String birthdayStr) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        dateFormat.setLenient(false);
        try {
            Date birthday = dateFormat.parse(birthdayStr);
            return true;
        } catch (ParseException parseException) {
            return false;
        }
    }
}
