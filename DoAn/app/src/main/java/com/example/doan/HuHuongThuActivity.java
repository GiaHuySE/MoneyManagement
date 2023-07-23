package com.example.doan;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.TextView;
import android.widget.Toast;

import com.example.doan.entity.GiaoDichNap;
import com.github.mikephil.charting.charts.BarChart;
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
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class HuHuongThuActivity extends AppCompatActivity  implements TransactionAdapter.OnItemClickListener, ItemOptionsBottomSheet.OnUserActionListener{

    FirebaseFirestore firestore;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    Button buttonChuyenHu, buttonChinhHu;
    ImageView back;
    TextView txtSoTienHuongThu,txtTongTienRut,txtTongTienNap,txtThangNam,txtThangNam1;
    TransactionAdapter transactionAdapter;
    ConstraintLayout layoutThangNam,incomeLayout,spendingLayout,layoutThangNam1;
    RecyclerView recyclerView;
    BarChart mBarChart;
    CheckBox chckThongKeTheoThang;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hu_huong_thu);
        getSupportActionBar().hide();
//        buttonChuyenHu = findViewById(R.id.btnChuyenHu);
//        buttonChinhHu = findViewById(R.id.buttonChinhSuaHu);
        back = findViewById(R.id.back);
        txtSoTienHuongThu = findViewById(R.id.txtTienHuongThu);
        txtTongTienRut = findViewById(R.id.textViewtongTienRut);
        txtTongTienNap = findViewById(R.id.textViewTongTienNap);
        //txtThangNam = findViewById(R.id.txtThangNam);
        //layoutThangNam = findViewById(R.id.layoutThangNam);
        txtThangNam1 = findViewById(R.id.txtThangNam1);
        layoutThangNam1 = findViewById(R.id.layoutThangNam1);
        recyclerView = findViewById(R.id.recycleViewListGiaoDich);
        incomeLayout =findViewById(R.id.incomeLayout);
        spendingLayout = findViewById(R.id.spendingLayout);
        mBarChart = findViewById(R.id.barChart);
        chckThongKeTheoThang = findViewById(R.id.chckBoxThongKe);
        incomeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HuHuongThuActivity.this,ThuNhapTungHuActivity.class);
                intent.putExtra("tenHu","Hũ hưởng thụ");
                startActivity(intent);
            }
        });

        spendingLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HuHuongThuActivity.this,ChiTieuTungHuActivity.class);
                intent.putExtra("tenHu","Hũ hưởng thụ");
                startActivity(intent);
            }
        });

        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HuHuongThuActivity.this,HomeActivity.class);
                startActivity(intent);
            }
        });

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // add 1 because January is 0
        int currentYear = calendar.get(Calendar.YEAR);
       // txtThangNam.setText("Tháng " + currentMonth + " " + currentYear);
        txtThangNam1.setText("Tháng " + currentMonth + " " + currentYear);
//        layoutThangNam.setOnClickListener(new View.OnClickListener() {
//            @RequiresApi(api = Build.VERSION_CODES.N)
//            @Override
//            public void onClick(View v) {
//                showMonthYearPicker();
//            }
//        });

        layoutThangNam1.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View v) {
                showMonthYearPicker1();
                //showMonthYearPicker();
            }
        });

        firestore = FirebaseFirestore.getInstance();
        //Get user uid
        String uid = currentUser.getUid();
        //Retrieve data from fire store documents and collections
        CollectionReference useRef = firestore.collection("users");
        DocumentReference userDocRef = useRef.document(uid).collection("duLieuHu").document("duLieuTien");
        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Listen failed.", error);
                    return;
                }
                if (value != null && value.exists()) {
                    Map<String, Object> mapObject = value.getData();

                    Map<String, Object> huHuongThu = (Map<String, Object>) mapObject.get("HuHuongThu");


                    Integer tyLeHuongThu = ((Long) huHuongThu.get("tyLe")).intValue();
                    Double soTienHuongThu = (Double) huHuongThu.get("soTien");

                    //format tiền VietNam
                    Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                    String tienHuongThu = currencyFormat.format(soTienHuongThu);

                    txtSoTienHuongThu.setText(tienHuongThu + "");

                } else {
                    Log.d("TAG_DATA_NULL", "Current data: null");
                }


            }
        });
        CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuRut").collection("subCollectionNap");
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            Double tienRutValue;
            String tongTienRut = null;

            double tongTien = 0;
            LocalDate currentDate = LocalDate.now();
            Month m = currentDate.getMonth();
            int y = currentDate.getYear();
            String month = String.valueOf(m).toLowerCase();
            String hu = "Hũ hưởng thụ";

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
                                    // get year and month
                                    Timestamp getDate = (Timestamp) mapValue.get("ngayRut");
                                    Date date = getDate.toDate();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    int year = calendar.get(Calendar.YEAR);
                                    String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                    String tenHu = (String) mapValue.get("tenHu");
                                    Log.d("onComplete: ",tenHu);
                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền rút
                                    if (month.equals(monthName.toLowerCase()) && year == y && hu.equals(tenHu)) {
                                        tienRutValue = (Double) mapValue.get("tienRut");
                                    } else {
                                        tienRutValue = 0.0;
                                    }

                                    //Log.d("FirestoreExample", "User " + documentSnapshot.getId() + " - Map Value: " + tienRutValue);
                                }
                            }
                            tongTien += tienRutValue;
                            Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                            tongTienRut = currencyFormat.format(tongTien);

                        }
                    }

                    if (tongTienRut != null) {
                        Log.d("TAG", "Name tiền rút: " + tongTienRut);
                        txtTongTienRut.setText(tongTienRut);

                    } else {
                        Log.d("TAG", "Name field does not exist in this document.");
                    }
                } else {
                    Log.w("FirestoreExample", "Error getting documents.", task.getException());
                }
            }
        });

        CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuNap").collection("subCollectionNap");
        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            Double tienNapValue;
            String tongTienNap = null;
            double tongTien = 0;
            LocalDate currentDate = LocalDate.now();
            Month m = currentDate.getMonth();
            int y = currentDate.getYear();
            String month = String.valueOf(m).toLowerCase();
            String hu = "Hũ hưởng thụ";
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
                                    String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                    String tenHu = (String) mapValue.get("tenHu");
                                    Log.d("onComplete: ",tenHu);
                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                    if (month.equals(monthName.toLowerCase()) && year == y && hu.equals(tenHu)) {
                                        tienNapValue = (Double) mapValue.get("tienNap");
                                    } else {
                                        tienNapValue = 0.0;
                                    }
                                }
                            }
                            tongTien += tienNapValue;
                            Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                            tongTienNap = currencyFormat.format(tongTien);

                        }
                    }

                    if (tongTienNap != null) {
                        Log.d("TAG", "Name tiền nap: " + tongTienNap);
                        txtTongTienNap.setText(tongTienNap);

                    } else {
                        Log.d("TAG", "Name field does not exist in this duLieuNap.");
                    }
                } else {
                    Log.w("FirestoreExample", "Error getting documents.", task.getException());
                }
            }
        });
        String hu = "Hũ hưởng thụ";
        List<GiaoDichNap> listGiaoDich = new ArrayList<>();
        //Retrieve data from fire store documents and collections
        CollectionReference collectionReference2 = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
        collectionReference2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                                    String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                    //get text from txtThangNam
                                    String monthYearText = txtThangNam1.getText().toString();
                                    String[] parts = monthYearText.split(" ");
                                    int month = Integer.parseInt(parts[1]);
                                    int yearPicker = Integer.parseInt(parts[2]);
                                    String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                    String monthWord = months[month].toLowerCase();
                                    String tenHu = (String) mapValue.get("tenHu");
                                    if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                        String id = (String) mapValue.get("uuid");
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
                    recyclerView.setLayoutManager(new LinearLayoutManager(HuHuongThuActivity.this));
                    transactionAdapter = new TransactionAdapter(HuHuongThuActivity.this, listGiaoDich);
                    transactionAdapter.setOnItemClickListener(HuHuongThuActivity.this);
                    recyclerView.setAdapter(transactionAdapter);

                }
            }
        });
        getValueForBarChart(new HomeFragment.FirestoreCallback() {
            @Override
            public void onCallback(List<BarEntry> incomeList, List<BarEntry> spendingList) {

                BarDataSet incomeDataSet = new BarDataSet(incomeList, "Thu nhập");
                incomeDataSet.setColor(Color.GREEN);
                incomeDataSet.setValueTextColor(Color.WHITE);
                incomeDataSet.setValueTextSize(10F);
                incomeDataSet.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                        return formatter.format(value);
                    }
                });
                BarDataSet spendingDataSet = new BarDataSet(spendingList, "Chi tiêu");
                spendingDataSet.setColor(Color.RED);
                spendingDataSet.setValueTextColor(Color.WHITE);
                spendingDataSet.setValueTextSize(10F);
                spendingDataSet.setValueFormatter(new IValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                        DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                        return formatter.format(value);
                    }
                });
                BarData barData = new BarData(incomeDataSet, spendingDataSet);
                barData.setBarWidth(0.3f);
                mBarChart.getDescription().setText("");
                // Set x-axis labels to display weeks
                final String[] weekLabels = {"Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4", "Tuần 5"};
                XAxis xAxis = mBarChart.getXAxis();
                xAxis.setValueFormatter(new IAxisValueFormatter() {
                    @Override
                    public String getFormattedValue(float value, AxisBase axis) {
                        int index = (int) value;
                        if (index >= 0 && index < weekLabels.length) {
                            return weekLabels[index];
                        } else {
                            return "";
                        }
                    }
                });

                // Adjust x-axis position and spacing
                xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                xAxis.setTextColor(Color.WHITE);
                xAxis.setDrawGridLines(false);
                xAxis.setGranularity(1f);
                xAxis.setCenterAxisLabels(true);

                // Set y-axis to start at 0
                mBarChart.getAxisLeft().setAxisMinimum(0f);
                mBarChart.getAxisRight().setEnabled(false);
                mBarChart.getAxisLeft().setTextColor(Color.WHITE);

                // Set bar data and adjust spacing
                mBarChart.setData(barData);

                int groupCount = 5; // Set the number of groups to 5
                float groupSpace = 0.1f;
                float barSpace = 0.15f;
                mBarChart.getXAxis().setAxisMinimum(-barSpace);
                mBarChart.getXAxis().setAxisMaximum(barData.getXMax() + barSpace);
                mBarChart.getAxisLeft().setAxisMinimum(0);
                mBarChart.groupBars(0, groupSpace, barSpace);

                // Animate chart
                mBarChart.animateY(1000);
            }
        });

        chckThongKeTheoThang.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    getValueForBarChartByMonth(new HomeFragment.FirestoreCallback() {
                        @Override
                        public void onCallback(List<BarEntry> incomeList, List<BarEntry> spendingList) {
                            BarDataSet incomeDataSet = new BarDataSet(incomeList, "Thu nhập");
                            incomeDataSet.setColor(Color.GREEN);
                            incomeDataSet.setValueTextColor(Color.WHITE);
                            incomeDataSet.setValueTextSize(10F);
                            incomeDataSet.setValueFormatter(new IValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                    if (value == 0) {
                                        return ""; // Return empty string to hide the value
                                    }
                                    DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                                    return formatter.format(value);
                                }
                            });
                            BarDataSet spendingDataSet = new BarDataSet(spendingList, "Chi tiêu");
                            spendingDataSet.setColor(Color.RED);
                            spendingDataSet.setValueTextColor(Color.WHITE);
                            spendingDataSet.setValueTextSize(10F);
                            spendingDataSet.setValueFormatter(new IValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                    if (value == 0) {
                                        return ""; // Return empty string to hide the value
                                    }
                                    DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                                    return formatter.format(value);
                                }
                            });
                            BarData barData = new BarData(incomeDataSet, spendingDataSet);
                            barData.setBarWidth(0.3f);
                            mBarChart.getDescription().setText("");
                            // Set x-axis labels to display weeks
                            final String[] weekLabels = {"Tháng 1", "Tháng 2", "Tháng 3", "Tháng 4", "Tháng 5", "Tháng 6", "Tháng 7", "Tháng 8", "Tháng 9", "Tháng 10", "Tháng 11", "Tháng 12", "Cả năm"};
                            XAxis xAxis = mBarChart.getXAxis();
                            xAxis.setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    int index = (int) value;
                                    if (index >= 0 && index < weekLabels.length) {
                                        return weekLabels[index];
                                    } else if (index == 12) {
                                        return weekLabels[weekLabels.length - 1];
                                    } else {
                                        return "";
                                    }
                                }
                            });

                            // Adjust x-axis position and spacing
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setTextColor(Color.WHITE);
                            xAxis.setDrawGridLines(false);
                            xAxis.setGranularity(1f);
                            xAxis.setCenterAxisLabels(true);

                            // Set y-axis to start at 0
                            mBarChart.getAxisLeft().setAxisMinimum(0f);
                            mBarChart.getAxisRight().setEnabled(false);
                            mBarChart.getAxisLeft().setTextColor(Color.WHITE);

                            // Set bar data and adjust spacing
                            mBarChart.setData(barData);

                            int groupCount = 5; // Set the number of groups to 5
                            float groupSpace = 0.3f;
                            float barSpace = 0.05f;
                            mBarChart.getXAxis().setAxisMinimum(-barSpace);
                            mBarChart.getXAxis().setAxisMaximum(barData.getXMax() + barSpace);
                            mBarChart.getAxisLeft().setAxisMinimum(0);
                            mBarChart.groupBars(0, groupSpace, barSpace);
                            mBarChart.setVisibleXRangeMaximum(12);
                            // Animate chart
                            mBarChart.animateY(1000);
                        }
                    });
                }else {
                    getValueForBarChart(new HomeFragment.FirestoreCallback() {
                        @Override
                        public void onCallback(List<BarEntry> incomeList, List<BarEntry> spendingList) {

                            BarDataSet incomeDataSet = new BarDataSet(incomeList, "Thu nhập");
                            incomeDataSet.setColor(Color.GREEN);
                            incomeDataSet.setValueTextColor(Color.WHITE);
                            incomeDataSet.setValueTextSize(10F);
                            incomeDataSet.setValueFormatter(new IValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                    DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                                    return formatter.format(value);
                                }
                            });
                            BarDataSet spendingDataSet = new BarDataSet(spendingList, "Chi tiêu");
                            spendingDataSet.setColor(Color.RED);
                            spendingDataSet.setValueTextColor(Color.WHITE);
                            spendingDataSet.setValueTextSize(10F);
                            spendingDataSet.setValueFormatter(new IValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                    DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                                    return formatter.format(value);
                                }
                            });
                            BarData barData = new BarData(incomeDataSet, spendingDataSet);
                            barData.setBarWidth(0.3f);
                            mBarChart.getDescription().setText("");
                            // Set x-axis labels to display weeks
                            final String[] weekLabels = {"Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4", "Tuần 5"};
                            XAxis xAxis = mBarChart.getXAxis();
                            xAxis.setValueFormatter(new IAxisValueFormatter() {
                                @Override
                                public String getFormattedValue(float value, AxisBase axis) {
                                    int index = (int) value;
                                    if (index >= 0 && index < weekLabels.length) {
                                        return weekLabels[index];
                                    } else {
                                        return "";
                                    }
                                }
                            });

                            // Adjust x-axis position and spacing
                            xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                            xAxis.setTextColor(Color.WHITE);
                            xAxis.setDrawGridLines(false);
                            xAxis.setGranularity(1f);
                            xAxis.setCenterAxisLabels(true);

                            // Set y-axis to start at 0
                            mBarChart.getAxisLeft().setAxisMinimum(0f);
                            mBarChart.getAxisRight().setEnabled(false);
                            mBarChart.getAxisLeft().setTextColor(Color.WHITE);

                            // Set bar data and adjust spacing
                            mBarChart.setData(barData);

                            int groupCount = 5; // Set the number of groups to 5
                            float groupSpace = 0.1f;
                            float barSpace = 0.15f;
                            mBarChart.getXAxis().setAxisMinimum(-barSpace);
                            mBarChart.getXAxis().setAxisMaximum(barData.getXMax() + barSpace);
                            mBarChart.getAxisLeft().setAxisMinimum(0);
                            mBarChart.groupBars(0, groupSpace, barSpace);

                            // Animate chart
                            mBarChart.animateY(1000);
                        }
                    });
                }
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showMonthYearPicker() {
        // Create a BottomSheetDialog to display the number picker
        BottomSheetDialog dialog = new BottomSheetDialog(HuHuongThuActivity.this);
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
        String hu = "Hũ hưởng thụ";
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
                txtThangNam1.setText(monthYearString);

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
                                            String monthYearText = txtThangNam1.getText().toString();
                                            String[] parts = monthYearText.split(" ");
                                            int month = Integer.parseInt(parts[1]);
                                            int yearPicker = Integer.parseInt(parts[2]);
                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                            String monthWord = months[month].toLowerCase();
                                            String tenHu = (String) mapValue.get("tenHu");
                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                                String id = (String) mapValue.get("uuid");

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
                            transactionAdapter = new TransactionAdapter(HuHuongThuActivity.this, listGiaoDich);
                            transactionAdapter.setOnItemClickListener(HuHuongThuActivity.this);
                            recyclerView.setAdapter(transactionAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(HuHuongThuActivity.this));
                            //recyclerView.setAdapter(new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDich));
                            recyclerView.setAdapter(transactionAdapter);
                        }
                    }
                });

                getValueForBarChart(new HomeFragment.FirestoreCallback() {
                    @Override
                    public void onCallback(List<BarEntry> incomeList, List<BarEntry> spendingList) {

                        BarDataSet incomeDataSet = new BarDataSet(incomeList, "Thu Nhập");
                        incomeDataSet.setColor(Color.GREEN);
                        incomeDataSet.setValueTextColor(Color.WHITE);
                        incomeDataSet.setValueTextSize(10F);
                        incomeDataSet.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                                return formatter.format(value);
                            }
                        });
                        BarDataSet spendingDataSet = new BarDataSet(spendingList, "Chi Tiêu");
                        spendingDataSet.setColor(Color.RED);
                        spendingDataSet.setValueTextColor(Color.WHITE);
                        spendingDataSet.setValueTextSize(10F);
                        spendingDataSet.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                                return formatter.format(value);
                            }
                        });
                        BarData barData = new BarData(incomeDataSet, spendingDataSet);
                        barData.setBarWidth(0.3f);
                        mBarChart.getDescription().setText("");
                        // Set x-axis labels to display weeks
                        final String[] weekLabels = {"Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4", "Tuần 5"};
                        XAxis xAxis = mBarChart.getXAxis();
                        xAxis.setTextColor(Color.WHITE);
                        xAxis.setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                int index = (int) value;
                                if (index >= 0 && index < weekLabels.length) {
                                    return weekLabels[index];
                                } else {
                                    return "";
                                }
                            }
                        });

                        // Adjust x-axis position and spacing
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1f);
                        xAxis.setCenterAxisLabels(true);

// Set y-axis to start at 0
                        mBarChart.getAxisLeft().setAxisMinimum(0f);
                        mBarChart.getAxisRight().setEnabled(false);
                        mBarChart.getAxisLeft().setTextColor(Color.WHITE);
// Set bar data and adjust spacing
                        mBarChart.setData(barData);

                        int groupCount = 5; // Set the number of groups to 5
                        float groupSpace = 0.08f;
                        float barSpace = 0.03f;
                        mBarChart.getXAxis().setAxisMinimum(-barSpace);
                        mBarChart.getXAxis().setAxisMaximum(barData.getXMax() + barSpace);
                        mBarChart.getAxisLeft().setAxisMinimum(0);
                        mBarChart.groupBars(0, groupSpace, barSpace);

// Animate chart
                        mBarChart.animateY(1000);
                    }
                });
                dialog.dismiss();
            }
        });

        //dialog.show();
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
        Intent intent = new Intent(HuHuongThuActivity.this,ChinhSuaGiaoDichActivity.class);
        intent.putExtra("id",id);
        intent.putExtra("date",date);
        intent.putExtra("soTien",soTien);
        intent.putExtra("mota",mota);
        intent.putExtra("tenHu",tenHu);
        intent.putExtra("loaiGiaoDich",loai);
        intent.putExtra("screen",4);
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
                Toast.makeText(HuHuongThuActivity.this, "Xóa thành công", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(HuHuongThuActivity.this, "Xóa không thành công", Toast.LENGTH_SHORT).show();
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
                        if(jarName.equals("Hũ thiết yếu")) {
                            tenHu = "HuThietYeu";
                        } else if(jarName.equals("Hũ giáo dục")) {
                            tenHu = "HuGiaoDuc";
                        }else if(jarName.equals("Hũ tiết kiệm")) {
                            tenHu = "HuTietKiem";
                        } else if(jarName.equals("Hũ đầu tư")) {
                            tenHu = "HuDauTu";
                        } else if(jarName.equals("Hũ hưởng thụ")) {
                            tenHu = "HuHuongThu";
                        } else if(jarName.equals("Hũ thiện tâm")) {
                            tenHu = "HuThienTam";
                        }
                        Map<String, Object> mapObject = value.getData();
                        Map<String, Object> hu = (Map<String, Object>) mapObject.get(tenHu);
                        Double currentSoTien = (Double) hu.get("soTien");
                        Double soTienUpdated = currentSoTien - tien;
                        if(!isUpdated){
                            isUpdated = true;
                            Map<String, Object> updatedUserMap = new HashMap<>();
                            updatedUserMap.put(tenHu + ".soTien", soTienUpdated);
                            CollectionReference collectionReference3 = firestore.collection("users").document(uid).collection("duLieuHu");
                            DocumentReference documentReference3 = collectionReference3.document("duLieuTien");
                            documentReference3.update(updatedUserMap).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void unused) {
                                    Log.d( "Cập nhật số tiền: ","Thành công");
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
                                                                String tenHu = (String) mapValue.get("tenHu");
                                                                Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                                                Date date = getDate.toDate();
                                                                Calendar calendar = Calendar.getInstance();
                                                                calendar.setTime(date);
                                                                int year = calendar.get(Calendar.YEAR);
                                                                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                                String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                                                //get text from txtThangNam
                                                                String monthYearText = txtThangNam1.getText().toString();
                                                                String[] parts = monthYearText.split(" ");
                                                                int month = Integer.parseInt(parts[1]);
                                                                int yearPicker = Integer.parseInt(parts[2]);
                                                                String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                                String monthWord = months[month].toLowerCase();

                                                                if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals("Hũ hưởng thụ")) {
                                                                    String id = (String) mapValue.get("uuid");
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
                                                recyclerView.setLayoutManager(new LinearLayoutManager(HuHuongThuActivity.this));
                                                transactionAdapter = new TransactionAdapter(HuHuongThuActivity.this, listGiaoDich);
                                                transactionAdapter.setOnItemClickListener(HuHuongThuActivity.this);
                                                recyclerView.setAdapter(transactionAdapter);

                                                CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuNap").collection("subCollectionNap");
                                                collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    Double tienNapValue;
                                                    String tongTienNap = null;
                                                    double tongTien = 0;
                                                    //                    LocalDate currentDate = LocalDate.now();
//                    Month m = currentDate.getMonth();
//                    int y = currentDate.getYear();
//                    String month = String.valueOf(m).toLowerCase();
                                                    String hu = "Hũ hưởng thụ";
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
                                                                            String tenHu = (String) mapValue.get("tenHu");
//                                            Calendar calendar = Calendar.getInstance();
//                                            calendar.setTime(date);
//                                            int year = calendar.get(Calendar.YEAR);
//                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                                                            Calendar calendar = Calendar.getInstance();
                                                                            calendar.setTime(date);
                                                                            int year = calendar.get(Calendar.YEAR);
                                                                            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                                                            //get text from txtThangNam
                                                                            String monthYearText = txtThangNam1.getText().toString();
                                                                            String[] parts = monthYearText.split(" ");
                                                                            int month = Integer.parseInt(parts[1]);
                                                                            int yearPicker = Integer.parseInt(parts[2]);
                                                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                                            String monthWord = months[month].toLowerCase();
                                                                            // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                                                                tienNapValue = (Double) mapValue.get("tienNap");
                                                                            } else {
                                                                                tienNapValue = 0.0;
                                                                            }
                                                                        }
                                                                    }
                                                                    tongTien += tienNapValue;
                                                                    Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                                                                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                                                                    tongTienNap = currencyFormat.format(tongTien);

                                                                }
                                                            }

                                                            if (tongTienNap != null) {
                                                                Log.d("TAG", "Name tiền nap: " + tongTienNap);
                                                                txtTongTienNap.setText(tongTienNap);
                                                                CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuRut").collection("subCollectionNap");
                                                                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                                                    Double tienRutValue;
                                                                    String tongTienRut = null;

                                                                    double tongTien = 0;

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
                                                                                            // get year and month
                                                                                            Timestamp getDate = (Timestamp) mapValue.get("ngayRut");
                                                                                            String tenHu = (String) mapValue.get("tenHu");
                                                                                            Date date = getDate.toDate();
                                                                                            Calendar calendar = Calendar.getInstance();
                                                                                            calendar.setTime(date);
                                                                                            int year = calendar.get(Calendar.YEAR);
                                                                                            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                                                                            //get text from txtThangNam
                                                                                            String monthYearText = txtThangNam1.getText().toString();
                                                                                            String[] parts = monthYearText.split(" ");
                                                                                            int month = Integer.parseInt(parts[1]);
                                                                                            int yearPicker = Integer.parseInt(parts[2]);
                                                                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                                                            String monthWord = months[month].toLowerCase();
                                                                                            // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                                                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                                                                                tienRutValue = (Double) mapValue.get("tienRut");
                                                                                            } else {
                                                                                                tienRutValue = 0.0;
                                                                                            }

                                                                                            //Log.d("FirestoreExample", "User " + documentSnapshot.getId() + " - Map Value: " + tienRutValue);
                                                                                        }
                                                                                    }
                                                                                    tongTien += tienRutValue;
                                                                                    Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                                                                                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                                                                                    tongTienRut = currencyFormat.format(tongTien);

                                                                                }
                                                                            }

                                                                            if (tongTienRut != null) {
                                                                                Log.d("TAG", "Name tiền rút: " + tongTienRut);
                                                                                txtTongTienRut.setText(tongTienRut);

                                                                            } else {
                                                                                Log.d("TAG", "Name field does not exist in this document.");
                                                                            }
                                                                        } else {
                                                                            Log.w("FirestoreExample", "Error getting documents.", task.getException());
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                Log.d("TAG", "Name field does not exist in this duLieuNap.");
                                                            }
                                                        } else {
                                                            Log.w("FirestoreExample", "Error getting documents.", task.getException());
                                                        }
                                                    }
                                                });
                                            }
                                        }
                                    });
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.d( "Cập nhật số tiền: ","Không thành công");
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
                //    recreate();
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
                        if(jarName.equals("Hũ thiết yếu"))
                        {
                            tenHu = "HuThietYeu";
                        } else if(jarName.equals("Hũ giáo dục"))

                        {
                            tenHu = "HuGiaoDuc";
                        }else if(jarName.equals("Hũ tiết kiệm"))

                        {
                            tenHu = "HuTietKiem";
                        } else if(jarName.equals("Hũ đầu tư"))
                        {
                            tenHu = "HuDauTu";
                        } else if(jarName.equals("Hũ hưởng thụ"))
                        {
                            tenHu = "HuHuongThu";
                        } else if(jarName.equals("Hũ thiện tâm"))
                        {
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

                        if(!isUpdated){
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
                                                                String tenHu = (String) mapValue.get("tenHu");
                                                                Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                                                Date date = getDate.toDate();
                                                                Calendar calendar = Calendar.getInstance();
                                                                calendar.setTime(date);
                                                                int year = calendar.get(Calendar.YEAR);
                                                                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                                String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                                                //get text from txtThangNam
                                                                String monthYearText = txtThangNam1.getText().toString();
                                                                String[] parts = monthYearText.split(" ");
                                                                int month = Integer.parseInt(parts[1]);
                                                                int yearPicker = Integer.parseInt(parts[2]);
                                                                String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                                String monthWord = months[month].toLowerCase();

                                                                if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals("Hũ hưởng thụ")) {
//                                        String id = (String) mapValue.get("id");
//                                        String tenHu = (String) mapValue.get("jarName");
//                                        Double soTien = (Double) mapValue.get("soTien");
//                                        String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
//                                        LichSuGiaoDich lichSuGiaoDich = new LichSuGiaoDich(id,getDate.toDate(),tenHu,soTien,loaiGiaoDich);
                                                                    String id = (String) mapValue.get("uuid");
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
                                                recyclerView.setLayoutManager(new LinearLayoutManager(HuHuongThuActivity.this));
                                                transactionAdapter = new TransactionAdapter(HuHuongThuActivity.this, listGiaoDich);
                                                transactionAdapter.setOnItemClickListener(HuHuongThuActivity.this);
                                                recyclerView.setAdapter(transactionAdapter);

                                                CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuNap").collection("subCollectionNap");
                                                collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                                    Double tienNapValue;
                                                    String tongTienNap = null;
                                                    double tongTien = 0;
                                                    //                    LocalDate currentDate = LocalDate.now();
//                    Month m = currentDate.getMonth();
//                    int y = currentDate.getYear();
//                    String month = String.valueOf(m).toLowerCase();
                                                    String hu = "Hũ hưởng thụ";
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
                                                                            String tenHu = (String) mapValue.get("tenHu");
//                                            Calendar calendar = Calendar.getInstance();
//                                            calendar.setTime(date);
//                                            int year = calendar.get(Calendar.YEAR);
//                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                                                            Calendar calendar = Calendar.getInstance();
                                                                            calendar.setTime(date);
                                                                            int year = calendar.get(Calendar.YEAR);
                                                                            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                                                            //get text from txtThangNam
                                                                            String monthYearText = txtThangNam1.getText().toString();
                                                                            String[] parts = monthYearText.split(" ");
                                                                            int month = Integer.parseInt(parts[1]);
                                                                            int yearPicker = Integer.parseInt(parts[2]);
                                                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                                            String monthWord = months[month].toLowerCase();
                                                                            // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                                                                tienNapValue = (Double) mapValue.get("tienNap");
                                                                            } else {
                                                                                tienNapValue = 0.0;
                                                                            }
                                                                        }
                                                                    }
                                                                    tongTien += tienNapValue;
                                                                    Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                                                                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                                                                    tongTienNap = currencyFormat.format(tongTien);

                                                                }
                                                            }

                                                            if (tongTienNap != null) {
                                                                Log.d("TAG", "Name tiền nap: " + tongTienNap);
                                                                txtTongTienNap.setText(tongTienNap);
                                                                CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuRut").collection("subCollectionNap");
                                                                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                                                    Double tienRutValue;
                                                                    String tongTienRut = null;

                                                                    double tongTien = 0;

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
                                                                                            // get year and month
                                                                                            Timestamp getDate = (Timestamp) mapValue.get("ngayRut");
                                                                                            String tenHu = (String) mapValue.get("tenHu");
                                                                                            Date date = getDate.toDate();
                                                                                            Calendar calendar = Calendar.getInstance();
                                                                                            calendar.setTime(date);
                                                                                            int year = calendar.get(Calendar.YEAR);
                                                                                            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                                                                            //get text from txtThangNam
                                                                                            String monthYearText = txtThangNam1.getText().toString();
                                                                                            String[] parts = monthYearText.split(" ");
                                                                                            int month = Integer.parseInt(parts[1]);
                                                                                            int yearPicker = Integer.parseInt(parts[2]);
                                                                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                                                            String monthWord = months[month].toLowerCase();
                                                                                            // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                                                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                                                                                tienRutValue = (Double) mapValue.get("tienRut");
                                                                                            } else {
                                                                                                tienRutValue = 0.0;
                                                                                            }

                                                                                            //Log.d("FirestoreExample", "User " + documentSnapshot.getId() + " - Map Value: " + tienRutValue);
                                                                                        }
                                                                                    }
                                                                                    tongTien += tienRutValue;
                                                                                    Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                                                                                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                                                                                    tongTienRut = currencyFormat.format(tongTien);

                                                                                }
                                                                            }

                                                                            if (tongTienRut != null) {
                                                                                Log.d("TAG", "Name tiền rút: " + tongTienRut);
                                                                                txtTongTienRut.setText(tongTienRut);

                                                                            } else {
                                                                                Log.d("TAG", "Name field does not exist in this document.");
                                                                            }
                                                                        } else {
                                                                            Log.w("FirestoreExample", "Error getting documents.", task.getException());
                                                                        }
                                                                    }
                                                                });
                                                            } else {
                                                                Log.d("TAG", "Name field does not exist in this duLieuNap.");
                                                            }
                                                        } else {
                                                            Log.w("FirestoreExample", "Error getting documents.", task.getException());
                                                        }
                                                    }
                                                });
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
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getValueForBarChart(final HomeFragment.FirestoreCallback firestoreCallback) {
        final List<BarEntry> incomeEntries = new ArrayList<>();
        final List<BarEntry> spendingEntries = new ArrayList<>();
        String uid = currentUser.getUid();
        CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuNap").collection("subCollectionNap");
        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            Double tienNapValue;
            LocalDate currentDate = LocalDate.now();
            String hu = "Hũ hưởng thụ";
            List<Double> inComeWeek1 = new ArrayList();
            List<Double> inComeWeek2 = new ArrayList();
            List<Double> inComeWeek3 = new ArrayList();
            List<Double> inComeWeek4 = new ArrayList();
            List<Double> inComeWeek5 = new ArrayList();

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
                                    String tenHu = (String) mapValue.get("tenHu");
                                    Date date = getDate.toDate();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    int year = calendar.get(Calendar.YEAR);
                                    int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                    String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                    //get text from txtThangNam
                                    String monthYearText = txtThangNam1.getText().toString();
                                    String[] parts = monthYearText.split(" ");
                                    int month = Integer.parseInt(parts[1]);
                                    int yearPicker = Integer.parseInt(parts[2]);
                                    String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                    String monthWord = months[month].toLowerCase();
                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                    if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                        tienNapValue = (Double) mapValue.get("tienNap");
                                        if (weekOfMonth == 1) {
                                            inComeWeek1.add(tienNapValue);
                                        }
                                        if (weekOfMonth == 2) {
                                            inComeWeek2.add(tienNapValue);
                                        }
                                        if (weekOfMonth == 3) {
                                            inComeWeek3.add(tienNapValue);
                                        }
                                        if (weekOfMonth == 4) {
                                            inComeWeek4.add(tienNapValue);
                                        }
                                        if (weekOfMonth == 5) {
                                            inComeWeek5.add(tienNapValue);
                                        }
                                    } else {
                                        tienNapValue = 0.0;
                                    }
                                }
                            }
                        }
                    }

                    double tongWeek1 = 0;
                    for (double i : inComeWeek1) {
                        tongWeek1 = tongWeek1 + i;

                    }
                    incomeEntries.add(new BarEntry(0F, (float) tongWeek1));
                    double tongWeek2 = 0;
                    for (double i : inComeWeek2) {
                        tongWeek2 = tongWeek2 + i;

                    }
                    incomeEntries.add(new BarEntry(1F, (float) tongWeek2));
                    double tongWeek3 = 0;
                    for (double i : inComeWeek3) {
                        tongWeek3 = tongWeek3 + i;

                    }
                    incomeEntries.add(new BarEntry(2F, (float) tongWeek3));
                    double tongWeek4 = 0;
                    for (double i : inComeWeek4) {
                        tongWeek4 = tongWeek4 + i;

                    }
                    incomeEntries.add(new BarEntry(3F, (float) tongWeek4));
                    double tongWeek5 = 0;
                    for (double i : inComeWeek5) {
                        tongWeek5 = tongWeek5 + i;

                    }
                    incomeEntries.add(new BarEntry(4F, (float) tongWeek5));


                    CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuRut").collection("subCollectionNap");
                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        Double tienRutValue;


                        List<Double> spendingWeek1 = new ArrayList();
                        List<Double> spendingWeek2 = new ArrayList();
                        List<Double> spendingWeek3 = new ArrayList();
                        List<Double> spendingWeek4 = new ArrayList();
                        List<Double> spendingWeek5 = new ArrayList();

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
                                                // get year and month
                                                Timestamp getDate = (Timestamp) mapValue.get("ngayRut");
                                                String tenHu = (String) mapValue.get("tenHu");
                                                Date date = getDate.toDate();
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTime(date);
                                                int year = calendar.get(Calendar.YEAR);
                                                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                                //
                                                String monthYearText = txtThangNam1.getText().toString();
                                                String[] parts = monthYearText.split(" ");
                                                int month = Integer.parseInt(parts[1]);
                                                int yearPicker = Integer.parseInt(parts[2]);
                                                String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                String monthWord = months[month].toLowerCase();
                                                // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền rút
                                                if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                                    tienRutValue = (Double) mapValue.get("tienRut");
                                                    if (weekOfMonth == 1) {
                                                        spendingWeek1.add(tienRutValue);
                                                    }
                                                    if (weekOfMonth == 2) {
                                                        spendingWeek2.add(tienRutValue);
                                                    }
                                                    if (weekOfMonth == 3) {
                                                        spendingWeek3.add(tienRutValue);
                                                    }
                                                    if (weekOfMonth == 4) {
                                                        spendingWeek4.add(tienRutValue);
                                                    }
                                                    if (weekOfMonth == 5) {
                                                        spendingWeek5.add(tienRutValue);
                                                    }
                                                } else {
                                                    tienRutValue = 0.0;
                                                }

                                                //Log.d("FirestoreExample", "User " + documentSnapshot.getId() + " - Map Value: " + tienRutValue);
                                            }
                                        }


                                    }
                                }
                                double tongWeek1 = 0;
                                for (double i : spendingWeek1) {
                                    tongWeek1 = tongWeek1 + i;

                                }
                                spendingEntries.add(new BarEntry(0F, (float) tongWeek1));
                                double tongWeek2 = 0;
                                for (double i : spendingWeek2) {
                                    tongWeek2 = tongWeek2 + i;

                                }
                                spendingEntries.add(new BarEntry(1F, (float) tongWeek2));
                                double tongWeek3 = 0;
                                for (double i : spendingWeek3) {
                                    tongWeek3 = tongWeek3 + i;

                                }
                                spendingEntries.add(new BarEntry(2F, (float) tongWeek3));
                                double tongWeek4 = 0;
                                for (double i : spendingWeek4) {
                                    tongWeek4 = tongWeek4 + i;

                                }
                                spendingEntries.add(new BarEntry(3F, (float) tongWeek4));
                                double tongWeek5 = 0;
                                for (double i : spendingWeek5) {
                                    tongWeek5 = tongWeek5 + i;

                                }
                                spendingEntries.add(new BarEntry(4F, (float) tongWeek5));

                                firestoreCallback.onCallback(incomeEntries, spendingEntries);
                            } else {
                                Log.w("FirestoreExample", "Error getting documents.", task.getException());
                            }
                        }
                    });

                } else {
                    Log.w("FirestoreExample", "Error getting documents.", task.getException());
                }
            }
        });
    }
    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showMonthYearPicker1() {
        // Create a BottomSheetDialog to display the number picker
        BottomSheetDialog dialog = new BottomSheetDialog(HuHuongThuActivity.this);
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
                txtThangNam1.setText(monthYearString);
                String uid = currentUser.getUid();
                CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuNap").collection("subCollectionNap");
                collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    Double tienNapValue;
                    String tongTienNap = null;
                    double tongTien = 0;
                    //                    LocalDate currentDate = LocalDate.now();
//                    Month m = currentDate.getMonth();
//                    int y = currentDate.getYear();
//                    String month = String.valueOf(m).toLowerCase();
                    String hu = "Hũ hưởng thụ";
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
                                            String tenHu = (String) mapValue.get("tenHu");
//                                            Calendar calendar = Calendar.getInstance();
//                                            calendar.setTime(date);
//                                            int year = calendar.get(Calendar.YEAR);
//                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                            Calendar calendar = Calendar.getInstance();
                                            calendar.setTime(date);
                                            int year = calendar.get(Calendar.YEAR);
                                            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                            //get text from txtThangNam
                                            String monthYearText = txtThangNam1.getText().toString();
                                            String[] parts = monthYearText.split(" ");
                                            int month = Integer.parseInt(parts[1]);
                                            int yearPicker = Integer.parseInt(parts[2]);
                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                            String monthWord = months[month].toLowerCase();
                                            // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                                tienNapValue = (Double) mapValue.get("tienNap");
                                            } else {
                                                tienNapValue = 0.0;
                                            }
                                        }
                                    }
                                    tongTien += tienNapValue;
                                    Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                                    tongTienNap = currencyFormat.format(tongTien);

                                }
                            }

                            if (tongTienNap != null) {
                                Log.d("TAG", "Name tiền nap: " + tongTienNap);
                                txtTongTienNap.setText(tongTienNap);
                                CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuRut").collection("subCollectionNap");
                                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                                    Double tienRutValue;
                                    String tongTienRut = null;

                                    double tongTien = 0;

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
                                                            // get year and month
                                                            Timestamp getDate = (Timestamp) mapValue.get("ngayRut");
                                                            String tenHu = (String) mapValue.get("tenHu");
                                                            Date date = getDate.toDate();
                                                            Calendar calendar = Calendar.getInstance();
                                                            calendar.setTime(date);
                                                            int year = calendar.get(Calendar.YEAR);
                                                            int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                            String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);

                                                            //get text from txtThangNam
                                                            String monthYearText = txtThangNam1.getText().toString();
                                                            String[] parts = monthYearText.split(" ");
                                                            int month = Integer.parseInt(parts[1]);
                                                            int yearPicker = Integer.parseInt(parts[2]);
                                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                            String monthWord = months[month].toLowerCase();
                                                            // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                                                tienRutValue = (Double) mapValue.get("tienRut");
                                                            } else {
                                                                tienRutValue = 0.0;
                                                            }

                                                            //Log.d("FirestoreExample", "User " + documentSnapshot.getId() + " - Map Value: " + tienRutValue);
                                                        }
                                                    }
                                                    tongTien += tienRutValue;
                                                    Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                                                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                                                    tongTienRut = currencyFormat.format(tongTien);

                                                }
                                            }

                                            if (tongTienRut != null) {
                                                Log.d("TAG", "Name tiền rút: " + tongTienRut);
                                                txtTongTienRut.setText(tongTienRut);

                                            } else {
                                                Log.d("TAG", "Name field does not exist in this document.");
                                            }
                                        } else {
                                            Log.w("FirestoreExample", "Error getting documents.", task.getException());
                                        }
                                    }
                                });
                            } else {
                                Log.d("TAG", "Name field does not exist in this duLieuNap.");
                            }
                        } else {
                            Log.w("FirestoreExample", "Error getting documents.", task.getException());
                        }
                    }
                });
                List<GiaoDichNap> listGiaoDich = new ArrayList<>();
                //Get user uid
                //Retrieve data from fire store documents and collections
                CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
                collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    String hu = "Hũ hưởng thụ";
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
                                            String monthYearText = txtThangNam1.getText().toString();
                                            String[] parts = monthYearText.split(" ");
                                            int month = Integer.parseInt(parts[1]);
                                            int yearPicker = Integer.parseInt(parts[2]);
                                            String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                            String monthWord = months[month].toLowerCase();
                                            String tenHu = (String) mapValue.get("tenHu");
                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker && tenHu.equals(hu)) {
                                                String id = (String) mapValue.get("uuid");

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
                            transactionAdapter = new TransactionAdapter(HuHuongThuActivity.this, listGiaoDich);
                            transactionAdapter.setOnItemClickListener(HuHuongThuActivity.this);
                            recyclerView.setAdapter(transactionAdapter);
                            recyclerView.setLayoutManager(new LinearLayoutManager(HuHuongThuActivity.this));
                            //recyclerView.setAdapter(new TransactionAdapter(LichSuGiaoDichActivity.this, listGiaoDich));
                            recyclerView.setAdapter(transactionAdapter);
                        }
                    }
                });

                getValueForBarChart(new HomeFragment.FirestoreCallback() {
                    @Override
                    public void onCallback(List<BarEntry> incomeList, List<BarEntry> spendingList) {

                        BarDataSet incomeDataSet = new BarDataSet(incomeList, "Thu Nhập");
                        incomeDataSet.setColor(Color.GREEN);
                        incomeDataSet.setValueTextColor(Color.WHITE);
                        incomeDataSet.setValueTextSize(10F);
                        incomeDataSet.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                                return formatter.format(value);
                            }
                        });
                        BarDataSet spendingDataSet = new BarDataSet(spendingList, "Chi Tiêu");
                        spendingDataSet.setColor(Color.RED);
                        spendingDataSet.setValueTextColor(Color.WHITE);
                        spendingDataSet.setValueTextSize(10F);
                        spendingDataSet.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                DecimalFormat formatter = new DecimalFormat("###,###,###.## Đ");
                                return formatter.format(value);
                            }
                        });
                        BarData barData = new BarData(incomeDataSet, spendingDataSet);
                        barData.setBarWidth(0.3f);
                        mBarChart.getDescription().setText("");
                        // Set x-axis labels to display weeks
                        final String[] weekLabels = {"Tuần 1", "Tuần 2", "Tuần 3", "Tuần 4", "Tuần 5"};
                        XAxis xAxis = mBarChart.getXAxis();
                        xAxis.setTextColor(Color.WHITE);
                        xAxis.setValueFormatter(new IAxisValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, AxisBase axis) {
                                int index = (int) value;
                                if (index >= 0 && index < weekLabels.length) {
                                    return weekLabels[index];
                                } else {
                                    return "";
                                }
                            }
                        });

                        // Adjust x-axis position and spacing
                        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                        xAxis.setDrawGridLines(false);
                        xAxis.setGranularity(1f);
                        xAxis.setCenterAxisLabels(true);

// Set y-axis to start at 0
                        mBarChart.getAxisLeft().setAxisMinimum(0f);
                        mBarChart.getAxisRight().setEnabled(false);
                        mBarChart.getAxisLeft().setTextColor(Color.WHITE);
// Set bar data and adjust spacing
                        mBarChart.setData(barData);

                        int groupCount = 5; // Set the number of groups to 5
                        float groupSpace = 0.1f;
                        float barSpace = 0.15f;
                        mBarChart.getXAxis().setAxisMinimum(-barSpace);
                        mBarChart.getXAxis().setAxisMaximum(barData.getXMax() + barSpace);
                        mBarChart.getAxisLeft().setAxisMinimum(0);
                        mBarChart.groupBars(0, groupSpace, barSpace);

// Animate chart
                        mBarChart.animateY(1000);
                    }
                });
                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getValueForBarChartByMonth(final HomeFragment.FirestoreCallback firestoreCallback) {
        final List<BarEntry> incomeEntries = new ArrayList<>();
        final List<BarEntry> spendingEntries = new ArrayList<>();
        String uid = currentUser.getUid();
        CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuNap").collection("subCollectionNap");
        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            Double tienNapValue;
            LocalDate currentDate = LocalDate.now();
            String hu = "Hũ hưởng thụ";
            List<Double> inComeWeek1 = new ArrayList();
            List<Double> inComeWeek2 = new ArrayList();
            List<Double> inComeWeek3 = new ArrayList();
            List<Double> inComeWeek4 = new ArrayList();
            List<Double> inComeWeek5 = new ArrayList();

            List<Double> inComeThang1 = new ArrayList();
            List<Double> inComeThang2 = new ArrayList();
            List<Double> inComeThang3 = new ArrayList();
            List<Double> inComeThang4 = new ArrayList();
            List<Double> inComeThang5 = new ArrayList();
            List<Double> inComeThang6 = new ArrayList();
            List<Double> inComeThang7 = new ArrayList();
            List<Double> inComeThang8 = new ArrayList();
            List<Double> inComeThang9 = new ArrayList();
            List<Double> inComeThang10 = new ArrayList();
            List<Double> inComeThang11 = new ArrayList();
            List<Double> inComeThang12 = new ArrayList();

            List<Double> inComeThang13 = new ArrayList();

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
                                    String tenHu = (String) mapValue.get("tenHu");
                                    Date date = getDate.toDate();
                                    Calendar calendar = Calendar.getInstance();
                                    calendar.setTime(date);
                                    int year = calendar.get(Calendar.YEAR);
                                    int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                    String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                    Log.d("onComplete: ", monthName);
                                    //get text from txtThangNam
                                    String monthYearText = txtThangNam1.getText().toString();
                                    String[] parts = monthYearText.split(" ");
                                    int month = Integer.parseInt(parts[1]);
                                    int yearPicker = Integer.parseInt(parts[2]);
                                    String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December", "Tháng 13"};
                                    String monthWord = months[month].toLowerCase();
                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                    if (year == yearPicker && tenHu.equals(hu)) {
                                        tienNapValue = (Double) mapValue.get("tienNap");

                                        if (monthName.equals("January")) {
                                            inComeThang1.add(tienNapValue);
                                        }
                                        if (monthName.equals("February")) {
                                            inComeThang2.add(tienNapValue);
                                        }
                                        if (monthName.equals("March")) {
                                            inComeThang3.add(tienNapValue);
                                        }
                                        if (monthName.equals("April")) {
                                            inComeThang4.add(tienNapValue);
                                        }
                                        if (monthName.equals("May")) {
                                            inComeThang5.add(tienNapValue);
                                        }
                                        if (monthName.equals("Jun")) {
                                            inComeThang6.add(tienNapValue);
                                        }
                                        if (monthName.equals("July")) {
                                            inComeThang7.add(tienNapValue);
                                        }
                                        if (monthName.equals("August")) {
                                            inComeThang8.add(tienNapValue);
                                        }
                                        if (monthName.equals("September")) {
                                            inComeThang9.add(tienNapValue);
                                        }
                                        if (monthName.equals("October")) {
                                            inComeThang10.add(tienNapValue);
                                        }
                                        if (monthName.equals("November")) {
                                            inComeThang11.add(tienNapValue);
                                        }
                                        if (monthName.equals("December")) {
                                            inComeThang12.add(tienNapValue);
                                        }
                                        if (monthName.equals("Tháng 13")) {
                                            inComeThang13.add(tienNapValue);
                                        }
                                    } else {
                                        tienNapValue = 0.0;
                                    }
                                }
                            }
                        }
                    }
                    double tongThang1 = 0;
                    for (double i : inComeThang1) {
                        tongThang1 = tongThang1 + i;
                    }
                    incomeEntries.add(new BarEntry(0f, (float) tongThang1));

                    double tongThang2 = 0;
                    for (double i : inComeThang2) {
                        tongThang2 = tongThang2 + i;
                    }
                    incomeEntries.add(new BarEntry(1f, (float) tongThang2));

                    double tongThang3 = 0;
                    for (double i : inComeThang3) {
                        tongThang3 = tongThang3 + i;
                    }
                    incomeEntries.add(new BarEntry(2f, (float) tongThang3));

                    double tongThang4 = 0;
                    for (double i : inComeThang4) {
                        tongThang4 = tongThang4 + i;
                    }
                    incomeEntries.add(new BarEntry(3f, (float) tongThang4));

                    double tongThang5 = 0;
                    for (double i : inComeThang5) {
                        tongThang5 = tongThang5 + i;
                    }
                    incomeEntries.add(new BarEntry(4f, (float) tongThang5));

                    double tongThang6 = 0;
                    for (double i : inComeThang6) {
                        tongThang6 = tongThang6 + i;
                    }
                    incomeEntries.add(new BarEntry(5f, (float) tongThang6));

                    double tongThang7 = 0;
                    for (double i : inComeThang7) {
                        tongThang7 = tongThang7 + i;
                    }
                    incomeEntries.add(new BarEntry(6f, (float) tongThang7));

                    double tongThang8 = 0;
                    for (double i : inComeThang8) {
                        tongThang8 = tongThang8 + i;
                    }
                    incomeEntries.add(new BarEntry(7f, (float) tongThang8));

                    double tongThang9 = 0;
                    for (double i : inComeThang9) {
                        tongThang9 = tongThang9 + i;
                    }
                    incomeEntries.add(new BarEntry(8f, (float) tongThang9));

                    double tongThang10 = 0;
                    for (double i : inComeThang10) {
                        tongThang10 = tongThang10 + i;
                    }
                    incomeEntries.add(new BarEntry(9f, (float) tongThang10));

                    double tongThang11 = 0;
                    for (double i : inComeThang11) {
                        tongThang11 = tongThang11 + i;
                    }
                    incomeEntries.add(new BarEntry(10f, (float) tongThang11));

                    double tongThang12 = 0;
                    for (double i : inComeThang12) {
                        tongThang12 = tongThang12 + i;
                    }
                    incomeEntries.add(new BarEntry(11f, (float) tongThang12));

                    double tongThang13 = 0;
                    for (double i : inComeThang13) {
                        tongThang13 = tongThang13 + i;
                    }
                    incomeEntries.add(new BarEntry(12f, (float) tongThang13));

                    CollectionReference collectionReference = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuRut").collection("subCollectionNap");
                    collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

                        Double tienRutValue;
                        List<Double> spendingThang1 = new ArrayList();
                        List<Double> spendingThang2 = new ArrayList();
                        List<Double> spendingThang3 = new ArrayList();
                        List<Double> spendingThang4 = new ArrayList();
                        List<Double> spendingThang5 = new ArrayList();
                        List<Double> spendingThang6 = new ArrayList();
                        List<Double> spendingThang7 = new ArrayList();
                        List<Double> spendingThang8 = new ArrayList();
                        List<Double> spendingThang9 = new ArrayList();
                        List<Double> spendingThang10 = new ArrayList();
                        List<Double> spendingThang11 = new ArrayList();
                        List<Double> spendingThang12 = new ArrayList();
                        List<Double> spendingThang13 = new ArrayList();

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
                                                // get year and month
                                                Timestamp getDate = (Timestamp) mapValue.get("ngayRut");
                                                String tenHu = (String) mapValue.get("tenHu");
                                                Date date = getDate.toDate();
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTime(date);
                                                int year = calendar.get(Calendar.YEAR);
                                                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                                //
                                                String monthYearText = txtThangNam1.getText().toString();
                                                String[] parts = monthYearText.split(" ");
                                                int month = Integer.parseInt(parts[1]);
                                                int yearPicker = Integer.parseInt(parts[2]);
                                                String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December", "Tháng 13"};
                                                String monthWord = months[month].toLowerCase();
                                                // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền rút
                                                if (year == yearPicker && tenHu.equals(hu)) {
                                                    tienRutValue = (Double) mapValue.get("tienRut");
                                                    if (monthName.equals("January")) {
                                                        spendingThang1.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("February")) {
                                                        spendingThang2.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("March")) {
                                                        spendingThang3.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("April")) {
                                                        spendingThang4.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("May")) {
                                                        spendingThang5.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("Jun")) {
                                                        spendingThang6.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("July")) {
                                                        spendingThang7.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("August")) {
                                                        spendingThang8.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("September")) {
                                                        spendingThang9.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("October")) {
                                                        spendingThang10.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("November")) {
                                                        spendingThang11.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("December")) {
                                                        spendingThang12.add(tienRutValue);
                                                    }
                                                    if (monthName.equals("Tháng 13")) {
                                                        spendingThang13.add(tienRutValue);
                                                    }
                                                } else {
                                                    tienRutValue = 0.0;
                                                }

                                                //Log.d("FirestoreExample", "User " + documentSnapshot.getId() + " - Map Value: " + tienRutValue);
                                            }
                                        }


                                    }
                                }

                                double tongRutThang1 = 0;
                                for (double i : spendingThang1) {
                                    tongRutThang1 = tongRutThang1 + i;
                                }
                                spendingEntries.add(new BarEntry(0F, (float) tongRutThang1));

                                double tongRutThang2 = 0;
                                for (double i : spendingThang2) {
                                    tongRutThang2 = tongRutThang2 + i;
                                }
                                spendingEntries.add(new BarEntry(1F, (float) tongRutThang2));

                                double tongRutThang3 = 0;
                                for (double i : spendingThang3) {
                                    tongRutThang3 = tongRutThang3 + i;
                                }
                                spendingEntries.add(new BarEntry(2F, (float) tongRutThang3));

                                double tongRutThang4 = 0;
                                for (double i : spendingThang4) {
                                    tongRutThang4 = tongRutThang4 + i;
                                }
                                spendingEntries.add(new BarEntry(3F, (float) tongRutThang4));

                                double tongRutThang5 = 0;
                                for (double i : spendingThang5) {
                                    tongRutThang5 = tongRutThang5 + i;
                                }
                                spendingEntries.add(new BarEntry(4F, (float) tongRutThang5));

                                double tongRutThang6 = 0;
                                for (double i : spendingThang6) {
                                    tongRutThang6 = tongRutThang6 + i;
                                }
                                spendingEntries.add(new BarEntry(5F, (float) tongRutThang6));

                                double tongRutThang7 = 0;
                                for (double i : spendingThang7) {
                                    tongRutThang7 = tongRutThang7 + i;
                                }
                                spendingEntries.add(new BarEntry(6F, (float) tongRutThang7));

                                double tongRutThang8 = 0;
                                for (double i : spendingThang8) {
                                    tongRutThang8 = tongRutThang8 + i;
                                }
                                spendingEntries.add(new BarEntry(7F, (float) tongRutThang8));

                                double tongRutThang9 = 0;
                                for (double i : spendingThang9) {
                                    tongRutThang9 = tongRutThang9 + i;
                                }
                                spendingEntries.add(new BarEntry(8F, (float) tongRutThang9));

                                double tongRutThang10 = 0;
                                for (double i : spendingThang10) {
                                    tongRutThang10 = tongRutThang10 + i;
                                }
                                spendingEntries.add(new BarEntry(9F, (float) tongRutThang10));

                                double tongRutThang11 = 0;
                                for (double i : spendingThang11) {
                                    tongRutThang11 = tongRutThang11 + i;
                                }
                                spendingEntries.add(new BarEntry(10F, (float) tongRutThang11));

                                double tongRutThang12 = 0;
                                for (double i : spendingThang12) {
                                    tongRutThang12 = tongRutThang12 + i;
                                }
                                spendingEntries.add(new BarEntry(11F, (float) tongRutThang12));

                                double tongRutThang13 = 0;
                                for (double i : spendingThang13) {
                                    tongRutThang13 = tongRutThang13 + i;
                                }
                                spendingEntries.add(new BarEntry(12F, (float) tongRutThang13));
                                firestoreCallback.onCallback(incomeEntries, spendingEntries);
                            } else {
                                Log.w("FirestoreExample", "Error getting documents.", task.getException());
                            }
                        }
                    });

                } else {
                    Log.w("FirestoreExample", "Error getting documents.", task.getException());
                }
            }
        });
    }
}