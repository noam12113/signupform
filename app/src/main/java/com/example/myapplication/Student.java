package com.example.myapplication;

public class Student {
    private String firstName;
    private String lastName;
    private String email;
    private String grade;
    private String major;
    private boolean receiveNotifications;

    public Student(String firstName, String lastName, String email, String grade, String major, boolean receiveNotifications) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.grade = grade;
        this.major = major;
        this.receiveNotifications = receiveNotifications;
    }

    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
    public String getEmail() { return email; }
    public String getGrade() { return grade; }
    public String getMajor() { return major; }
    public boolean isReceiveNotifications() { return receiveNotifications; }

    public String getFormattedDetails() {
        return "שם פרטי: " + firstName + "\n" +
                "שם משפחה: " + lastName + "\n" +
                "אימייל: " + email + "\n" +
                "כיתה: " + grade + "\n" +
                "מגמה: " + major + "\n" +
                "קבלת התראות: " + (receiveNotifications ? "כן" : "לא");
    }
}