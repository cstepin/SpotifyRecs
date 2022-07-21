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

    // Python transcribed from: https://stackoverflow.com/questions/1119722/base-62-conversion
    static String BASE62 = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    public static int decode(String encoded) {
        /* Decode a Base X encoded string into the number

        Arguments:
    -   `encoded`: The encoded string */

        int base = BASE62.length();
        int strlen = encoded.length();

        int num = 0;

        int idx = 0;

        for(int i = 0; i < strlen; i++) {
            String c = encoded.substring(i, i + 1);
            int power = (strlen - (idx + 1));

            num += BASE62.indexOf(c) * (base^power);
            idx += 1;
        }

        return num;
    }
}
