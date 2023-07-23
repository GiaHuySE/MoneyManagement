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
import com.example.doan.entity.GiaoDichRut;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
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

public class ChiTieuTungHuActivity extends AppCompatActivity {
    private EditText editTextDate,  txtTienChiTieu, editTextMoTa;
    private TextView txtHuy, txtLuu, txtLuong, txtFood, txtShopping, txtXang, txtPhongTro, txtDien,txtTenHu;
    private Calendar calendar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseFirestore firestore;
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_chi_tieu_tung_hu);
        firestore = FirebaseFirestore.getInstance();
        txtTienChiTieu = findViewById(R.id.editTextTienChuyen);
        editTextMoTa = findViewById(R.id.editTextMoTa);
        txtHuy = findViewById(R.id.textViewHuy);
        editTextDate = findViewById(R.id.editTextDate);
        // input nhãn

        txtLuong = findViewById(R.id.textViewTagLuong);
        txtFood = findViewById(R.id.textViewTagFood);
        txtShopping = findViewById(R.id.textViewTagShopping);
        txtXang = findViewById(R.id.textViewTagXang);
        txtPhongTro = findViewById(R.id.textViewTagPhongTro);
        txtDien = findViewById(R.id.textViewTagDien);
        txtTenHu = findViewById(R.id.textView5);
        txtHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                Intent intent = new Intent(ChiTieuTungHuActivity.this, HomeActivity.class);
//                startActivity(intent);
                finish();
            }
        });

        Intent intent =getIntent();
        String ten = intent.getStringExtra("tenHu");
        if(ten.isEmpty()){
            Log.d(TAG, "onCreate: "+"true");

        }else {
            Log.d(TAG, "onCreate: "+"false");
            txtTenHu.setText(ten);
        }
        txtTienChiTieu.setInputType(InputType.TYPE_CLASS_NUMBER);
        txtTienChiTieu.addTextChangedListener(new TextWatcher() {
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
                    txtTienChiTieu.removeTextChangedListener(this);

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
                        txtTienChiTieu.setText(formatted);
                        txtTienChiTieu.setSelection(formatted.length());
                    } else {
                        current = "";
                    }

                    txtTienChiTieu.addTextChangedListener(this);
                }
            }
        });

        // get current date in VietNam
        calendar = Calendar.getInstance(TimeZone.getTimeZone("Asia/Ho_Chi_Minh"));
        int day = calendar.get(Calendar.DAY_OF_MONTH);
        int month = calendar.get(Calendar.MONTH) + 1;
        int year = calendar.get(Calendar.YEAR);
        //String formattedDate = String.format("%02d/%02d/%04d EEEE", day, month, year);
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

                if(selectedDate.after(startDate) && selectedDate.before(currentDate) || selectedDate.equals(currentDate)){
                    // Format the date in Vietnam format
                    String formattedDate = sdf.format(calendar.getTime());
                    // Set the formatted date to the EditText field
                    editTextDate.setText(formattedDate);
                }
                else{
                    Toast.makeText(ChiTieuTungHuActivity.this, "Vui lòng chọn một ngày trong vòng 15 ngày trước hoặc ngày hiện tại", Toast.LENGTH_SHORT).show();
                }
            }
        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        editTextDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
            }
        });

        txtDien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = editTextMoTa.getText().toString();
                String dien = txtDien.getText().toString();
                editTextMoTa.setText(currentText + dien + " ");
            }
        });

        txtLuong.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = editTextMoTa.getText().toString();
                String luong = txtLuong.getText().toString();
                editTextMoTa.setText(currentText + luong + " ");
            }
        });

        txtFood.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = editTextMoTa.getText().toString();
                String food = txtFood.getText().toString();
                editTextMoTa.setText(currentText + food + " ");
            }
        });

        txtShopping.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = editTextMoTa.getText().toString();
                String shopping = txtShopping.getText().toString();
                editTextMoTa.setText(currentText + shopping + " ");
            }
        });

        txtXang.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = editTextMoTa.getText().toString();
                String xang = txtXang.getText().toString();
                editTextMoTa.setText(currentText + xang + " ");
            }
        });

        txtPhongTro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = editTextMoTa.getText().toString();
                String pt = txtPhongTro.getText().toString();
                editTextMoTa.setText(currentText + pt + " ");
            }
        });

        txtDien.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String currentText = editTextMoTa.getText().toString();
                String dien = txtDien.getText().toString();
                editTextMoTa.setText(currentText + dien + " ");
            }
        });

        String uid = currentUser.getUid();
        txtLuu = findViewById(R.id.textViewLuu);
        txtLuu.setOnClickListener(new View.OnClickListener() {
            private double tienRut;
            @Override
            public void onClick(View v) {
                String tien = txtTienChiTieu.getText().toString().replaceAll("[^\\d]", "");
                String ngay = editTextDate.getText().toString();
                String moTa = editTextMoTa.getText().toString();
                tienRut = Double.parseDouble(tien);
                Date ngayRut;
                try {
                    ngayRut = new SimpleDateFormat("dd/MM/yyyy").parse(ngay);
                } catch (ParseException e) {
                    throw new RuntimeException(e);
                }
                if (!tien.equals("0")) {
                    Calendar calendar = Calendar.getInstance();
                    SimpleDateFormat timeFormat = new SimpleDateFormat("HH:mm:ss");
                    String currentTime = timeFormat.format(calendar.getTime());


                    String selectedDateString = editTextDate.getText().toString();
                    Date selectedDate;
                    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy EEEE", new Locale("vi"));
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
                    UUID uuid = UUID.randomUUID();
                    GiaoDichRut giaoDichRut = new GiaoDichRut(String.valueOf(uuid),ten, tienRut, combinedDate, moTa);
                    GiaoDichNap giaoDichNap = new GiaoDichNap(String.valueOf(uuid),ten,tienRut,combinedDate,moTa,"Chi tiêu");

                    Map<String, GiaoDichRut> userRut = new HashMap<>();
                    userRut.put(String.valueOf(uuid), giaoDichRut);
                    Map<String, GiaoDichNap> giaoDich = new HashMap<>();
                    giaoDich.put(String.valueOf(uuid), giaoDichNap);
                    DocumentReference documentReference = firestore.collection("users").document(uid);
                    documentReference.collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich").document(String.valueOf(uuid))
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
                    documentReference.collection("duLieuHu").document("duLieuRut").collection("subCollectionNap").document(String.valueOf(uuid))
                            .set(userRut).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("TAG", "New document added to duLieuRut collection");
                                    Toast.makeText(ChiTieuTungHuActivity.this, "Rút tiền khỏi hũ thành công", Toast.LENGTH_SHORT).show();
//                                    Intent intent = new Intent(ChiTieuTungHuActivity.this, HomeActivity.class);
//                                    startActivity(intent);
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("TAG", "Error adding document to duLieuRut collection: " + e.getMessage());
                                }
                            });
                    DocumentReference documentReference1 = firestore.collection("users").document(uid);

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
                                }  if (ten.equals("Hũ giáo dục")) {
                                    jarName = "HuGiaoDuc";
                                }  if (ten.equals("Hũ hưởng thụ")) {
                                    jarName = "HuHuongThu";
                                }  if (ten.equals("Hũ thiện tâm")) {
                                    jarName = "HuThienTam";
                                }  if (ten.equals("Hũ thiết yếu")) {
                                    jarName = "HuThietYeu";
                                }  if (ten.equals("Hũ tiết kiệm")) {
                                    jarName = "HuTietKiem";
                                }
                                Map<String, Object> mapObject = value.getData();
                                Map<String, Object> huData = (Map<String, Object>) mapObject.get(jarName);
                                Double currentSoTien = (Double) huData.get("soTien");
                                Double upDateSoTien =  currentSoTien - tienRut;
                                Map<String, Object> updatedUserMap = new HashMap<>();
                                if (!isUpdated) {
                                    isUpdated = true;
                                    updatedUserMap.put(jarName+".soTien", upDateSoTien);
                                    documentReference1.collection("duLieuHu").document("duLieuTien").update(updatedUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Log.d("TAG", "Updated");
                                            Toast.makeText(ChiTieuTungHuActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                                            getIntent().removeExtra("tenHu");
                                            if (ten.equals("Hũ đầu tư")) {
                                                Intent intent1 = new Intent(ChiTieuTungHuActivity.this,HuDauTuActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ giáo dục")) {
                                                Intent intent1 = new Intent(ChiTieuTungHuActivity.this,HuGiaoDucActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ hưỏng thụ")) {
                                                Intent intent1 = new Intent(ChiTieuTungHuActivity.this,HuHuongThuActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ thiện tâm")) {
                                                Intent intent1 = new Intent(ChiTieuTungHuActivity.this,HuThienTamActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ thiết yếu")) {
                                                Intent intent1 = new Intent(ChiTieuTungHuActivity.this,HuThietYeuActivity.class);
                                                startActivity(intent1);
                                            } else if (ten.equals("Hũ tiết kiệm")) {
                                                Intent intent1 = new Intent(ChiTieuTungHuActivity.this,HuTietKiemActivity.class);
                                                startActivity(intent1);
                                            }
                                        }
                                    }).addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(ChiTieuTungHuActivity.this, "Lưu không thành công", Toast.LENGTH_SHORT).show();
                                        }
                                    });
                                }
                            }
                        }
                    });
                }
            }
        });
    }
}