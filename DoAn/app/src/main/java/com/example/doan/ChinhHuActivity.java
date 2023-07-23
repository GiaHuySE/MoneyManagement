package com.example.doan;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class ChinhHuActivity extends AppCompatActivity {

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore firestore;
    TextView txtTongTyLe,txtLuu;
    ImageView giamTyLeThietYeu,giamTyLeGiaoDuc,giamTyLeTietKiem,giamTyLeHuongThu,giamTyLeDauTu,giamTyLeThienTam,back;
    ImageView tangTyLeThietYeu,tangTyLeGiaoDuc,tangTyLeTietKiem,tangTyLeHuongThu,tangTyLeDauTu,tangTyLeThienTam;
    EditText editTextThietYeu,editTextGiaoDuc,editTextTietKiem,editTextHuongThu,editTextDauTu,editTextThienTam;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_income);
        setContentView(R.layout.activity_chinh_hu);

        editTextThietYeu  = findViewById(R.id.editTextTyLeThietYeu);
        editTextThietYeu.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextGiaoDuc = findViewById(R.id.editTextTyLeGiaoDuc);
        editTextGiaoDuc.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextTietKiem = findViewById(R.id.editTextTyLeTietKiem);
        editTextTietKiem.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextHuongThu = findViewById(R.id.editTextTyLeHuongThu);
        editTextHuongThu.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextDauTu = findViewById(R.id.editTextTyLeDauTu);
        editTextDauTu.setInputType(InputType.TYPE_CLASS_NUMBER);
        editTextThienTam = findViewById(R.id.editTextTyLeThienTam);
        editTextThienTam.setInputType(InputType.TYPE_CLASS_NUMBER);
        back = findViewById(R.id.back);
        txtTongTyLe = findViewById(R.id.tongTyLe);
        giamTyLeThietYeu = findViewById(R.id.giamTyLeThietYeu);
        giamTyLeGiaoDuc = findViewById(R.id.giamTyLeGiaoDuc);
        giamTyLeTietKiem = findViewById(R.id.giamTyLeTietKiem);
        giamTyLeHuongThu = findViewById(R.id.giamTyLeHuongThu);
        giamTyLeDauTu = findViewById(R.id.giamTyLeDauTu);
        giamTyLeThienTam = findViewById(R.id.giamTyLeThienTam);
        tangTyLeThietYeu = findViewById(R.id.tangTyLeThietYeu);
        tangTyLeGiaoDuc = findViewById(R.id.tangTyLeGiaoDuc);
        tangTyLeTietKiem = findViewById(R.id.tangTyLeTietKiem);
        tangTyLeHuongThu = findViewById(R.id.tangTyLeHuongThu);
        tangTyLeDauTu = findViewById(R.id.tangTyLeDauTu);
        tangTyLeThienTam = findViewById(R.id.tangTyLeThienTam);
        txtLuu = findViewById(R.id.txtLuuChinhTyLe);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        int tyleThietYeu = getIntent().getIntExtra("tyLeThietYeu",0);
        int tyleGiaoDuc = getIntent().getIntExtra("tyLeGiaoDuc",0);
        int tyleTietKiem = getIntent().getIntExtra("tyLeTietKiem",0);
        int tyleHuongThu = getIntent().getIntExtra("tyLeHuongThu",0);
        int tyleDauTu = getIntent().getIntExtra("tyLeDauTu",0);
        int tyleThienTam = getIntent().getIntExtra("tyLeThienTam",0);
        int tongTyLe = tyleThietYeu + tyleGiaoDuc + tyleTietKiem + tyleHuongThu + tyleDauTu + tyleThienTam;

        editTextThietYeu.setText(String.valueOf(tyleThietYeu));
        editTextGiaoDuc.setText(String.valueOf(tyleGiaoDuc));
        editTextTietKiem.setText(String.valueOf(tyleTietKiem));
        editTextHuongThu.setText(String.valueOf(tyleHuongThu));
        editTextDauTu.setText(String.valueOf(tyleDauTu));
        editTextThienTam.setText(String.valueOf(tyleThienTam));
        txtTongTyLe.setText(String.valueOf(tongTyLe)+ "%");

        editTextThietYeu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                    String inputString = s.toString();
                    if(inputString.isEmpty()){
                        int input = 0;
                        int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                        int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                        int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                        int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                        int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                        int tongTyLe = input + tyLeGiaoDuc +tyLeTietKiem + tyLeHuongThu + tyLeDauTu + tyLeThienTam;
                        txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                    }else {
                        int input = Integer.parseInt(inputString);
                        int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                        int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                        int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                        int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                        int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                        int tongTyLe = input + tyLeGiaoDuc +tyLeTietKiem + tyLeHuongThu + tyLeDauTu + tyLeThienTam;
                        txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                    }

            }

            @Override
            public void afterTextChanged(Editable s) {
                int input = 0;
                if (!TextUtils.isEmpty(s.toString())) {
                    input = Integer.parseInt(s.toString());
                }

                // Check if the input value is within the valid range
                if (input < 0 || input > 100) {
                    // If input value is out of range, set it to the closest valid value
                    if (input < 0) {
                        input = 0;
                    } else {
                        input = 100;
                    }
                    // Update the EditText with the valid value
                    editTextThietYeu.setText(String.valueOf(input));
                    editTextThietYeu.setSelection(String.valueOf(input).length());
                }
            }
        });
        editTextTietKiem.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputString = s.toString();
                if(inputString.isEmpty()){
                    int input = 0;
                    int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                    int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                    int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                    int tongTyLe = tyLeThietYeu + tyLeGiaoDuc +input + tyLeHuongThu + tyLeDauTu + tyLeThienTam;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }else {
                    int input = Integer.parseInt(inputString);
                    int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                    int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                    int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                    int tongTyLe = tyLeThietYeu + tyLeGiaoDuc +input + tyLeHuongThu + tyLeDauTu + tyLeThienTam;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int input = 0;
                if (!TextUtils.isEmpty(s.toString())) {
                    input = Integer.parseInt(s.toString());
                }

                // Check if the input value is within the valid range
                if (input < 0 || input > 100) {
                    // If input value is out of range, set it to the closest valid value
                    if (input < 0) {
                        input = 0;
                    } else {
                        input = 100;
                    }
                    // Update the EditText with the valid value
                    editTextTietKiem.setText(String.valueOf(input));
                    editTextTietKiem.setSelection(String.valueOf(input).length());
                }
            }
        });

        editTextGiaoDuc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputString = s.toString();
                if(inputString.isEmpty()){
                    int input = 0;
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                    int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                    int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                    int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                    int tongTyLe = tyLeThietYeu + input +tyLeTietKiem + tyLeHuongThu + tyLeDauTu + tyLeThienTam;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }else {
                    int input = Integer.parseInt(inputString);
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                    int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                    int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                    int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                    int tongTyLe = tyLeThietYeu + input +tyLeTietKiem + tyLeHuongThu + tyLeDauTu + tyLeThienTam;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int input = 0;
                if (!TextUtils.isEmpty(s.toString())) {
                    input = Integer.parseInt(s.toString());
                }

                // Check if the input value is within the valid range
                if (input < 0 || input > 100) {
                    // If input value is out of range, set it to the closest valid value
                    if (input < 0) {
                        input = 0;
                    } else {
                        input = 100;
                    }
                    // Update the EditText with the valid value
                    editTextGiaoDuc.setText(String.valueOf(input));
                    editTextGiaoDuc.setSelection(String.valueOf(input).length());
                }
            }
        });

        editTextHuongThu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputString = s.toString();
                if(inputString.isEmpty()){
                    int input = 0;
                    int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                    int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                    int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                    int tongTyLe = tyLeThietYeu + tyLeGiaoDuc +tyLeTietKiem + input + tyLeDauTu + tyLeThienTam;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }else {
                    int input = Integer.parseInt(inputString);
                    int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                    int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                    int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                    int tongTyLe = tyLeThietYeu + tyLeGiaoDuc +tyLeTietKiem + input + tyLeDauTu + tyLeThienTam;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int input = 0;
                if (!TextUtils.isEmpty(s.toString())) {
                    input = Integer.parseInt(s.toString());
                }

                // Check if the input value is within the valid range
                if (input < 0 || input > 100) {
                    // If input value is out of range, set it to the closest valid value
                    if (input < 0) {
                        input = 0;
                    } else {
                        input = 100;
                    }
                    // Update the EditText with the valid value
                    editTextHuongThu.setText(String.valueOf(input));
                    editTextHuongThu.setSelection(String.valueOf(input).length());
                }
            }
        });
        editTextDauTu.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputString = s.toString();
                if(inputString.isEmpty()){
                    int input = 0;
                    int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                    int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                    int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                    int tongTyLe = tyLeThietYeu + tyLeGiaoDuc +tyLeTietKiem + tyLeHuongThu + input + tyLeThienTam;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }else {
                    int input = Integer.parseInt(inputString);
                    int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                    int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                    int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tyLeThienTam = Integer.parseInt(editTextThienTam.getText().toString());
                    int tongTyLe = tyLeThietYeu + tyLeGiaoDuc +tyLeTietKiem + tyLeHuongThu + input + tyLeThienTam;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int input = 0;
                if (!TextUtils.isEmpty(s.toString())) {
                    input = Integer.parseInt(s.toString());
                }

                // Check if the input value is within the valid range
                if (input < 0 || input > 100) {
                    // If input value is out of range, set it to the closest valid value
                    if (input < 0) {
                        input = 0;
                    } else {
                        input = 100;
                    }
                    // Update the EditText with the valid value
                    editTextDauTu.setText(String.valueOf(input));
                    editTextDauTu.setSelection(String.valueOf(input).length());
                }
            }
        });
        editTextThienTam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String inputString = s.toString();
                if(inputString.isEmpty()){
                    int input = 0;
                    int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                    int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                    int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                    int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tongTyLe = tyLeThietYeu + tyLeGiaoDuc +tyLeTietKiem + tyLeHuongThu + tyLeDauTu + input;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }else {
                    int input = Integer.parseInt(inputString);
                    int tyLeGiaoDuc = Integer.parseInt(editTextGiaoDuc.getText().toString());
                    int tyLeTietKiem = Integer.parseInt(editTextTietKiem.getText().toString());
                    int tyLeHuongThu = Integer.parseInt(editTextHuongThu.getText().toString());
                    int tyLeDauTu = Integer.parseInt(editTextDauTu.getText().toString());
                    int tyLeThietYeu = Integer.parseInt(editTextThietYeu.getText().toString());
                    int tongTyLe = tyLeThietYeu + tyLeGiaoDuc +tyLeTietKiem + tyLeHuongThu + tyLeDauTu + input;
                    txtTongTyLe.setText(String.valueOf(tongTyLe)+"%");
                }
            }

            @Override
            public void afterTextChanged(Editable s) {
                int input = 0;
                if (!TextUtils.isEmpty(s.toString())) {
                    input = Integer.parseInt(s.toString());
                }

                // Check if the input value is within the valid range
                if (input < 0 || input > 100) {
                    // If input value is out of range, set it to the closest valid value
                    if (input < 0) {
                        input = 0;
                    } else {
                        input = 100;
                    }
                    // Update the EditText with the valid value
                    editTextThienTam.setText(String.valueOf(input));
                    editTextThienTam.setSelection(String.valueOf(input).length());
                }
            }
        });

        giamTyLeThietYeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeGiamThietYeu = Integer.parseInt(String.valueOf(editTextThietYeu.getText())) - 5;
                editTextThietYeu.setText(String.valueOf(tyLeGiamThietYeu));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) - 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });

        giamTyLeGiaoDuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeGiamGiaoDuc = Integer.parseInt(String.valueOf(editTextGiaoDuc.getText())) - 5 ;
                editTextGiaoDuc.setText(String.valueOf(tyLeGiamGiaoDuc));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) - 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });
        giamTyLeTietKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeGiamTietKiem = Integer.parseInt(String.valueOf(editTextTietKiem.getText())) - 5;
                editTextTietKiem.setText(String.valueOf(tyLeGiamTietKiem));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) - 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });
        giamTyLeHuongThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeGiamHuongThu = Integer.parseInt(String.valueOf(editTextHuongThu.getText())) - 5;
                editTextHuongThu.setText(String.valueOf(tyLeGiamHuongThu));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) - 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });
        giamTyLeDauTu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeGiamDauTu = Integer.parseInt(String.valueOf(editTextDauTu.getText())) - 5 ;
                editTextDauTu.setText(String.valueOf(tyLeGiamDauTu));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) - 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });
        giamTyLeThienTam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeGiamThienTam = Integer.parseInt(String.valueOf(editTextThienTam.getText())) - 5;
                editTextThienTam.setText(String.valueOf(tyLeGiamThienTam));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) - 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });

        tangTyLeThietYeu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeTangThietYeu = Integer.parseInt(String.valueOf(editTextThietYeu.getText())) + 5;
                editTextThietYeu.setText(String.valueOf(tyLeTangThietYeu));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) + 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });
        tangTyLeGiaoDuc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeTangGiaoDuc = Integer.parseInt(String.valueOf(editTextGiaoDuc.getText())) + 5;
                editTextGiaoDuc.setText(String.valueOf(tyLeTangGiaoDuc));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) + 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });
        tangTyLeTietKiem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeTangTietKiem = Integer.parseInt(String.valueOf(editTextTietKiem.getText())) + 5;
                editTextTietKiem.setText(String.valueOf(tyLeTangTietKiem));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) + 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });
        tangTyLeHuongThu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeTangHuongThu = Integer.parseInt(String.valueOf(editTextHuongThu.getText())) + 5;
                editTextHuongThu.setText(String.valueOf(tyLeTangHuongThu));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) + 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");

            }
        });
        tangTyLeDauTu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeTangDauTu = Integer.parseInt(String.valueOf(editTextDauTu.getText())) + 5;
                editTextDauTu.setText(String.valueOf(tyLeTangDauTu));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) + 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });
        tangTyLeThienTam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int tyLeTangThienTam = Integer.parseInt(String.valueOf(editTextThienTam.getText())) + 5;
                editTextThienTam.setText(String.valueOf(tyLeTangThienTam));
//                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
//                int tong = Integer.parseInt(tongTyLe) + 5;
//                txtTongTyLe.setText(String.valueOf(tong) + "%");
            }
        });

        txtLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String tongTyLe = txtTongTyLe.getText().toString().replace("%","");
                int tong = Integer.parseInt(tongTyLe) ;
                if(tong == 100){
                    firestore = FirebaseFirestore.getInstance();
                    String uid = currentUser.getUid();
                    //Retrieve data from fire store documents and collections
                    CollectionReference useRef = firestore.collection("users");
                    DocumentReference userDocRef = useRef.document(uid).collection("duLieuHu").document("duLieuTien");
                    Map<String, Object> updatedTyLe = new HashMap<>();
                    updatedTyLe.put("HuThietYeu.tyLe", Integer.parseInt(String.valueOf(editTextThietYeu.getText())));
                    updatedTyLe.put("HuGiaoDuc.tyLe", Integer.parseInt(String.valueOf(editTextGiaoDuc.getText())));
                    updatedTyLe.put("HuTietKiem.tyLe", Integer.parseInt(String.valueOf(editTextTietKiem.getText())));
                    updatedTyLe.put("HuHuongThu.tyLe", Integer.parseInt(String.valueOf(editTextHuongThu.getText())));
                    updatedTyLe.put("HuDauTu.tyLe", Integer.parseInt(String.valueOf(editTextDauTu.getText())));
                    updatedTyLe.put("HuThienTam.tyLe", Integer.parseInt(String.valueOf(editTextThienTam.getText())));
                    userDocRef.update(updatedTyLe).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Toast.makeText(ChinhHuActivity.this, "Chỉnh sửa tỉ lệ thành công", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChinhHuActivity.this, "Chỉnh sửa thất bại", Toast.LENGTH_SHORT).show();
                        }
                    });
                }else{
                    Toast.makeText(ChinhHuActivity.this, "Tổng tỷ lệ phải bằng 100%", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

}