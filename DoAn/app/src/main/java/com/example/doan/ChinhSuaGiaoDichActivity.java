package com.example.doan;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doan.entity.GiaoDichNap;
import com.example.doan.entity.LichSuGiaoDich;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

public class ChinhSuaGiaoDichActivity extends AppCompatActivity {
    BottomNavigationView transactionMenu;

    TextView txtHuy, txtLuu, txtLuong, txtFood, txtShopping, txtXang, txtPhongTro, txtDien;
    EditText editTextDate, editTextTag;
    Calendar calendar;
    // Init Firestore Database
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    // Init declare for Firebase Authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chinh_sua_giao_dich);
        FirebaseApp.initializeApp(this);
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();

        Log.d("onClick6: ", uid);
        EditText txtTienNap = findViewById(R.id.editTextTienThuNhap);
        EditText txtMoTa = findViewById(R.id.editTextMoTa);
        EditText txtngayNap = findViewById(R.id.editTextDate);


        txtTienNap.setInputType(InputType.TYPE_CLASS_NUMBER);


        txtTienNap.addTextChangedListener(new TextWatcher() {
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
                    txtTienNap.removeTextChangedListener(this);

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
                        txtTienNap.setText(formatted);
                        txtTienNap.setSelection(formatted.length());
                    } else {
                        current = "";
                    }

                    txtTienNap.addTextChangedListener(this);
                }
            }
        });
        txtLuu = findViewById(R.id.textViewLuu);


        txtHuy = findViewById(R.id.textViewHuy);
        txtHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        editTextDate = findViewById(R.id.editTextDate);
        // get current date in VietNam
        calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy EEEE", new Locale("vi", "VN"));
        String formattedDate = sdf.format(calendar.getTime());

        // editTextDate.setText(formattedDate);
        Intent data = getIntent();
        Double tienGiaoDich = data.getDoubleExtra("soTien", 0.0);
        Date date = (Date) data.getSerializableExtra("date");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy EEEE HH:mm:ss", new Locale("vi"));
        String formatte = dateFormat.format(date);
        NumberFormat currencyFormat = NumberFormat.getNumberInstance(new Locale("vi", "VN"));
        currencyFormat.setMaximumFractionDigits(0);
        currencyFormat.setGroupingUsed(true);
        String formattedAmount = currencyFormat.format(tienGiaoDich);
        String mota = data.getStringExtra("mota");
        editTextDate.setText(formatte);
        txtTienNap.setText(formattedAmount);
        txtMoTa.setText(mota);
        String jarName = data.getStringExtra("tenHu");
        Log.d("ten", jarName);


        //get date in Date picker
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                // Set the selected date to the Calendar instance
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, monthOfYear);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                Calendar selectedDate = Calendar.getInstance();
                selectedDate.set(year, monthOfYear, dayOfMonth);
                // Create a Calendar instance for the current date
                Calendar currentDate = Calendar.getInstance();

                // Calculate the start date of the 15-day period
                Calendar startDate = Calendar.getInstance();
                startDate.add(Calendar.DATE, -15);

                if (selectedDate.after(startDate) && selectedDate.before(currentDate) || selectedDate.equals(currentDate)) {
//                    // Format the date in Vietnam format
//                    String formattedDate = sdf.format(calendar.getTime());
//                    // Set the formatted date to the EditText field
//                    editTextDate.setText(formattedDate);
                    // Format the date with the desired time format

                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy EEEE HH:mm:ss", new Locale("vi"));
                    String formattedDate = sdf.format(calendar.getTime());
                    // Set the formatted date to the EditText field
                    editTextDate.setText(formattedDate);
                } else {
                    Toast.makeText(ChinhSuaGiaoDichActivity.this, "Vui lòng chọn một ngày trong vòng 15 ngày trước hoặc ngày hiện tại", Toast.LENGTH_SHORT).show();
                }

            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });


        // input nhãn

        txtLuong = findViewById(R.id.textViewTagLuong);
        txtFood = findViewById(R.id.textViewTagFood);
        txtShopping = findViewById(R.id.textViewTagShopping);
        txtXang = findViewById(R.id.textViewTagXang);
        txtPhongTro = findViewById(R.id.textViewTagPhongTro);
        txtDien = findViewById(R.id.textViewTagDien);

        txtDien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = txtMoTa.getText().toString();
                String dien = txtDien.getText().toString();
                txtMoTa.setText(currentText + dien + " ");
            }
        });

        txtLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = txtMoTa.getText().toString();
                String luong = txtLuong.getText().toString();
                txtMoTa.setText(currentText + luong + " ");
            }
        });

        txtFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = txtMoTa.getText().toString();
                String food = txtFood.getText().toString();
                txtMoTa.setText(currentText + food + " ");
            }
        });

        txtShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = txtMoTa.getText().toString();
                String shopping = txtShopping.getText().toString();
                txtMoTa.setText(currentText + shopping + " ");
            }
        });

        txtXang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = txtMoTa.getText().toString();
                String xang = txtXang.getText().toString();
                txtMoTa.setText(currentText + xang + " ");
            }
        });

        txtPhongTro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = txtMoTa.getText().toString();
                String pt = txtPhongTro.getText().toString();


                txtMoTa.setText(currentText + pt + " ");
            }
        });

        txtDien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = txtMoTa.getText().toString();
                String dien = txtDien.getText().toString();
                txtMoTa.setText(currentText + dien + " ");
            }
        });
        txtLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String tien = txtTienNap.getText().toString().replaceAll("[^\\d]", "");
                String moTaText = txtMoTa.getText().toString();
                String ngay = txtngayNap.getText().toString();
                Double tienNap = Double.parseDouble(tien);
                Intent intent = getIntent();
                Date date1 = (Date) data.getSerializableExtra("date");
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(date1);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                Date updatedDate = calendar.getTime();
                Log.d("onClick1: ", String.valueOf(updatedDate));
                String uuid = intent.getStringExtra("id");

                String tenHu = intent.getStringExtra("tenHu");
                String loai = intent.getStringExtra("loaiGiaoDich");
                // Create a SimpleDateFormat instance with the desired input format
                SimpleDateFormat inputFormat = new SimpleDateFormat("dd/MM/yyyy EEEE hh:mm:ss", new Locale("vi"));
                Map<String, Object> updatedUserMap = new HashMap<>();
                Map<String, Object> updatedThuNhap = new HashMap<>();
                // Parse the date string into a Date object
                Date date = null;
                try {
                    date = inputFormat.parse(ngay);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                //GiaoDichNap giaoDichNap = new GiaoDichNap(uuid,tenHu,tienNap,date,moTa,loai);
                if (!date.equals(date1)) {
                    updatedUserMap.put(uuid + ".ngayNap", date);
                    if (loai.equals("Thu nhập")) {
                        updatedThuNhap.put(uuid + ".ngayNap", date);

                    } else if (loai.equals("Chi tiêu")) {
                        updatedThuNhap.put(uuid + ".ngayRut", date);
                    }
                }
                if (!mota.equals(moTaText)) {
                    updatedUserMap.put(uuid + ".moTa", moTaText);
                    updatedThuNhap.put(uuid + ".moTa", moTaText);
                    Log.d("mota ", "true");
                }
                if (!tienNap.equals(tienGiaoDich)) {
                    if (loai.equals("Thu nhập")) {
                        DocumentReference documentReference1 = db.collection("users").document(uid);
                        documentReference1.collection("duLieuHu").document("duLieuTien").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            boolean isUpdated = false;

                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.w(TAG, "Listen failed.", error);
                                    return;
                                }
                                if (value != null && value.exists()) {
                                    String tenHu = null;
                                    if (jarName.equals("Hũ thiết yếu")) {
                                        tenHu = "HuThietYeu";
                                    } else if (jarName.equals("Hũ giáo dục")) {
                                        tenHu = "HuGiaoDuc";
                                    } else if (jarName.equals("Hũ tiết kiệm")) {
                                        tenHu = "HuTietKiem";
                                    } else if (jarName.equals("Hũ đầu tư")) {
                                        tenHu = "HuDauTu";
                                    } else if (jarName.equals("Hũ hưởng thụ")) {
                                        tenHu = "HuHuongThu";
                                    } else if (jarName.equals("Hũ thiện tâm")) {
                                        tenHu = "HuThienTam";
                                    }
                                    Map<String, Object> mapObject = value.getData();
                                    Map<String, Object> hu = (Map<String, Object>) mapObject.get(tenHu);
                                    Double currentSoTien = (Double) hu.get("soTien");
                                    Log.d("onEvent: ", String.valueOf(currentSoTien));
                                    Double tienUpdate = currentSoTien - tienGiaoDich + tienNap;
                                    if (!isUpdated) {
                                        isUpdated = true;
                                        Map<String, Object> updatedTien = new HashMap<>();
                                        updatedTien.put(tenHu + ".soTien", tienUpdate);
                                        CollectionReference collectionReference3 = db.collection("users").document(uid).collection("duLieuHu");
                                        DocumentReference documentReference3 = collectionReference3.document("duLieuTien");
                                        documentReference3.update(updatedTien).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(ChinhSuaGiaoDichActivity.this, "Cập nhât số tiền thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChinhSuaGiaoDichActivity.this, "Cập nhât số tiền không thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                        updatedThuNhap.put(uuid + ".tienNap", tienNap);

                    } else if (loai.equals("Chi tiêu")) {
                        DocumentReference documentReference1 = db.collection("users").document(uid);
                        documentReference1.collection("duLieuHu").document("duLieuTien").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            boolean isUpdated = false;

                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.w(TAG, "Listen failed.", error);
                                    return;
                                }
                                if (value != null && value.exists()) {
                                    String tenHu = null;
                                    if (jarName.equals("Hũ thiết yếu")) {
                                        tenHu = "HuThietYeu";
                                    } else if (jarName.equals("Hũ giáo dục")) {
                                        tenHu = "HuGiaoDuc";
                                    } else if (jarName.equals("Hũ tiết kiếm")) {
                                        tenHu = "HuTietKiem";
                                    } else if (jarName.equals("Hũ đầu tư")) {
                                        tenHu = "HuDauTu";
                                    } else if (jarName.equals("Hũ hưởng thụ")) {
                                        tenHu = "HuHuongThu";
                                    } else if (jarName.equals("Hũ thiện tâm")) {
                                        tenHu = "HuThienTam";
                                    }
                                    Map<String, Object> mapObject = value.getData();
                                    Map<String, Object> hu = (Map<String, Object>) mapObject.get(tenHu);
                                    Double currentSoTien = (Double) hu.get("soTien");
                                    Log.d("onEvent: ", String.valueOf(currentSoTien));
                                    Double tienUpdate = currentSoTien + tienGiaoDich - tienNap;
                                    if (!isUpdated) {
                                        isUpdated = true;
                                        Map<String, Object> updatedTien = new HashMap<>();
                                        updatedTien.put(tenHu + ".soTien", tienUpdate);
                                        CollectionReference collectionReference3 = db.collection("users").document(uid).collection("duLieuHu");
                                        DocumentReference documentReference3 = collectionReference3.document("duLieuTien");
                                        documentReference3.update(updatedTien).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void unused) {
                                                Toast.makeText(ChinhSuaGiaoDichActivity.this, "Cập nhât số tiền thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(ChinhSuaGiaoDichActivity.this, "Cập nhât số tiền không thành công", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                    }
                                }
                            }
                        });
                        updatedThuNhap.put(uuid + ".tienRut", tienNap);
                    }
                }
                if (loai.equals("Chi tiêu")) {
                    DocumentReference documentReference3 = db.collection("users").document(uid);
                    documentReference3.collection("duLieuHu").document("duLieuRut").collection("subCollectionNap").document(uuid)
                            .update(updatedThuNhap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                } else if (loai.equals("Thu nhập")) {
                    updatedThuNhap.put(uuid + ".tienNap", tienNap);
                    DocumentReference documentReference2 = db.collection("users").document(uid);
                    documentReference2.collection("duLieuHu").document("duLieuNap").collection("subCollectionNap").document(uuid)
                            .update(updatedThuNhap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {

                                }
                            });
                }
                updatedUserMap.put(uuid + ".tienNap", tienNap);
                DocumentReference documentReference2 = db.collection("users").document(uid);
                documentReference2.collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich").document(uuid)
                        .update(updatedUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "New document added to lichSuGiaoDich collection");
                                Intent intent = getIntent();
                                int screen = intent.getIntExtra("screen", 0);
                                if (screen == 5) {
                                    Intent intent1 = new Intent(ChinhSuaGiaoDichActivity.this, HuDauTuActivity.class);
                                    startActivity(intent1);
                                } else if (screen == 2) {
                                    Intent intent1 = new Intent(ChinhSuaGiaoDichActivity.this, HuGiaoDucActivity.class);
                                    startActivity(intent1);
                                } else if (screen == 4) {
                                    Intent intent1 = new Intent(ChinhSuaGiaoDichActivity.this, HuHuongThuActivity.class);
                                    startActivity(intent1);
                                } else if (screen == 6) {
                                    Intent intent1 = new Intent(ChinhSuaGiaoDichActivity.this, HuThienTamActivity.class);
                                    startActivity(intent1);
                                } else if (screen == 1) {
                                    Intent intent1 = new Intent(ChinhSuaGiaoDichActivity.this, HuThietYeuActivity.class);
                                    startActivity(intent1);
                                } else if (screen == 3) {
                                    Intent intent1 = new Intent(ChinhSuaGiaoDichActivity.this, HuTietKiemActivity.class);
                                    startActivity(intent1);
                                }else if (screen == 7) {
                                    Intent intent1 = new Intent(ChinhSuaGiaoDichActivity.this, LichSuGiaoDichActivity.class);
                                    startActivity(intent1);
                                }


                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error adding document to  lichSuGiaoDich collection: " + e.getMessage());
                            }
                        });

            }
        });
    }
}