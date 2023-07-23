package com.example.doan;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doan.entity.GiaoDichNap;
import com.example.doan.entity.LichSuGiaoDich;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class LichSuGiaoDichActivity extends AppCompatActivity implements TransactionAdapter.OnItemClickListener, ItemOptionsBottomSheet.OnUserActionListener {

    TextView txtThangNam;
    ConstraintLayout layoutThangNam;
    FirebaseFirestore firestore;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    RecyclerView recyclerView;
    ImageView imgeBack;
    TransactionAdapter transactionAdapter;
    //CheckBox checkBoxThuNhap,checkBoxChiTieu;

    RadioButton radioButtonThuNhap, radioButtonChiTieu, radioButtonTatCa;


    @RequiresApi(api = Build.VERSION_CODES.N)
    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();
        setContentView(R.layout.activity_lich_su_giao_dich);

        firestore = FirebaseFirestore.getInstance();
        txtThangNam = findViewById(R.id.txtThangNam);
        layoutThangNam = findViewById(R.id.layoutThangNam);
        recyclerView = findViewById(R.id.recycleViewListGiaoDich);
        imgeBack = findViewById(R.id.back);
        radioButtonThuNhap = findViewById(R.id.radioThuNhap);
        radioButtonChiTieu = findViewById(R.id.radioChiTieu);
        radioButtonTatCa = findViewById(R.id.radioTatca);


        imgeBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LichSuGiaoDichActivity.this, HomeActivity.class);
                startActivity(intent);
            }
        });
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // add 1 because January is 0
        int currentYear = calendar.get(Calendar.YEAR);
        txtThangNam.setText("Tháng " + currentMonth + " " + currentYear);


        layoutThangNam.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showMonthYearPicker();
            }
        });


        List<GiaoDichNap> listGiaoDich = new ArrayList<>();
        //Get user uid
        String uid = currentUser.getUid();
        //Retrieve data from fire store documents and collections
        CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> tienRutInfo = documentSnapshot.getData();
                            for (String key : tienRutInfo.keySet()) {
                                Object value = tienRutInfo.get(key);
                                if (value instanceof Map) {
                                    Map<String, Object> mapValue = (Map<String, Object>) value;
                                    Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                    Date date = getDate.toDate();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    int year = calendar.get(Calendar.YEAR);
                                    int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                    String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                    //get text from txtThangNam
                                    String monthYearText = txtThangNam.getText().toString();
                                    String[] parts = monthYearText.split(" ");
                                    int month = Integer.parseInt(parts[1]);
                                    int yearPicker = Integer.parseInt(parts[2]);
                                    String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                    String monthWord = months[month].toLowerCase();

                                    if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker) {
                                        String id = (String) mapValue.get("uuid");
                                        String tenHu = (String) mapValue.get("tenHu");
                                        Double soTien = (Double) mapValue.get("tienNap");
                                        String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                        String moTa = (String) mapValue.get("moTa");
                                        GiaoDichNap giaoDichNap = new GiaoDichNap(id, tenHu, soTien, getDate.toDate(), moTa, loaiGiaoDich);
                                        listGiaoDich.add(giaoDichNap);
                                    }
                                }
                            }
                        }
                    }
                    if (!listGiaoDich.isEmpty()) {
                        listGiaoDich.sort(Comparator.comparing(obj -> Math.abs(System.currentTimeMillis() - obj.getNgayNap().getTime())));
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(LichSuGiaoDichActivity.this));
                    transactionAdapter = new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDich);
                    transactionAdapter.setOnItemClickListener(LichSuGiaoDichActivity.this);
                    recyclerView.setAdapter(transactionAdapter);
                }
            }
        });

        radioButtonThuNhap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lichSuGiaoDichNap();
            }
        });


        radioButtonChiTieu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                lichSuGiaoRut();
            }
        });

        radioButtonTatCa.setChecked(true);
        radioButtonTatCa.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<GiaoDichNap> listGiaoDich = new ArrayList<>();
                //Get user uid
                String uid = currentUser.getUid();
                //Retrieve data from fire store documents and collections
                CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.exists()) {
                                    Map<String, Object> tienRutInfo = documentSnapshot.getData();
                                    for (String key : tienRutInfo.keySet()) {
                                        Object value = tienRutInfo.get(key);
                                        if (value instanceof Map) {
                                            Map<String, Object> mapValue = (Map<String, Object>) value;
                                            Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                            Date date = getDate.toDate();
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime(date);
                                            int year = calendar.get(Calendar.YEAR);
                                            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                            //get text from txtThangNam
                                            String monthYearText = txtThangNam.getText().toString();
                                            String[] parts = monthYearText.split(" ");
                                            int month = Integer.parseInt(parts[1]);
                                            int yearPicker = Integer.parseInt(parts[2]);
                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                            String monthWord = months[month].toLowerCase();

                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker) {
                                                String id = (String) mapValue.get("uuid");
                                                String tenHu = (String) mapValue.get("tenHu");
                                                Double soTien = (Double) mapValue.get("tienNap");
                                                String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                                String moTa = (String) mapValue.get("moTa");
                                                GiaoDichNap giaoDichNap = new GiaoDichNap(id, tenHu, soTien, getDate.toDate(), moTa, loaiGiaoDich);
                                                listGiaoDich.add(giaoDichNap);
                                            }
                                        }
                                    }
                                }
                            }
                            if (!listGiaoDich.isEmpty()) {
                                listGiaoDich.sort(Comparator.comparing(obj -> Math.abs(System.currentTimeMillis() - obj.getNgayNap().getTime())));
                            }
                            recyclerView.setLayoutManager(new LinearLayoutManager(LichSuGiaoDichActivity.this));
                            transactionAdapter = new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDich);
                            transactionAdapter.setOnItemClickListener(LichSuGiaoDichActivity.this);
                            recyclerView.setAdapter(transactionAdapter);
                        }
                    }
                });
            }
        });
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showMonthYearPicker() {
        // Create a BottomSheetDialog to display the number picker
        BottomSheetDialog dialog = new BottomSheetDialog(LichSuGiaoDichActivity.this);
        dialog.setContentView(R.layout.month_year_picker_dialog);

        // Initialize the month and year number pickers
        NumberPicker monthPicker = dialog.findViewById(R.id.month_picker);
        NumberPicker yearPicker = dialog.findViewById(R.id.year_picker);

        // Set the minimum and maximum values for the month and year pickers
        monthPicker.setMinValue(1);
        monthPicker.setMaxValue(12);
        yearPicker.setMinValue(1900);
        yearPicker.setMaxValue(2100);

        // Set the initial values for the month and year pickers
        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1;
        int currentYear = calendar.get(Calendar.YEAR);
        monthPicker.setValue(currentMonth);
        yearPicker.setValue(currentYear);

        // Set the title for the dialog
        TextView titleTextView = dialog.findViewById(R.id.title_textview);
        titleTextView.setText("Select Month and Year");

        // Set the click listener for the OK button
        Button okButton = dialog.findViewById(R.id.ok_button);
        okButton.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.O)
            @Override
            public void onClick(View view) {
                int selectedMonth = monthPicker.getValue();
                int selectedYear = yearPicker.getValue();

                // Update the TextView with the selected month and year
                String monthYearString = getVietnameseMonthName(selectedMonth) + " " + selectedYear;
                txtThangNam.setText(monthYearString);

                List<GiaoDichNap> listGiaoDich = new ArrayList<>();
                //Get user uid
                String uid = currentUser.getUid();
                //Retrieve data from fire store documents and collections
                CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @RequiresApi(api = Build.VERSION_CODES.N)
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                if (documentSnapshot.exists()) {
                                    Map<String, Object> tienRutInfo = documentSnapshot.getData();
                                    for (String key : tienRutInfo.keySet()) {
                                        Log.d("key: ", key);
                                        Object value = tienRutInfo.get(key);
                                        if (value instanceof Map) {
                                            Map<String, Object> mapValue = (Map<String, Object>) value;
                                            Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                            Date date = getDate.toDate();
                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime(date);
                                            int year = calendar.get(Calendar.YEAR);
                                            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                            //get text from txtThangNam
                                            String monthYearText = txtThangNam.getText().toString();
                                            String[] parts = monthYearText.split(" ");
                                            int month = Integer.parseInt(parts[1]);
                                            int yearPicker = Integer.parseInt(parts[2]);
                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                            String monthWord = months[month].toLowerCase();

                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker) {
                                                String id = (String) mapValue.get("uuid");
                                                String tenHu = (String) mapValue.get("tenHu");
                                                Double soTien = (Double) mapValue.get("tienNap");
                                                String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                                String moTa = (String) mapValue.get("moTa");
                                                GiaoDichNap giaoDichNap = new GiaoDichNap(id, tenHu, soTien, getDate.toDate(), moTa, loaiGiaoDich);
                                                listGiaoDich.add(giaoDichNap);
                                            }
                                        }
                                    }
                                }
                            }
                            if (!listGiaoDich.isEmpty()) {
                                Comparator<GiaoDichNap> comparator = Comparator.comparing(GiaoDichNap::getNgayNap);
                                Comparator<GiaoDichNap> reverseSort = comparator.reversed();
                                listGiaoDich.sort(Comparator.comparing(obj -> Math.abs(System.currentTimeMillis() - obj.getNgayNap().getTime())));
                                Collections.sort(listGiaoDich, reverseSort);
                            }
                            transactionAdapter = new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDich);
                            transactionAdapter.setOnItemClickListener(LichSuGiaoDichActivity.this);
                            recyclerView.setAdapter(transactionAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(LichSuGiaoDichActivity.this));
                            //recyclerView.setAdapter(new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDich));
                            recyclerView.setAdapter(transactionAdapter);
                        }
                    }
                });

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    private String getVietnameseMonthName(int month) {
        String[] monthNames = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12"};
        return monthNames[month - 1];
    }

    @Override
    public void onUpdate(GiaoDichNap giaoDichNap) {
        String id = giaoDichNap.getUuid();
        Date date = giaoDichNap.getNgayNap();
        Double soTien = giaoDichNap.getTienNap();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy EEEE", new Locale("vi"));
        String formattedDate = dateFormat.format(date);
        String mota = giaoDichNap.getMoTa();
        String tenHu = giaoDichNap.getTenHu();
        String loai = giaoDichNap.getLoaiGiaoDich();
        Intent intent = new Intent(LichSuGiaoDichActivity.this, ChinhSuaGiaoDichActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("date", date);
        intent.putExtra("soTien", soTien);
        intent.putExtra("mota", mota);
        intent.putExtra("tenHu", tenHu);
        intent.putExtra("loaiGiaoDich", loai);
        intent.putExtra("screen", 7);
        startActivity(intent);
    }

    @Override
    public void onDelete(GiaoDichNap giaoDichNap) {
        String id = giaoDichNap.getUuid();
        String uid = currentUser.getUid();

        //Retrieve data from fire store documents and collections
        CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
        DocumentReference documentReference = collectionReference.document(id);
        documentReference.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(LichSuGiaoDichActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(LichSuGiaoDichActivity.this, "Xóa không thành công", Toast.LENGTH_SHORT).show();
            }
        });


        String loaiGiaoDich = giaoDichNap.getLoaiGiaoDich();
        if (loaiGiaoDich.equals("Thu nhập")) {
            CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuNap").collection("subCollectionNap");
            DocumentReference documentReference1 = collectionReference1.document(id);
            documentReference1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            }).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                }
            });
            CollectionReference collectionReference2 = firestore.collection("users").document(uid).collection("duLieuHu");
            DocumentReference documentReference2 = collectionReference2.document("duLieuTien");
            documentReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                boolean isUpdated = false;

                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    if (value != null && value.exists()) {
                        String jarName = giaoDichNap.getTenHu();
                        String tenHu = null;
                        Double tien = giaoDichNap.getTienNap();
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
                        Double soTienUpdated = currentSoTien - tien;
                        if (!isUpdated) {
                            isUpdated = true;
                            Map<String, Object> updatedUserMap = new HashMap<>();
                            updatedUserMap.put(tenHu + ".soTien", soTienUpdated);
                            CollectionReference collectionReference3 = firestore.collection("users").document(uid).collection("duLieuHu");
                            DocumentReference documentReference3 = collectionReference3.document("duLieuTien");
                            documentReference3.update(updatedUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d("Cập nhật số tiền: ", "Thành công");
                                    List<GiaoDichNap> listGiaoDich = new ArrayList<>();
                                    //Get user uid
                                    String uid = currentUser.getUid();
                                    //Retrieve data from fire store documents and collections
                                    CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
                                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                    if (documentSnapshot.exists()) {
                                                        Map<String, Object> tienRutInfo = documentSnapshot.getData();
                                                        for (String key : tienRutInfo.keySet()) {
                                                            Object value = tienRutInfo.get(key);
                                                            if (value instanceof Map) {
                                                                Map<String, Object> mapValue = (Map<String, Object>) value;
                                                                Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                                                Date date = getDate.toDate();
                                                                Calendar calendar = Calendar.getInstance();
                                                                calendar.setTime(date);
                                                                int year = calendar.get(Calendar.YEAR);
                                                                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                                String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                                                //get text from txtThangNam
                                                                String monthYearText = txtThangNam.getText().toString();
                                                                String[] parts = monthYearText.split(" ");
                                                                int month = Integer.parseInt(parts[1]);
                                                                int yearPicker = Integer.parseInt(parts[2]);
                                                                String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                                String monthWord = months[month].toLowerCase();

                                                                if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker) {
                                                                    String id = (String) mapValue.get("uuid");
                                                                    String tenHu = (String) mapValue.get("tenHu");
                                                                    Double soTien = (Double) mapValue.get("tienNap");
                                                                    String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                                                    String moTa = (String) mapValue.get("moTa");
                                                                    GiaoDichNap giaoDichNap = new GiaoDichNap(id, tenHu, soTien, getDate.toDate(), moTa, loaiGiaoDich);
                                                                    listGiaoDich.add(giaoDichNap);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (!listGiaoDich.isEmpty()) {
                                                    listGiaoDich.sort(Comparator.comparing(obj -> Math.abs(System.currentTimeMillis() - obj.getNgayNap().getTime())));
                                                }
                                                recyclerView.setLayoutManager(new LinearLayoutManager(LichSuGiaoDichActivity.this));
                                                transactionAdapter = new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDich);
                                                transactionAdapter.setOnItemClickListener(LichSuGiaoDichActivity.this);
                                                recyclerView.setAdapter(transactionAdapter);

                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d("Cập nhật số tiền: ", "Không thành công");
                                }
                            });

                        }
                    }
                }
            });
        } else if (loaiGiaoDich.equals("Chi tiêu")) {
            CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuRut").collection("subCollectionNap");
            DocumentReference documentReference1 = collectionReference1.document(id);
            documentReference1.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    recreate();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                }
            });

            CollectionReference collectionReference2 = firestore.collection("users").document(uid).collection("duLieuHu");
            DocumentReference documentReference2 = collectionReference2.document("duLieuTien");
            documentReference2.addSnapshotListener(new EventListener<DocumentSnapshot>() {
                boolean isUpdated = false;

                @Override
                public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                    if (error != null) {
                        Log.w(TAG, "Listen failed.", error);
                        return;
                    }
                    if (value != null && value.exists()) {
                        String jarName = giaoDichNap.getTenHu();
                        String tenHu = null;
                        Double tien = giaoDichNap.getTienNap();
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
                        Log.d("So tien ", String.valueOf(currentSoTien));
                        Log.d("So tien ", tenHu);
                        Double soTienUpdated = currentSoTien + tien;
                        Log.d("So tien ", String.valueOf(soTienUpdated));
                        Map<String, Object> updatedUserMap = new HashMap<>();
                        updatedUserMap.put(tenHu + ".soTien", soTienUpdated);

                        if (!isUpdated) {
                            isUpdated = true;
                            CollectionReference collectionReference3 = firestore.collection("users").document(uid).collection("duLieuHu");
                            DocumentReference documentReference3 = collectionReference3.document("duLieuTien");
                            documentReference3.update(updatedUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    List<GiaoDichNap> listGiaoDich = new ArrayList<>();
                                    //Get user uid
                                    String uid = currentUser.getUid();
                                    //Retrieve data from fire store documents and collections
                                    CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
                                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @RequiresApi(api = Build.VERSION_CODES.N)
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                                                    if (documentSnapshot.exists()) {
                                                        Map<String, Object> tienRutInfo = documentSnapshot.getData();
                                                        for (String key : tienRutInfo.keySet()) {
                                                            Object value = tienRutInfo.get(key);
                                                            if (value instanceof Map) {
                                                                Map<String, Object> mapValue = (Map<String, Object>) value;
                                                                Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                                                Date date = getDate.toDate();
                                                                Calendar calendar = Calendar.getInstance();
                                                                calendar.setTime(date);
                                                                int year = calendar.get(Calendar.YEAR);
                                                                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                                String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                                                //get text from txtThangNam
                                                                String monthYearText = txtThangNam.getText().toString();
                                                                String[] parts = monthYearText.split(" ");
                                                                int month = Integer.parseInt(parts[1]);
                                                                int yearPicker = Integer.parseInt(parts[2]);
                                                                String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                                String monthWord = months[month].toLowerCase();

                                                                if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker) {
//                                        String id = (String) mapValue.get("id");
//                                        String tenHu = (String) mapValue.get("jarName");
//                                        Double soTien = (Double) mapValue.get("soTien");
//                                        String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
//                                        LichSuGiaoDich lichSuGiaoDich = new LichSuGiaoDich(id,getDate.toDate(),tenHu,soTien,loaiGiaoDich);
                                                                    String id = (String) mapValue.get("uuid");
                                                                    String tenHu = (String) mapValue.get("tenHu");
                                                                    Double soTien = (Double) mapValue.get("tienNap");
                                                                    String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                                                    String moTa = (String) mapValue.get("moTa");
                                                                    GiaoDichNap giaoDichNap = new GiaoDichNap(id, tenHu, soTien, getDate.toDate(), moTa, loaiGiaoDich);
                                                                    listGiaoDich.add(giaoDichNap);
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                                if (!listGiaoDich.isEmpty()) {
                                                    listGiaoDich.sort(Comparator.comparing(obj -> Math.abs(System.currentTimeMillis() - obj.getNgayNap().getTime())));
                                                }
                                                recyclerView.setLayoutManager(new LinearLayoutManager(LichSuGiaoDichActivity.this));
                                                transactionAdapter = new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDich);
                                                transactionAdapter.setOnItemClickListener(LichSuGiaoDichActivity.this);
                                                recyclerView.setAdapter(transactionAdapter);

                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {

                                }
                            });
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onItemClick(GiaoDichNap giaoDichNap) {
        ItemOptionsBottomSheet itemOptionsBottomSheet = new ItemOptionsBottomSheet();
        itemOptionsBottomSheet.setListener(this);
        itemOptionsBottomSheet.setGiaoDichNap(giaoDichNap);
        itemOptionsBottomSheet.show(getSupportFragmentManager(), "option_dialog");
    }

    private void lichSuGiaoDichNap() {
        List<GiaoDichNap> listGiaoDichThuNhap = new ArrayList<>();
        //Get user uid
        String uid = currentUser.getUid();
        //Retrieve data from fire store documents and collections
        CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> tienRutInfo = documentSnapshot.getData();
                            for (String key : tienRutInfo.keySet()) {
                                Object value = tienRutInfo.get(key);
                                if (value instanceof Map) {
                                    Map<String, Object> mapValue = (Map<String, Object>) value;
                                    Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                    String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                    Date date = getDate.toDate();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    int year = calendar.get(Calendar.YEAR);
                                    int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                    String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                    //get text from txtThangNam
                                    String monthYearText = txtThangNam.getText().toString();
                                    String[] parts = monthYearText.split(" ");
                                    int month = Integer.parseInt(parts[1]);
                                    int yearPicker = Integer.parseInt(parts[2]);
                                    String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                    String monthWord = months[month].toLowerCase();

                                    if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && loaiGiaoDich.equals("Thu nhập")) {
                                        String id = (String) mapValue.get("uuid");
                                        String tenHu = (String) mapValue.get("tenHu");
                                        Double soTien = (Double) mapValue.get("tienNap");
//                                                   String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                        String moTa = (String) mapValue.get("moTa");
                                        GiaoDichNap giaoDichNap = new GiaoDichNap(id, tenHu, soTien, getDate.toDate(), moTa, loaiGiaoDich);
                                        listGiaoDichThuNhap.add(giaoDichNap);
                                    }
                                }
                            }
                        }
                    }
                    if (!listGiaoDichThuNhap.isEmpty()) {
                        listGiaoDichThuNhap.sort(Comparator.comparing(obj -> Math.abs(System.currentTimeMillis() - obj.getNgayNap().getTime())));
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(LichSuGiaoDichActivity.this));
                    transactionAdapter = new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDichThuNhap);
                    transactionAdapter.setOnItemClickListener(LichSuGiaoDichActivity.this);
                    recyclerView.setAdapter(transactionAdapter);
                }
            }
        });
    }

    private void lichSuGiaoRut() {
        List<GiaoDichNap> listGiaoDichChiTieu = new ArrayList<>();
        //Get user uid
        String uid = currentUser.getUid();
        //Retrieve data from fire store documents and collections
        CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> tienRutInfo = documentSnapshot.getData();
                            for (String key : tienRutInfo.keySet()) {
                                Object value = tienRutInfo.get(key);
                                if (value instanceof Map) {
                                    Map<String, Object> mapValue = (Map<String, Object>) value;
                                    Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                    String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                    Date date = getDate.toDate();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    int year = calendar.get(Calendar.YEAR);
                                    int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                    String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                    //get text from txtThangNam
                                    String monthYearText = txtThangNam.getText().toString();
                                    String[] parts = monthYearText.split(" ");
                                    int month = Integer.parseInt(parts[1]);
                                    int yearPicker = Integer.parseInt(parts[2]);
                                    String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                    String monthWord = months[month].toLowerCase();

                                    if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && loaiGiaoDich.equals("Chi tiêu")) {
                                        String id = (String) mapValue.get("uuid");
                                        String tenHu = (String) mapValue.get("tenHu");
                                        Double soTien = (Double) mapValue.get("tienNap");
//                                                   String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                        String moTa = (String) mapValue.get("moTa");
                                        GiaoDichNap giaoDichNap = new GiaoDichNap(id, tenHu, soTien, getDate.toDate(), moTa, loaiGiaoDich);
                                        listGiaoDichChiTieu.add(giaoDichNap);
                                    }
                                }
                            }
                        }
                    }
                    if (!listGiaoDichChiTieu.isEmpty()) {
                        listGiaoDichChiTieu.sort(Comparator.comparing(obj -> Math.abs(System.currentTimeMillis() - obj.getNgayNap().getTime())));
                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(LichSuGiaoDichActivity.this));
                    transactionAdapter = new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDichChiTieu);
                    transactionAdapter.setOnItemClickListener(LichSuGiaoDichActivity.this);
                    recyclerView.setAdapter(transactionAdapter);
                }
            }
        });
    }

}