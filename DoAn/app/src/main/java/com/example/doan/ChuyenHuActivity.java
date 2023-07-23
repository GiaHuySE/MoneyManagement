package com.example.doan;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doan.entity.GiaoDichChuyenTaiSan;
import com.example.doan.entity.GiaoDichRut;
import com.example.doan.entity.SpinnerItem;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class ChuyenHuActivity extends AppCompatActivity {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    FirebaseFirestore firestore;
    EditText editTextSoTienChuyen,editTextMoTa;
    TextView txtHuy,txtLuu;
    Spinner spinnerTaiSanChuyen,spinnerTaiSanNhan;
    SpinnerAdapter adapter;
    String currentItemTenHuSelectedSpinnerTaiSanChuyen;
    Double currentItemSoTienSelectedSpinnerTaiSanChuyen;
    String currentItemTenHuSelectedSpinnerTaiSanNhan;
    Double currentItemSoTienSelectedSpinnerTaiSanNhan;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_income);
        setContentView(R.layout.activity_chuyen_hu);
        txtHuy = findViewById(R.id.textViewHuy);
        txtLuu = findViewById(R.id.textViewLuu);
        editTextSoTienChuyen = findViewById(R.id.editTextTienChuyen);
        spinnerTaiSanChuyen = findViewById(R.id.spinnerTaiSanChuyen);
        spinnerTaiSanNhan = findViewById(R.id.spinnerTaiSanNhan);
        txtLuu = findViewById(R.id.textViewLuu);
        //Chuyển về giao diện chính
        txtHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });


        
        //format số tiền ghi nhập
        editTextSoTienChuyen.setInputType(InputType.TYPE_CLASS_NUMBER);
//        editTextSoTienChuyen.addTextChangedListener(new TextWatcher() {
//            DecimalFormat df = new DecimalFormat("#,###");
//            private String current = "";
//            @Override
//            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
//
//            }
//
//            @Override
//            public void onTextChanged(CharSequence s, int start, int before, int count) {
//
//            }
//
//            @Override
//            public void afterTextChanged(Editable s) {
//                if (!s.toString().equals(current)) {
//                    editTextSoTienChuyen.removeTextChangedListener(this);
//
//                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance(Locale.US).getCurrency().getSymbol());
//                    String cleanString = s.toString().replaceAll(replaceable, "");
//
//                    if (!TextUtils.isEmpty(cleanString)) {
//                        double parsed = Double.parseDouble(cleanString);
//                        String formatted = df.format(parsed);
//                        current = formatted;
//                        editTextSoTienChuyen.setText(formatted);
//                        editTextSoTienChuyen.setSelection(formatted.length());
//                    } else {
//                        current = "";
//                    }
//
//                    editTextSoTienChuyen.addTextChangedListener(this);
//                }
//            }
//        });

        editTextSoTienChuyen.addTextChangedListener(new TextWatcher() {
            DecimalFormat df = new DecimalFormat("#,###");
            private String current = "";

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if (!s.toString().equals(current)) {
                    editTextSoTienChuyen.removeTextChangedListener(this);

                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance(Locale.US).getCurrency().getSymbol());
                    String cleanString = s.toString().replaceAll(replaceable, "");

                    if (!TextUtils.isEmpty(cleanString)) {
                        double parsed = Double.parseDouble(cleanString);
                        if (parsed < 0) {
                            parsed = 0;
                        } else if (parsed > 100000000000L) {
                            parsed = 100000000000L;
                        }
                        String formatted = df.format(parsed);
                        current = formatted;
                        editTextSoTienChuyen.setText(formatted);
                        editTextSoTienChuyen.setSelection(formatted.length());
                    } else {
                        current = "";
                    }

                    editTextSoTienChuyen.addTextChangedListener(this);
                }
            }
        });
        //Lấy giá trị bỏ vào spinner
        firestore = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
        //Retrieve data from fire store documents and collections
        CollectionReference useRef = firestore.collection("users");
        DocumentReference userDocRef = useRef.document(uid).collection("duLieuHu").document("duLieuTien");
        List<SpinnerItem> items = new ArrayList<>();
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if(documentSnapshot.exists()){
                    Map<String,Object> data = documentSnapshot.getData();
                    if(data != null){
                        for (String fieldName :data.keySet()){
                            Map<String,Object> hu = (Map<String, Object>) data.get(fieldName);
                            Double soTien = (Double) hu.get("soTien");
                            String tenHu = "";
                            if(fieldName.equals("HuDauTu")){
                                tenHu = "Hũ đầu tư";
                            } else if (fieldName.equals("HuGiaoDuc")) {
                                tenHu = "Hũ giáo dục";
                            } else if (fieldName.equals("HuHuongThu")) {
                                tenHu = "Hũ hưỏng thụ";
                            } else if (fieldName.equals("HuThienTam")) {
                                tenHu = "Hũ thiện tâm";
                            } else if (fieldName.equals("HuThietYeu")) {
                                tenHu = "Hũ thiết yếu";
                            } else if (fieldName.equals("HuTietKiem")) {
                                tenHu = "Hũ tiết kiệm";
                            }
                            Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                            String soTienHu = currencyFormat.format(soTien);
                            SpinnerItem spinnerItem = new SpinnerItem(tenHu,soTienHu);
                            items.add(spinnerItem);
                            Log.d("FieldName",fieldName);
                            Log.d("SoTien", String.valueOf(soTien));
                            Log.d("Spinner list", String.valueOf(items));
                        }

                        adapter = new SpinnerAdapter(ChuyenHuActivity.this,items);
                        spinnerTaiSanChuyen.setAdapter(adapter);
                        spinnerTaiSanNhan.setAdapter(adapter);
                    }else {
                        Log.d(TAG, "Document does not exist");
                    }
                }
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e(TAG, "Error getting document", e);
            }
        });


        spinnerTaiSanChuyen.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String soTienSelected = null;
                currentItemTenHuSelectedSpinnerTaiSanChuyen = String.valueOf(items.get(position).getTenHu());
                soTienSelected = items.get(position).getSoTien();
                Locale locale = new Locale("vi", "VN");
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                DecimalFormat decimalFormatter = (DecimalFormat) NumberFormat.getInstance(locale);
                decimalFormatter.setParseBigDecimal(true);
                Number soTienFormat = 0;
                try {
                    soTienFormat = decimalFormatter.parse(soTienSelected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentItemSoTienSelectedSpinnerTaiSanChuyen = soTienFormat.doubleValue();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        spinnerTaiSanNhan.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String soTienSelected = null;
                currentItemTenHuSelectedSpinnerTaiSanNhan = String.valueOf(items.get(position).getTenHu());
                soTienSelected = items.get(position).getSoTien();
                Locale locale = new Locale("vi", "VN");
                NumberFormat currencyFormatter = NumberFormat.getCurrencyInstance(locale);
                DecimalFormat decimalFormatter = (DecimalFormat) NumberFormat.getInstance(locale);
                decimalFormatter.setParseBigDecimal(true);
                Number soTienFormat = 0;
                try {
                    soTienFormat = decimalFormatter.parse(soTienSelected);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                currentItemSoTienSelectedSpinnerTaiSanNhan = soTienFormat.doubleValue();

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        txtLuu.setOnClickListener(new View.OnClickListener() {
            private double tienchuyen;
            DocumentReference documentReference = firestore.collection("users").document(uid);
            @Override
            public void onClick(View v) {
                String tien = editTextSoTienChuyen.getText().toString().replaceAll("[^\\d]", "");

                tienchuyen = Double.parseDouble(tien);
                String huNhan = "";
                String huChuyen = "";
                if(currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ đầu tư")){
                    huChuyen = "HuDauTu";
                } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ giáo dục")) {
                    huChuyen = "HuGiaoDuc";
                } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ hưỏng thụ" )) {
                    huChuyen = "HuHuongThu";
                } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ thiện tâm")) {
                    huChuyen = "HuThienTam";
                } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ thiết yếu")) {
                    huChuyen = "HuThietYeu";
                } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ tiết kiệm")) {
                    huChuyen = "HuTietKiem";
                }

                if(currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ đầu tư")){
                    huNhan = "HuDauTu";
                } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ giáo dục")) {
                    huNhan = "HuGiaoDuc";
                } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ hưỏng thụ" )) {
                    huNhan = "HuHuongThu";
                } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ thiện tâm")) {
                    huNhan = "HuThienTam";
                } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ thiết yếu")) {
                    huNhan = "HuThietYeu";
                } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ tiết kiệm")) {
                    huNhan = "HuTietKiem";
                }
                if(huChuyen.equals(huNhan)==false){
                    if(!tien.equals("0")){
                        if(currentItemSoTienSelectedSpinnerTaiSanChuyen >= tienchuyen ){
                            GiaoDichChuyenTaiSan giaoDichChuyenTaiSan = new GiaoDichChuyenTaiSan(currentItemTenHuSelectedSpinnerTaiSanChuyen,tienchuyen,currentItemTenHuSelectedSpinnerTaiSanNhan);
                            Date dNow = new Date();
                            SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                            String dateTimeID = ft.format(dNow);
                            Map<String, GiaoDichChuyenTaiSan> userChuyenTaiSan =new HashMap<>();
                            userChuyenTaiSan.put(dateTimeID,giaoDichChuyenTaiSan);
                            documentReference.collection("duLieuHu").document("duLieuChuyen").collection("subCollectionNap").document()
                                    .set(userChuyenTaiSan).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            Toast.makeText(ChuyenHuActivity.this, "Chuyển tài sản thành công", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(ChuyenHuActivity.this,HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ChuyenHuActivity.this, "Chuyển tài sản thất bại", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                            Map<String, Object> updatedTienCacHuSauKhiGiaoDich = new HashMap<>();
                            Double tienHuSauKhiChuyen = currentItemSoTienSelectedSpinnerTaiSanChuyen - tienchuyen;
                            Double tienHuSauKhiNhan = currentItemSoTienSelectedSpinnerTaiSanNhan + tienchuyen;

                            String tenHuChuyen = "";
                            if(currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ đầu tư")){
                                tenHuChuyen = "HuDauTu";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ giáo dục")) {
                                tenHuChuyen = "HuGiaoDuc";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ hưỏng thụ" )) {
                                tenHuChuyen = "HuHuongThu";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ thiện tâm")) {
                                tenHuChuyen = "HuThienTam";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ thiết yếu")) {
                                tenHuChuyen = "HuThietYeu";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ tiết kiệm")) {
                                tenHuChuyen = "HuTietKiem";
                            }

                            String tenHuNhan = "";
                            if(currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ đầu tư")){
                                tenHuNhan = "HuDauTu";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ giáo dục")) {
                                tenHuNhan = "HuGiaoDuc";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ hưỏng thụ" )) {
                                tenHuNhan = "HuHuongThu";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ thiện tâm")) {
                                tenHuNhan = "HuThienTam";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ thiết yếu")) {
                                tenHuNhan = "HuThietYeu";
                            } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ tiết kiệm")) {
                                tenHuNhan = "HuTietKiem";
                            }

                            updatedTienCacHuSauKhiGiaoDich.put(tenHuChuyen +".soTien",tienHuSauKhiChuyen);
                            updatedTienCacHuSauKhiGiaoDich.put(tenHuNhan +".soTien",tienHuSauKhiNhan);
                            documentReference.collection("duLieuHu").document("duLieuTien").update(updatedTienCacHuSauKhiGiaoDich).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Toast.makeText(ChuyenHuActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(ChuyenHuActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }
                        else {
                            Toast.makeText(ChuyenHuActivity.this, "Số tiền chuyển phải nhỏ hơn hoặc bằng số tiền có trong hũ", Toast.LENGTH_SHORT).show();
                        }
//                        Map<String, Object> updatedTienCacHuSauKhiGiaoDich = new HashMap<>();
//                        Double tienHuSauKhiChuyen = currentItemSoTienSelectedSpinnerTaiSanChuyen - tienchuyen;
//                        Double tienHuSauKhiNhan = currentItemSoTienSelectedSpinnerTaiSanNhan + tienchuyen;
//
//                        String tenHuChuyen = "";
//                        if(currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ đầu tư")){
//                            tenHuChuyen = "HuDauTu";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ giáo dục")) {
//                            tenHuChuyen = "HuGiaoDuc";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ hưỏng thụ" )) {
//                            tenHuChuyen = "HuHuongThu";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ thiện tâm")) {
//                            tenHuChuyen = "HuThienTam";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ thiết yếu")) {
//                            tenHuChuyen = "HuThietYeu";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanChuyen.equals("Hũ tiết kiệm")) {
//                            tenHuChuyen = "HuTietKiem";
//                        }
//
//                        String tenHuNhan = "";
//                        if(currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ đầu tư")){
//                            tenHuNhan = "HuDauTu";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ giáo dục")) {
//                            tenHuNhan = "HuGiaoDuc";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ hưỏng thụ" )) {
//                            tenHuNhan = "HuHuongThu";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ thiện tâm")) {
//                            tenHuNhan = "HuThienTam";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ thiết yếu")) {
//                            tenHuNhan = "HuThietYeu";
//                        } else if (currentItemTenHuSelectedSpinnerTaiSanNhan.equals("Hũ tiết kiệm")) {
//                            tenHuNhan = "HuTietKiem";
//                        }
//
//                        updatedTienCacHuSauKhiGiaoDich.put(tenHuChuyen +".soTien",tienHuSauKhiChuyen);
//                        updatedTienCacHuSauKhiGiaoDich.put(tenHuNhan +".soTien",tienHuSauKhiNhan);
//                        documentReference.collection("duLieuHu").document("duLieuTien").update(updatedTienCacHuSauKhiGiaoDich).addOnSuccessListener(new OnSuccessListener<Void>() {
//                            @Override
//                            public void onSuccess(Void unused) {
//                                Toast.makeText(ChuyenHuActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
//                            }
//                        }).addOnFailureListener(new OnFailureListener() {
//                            @Override
//                            public void onFailure(@NonNull Exception e) {
//                                Toast.makeText(ChuyenHuActivity.this, "Lưu thất bại", Toast.LENGTH_SHORT).show();
//                            }
//                        });
                    }
                    else {
                        Toast.makeText(ChuyenHuActivity.this, "Hãy nhập số tiền", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(ChuyenHuActivity.this, "Không được chọn cùng một hũ", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        finish();
    }
}