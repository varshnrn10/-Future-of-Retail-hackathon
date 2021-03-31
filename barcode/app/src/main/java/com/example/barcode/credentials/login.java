package com.example.barcode.credentials;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.example.barcode.R;
import com.example.barcode.billing.CreateProduct;
import com.example.barcode.billing.scan;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class login extends AppCompatActivity {

    public GoogleSignInClient mGoogleSignInClient;
    int RC_SIGN_IN = 1;
    LinearLayout linearLayout;
    private FirebaseAuth mAuth;
    TextInputEditText mobile, password;
    TextInputLayout mobileLayout, passwordLayout;
    Button login;
    String MOBILE, PASSWORD;
    String regex = "(0/91)?[0-9][0-9]{9}";
    Pattern pattern = Pattern.compile(regex);
    SharedPreferences.Editor editor;
    SharedPreferences sharedPreferences;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        shareUpdateUI();
        linearLayout = findViewById(R.id.sign);
        mobile = findViewById(R.id.mobile);
        mobileLayout = findViewById(R.id.mobileLayout);
        password = findViewById(R.id.password);
        passwordLayout = findViewById(R.id.passLayout);
        login = findViewById(R.id.loginBtn);

        mAuth = FirebaseAuth.getInstance();
        sharedPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);
        linearLayout.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), Registration.class);
            startActivity(intent);
            finish();
        });
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        SignInButton signInButton = findViewById(R.id.sign_in_button);
        signInButton.setSize(SignInButton.SIZE_STANDARD);
        findViewById(R.id.sign_in_button).setOnClickListener(view -> {
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
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
                if (editable.toString().length() > 9) {
                    mobileLayout.setError(null);
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
                    passwordLayout.setError(null);
                }
            }
        });
        login.setOnClickListener(view -> {
            MOBILE = mobile.getText().toString();
            PASSWORD = password.getText().toString();
            Matcher matcher = pattern.matcher(MOBILE);
            if (MOBILE.isEmpty()) {
                mobileLayout.setError("Enter mobile number");
                mobile.requestFocus();
            } else if (!matcher.matches()) {
                mobileLayout.setError("Enter valid mobile number");
                mobile.requestFocus();
            } else if (PASSWORD.isEmpty()) {
                passwordLayout.setError("Enter password");
                password.requestFocus();
            } else if (PASSWORD.length() < 7) {
                passwordLayout.setError("Enter password length min 8 characters");
                password.requestFocus();
            } else {
                isUser();
            }
        });
    }

    private void isUser() {

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("user");
        Query user = reference.orderByChild("mobile").equalTo(MOBILE);
        user.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    String PASSdb = snapshot.child(sharedPreferences.getString("uid",null)).child("password").getValue(String.class);
                    if (PASSWORD.equals(PASSdb)) {
                        String name = snapshot.child(MOBILE).child("name").getValue(String.class);
                        String email = snapshot.child(MOBILE).child("email").getValue(String.class);
                        editor = sharedPreferences.edit();
                        editor.putString("mobile", MOBILE);
                        editor.putString("email",email);
                        editor.putString("name",name);
                        editor.apply();
                        Toast.makeText(login.this, "Login Successful", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), CreateProduct.class);
                        startActivity(intent);
                        finish();
                    } else {
                        Toast.makeText(login.this, "Wrong Credentials", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(login.this, "Account does not exits", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.e("id", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.e("error", "Google sign in failed", e);
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.e("message", "signInWithCredential:success");
                        FirebaseUser user = mAuth.getCurrentUser();
                        String name = user.getDisplayName();
                        String email = user.getEmail();
                        String mobile = user.getPhoneNumber();
                        Log.e("user info",name+" "+email+" "+mobile);
                        Uri photo = user.getPhotoUrl();
                        editor = sharedPreferences.edit();
                        editor.putString("mobile", mobile);
                        editor.putString("name", name);
                        editor.putString("email", email);
                        editor.putString("photo", photo.toString());
                        editor.apply();
                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("message", "signInWithCredential:failure", task.getException());
                        updateUI(null);
                    }
                });
    }

    private void updateUI(FirebaseUser user) {
        if (user.getUid() != null) {
            Toast.makeText(this, "Login Success", Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(this, CreateProduct.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Login Failed", Toast.LENGTH_SHORT).show();
        }
    }

    private void shareUpdateUI() {
        Intent intent;
        SharedPreferences sharedPreferences = getApplicationContext().getSharedPreferences("Login", 0);
        if (FirebaseAuth.getInstance().getCurrentUser() != null || sharedPreferences.contains("mobile")) {
            intent = new Intent(this, CreateProduct.class);
            startActivity(intent);
        }

    }
}