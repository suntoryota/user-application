package com.example.demo.constant;

public class AppConstant {
    public static final String SUCCESS = "Success";
    public static final String ERROR = "Error";
    

    public static final String USER_NOT_FOUND = "User not found";
    public static final String EMAIL_ALREADY_EXISTS = "Email already exists";
    public static final String SUCCESS_MESSAGE = "Message sent successfully";

    public static class ValidationMessage {
        public static final String NAME_REQUIRED = "Name is required";
        public static final String NAME_LENGTH = "Name must be between 2 and 50 characters";
        public static final String NAME_FORMAT = "Name can only contain letters and spaces";
        public static final String EMAIL_REQUIRED = "Email is required";
        public static final String INVALID_EMAIL = "Invalid email format";
        public static final String EMAIL_LENGTH = "Email must not exceed 100 characters";
        public static final String PHONE_INVALID = "Phone number must be 10-15 digits";
        public static final String STATUS_REQUIRED = "Status is required";
    }
}
