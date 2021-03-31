package com.example.barcode.credentials;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.barcode.R;
import com.example.barcode.billing.CreateProduct;
import com.example.barcode.billing.RegHelper;
import com.example.barcode.databinding.ActivityMainBinding;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.TaskExecutors;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.concurrent.TimeUnit;

import static com.example.barcode.credentials.Registration.EMAIL;
import static com.example.barcode.credentials.Registration.MOBILE;
import static com.example.barcode.credentials.Registration.NAME;
import static com.example.barcode.credentials.Registration.PASS;

public class OtpAuth extends AppCompatActivity {

    Button verify;
    TextInputEditText otp;
    TextInputLayout otpLay;
    ProgressBar progressBar;
    String phoneNo;
    String code;
    public static String uid;
    ProgressDialog pd;
    DatabaseReference databaseReference;
    SharedPreferences sharedPreferences;
    FirebaseDatabase firebaseDatabase;

    public PhoneAuthProvider.ForceResendingToken forceResendingToken;
    public PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks;

    public String mVerificationId;

    private static final String TAG = "MAIN_TAG";

    public FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_auth);

        verify = findViewById(R.id.verify_btn);
        otp = findViewById(R.id.otp);
        progressBar = findViewById(R.id.progress_bar);
        phoneNo = getIntent().getStringExtra("phoneNo");
        firebaseAuth = FirebaseAuth.getInstance();
        pd = new ProgressDialog(this);
        pd.setTitle("please wait...");
        otpLay = findViewById(R.id.otpLayout);

        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("user");
        otp.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (editable.length()>0) {
                    otpLay.setError(null);
                }
            }
        });


        callbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                signInWithPhoneAuthCredentials(phoneAuthCredential);

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                pd.dismiss();
                Toast.makeText(OtpAuth.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(verificationId, forceResendingToken);
                Log.e(TAG,"onCodeSent "+verificationId);
                mVerificationId = verificationId;

                Toast.makeText(OtpAuth.this, "Verification Code Sent...", Toast.LENGTH_SHORT).show();

            }
        };
        startPhoneNumberVerification(phoneNo);

        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                code = otp.getText().toString();

                if (code.isEmpty()) {
                    otpLay.setError("Please Enter OTP");
                    otp.requestFocus();
                } else {
                    verifyPhoneNumberWithCode(mVerificationId, code);
                }
            }
        });

    }


    private void startPhoneNumberVerification(String phoneNo) {
        pd.setMessage("Verifying Phone Number");
        pd.show();

        PhoneAuthOptions options = PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber("+91"+phoneNo)
                .setTimeout(60L,TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(callbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private void verifyPhoneNumberWithCode(String mVerificationId, String code) {
        pd.setMessage("Verifying Code");
        pd.show();
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(mVerificationId,code);
        signInWithPhoneAuthCredentials(credential);
    }

    private void signInWithPhoneAuthCredentials(PhoneAuthCredential credential) {
        pd.setMessage("Logging in");
        firebaseAuth.signInWithCredential(credential)
                .addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                    @Override
                    public void onSuccess(AuthResult authResult) {
                        pd.dismiss();

                        RegHelper helper = new RegHelper(EMAIL, NAME, MOBILE, PASS);
                        uid=String.valueOf((int)(Math.random()*(10000-1+1)+1));
                        databaseReference.child( uid).setValue(helper)
                                .addOnSuccessListener(aVoid -> Toast.makeText(OtpAuth.this, "Account created", Toast.LENGTH_SHORT).show())
                                .addOnFailureListener(e -> Toast.makeText(OtpAuth.this, "Something wrong", Toast.LENGTH_SHORT).show());
                        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString("mobile", MOBILE);
                        editor.putString("uid",uid);
                        Log.e("mobile","mobile"+MOBILE);
                        editor.putString("name",NAME);
                        editor.putString("email",EMAIL);
                        editor.apply();

                        Intent intent = new Intent(OtpAuth.this, CreateProduct.class);
                        startActivity(intent);
                    }
                })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                pd.dismiss();
                Toast.makeText(OtpAuth.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


}