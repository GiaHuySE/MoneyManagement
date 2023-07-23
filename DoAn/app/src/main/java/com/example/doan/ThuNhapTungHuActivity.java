package com.example.doan;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
import java.util.UUID;

public class ThuNhapTungHuActivity extends AppCompatActivity {
    TextView txtHuy, txtLuu, txtLuong, txtFood, txtShopping, txtXang, txtPhongTro, txtDien,txtView5;
    EditText editTextDate, editTextTag;
    Calendar calendar;
    // Init Firestore Database
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    // Init declare for Firebase Authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thu_nhap_tung_hu);
        getSupportActionBar().hide();

        FirebaseApp.initializeApp(this);


        EditText txtTienNap = findViewById(R.id.editTextTienThuNhap);
        EditText txtMoTa = findViewById(R.id.editTextMoTa);
        EditText txtngayNap = findViewById(R.id.editTextDate);
        txtView5 = findViewById(R.id.textView5);
        Intent intent = getIntent();
        String ten = intent.getStringExtra("tenHu");
        txtView5.setText(ten);
        Log.d(TAG, "onCreate: "+ten);


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
//                Intent intent = new Intent(ThuNhapTungHuActivity.this, HomeActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        editTextDate = findViewById(R.id.editTextDate);
        // get current date in VietNam
        calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy EEEE", new Locale("vi", "VN"));
        String formattedDate = sdf.format(calendar.getTime());

        editTextDate.setText(formattedDate);

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
                    // Format the date in Vietnam format
                    String formattedDate = sdf.format(calendar.getTime());
                    // Set the formatted date to the EditText field
                    Log.d("ngày", formattedDate);
                    editTextDate.setText(formattedDate);
                } else {
                    Toast.makeText(ThuNhapTungHuActivity.this, "Vui lòng chọn một ngày trong vòng 15 ngày trước hoặc ngày hiện tại", Toast.LENGTH_SHORT).show();
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
        String jarName = null;
        if (ten.equals("Hũ đầu tư")) {
            jarName = "HuDauTu";
        } else if (ten.equals("Hũ giáo dục")) {
            jarName = "HuGiaoDuc";
        } else if (ten.equals("Hũ hưởng thụ")) {
            jarName = "HuHuongThu";
        } else if (ten.equals("Hũ thiện tâm")) {
            jarName = "HuThienTam";
        } else if (ten.equals("Hũ thiết yếu")) {
            jarName = "HuThietYeu";
        } else if (ten.equals("Hũ tiết kiệm")) {
            jarName = "HuTietKiem";
        }
        Log.d(TAG, "jar Name: "+jarName);
        txtLuu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                try {
                    String tien = txtTienNap.getText().toString().replaceAll("[^\\d]", "");
                    String moTa = txtMoTa.getText().toString();
                    String ngay = txtngayNap.getText().toString();
                    Double tienNap = Double.parseDouble(tien);
                    Date ngayNap = null;
                    ngayNap = new SimpleDateFormat("dd/MM/yyyy").parse(ngay);

                    FirebaseUser user = mAuth.getCurrentUser();
                    String uid = user.getUid();

                    Date dNow = new Date();
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    String currentTime = timeFormat.format(calendar.getTime());


                    String selectedDateString = txtngayNap.getText().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy EEEE", new Locale("vi"));
                    Date selectedDate = dateFormat.parse(selectedDateString);

                    // Combine the date and time
                    String dateTimeString = selectedDateString + " " + currentTime;
                    SimpleDateFormat combinedFormat = new SimpleDateFormat("dd/MM/yyyy EEEE HH:mm:ss", new Locale("vi"));
                    Date combinedDate = combinedFormat.parse(dateTimeString);
                    createGiaoDichNap(combinedDate,ten,tienNap,"Thu nhập",moTa);

                    DocumentReference documentReference1 = firestoreDB.collection("users").document(uid);

                    documentReference1.collection("duLieuHu").document("duLieuTien").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                        String jarName = "";
                        boolean isUpdated = false;
                        @Override
                        public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Listen failed.", error);
                                return;
                            }
                            if (value != null && value.exists()) {
                                if (ten.equals("Hũ đầu tư")) {
                                    jarName = "HuDauTu";
                                } if (ten.equals("Hũ giáo dục")) {
                                    jarName = "HuGiaoDuc";
                                } if (ten.equals("Hũ hưởng thụ")) {
                                    jarName = "HuHuongThu";
                                }  if (ten.equals("Hũ thiện tâm")) {
                                    jarName = "HuThienTam";
                                }  if (ten.equals("Hũ thiết yếu")) {
                                    jarName = "HuThietYeu";
                                }  if (ten.equals("Hũ tiết kiệm")) {
                                    jarName = "HuTietKiem";
                                }
                                Log.d(TAG, "jar Name: "+jarName);
                                Map<String, Object> mapObject = value.getData();
                                Map<String, Object> huData = (Map<String, Object>) mapObject.get(jarName);
                                Double currentSoTien = (Double) huData.get("soTien");
                                Double upDateSoTien = tienNap + currentSoTien;
                                Map<String, Object> updatedUserMap = new HashMap<>();
                                if (!isUpdated) {
                                    isUpdated = true;
                                    updatedUserMap.put(jarName+".soTien", upDateSoTien);
                                    documentReference1.collection("duLieuHu").document("duLieuTien").update(updatedUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG", "Updated");
                                            Toast.makeText(ThuNhapTungHuActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(IncomeActivity.this, HomeActivity.class);
//                                            startActivity(intent);
                                            getIntent().removeExtra("tenHu");
                                            if (ten.equals("Hũ đầu tư")) {
                                                Intent intent1 = new Intent(ThuNhapTungHuActivity.this,HuDauTuActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ giáo dục")) {
                                                Intent intent1 = new Intent(ThuNhapTungHuActivity.this,HuGiaoDucActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ hưởng thụ")) {
                                                Intent intent1 = new Intent(ThuNhapTungHuActivity.this,HuHuongThuActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ thiện tâm")) {
                                                Intent intent1 = new Intent(ThuNhapTungHuActivity.this,HuThienTamActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ thiết yếu")) {
                                                Intent intent1 = new Intent(ThuNhapTungHuActivity.this,HuThietYeuActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ tiết kiệm")) {
                                                Intent intent1 = new Intent(ThuNhapTungHuActivity.this,HuTietKiemActivity.class);
                                                startActivity(intent1);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ThuNhapTungHuActivity.this, "Lưu không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    private void createGiaoDichNap(Date date, String jarName, Double soTien, String loaiGiaoDich, String moTa) {
        UUID uuid = UUID.randomUUID();
        GiaoDichNap giaoDichNap = new GiaoDichNap();
        giaoDichNap.setUuid(String.valueOf(uuid));
        giaoDichNap.setNgayNap(date);
        giaoDichNap.setTenHu(jarName);
        giaoDichNap.setLoaiGiaoDich(loaiGiaoDich);
        giaoDichNap.setTienNap(soTien);
        giaoDichNap.setMoTa(moTa);
        Map<String, GiaoDichNap> giaoDichNapMap = new HashMap<>();
        giaoDichNapMap.put(String.valueOf(uuid), giaoDichNap);
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        DocumentReference documentReference2 = firestoreDB.collection("users").document(uid);
        documentReference2.collection("duLieuHu").document("duLieuNap")
                .collection("subCollectionNap").document(String.valueOf(uuid)).set(giaoDichNapMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Log.d("TAG", "New document added to duLieuNap collection");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Error adding document to duLieuNap collection: " + e.getMessage());
                    }
                });
        DocumentReference documentReference3 = firestoreDB.collection("users").document(uid);
        documentReference2.collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich").document(String.valueOf(uuid))
                .set(giaoDichNapMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Log.d("TAG", "New document added to lichSuGiaoDich collection");
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("TAG", "Error adding document to  lichSuGiaoDich collection: " + e.getMessage());
                    }
                });
    }
}