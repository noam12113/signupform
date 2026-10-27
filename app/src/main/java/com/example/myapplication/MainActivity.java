package com.example.myapplication;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText etFirstName, etLastName, etEmail;
    private RadioGroup rgGrade;
    private Spinner spinnerMajor;
    private CheckBox cbNotifications, cbTerms;
    private Button btnRegister;
    private TextView tvResult, tvGradeError, tvMajorError, tvTermsError;

    private static final String PREFS_NAME = "StudentPrefs";
    private static final String KEY_FIRST_NAME = "firstName";
    private static final String KEY_LAST_NAME = "lastName";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_GRADE = "grade";
    private static final String KEY_MAJOR = "major";
    private static final String KEY_NOTIFICATIONS = "notifications";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // קישור רכיבי הממשק
        etFirstName = findViewById(R.id.etFirstName);
        etLastName = findViewById(R.id.etLastName);
        etEmail = findViewById(R.id.etEmail);
        rgGrade = findViewById(R.id.rgGrade);
        spinnerMajor = findViewById(R.id.spinnerMajor);
        cbNotifications = findViewById(R.id.cbNotifications);
        cbTerms = findViewById(R.id.cbTerms);
        btnRegister = findViewById(R.id.btnRegister);
        tvResult = findViewById(R.id.tvResult);
        tvGradeError = findViewById(R.id.tvGradeError);
        tvMajorError = findViewById(R.id.tvMajorError);
        tvTermsError = findViewById(R.id.tvTermsError);

        // אתחול רשימת ה-Spinner מתוך קובץ המחרוזות
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this,
                R.array.majors_array,
                android.R.layout.simple_spinner_item
        );
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerMajor.setAdapter(adapter);

        // טעינת נתונים שמורים מהפעלה קודמת (אם קיימים)
        loadSavedStudentData();

        // האזנה ללחיצה על כפתור ההתחברות/רישום
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validateAndRegister();
            }
        });
    }

    private void validateAndRegister() {
        boolean isValid = true;

        // איפוס הודעות שגיאה קודמות
        etFirstName.setError(null);
        etLastName.setError(null);
        etEmail.setError(null);
        tvGradeError.setVisibility(View.GONE);
        tvMajorError.setVisibility(View.GONE);
        tvTermsError.setVisibility(View.GONE);

        // 1. בדיקת שם פרטי
        String firstName = etFirstName.getText().toString().trim();
        if (firstName.isEmpty()) {
            etFirstName.setError(getString(R.string.error_required));
            isValid = false;
        }

        // 2. בדיקת שם משפחה
        String lastName = etLastName.getText().toString().trim();
        if (lastName.isEmpty()) {
            etLastName.setError(getString(R.string.error_required));
            isValid = false;
        }

        // 3. בדיקת אימייל ותקינות מבנה
        String email = etEmail.getText().toString().trim();
        if (email.isEmpty()) {
            etEmail.setError(getString(R.string.error_required));
            isValid = false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            etEmail.setError(getString(R.string.error_invalid_email));
            isValid = false;
        }

        // 4. בדיקת בחירת כיתה ב-RadioGroup
        String grade = "";
        int selectedGradeId = rgGrade.getCheckedRadioButtonId();
        if (selectedGradeId == -1) {
            tvGradeError.setText(getString(R.string.error_grade));
            tvGradeError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            RadioButton selectedRb = findViewById(selectedGradeId);
            grade = selectedRb.getText().toString();
        }

        // 5. בדיקת בחירת מגמה ב-Spinner
        String major = "";
        if (spinnerMajor.getSelectedItemPosition() == 0) {
            tvMajorError.setText(getString(R.string.error_major));
            tvMajorError.setVisibility(View.VISIBLE);
            isValid = false;
        } else {
            major = spinnerMajor.getSelectedItem().toString();
        }

        // 6. בדיקת אישור תנאי שימוש
        if (!cbTerms.isChecked()) {
            tvTermsError.setText(getString(R.string.error_terms));
            tvTermsError.setVisibility(View.VISIBLE);
            isValid = false;
        }

        // אם אחת מהבדיקות נכשלה - עוצרים כאן
        if (!isValid) {
            return;
        }

        // קבלת סטטוס תיבת התראות (אופציונלי)
        boolean receiveNotifications = cbNotifications.isChecked();

        // יצירת אובייקט מסוג Student
        Student student = new Student(firstName, lastName, email, grade, major, receiveNotifications);

        // שמירת הנתונים ב-SharedPreferences
        saveStudentData(student);

        // הצגת הנתונים ב-TextView והפיכתו לנראה
        String resultText = getString(R.string.result_title) + "\n\n" + student.getFormattedDetails();
        tvResult.setText(resultText);
        tvResult.setVisibility(View.VISIBLE);
    }

    private void saveStudentData(Student student) {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(KEY_FIRST_NAME, student.getFirstName());
        editor.putString(KEY_LAST_NAME, student.getLastName());
        editor.putString(KEY_EMAIL, student.getEmail());
        editor.putString(KEY_GRADE, student.getGrade());
        editor.putString(KEY_MAJOR, student.getMajor());
        editor.putBoolean(KEY_NOTIFICATIONS, student.isReceiveNotifications());
        editor.apply();
    }

    private void loadSavedStudentData() {
        SharedPreferences prefs = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        if (prefs.contains(KEY_EMAIL)) {
            String firstName = prefs.getString(KEY_FIRST_NAME, "");
            String lastName = prefs.getString(KEY_LAST_NAME, "");
            String email = prefs.getString(KEY_EMAIL, "");
            String grade = prefs.getString(KEY_GRADE, "");
            String major = prefs.getString(KEY_MAJOR, "");
            boolean notifications = prefs.getBoolean(KEY_NOTIFICATIONS, false);

            Student student = new Student(firstName, lastName, email, grade, major, notifications);

            String resultText = getString(R.string.result_title) + "\n\n" + student.getFormattedDetails();
            tvResult.setText(resultText);
            tvResult.setVisibility(View.VISIBLE);
        }
    }
}