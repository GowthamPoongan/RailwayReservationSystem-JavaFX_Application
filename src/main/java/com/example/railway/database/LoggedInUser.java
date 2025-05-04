package com.example.railway.database;

public class LoggedInUser {
    private static String email;
    private static String fullName;

    public static String getEmail() {
        return email;
    }

    public static void setEmail(String email) {
        LoggedInUser.email = email;
    }

    public static String getFullName() {
        return fullName;
    }

    public static void setFullName(String fullName) {
        LoggedInUser.fullName = fullName;
    }

    public static void clear() {
        email = null;
    }

}
