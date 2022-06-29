package com.example.spotifyrecs.resources;

public class Resources {
    public static String authToken;

    public static int getReqCode(){
        return 1337;
    }

    public static String getClientId(){
        return "f67855f9416e4ca999b13ec503540bc8";
    }

    public static String getRedirectUrl(){
        return "http://localhost:8080";
    }

    public static void setAuthToken(String token) {authToken = token; }

    public static String getAuthToken() {return authToken;}
}
