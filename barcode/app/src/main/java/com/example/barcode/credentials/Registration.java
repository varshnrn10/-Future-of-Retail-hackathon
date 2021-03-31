package com.example.barcode.credentials;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.barcode.billing.CreateProduct;
import com.example.barcode.R;
import com.example.barcode.billing.RegHelper;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {

    LinearLayout linearLayout;
    Button create;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextInputEditText name, email, password, cpassword, mobile;
    TextInputLayout nameLay, emailLay, passLay, cpasswordLay, mobileLay;
    public static String NAME, EMAIL, MOBILE, PASS, CPASS;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";
    String regex = "(0/91)?[0-9][0-9]{9}";
    Pattern pattern = Pattern.compile(regex);
    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        name = findViewById(R.id.name);
        nameLay = findViewById(R.id.nameLayout);
        email = findViewById(R.id.email);
        emailLay = findViewById(R.id.emailLayout);
        password = findViewById(R.id.password);
        passLay = findViewById(R.id.passLayout);
        cpassword = findViewById(R.id.cpassword);
        cpasswordLay = findViewById(R.id.cpassLayout);
        mobile = findViewById(R.id.mobile);
        mobileLay = findViewById(R.id.mobileLayout);
        create = findViewById(R.id.create);
        linearLayout = findViewById(R.id.login);
        linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), login.class);
            startActivity(intent);
        });
        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0) {
                    nameLay.setError(null);
                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 0 || editable.toString().matches(emailPattern)) {
                    emailLay.setError(null);
                }
            }
        });
        mobile.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                Matcher matcher = pattern.matcher(editable.toString());
                if (editable.toString().length() > 9 || matcher.matches()) {
                    mobileLay.setError(null);
                }
            }
        });
        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 7) {
                    passLay.setError(null);
                }
            }
        });
        cpassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.toString().length() > 7 || password.getText().toString().matches(editable.toString())) {
                    cpasswordLay.setError(null);
                }
            }
        });


        create.setOnClickListener(view -> {

            firebaseDatabase = FirebaseDatabase.getInstance();
            databaseReference = firebaseDatabase.getReference("user");
            NAME = name.getText().toString();
            EMAIL = email.getText().toString();
            PASS = password.getText().toString();
            CPASS = cpassword.getText().toString();
            MOBILE = mobile.getText().toString();
            Matcher matcher = pattern.matcher(MOBILE);

            if (NAME.isEmpty()) {
                nameLay.setError("Enter name");
                name.requestFocus();
            } else if (EMAIL.isEmpty()) {
                emailLay.setError("Enter email address");
                email.requestFocus();
            } else if (!EMAIL.matches(emailPattern)) {
                emailLay.setError("Enter valid email address");
                email.requestFocus();
            } else if (MOBILE.isEmpty()) {
                mobileLay.setError("Enter mobile number");
                mobile.requestFocus();
            } else if (!matcher.matches()) {
                mobileLay.setError("Enter valid mobile number");
                mobile.requestFocus();
            } else if (PASS.length() < 7) {
                passLay.setError("Enter password");
                password.requestFocus();
            } else if (CPASS.length() < 7) {
                cpasswordLay.setError("Enter confirm password");
                cpassword.requestFocus();
            } else if (!CPASS.equals(PASS)) {
                cpasswordLay.setError("Passwords do not match");
                cpassword.requestFocus();
            } else {
                Intent intent = new Intent(this, OtpAuth.class);
                intent.putExtra("phoneNo",MOBILE);
                startActivity(intent);
                finish();
                /*Intent intent = new Intent(this, CreateProduct.class);
                startActivity(intent);
                finish();*/
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent = new Intent(this,login.class);
        startActivity(intent);
    }

}