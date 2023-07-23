package com.example.doan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInEmailActivity extends AppCompatActivity {
    //Init Firebase Auth
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    Boolean isShowPassword = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_email);
        getSupportActionBar().hide();
        EditText txtSignInEmail = findViewById(R.id.txtSignInEmail);
        EditText txtSignInPassword = findViewById(R.id.txtSignInPassword);
        Button btnSignInEmail = findViewById(R.id.btnSignInEmail);

//        ImageView imgShowPassWord = findViewById(R.id.btnShowPasswordSignIn);

        btnSignInEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email = txtSignInEmail.getText().toString();
                String password = txtSignInPassword.getText().toString();
                signIn(email, password);
            }
        });

//        imgShowPassWord.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isShowPassword) {
//                    // Show the password
//                    txtSignInPassword.setTransformationMethod(null);
//                    isShowPassword = false;
//                } else {
//                    // Hide the password
//                    txtSignInPassword.setTransformationMethod(new PasswordTransformationMethod());
//                    isShowPassword = true;
//                }
//                // Move cursor to the end of the text
//                txtSignInPassword.setSelection(txtSignInPassword.getText().length());
//            }
//        });
//        txtSignInPassword.setTransformationMethod(new PasswordTransformationMethod());
    }

    private void signIn(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success
                            FirebaseUser user = mAuth.getCurrentUser();
                            // Navigate to the main activity or do something else
                            Intent intent = new Intent(SignInEmailActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            // Sign in failed
                            Toast.makeText(SignInEmailActivity.this, "Tài khoản hoặc mật khẩu sai, vui lòng đăng nhập lại.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}