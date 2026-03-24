package com.inn.cafe.constants;

public class CafeConstants {

    public static final String SOMETHING_WENT_WRONG = "Something went wrong";
    public static final String INVALID_DATA = "Invalid data";
    public static final String UNAUTHORIZED_ACCESS = "Unauthorized access";
    public static final String STORE_LOCATION = "D:\\PROJECTS\\cafeFiles";

    // OTP Constants
    public static final String OTP_REQ_PREFIX = "otp:req:";
    public static final String OTP_COOLDOWN_PREFIX = "otp:cooldown:";
    public static final String OTP_PREFIX = "otp:";
    public static final int OTP_TTL = 300; // 5 mins
    public static final int COOLDOWN_TTL = 60; // 1 min
    public static final int REQ_COUNT_TTL = 600; // 10 mins
    public static final int MAX_OTP_REQUESTS = 5;
    
    public static final java.util.Set<String> NORMAL_DOMAINS = new java.util.HashSet<>(java.util.Arrays.asList("gmail.com", "hotmail.com", "yahoo.com"));
    public static final java.util.Set<String> ADMIN_DOMAINS = new java.util.HashSet<>(java.util.Arrays.asList("cafexpert.com"));
}
