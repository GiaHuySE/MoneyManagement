package com.example.doan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.method.PasswordTransformationMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.doan.entity.Hu;
import com.example.doan.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;


import java.util.HashMap;
import java.util.Map;

public class SignUpEmailActivity extends AppCompatActivity {
    // Init declare for Firebase Database
    FirebaseDatabase database = FirebaseDatabase.getInstance();
    // Init declare for Firebase Database with users Collection
//    DatabaseReference myRef = database.getReference("users");
    // Init declare for Firebase Authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    // Init Firestore Database
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    Boolean isShowPassword = true;
    Boolean isShowRepeatPassword = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);
        getSupportActionBar().hide();
        // Init Firebase App
        FirebaseApp.initializeApp(this);

        // Declare Button and Edit Text
        Button btnSignUpEmail = findViewById(R.id.btnSignUpEmail);
//        Button btnShowPassword = findViewById(R.id.btnShowPassword);
//        Button btnShowRepeatPassword = findViewById(R.id.btnShowRepeatPassword);
        EditText txtEmailSignUp = findViewById(R.id.txtSignUpEmail);
        EditText txtPasswordSignUp = findViewById(R.id.txtSignUpPassword);
        EditText txtPasswordSignUpRepeat = findViewById(R.id.txtSignUpRepeatPassword);
        EditText txtTenNguoiDung = findViewById(R.id.txtUserName);

        // Button Sign Up event
        btnSignUpEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = txtEmailSignUp.getText().toString();
                String password = txtPasswordSignUp.getText().toString();
                String confirmPassword = txtPasswordSignUpRepeat.getText().toString();
                String tenNguoiDung = txtTenNguoiDung.getText().toString();

                if (tenNguoiDung == null || tenNguoiDung.isEmpty()) {
                    Toast.makeText(SignUpEmailActivity.this, "Vui lòng nhập tên người dùng",
                            Toast.LENGTH_SHORT).show();
                }
                else if(email.isEmpty()) {
                    Toast.makeText(SignUpEmailActivity.this, "Vui lòng nhập Email.",
                            Toast.LENGTH_SHORT).show();
                }
                else if(password.isEmpty()) {
                    Toast.makeText(SignUpEmailActivity.this, "Vui lòng nhập mật khẩu.",
                            Toast.LENGTH_SHORT).show();
                }
                else if(password.length() < 6) {
                    Toast.makeText(SignUpEmailActivity.this, "Mật khẩu quá ngắn, vui lòng nhập lại mật khẩu.",
                            Toast.LENGTH_SHORT).show();
                }
                else if(!password.equals(confirmPassword)) {
                    Toast.makeText(SignUpEmailActivity.this, "Mật khẩu xác nhận không khớp, vui lòng nhập lại mật khẩu.",
                            Toast.LENGTH_SHORT).show();
                } else {
                    // When creating a user, automatically create jars for that user
                    mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SignUpEmailActivity.this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign up success, update UI with the signed-in user's information
                                FirebaseUser user = mAuth.getCurrentUser();
                                // Create a new user in the Realtime Database
//                                String userId = myRef.push().getKey();
                                String uid = user.getUid();
                                User newUser = new User(email, tenNguoiDung);
//                                myRef.child(userId).setValue(newUser);
                                // Signup successful, do something here (e.g. go to the main activity)
                                Map<String, Hu> userMap = new HashMap<>();
//                                userMap.put("email", email);
                                userMap.put("HuThietYeu", new Hu(55, 0.0));
                                userMap.put("HuGiaoDuc", new Hu(10, 0.0));
                                userMap.put("HuTietKiem", new Hu(10, 0.0));
                                userMap.put("HuHuongThu", new Hu(10, 0.0));
                                userMap.put("HuDauTu", new Hu(10, 0.0));
                                userMap.put("HuThienTam", new Hu(5, 0.0));
                                DocumentReference documentReference = firestoreDB.collection("users").document(uid);
                                documentReference.collection("duLieuHu").document("duLieuTien").set(userMap);
                                documentReference.collection("duLieuNguoiDung").document("duLieuTaiKhoan").set(newUser);

                                signIn(email, password);

//                                firestoreDB.collection("users").document(uid).set(userMap);

                            } else {
                                // If sign up fails, display a message to the user.
                                Toast.makeText(SignUpEmailActivity.this, "Đăng ký thất bại, vui lòng nhập lại Email và đăng ký lại.",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });

//        // Show/hide password
//        btnShowPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isShowPassword) {
//                    // Show the password
//                    txtPasswordSignUp.setTransformationMethod(null);
//                    isShowPassword = false;
//                } else {
//                    // Hide the password
//                    txtPasswordSignUp.setTransformationMethod(new PasswordTransformationMethod());
//                    isShowPassword = true;
//                }
//                // Move cursor to the end of the text
//                txtPasswordSignUp.setSelection(txtPasswordSignUp.getText().length());
//            }
//        });
//        txtPasswordSignUp.setTransformationMethod(new PasswordTransformationMethod());
//
//        // Show/hide repeat password
//        btnShowRepeatPassword.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (isShowRepeatPassword) {
//                    // Show the password
//                    txtPasswordSignUpRepeat.setTransformationMethod(null);
//                    isShowRepeatPassword = false;
//                } else {
//                    // Hide the password
//                    txtPasswordSignUpRepeat.setTransformationMethod(new PasswordTransformationMethod());
//                    isShowRepeatPassword = true;
//                }
//                // Move cursor to the end of the text
//                txtPasswordSignUpRepeat.setSelection(txtPasswordSignUpRepeat.getText().length());
//            }
//        });
//        txtPasswordSignUpRepeat.setTransformationMethod(new PasswordTransformationMethod());
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
                            Intent intent = new Intent(SignUpEmailActivity.this, HomeActivity.class);
                            startActivity(intent);
                        } else {
                            // Sign in failed
                            Toast.makeText(SignUpEmailActivity.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}