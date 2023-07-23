package com.example.doan;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
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
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doan.entity.GiaoDichNap;
import com.example.doan.entity.GiaoDichRut;
import com.example.doan.entity.LichSuGiaoDich;
import com.example.doan.entity.SpinnerItem;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.sql.Timestamp;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.UUID;


public class SpendingActivity extends AppCompatActivity {

    private EditText editTextDate,  txtTienChiTieu, editTextMoTa;
    private TextView txtHuy, txtLuu, txtLuong, txtFood, txtShopping, txtXang, txtPhongTro, txtDien;
    private Calendar calendar;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseUser currentUser = mAuth.getCurrentUser();
    private FirebaseFirestore firestore;
    private Spinner spinner;
    private SpinnerAdapter adapter;
    private String currentItemTenHuSelectedSpinner;
    Button btnTest;
    Double currentItemSoTienSelectedSpinner;
    Integer currentItemTyleSelectedSpinner;

    private Long tyLe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_spending);
        txtTienChiTieu = findViewById(R.id.editTextTienChuyen);
        editTextMoTa = findViewById(R.id.editTextMoTa);
        txtHuy = findViewById(R.id.textViewHuy);
        spinner = findViewById(R.id.spinner);
        editTextDate = findViewById(R.id.editTextDate);
        // input nhãn

        txtLuong = findViewById(R.id.textViewTagLuong);
        txtFood = findViewById(R.id.textViewTagFood);
        txtShopping = findViewById(R.id.textViewTagShopping);
        txtXang = findViewById(R.id.textViewTagXang);
        txtPhongTro = findViewById(R.id.textViewTagPhongTro);
        txtDien = findViewById(R.id.textViewTagDien);

        txtHuy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SpendingActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });

        txtTienChiTieu.setInputType(InputType.TYPE_CLASS_NUMBER);

//        txtTienChiTieu.addTextChangedListener(new TextWatcher() {
//            DecimalFormat df = new DecimalFormat("#,###");
//            private String current = "";
//
//
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
//                    txtTienChiTieu.removeTextChangedListener(this);
//
//                    String replaceable = String.format("[%s,.\\s]", NumberFormat.getCurrencyInstance(Locale.US).getCurrency().getSymbol());
//                    String cleanString = s.toString().replaceAll(replaceable, "");
//
//                    if (!TextUtils.isEmpty(cleanString)) {
//                        double parsed = Double.parseDouble(cleanString);
//                        String formatted = df.format(parsed);
//                        current = formatted;
//                        txtTienChiTieu.setText(formatted);
//                        txtTienChiTieu.setSelection(formatted.length());
//                    } else {
//                        current = "";
//                    }
//
//                    txtTienChiTieu.addTextChangedListener(this);
//                }
//            }
//        });

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
                    Log.d("ngày",formattedDate);
                    editTextDate.setText(formattedDate);
                }
                else{
                    Toast.makeText(SpendingActivity.this, "Vui lòng chọn một ngày trong vòng 15 ngày trước hoặc ngày hiện tại", Toast.LENGTH_SHORT).show();
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


        firestore = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
        //Retrieve data from fire store documents and collections
        CollectionReference useRef = firestore.collection("users");
        DocumentReference userDocRef = useRef.document(uid).collection("duLieuHu").document("duLieuTien");

        List<SpinnerItem> items = new ArrayList<>();
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    if (data != null) {
                        for (String fieldName : data.keySet()) {
                            Map<String, Object> hu = (Map<String, Object>) data.get(fieldName);
                            Double soTien = (Double) hu.get("soTien");
                            tyLe = (Long) hu.get("tyLe");
                            String tenHu = "";
                            if (fieldName.equals("HuDauTu")) {
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
                            SpinnerItem spinnerItem = new SpinnerItem(tenHu, soTienHu);
                            items.add(spinnerItem);
                            Log.d("FieldName", fieldName);
                            Log.d("SoTien", String.valueOf(soTien));
                            Log.d("TyLe", String.valueOf(tyLe));
                            Log.d("Spinner list", String.valueOf(items));
                        }

                        adapter = new SpinnerAdapter(SpendingActivity.this, items);
                        spinner.setAdapter(adapter);
                    } else {
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

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


            @SuppressLint("LongLogTag")
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String soTienSelected = null;
                currentItemTenHuSelectedSpinner = String.valueOf(items.get(position).getTenHu());
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
                currentItemTyleSelectedSpinner = items.get(position).getTyle();
                currentItemSoTienSelectedSpinner = soTienFormat.doubleValue();
                Log.d("Selected field name", currentItemTenHuSelectedSpinner);
                Log.d("Selected field name so tien", String.valueOf(currentItemSoTienSelectedSpinner));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });




        txtLuu = findViewById(R.id.textViewLuu);
        txtLuu.setOnClickListener(new View.OnClickListener() {
            private double tienRut;
            DocumentReference documentReference = firestore.collection("users").document(uid);

            @RequiresApi(api = Build.VERSION_CODES.O)
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
                    if (currentItemSoTienSelectedSpinner >= tienRut) {

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
                        GiaoDichRut giaoDichRut = new GiaoDichRut(String.valueOf(uuid),currentItemTenHuSelectedSpinner, tienRut, combinedDate, moTa);
                        //LichSuGiaoDich lichSuGiaoDich = new LichSuGiaoDich(String.valueOf(uuid),date, currentItemTenHuSelectedSpinner, tienRut, "Chi tiêu");
                        GiaoDichNap giaoDichNap = new GiaoDichNap(String.valueOf(uuid),currentItemTenHuSelectedSpinner,tienRut,combinedDate,moTa,"Chi tiêu");
                        Date dNow = new Date();
                        SimpleDateFormat ft = new SimpleDateFormat("yyMMddhhmmssMs");
                        String dateTimeID = ft.format(dNow);
                        Map<String, GiaoDichRut> userRut = new HashMap<>();
                        userRut.put(String.valueOf(uuid), giaoDichRut);
                        Map<String, GiaoDichNap> giaoDich = new HashMap<>();
                        giaoDich.put(String.valueOf(uuid), giaoDichNap);
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
                                        Toast.makeText(SpendingActivity.this, "Rút tiền khỏi hũ thành công", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(SpendingActivity.this, HomeActivity.class);
                                        startActivity(intent);
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.e("TAG", "Error adding document to duLieuRut collection: " + e.getMessage());
                                    }
                                });

                        Map<String, Object> updatedUserMap = new HashMap<>();
                        Double tienHuSauKhiRut = currentItemSoTienSelectedSpinner - tienRut;

                        // String fielDataUpdate = currentItemTenHuSelectedSpinner+"soTien";
                        String tenHu = "";
                        if (currentItemTenHuSelectedSpinner.equals("Hũ đầu tư")) {
                            tenHu = "HuDauTu";
                        } else if (currentItemTenHuSelectedSpinner.equals("Hũ giáo dục")) {
                            tenHu = "HuGiaoDuc";
                        } else if (currentItemTenHuSelectedSpinner.equals("Hũ hưỏng thụ")) {
                            tenHu = "HuHuongThu";
                        } else if (currentItemTenHuSelectedSpinner.equals("Hũ thiện tâm")) {
                            tenHu = "HuThienTam";
                        } else if (currentItemTenHuSelectedSpinner.equals("Hũ thiết yếu")) {
                            tenHu = "HuThietYeu";
                        } else if (currentItemTenHuSelectedSpinner.equals("Hũ tiết kiệm")) {
                            tenHu = "HuTietKiem";
                        }
                        updatedUserMap.put(tenHu + ".soTien", tienHuSauKhiRut);

                        documentReference.collection("duLieuHu").document("duLieuTien").update(updatedUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void unused) {
                                Log.d("TAG", "Updated");
                                Toast.makeText(SpendingActivity.this, "Lưu thành công", Toast.LENGTH_SHORT).show();
                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.e("TAG", "Error: " + e.getMessage());
                            }
                        });
                    } else {
                        Toast.makeText(SpendingActivity.this, "Số tiền rút phải nhỏ hơn hoặc bằng số tiền có trong hũ", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(SpendingActivity.this, "Hãy nhập số tiền", Toast.LENGTH_SHORT).show();
                }

            }

        });
    }
}