package com.example.doan;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doan.entity.GiaoDichNap;
import com.example.doan.entity.Hu;
import com.example.doan.entity.LichSuGiaoDich;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.FirebaseApp;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;

public class IncomeActivity extends AppCompatActivity {
    BottomNavigationView transactionMenu;
    TextView txtHuy, txtLuu, txtLuong, txtFood, txtShopping, txtXang, txtPhongTro, txtDien;
    EditText editTextDate, editTextTag;
    Calendar calendar;
    Button btuChonHu;
    // Init Firestore Database
    FirebaseFirestore firestoreDB = FirebaseFirestore.getInstance();
    // Init declare for Firebase Authentication
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
//    FirebaseUser currentUser = mAuth.getCurrentUser();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_income);
        // Init Firebase App
        FirebaseApp.initializeApp(this);


        EditText txtTienNap = findViewById(R.id.editTextTienThuNhap);
        EditText txtMoTa = findViewById(R.id.editTextMoTa);
        EditText txtngayNap = findViewById(R.id.editTextDate);
        btuChonHu = findViewById(R.id.btnChonHu);


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
                Intent intent = new Intent(IncomeActivity.this, HomeActivity.class);
                startActivity(intent);
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
                    Toast.makeText(IncomeActivity.this, "Vui lòng chọn một ngày trong vòng 15 ngày trước hoặc ngày hiện tại", Toast.LENGTH_SHORT).show();
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
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View v) {

                TextView textViewHu = findViewById(R.id.txtViewHu);
                String getTenHu = (String) textViewHu.getText();
                String tien = txtTienNap.getText().toString().replaceAll("[^\\d]", "");
                String moTa = txtMoTa.getText().toString();
                String ngay = txtngayNap.getText().toString();
                Double tienNap = Double.parseDouble(tien);
                Date ngayNap = null;
                try {
                    ngayNap = new SimpleDateFormat("dd/MM/yyyy").parse(ngay);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                FirebaseUser user = mAuth.getCurrentUser();
                String uid = user.getUid();

                if(getTenHu.equals("Tất cả các hũ")){
                    try {
                        GiaoDichNap giaoDichNap = new GiaoDichNap();
                        giaoDichNap.setTienNap(tienNap);
                        giaoDichNap.setMoTa(moTa);
                        giaoDichNap.setNgayNap(ngayNap);

                        Date dNow = new Date();
                        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");

                        int ptramThietYeu = getIntent().getIntExtra("ptramThietYeu", 0);
                        int ptramGiaoDuc = getIntent().getIntExtra("ptramGiaoDuc", 0);
                        int ptramTietKiem = getIntent().getIntExtra("ptramTietKiem", 0);
                        int ptramDauTu = getIntent().getIntExtra("ptramDauTu", 0);
                        int ptramHuongThu = getIntent().getIntExtra("ptramHuongThu", 0);
                        int ptramThienTam = getIntent().getIntExtra("ptramThienTam", 0);
                        Double tienHuThietYeu = (tienNap / 100) * ptramThietYeu;
                        Double tienHuGiaoDuc = (tienNap / 100) * ptramGiaoDuc;
                        Double tienHuTietKiem = (tienNap / 100) * ptramTietKiem;
                        Double tienHuHuongThu = (tienNap / 100) * ptramHuongThu;
                        Double tienHuDauTu = (tienNap / 100) * ptramDauTu;
                        Double tienHuThienTam = (tienNap / 100) * ptramThienTam;

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

                        createGiaoDichNap(combinedDate, "Hũ thiết yếu", tienHuThietYeu, "Thu nhập", moTa);
                        createGiaoDichNap(combinedDate, "Hũ giáo dục", tienHuGiaoDuc, "Thu nhập", moTa);
                        createGiaoDichNap(combinedDate, "Hũ tiết kiệm", tienHuTietKiem, "Thu nhập", moTa);
                        createGiaoDichNap(combinedDate, "Hũ đầu tư", tienHuDauTu, "Thu nhập", moTa);
                        createGiaoDichNap(combinedDate, "Hũ hưởng thụ", tienHuHuongThu, "Thu nhập", moTa);
                        createGiaoDichNap(combinedDate, "Hũ thiện tâm", tienHuThienTam, "Thu nhập", moTa);


                        DocumentReference documentReference1 = firestoreDB.collection("users").document(uid);

                        documentReference1.collection("duLieuHu").document("duLieuTien").addSnapshotListener(new EventListener<DocumentSnapshot>() {
                            private Double currentSoTienTietKiem;
                            private Double currentSoTienThienTam;
                            private Double currentSoTienHuongThu;
                            private Double currentSoTienGiaoDuc;
                            private Double currentSoTienDauTu;
                            private Double currentSoTienThietYeu;
                            long tyLeThietYeu, tyLeGiaoDuc, tyLeTietKiem, tyLeDautu, tyLehuongThu, tyLeThienTam;
                            boolean isUpdated = false;

                            @Override
                            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                                if (error != null) {
                                    Log.w(TAG, "Listen failed.", error);
                                    return;
                                }
                                if (value != null && value.exists()) {
                                    Map<String, Object> mapObject = value.getData();

                                    Object obj = mapObject.get("HuThietYeu");
                                    Map<String, Object> huDauTu = (Map<String, Object>) mapObject.get("HuDauTu");
                                    Map<String, Object> huGiaoDuc = (Map<String, Object>) mapObject.get("HuGiaoDuc");
                                    Map<String, Object> huHuongThu = (Map<String, Object>) mapObject.get("HuHuongThu");
                                    Map<String, Object> huThienTam = (Map<String, Object>) mapObject.get("HuThienTam");
                                    Map<String, Object> huThietYeu = (Map<String, Object>) mapObject.get("HuThietYeu");
                                    Map<String, Object> huTietKiem = (Map<String, Object>) mapObject.get("HuTietKiem");


                                    currentSoTienThietYeu = (Double) huThietYeu.get("soTien");
                                    currentSoTienDauTu = (Double) huDauTu.get("soTien");
                                    currentSoTienGiaoDuc = (Double) huGiaoDuc.get("soTien");
                                    currentSoTienHuongThu = (Double) huHuongThu.get("soTien");
                                    currentSoTienThienTam = (Double) huThienTam.get("soTien");
                                    currentSoTienTietKiem = (Double) huTietKiem.get("soTien");
                                    Log.d("tien thien tam ", String.valueOf(currentSoTienThienTam));

                                    tyLeThietYeu = (long) huThietYeu.get(("tyLe"));
                                    tyLeDautu = (long) huDauTu.get("tyLe");
                                    tyLeGiaoDuc = (long) huGiaoDuc.get(("tyLe"));
                                    tyLehuongThu = (long) huHuongThu.get(("tyLe"));
                                    tyLeTietKiem = (long) huTietKiem.get(("tyLe"));
                                    tyLeThienTam = (long) huThienTam.get(("tyLe"));
                                    Log.d("Tỉ Lệ  ", String.valueOf(tyLeThienTam));

                                } else {
                                    Log.d("TAG_DATA_NULL", "Current data: null");
                                }

                                Map<String, Object> updatedUserMap = new HashMap<>();
                                Double tienHuThietYeu = (tienNap / 100) * tyLeThietYeu;
                                Double tienHuGiaoDuc = (tienNap / 100) * tyLeGiaoDuc;
                                Double tienHuTietKiem = (tienNap / 100) * tyLeTietKiem;
                                Double tienHuHuongThu = (tienNap / 100) * tyLehuongThu;
                                Double tienHuDauTu = (tienNap / 100) * tyLeDautu;
                                Double tienHuThienTam = (tienNap / 100) * tyLeThienTam;
                                Log.d("Số tiền thiện tâm: ", String.valueOf(tienHuThienTam));
                                double updateTienThietYeu = currentSoTienThietYeu + tienHuThietYeu;
                                double updateTienGiaoDuc = currentSoTienGiaoDuc + tienHuGiaoDuc;
                                double updateTienTietKiem = currentSoTienTietKiem + tienHuTietKiem;
                                double updateTienHuongThu = currentSoTienHuongThu + tienHuHuongThu;
                                double updateTienDauTu = currentSoTienDauTu + tienHuDauTu;
                                double updateTienThienTam = currentSoTienThienTam + tienHuThienTam;

                                String ngay = txtngayNap.getText().toString();
                                if (!isUpdated) {
                                    isUpdated = true;
                                    updatedUserMap.put("HuThietYeu.soTien", updateTienThietYeu);
                                    updatedUserMap.put("HuGiaoDuc.soTien", updateTienGiaoDuc);
                                    updatedUserMap.put("HuTietKiem.soTien", updateTienTietKiem);
                                    updatedUserMap.put("HuHuongThu.soTien", updateTienHuongThu);
                                    updatedUserMap.put("HuDauTu.soTien", updateTienDauTu);
                                    updatedUserMap.put("HuThienTam.soTien", updateTienThienTam);
                                    documentReference1.collection("duLieuHu").document("duLieuTien").update(updatedUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG", "Updated");
                                            Toast.makeText(IncomeActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                                            Intent intent = new Intent(IncomeActivity.this, HomeActivity.class);
                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(IncomeActivity.this, "Lưu không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        });

                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }
                }
                else {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    String currentTime = timeFormat.format(calendar.getTime());


                    String selectedDateString = txtngayNap.getText().toString();
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy EEEE", new Locale("vi"));
                    Date selectedDate ;
                    try {
                        selectedDate = dateFormat.parse(selectedDateString);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

// Combine the date and time
                    String dateTimeString = selectedDateString + " " + currentTime;
                    SimpleDateFormat combinedFormat = new SimpleDateFormat("dd/MM/yyyy EEEE HH:mm:ss", new Locale("vi"));
                    Date combinedDate;
                    try {
                        combinedDate = combinedFormat.parse(dateTimeString);
                    } catch (ParseException e) {
                        throw new RuntimeException(e);
                    }

                    createGiaoDichNap(combinedDate,getTenHu,tienNap,"Thu nhập",moTa);

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
                                if (getTenHu.equals("Hũ đầu tư")) {
                                    jarName = "HuDauTu";
                                } if (getTenHu.equals("Hũ giáo dục")) {
                                    jarName = "HuGiaoDuc";
                                } if (getTenHu.equals("Hũ hưởng thụ")) {
                                    jarName = "HuHuongThu";
                                }  if (getTenHu.equals("Hũ thiện tâm")) {
                                    jarName = "HuThienTam";
                                }  if (getTenHu.equals("Hũ thiết yếu")) {
                                    jarName = "HuThietYeu";
                                }  if (getTenHu.equals("Hũ tiết kiệm")) {
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
                                            Toast.makeText(IncomeActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
//                                            Intent intent = new Intent(IncomeActivity.this, HomeActivity.class);
//                                            startActivity(intent);
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                           // Toast.makeText(ThuNhapTungHuActivity.this, "Lưu không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
        btuChonHu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showCheckBox();
            }
        });
    }
    private  void creatLichSuGiaoDich(Date date,String jarNamme,Double soTien,String loaiGiaoDich){
        UUID uuid = UUID.randomUUID();
        LichSuGiaoDich lichSuGiaoDich = new LichSuGiaoDich();
        lichSuGiaoDich.setId(String.valueOf(uuid));
        lichSuGiaoDich.setThoiGian(date);
        lichSuGiaoDich.setSoTien(soTien);
        lichSuGiaoDich.setJarName(jarNamme);
        lichSuGiaoDich.setLoaiGiaoDich(loaiGiaoDich);
        Map<String, LichSuGiaoDich> giaoDich = new HashMap<>();
        giaoDich.put(String.valueOf(uuid), lichSuGiaoDich);
        FirebaseUser user = mAuth.getCurrentUser();
        String uid = user.getUid();
        DocumentReference documentReference2 = firestoreDB.collection("users").document(uid);
        documentReference2.collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich").document(String.valueOf(uuid))
                .set(giaoDich).addOnSuccessListener(new OnSuccessListener<Void>() {
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

    private void createGiaoDichNap(Date date,String jarName,Double soTien,String loaiGiaoDich ,String moTa ){
        UUID uuid = UUID.randomUUID();
        GiaoDichNap giaoDichNap = new GiaoDichNap();
        giaoDichNap.setUuid(String.valueOf(uuid));
        giaoDichNap.setNgayNap(date);
        giaoDichNap.setTenHu(jarName);
        giaoDichNap.setLoaiGiaoDich(loaiGiaoDich);
        giaoDichNap.setTienNap(soTien);
        giaoDichNap.setMoTa(moTa);
        Map<String,GiaoDichNap> giaoDichNapMap =new HashMap<>();
        giaoDichNapMap.put(String.valueOf(uuid),giaoDichNap);
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
    private void showCheckBox(){
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(IncomeActivity.this);
        bottomSheetDialog.setContentView(R.layout.check_box_dialog);
        TextView titleTextView = bottomSheetDialog.findViewById(R.id.title_textview);
        TextView textViewHu = findViewById(R.id.txtViewHu);
        Button btnXacNhan = bottomSheetDialog.findViewById(R.id.btnXacNhan);
        RadioButton radioButtonTatCa,radioButtonThietYeu,radioButtonGiaoDuc,radioButtonTietKiem,radioButtonDauTu,radioButtonHuongThu,radioButtonThienTam;
        radioButtonTatCa = bottomSheetDialog.findViewById(R.id.radioTatCa);
        radioButtonThietYeu  = bottomSheetDialog.findViewById(R.id.radioThietYeu);
        radioButtonGiaoDuc = bottomSheetDialog.findViewById(R.id.radioGiaoDuc);
        radioButtonTietKiem = bottomSheetDialog.findViewById(R.id.radioTietKiem);
        radioButtonDauTu= bottomSheetDialog.findViewById(R.id.radioDauTu);
        radioButtonHuongThu = bottomSheetDialog.findViewById(R.id.radioHuongThu);
        radioButtonThienTam = bottomSheetDialog.findViewById(R.id.radioThienTam);
        titleTextView.setText("Chọn hũ");
        bottomSheetDialog.show();

        radioButtonTatCa.setChecked(true);
        btnXacNhan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButtonTatCa.isChecked()){
                    textViewHu.setText("Tất cả các hũ");
                } else if (radioButtonThietYeu.isChecked()) {
                    textViewHu.setText("Hũ thiết yếu");
                } else if (radioButtonGiaoDuc.isChecked()) {
                    textViewHu.setText("Hũ giáo dục");
                } else if (radioButtonTietKiem.isChecked()) {
                    textViewHu.setText("Hũ tiết kiệm");
                } else if (radioButtonDauTu.isChecked()) {
                    textViewHu.setText("Hũ đầu tư");
                }else if(radioButtonHuongThu.isChecked()){
                    textViewHu.setText("Hũ hưởng thụ");
                } else if (radioButtonThienTam.isChecked()) {
                    textViewHu.setText("Hũ thiện tâm");
                }
                bottomSheetDialog.dismiss();
            }
        });

    }
}