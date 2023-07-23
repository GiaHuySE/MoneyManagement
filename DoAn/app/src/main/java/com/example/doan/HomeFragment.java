package com.example.doan;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.icu.util.Calendar;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.NumberPicker;
import android.widget.PopupMenu;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.entity.GiaoDichNap;
import com.example.doan.entity.Hu;
import com.example.doan.entity.LichSuGiaoDich;
import com.example.doan.entity.User;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LegendEntry;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.navigation.NavigationView;
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

import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class HomeFragment extends Fragment implements TransactionAdapter.OnItemClickListener,ItemOptionsBottomSheet.OnUserActionListener{
    FirebaseFirestore firestore;
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    FirebaseUser currentUser = mAuth.getCurrentUser();
    TextView txtPhanTramThietYeu, txtPhanTramGiaoDuc, txtPhanTramTietKiem, txtPhanTramHuongThu, txtPhanTramDauTu, txtPhanTramThienTam, txtThangNam;
    TextView txtSoTienThietYeu, txtSoTienGiaoDuc, txtSoTienTietKiem, txtSoTienHuongThu, txtSoTienDauTu, txtSoTienThienTam, txtTongTienRut, txtTongTienNap, txtSoDu,txtThangNam1;
    TextView txtTenNguoiDung;
    Button buttonChuyenHu, buttonChinhHu,buttonTatCaGiaoDich;
    PieChart pieChart;
    BarChart mBarChart;
    ConstraintLayout layoutThangNam,layoutThangNam1;
    RecyclerView recyclerView;
    LinearLayout huThietYeuLayout,huGiaoDucLayout,huTietKiemLayout,huHuongThuLayout,huDauTuLayout,huThienTamLayout;
    CheckBox chckThongKeTheoThang;
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private ImageView imageView;

    @RequiresApi(api = Build.VERSION_CODES.O)
    @SuppressLint({"MissingInflatedId", "LongLogTag"})
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        // Initialize the Firestore database
        firestore = FirebaseFirestore.getInstance();

        txtPhanTramThietYeu = view.findViewById(R.id.txtPhanTramThietYeu);
        txtPhanTramGiaoDuc = view.findViewById(R.id.txtPhanTramGiaoDuc);
        txtPhanTramTietKiem = view.findViewById(R.id.txtPhanTramTietKiem);
        txtPhanTramHuongThu = view.findViewById(R.id.txtPhanTramHuongThu);
        txtPhanTramDauTu = view.findViewById(R.id.txtPhanTramDauTu);
        txtPhanTramThienTam = view.findViewById(R.id.txtPhanTramThienTam);

        txtSoTienThietYeu = view.findViewById(R.id.textViewSoTienThietYeu);
        txtSoTienGiaoDuc = view.findViewById(R.id.textViewSoTienGiaoDuc);
        txtSoTienTietKiem = view.findViewById(R.id.textViewSoTienTietKiem);
        txtSoTienHuongThu = view.findViewById(R.id.textViewSoTienHuongThu);
        txtSoTienDauTu = view.findViewById(R.id.textViewSoTienDauTu);
        txtSoTienThienTam = view.findViewById(R.id.textViewSoTienThienTam);
        txtTongTienRut = view.findViewById(R.id.textViewtongTienRut);
        txtTongTienNap = view.findViewById(R.id.textViewTongTienNap);
        txtSoDu = view.findViewById(R.id.txtSoDu);
        mBarChart = view.findViewById(R.id.barChart);
        layoutThangNam = view.findViewById(R.id.layoutThangNam);
        txtThangNam = view.findViewById(R.id.txtThangNam);
        recyclerView = view.findViewById(R.id.recycleViewGiaoDich);
        buttonTatCaGiaoDich = view.findViewById(R.id.btnTatCaGiaoDich);
        huThietYeuLayout = view.findViewById(R.id.linearLayout1);
        huGiaoDucLayout = view.findViewById(R.id.linearLayout2);
        huTietKiemLayout = view.findViewById(R.id.linearLayout3);
        huHuongThuLayout = view.findViewById(R.id.linearLayout4);
        huDauTuLayout = view.findViewById(R.id.linearLayout5);
        huThienTamLayout = view.findViewById(R.id.linearLayout6);
//        buttonChinhHu = view.findViewById(R.id.btnChinhTiLe);
//        txtSoDuThang = view.findViewById(R.id.txtSoDuThang);
        layoutThangNam1 =view.findViewById(R.id.layoutThangNam1);
        txtThangNam1 = view.findViewById(R.id.txtThangNam1);
        txtTenNguoiDung = view.findViewById(R.id.txtTenNguoiDung);
        chckThongKeTheoThang = view.findViewById(R.id.chckBoxThongKe);
        //Get user uid
        String uid = currentUser.getUid();
        //Retrieve data from fire store documents and collections
        CollectionReference useRef = firestore.collection("users");
        DocumentReference userDocRef = useRef.document(uid).collection("duLieuHu").document("duLieuTien");

        // Get user name
        CollectionReference userNameRef = firestore.collection("users");
        DocumentReference userNameDocRef = userNameRef.document(uid).collection("duLieuNguoiDung").document("duLieuTaiKhoan");
        userNameDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if(error != null) {
                    Log.w(TAG, "Listen failed on tenNguoiDung.", error);
                    return;
                }
                if(value != null && value.exists()) {
                    User user = value.toObject(User.class);
                    String tenNguoiDung = user.getName();

                    txtTenNguoiDung.setText(tenNguoiDung);
                }
                if(value == null || !value.exists()) {
                    txtTenNguoiDung.setText("Radical Beam");
                }
            }
        });

        userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
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

                    Integer tyLeThietYeu = ((Long) huThietYeu.get("tyLe")).intValue();
                    Double soTienThietYeu = (Double) huThietYeu.get("soTien");
                    Integer tyLeDauTu = ((Long) huDauTu.get("tyLe")).intValue();
                    Double soTienDauTu = (Double) huDauTu.get("soTien");
                    Integer tyLeGiaoDuc = ((Long) huGiaoDuc.get("tyLe")).intValue();
                    Double soTienGiaoDuc = (Double) huGiaoDuc.get("soTien");
                    Integer tyLeHuongThu = ((Long) huHuongThu.get("tyLe")).intValue();
                    Double soTienHuongThu = (Double) huHuongThu.get("soTien");
                    Integer tyLeThienTam = ((Long) huThienTam.get("tyLe")).intValue();
                    Double soTienThienTam = (Double) huThienTam.get("soTien");
                    Integer tyLeTietKiem = ((Long) huTietKiem.get("tyLe")).intValue();
                    Double soTienTietKiem = (Double) huTietKiem.get("soTien");

                    Double soTienTong = soTienThietYeu + soTienDauTu + soTienGiaoDuc + soTienHuongThu + soTienThienTam + soTienTietKiem;

                    //format tiền VietNam
                    Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                    NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                    String tienThietYeu = currencyFormat.format(soTienThietYeu);
                    String tienDauTu = currencyFormat.format(soTienDauTu);
                    String tienGiaoDuc = currencyFormat.format(soTienGiaoDuc);
                    String tienHuongThu = currencyFormat.format(soTienHuongThu);
                    String tienThienTam = currencyFormat.format(soTienThienTam);
                    String tienTietKiem = currencyFormat.format(soTienTietKiem);
                    String tienTong = currencyFormat.format(soTienTong);


                    txtPhanTramThietYeu.setText(String.valueOf(tyLeThietYeu) + "%");
                    txtSoTienThietYeu.setText(tienThietYeu + "");
                    txtPhanTramDauTu.setText(String.valueOf(tyLeDauTu) + "%");
                    txtSoTienDauTu.setText(tienDauTu + "");
                    txtPhanTramGiaoDuc.setText(String.valueOf(tyLeGiaoDuc) + "%");
                    txtSoTienGiaoDuc.setText(tienGiaoDuc + "");
                    txtPhanTramHuongThu.setText(String.valueOf(tyLeHuongThu) + "%");
                    txtSoTienHuongThu.setText(tienHuongThu + "");
                    txtPhanTramThienTam.setText(String.valueOf(tyLeThienTam) + "%");
                    txtSoTienThienTam.setText(tienThienTam + "");
                    txtPhanTramTietKiem.setText(String.valueOf(tyLeTietKiem) + "%");
                    txtSoTienTietKiem.setText(tienTietKiem + "");
                    txtSoDu.setText(tienTong);
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
                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền rút
                                    if (month.equals(monthName.toLowerCase()) && year == y) {
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

                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                    if (month.equals(monthName.toLowerCase()) && year == y) {
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

                        txtTongTienNap.setText(tongTienNap);

                    } else {
                        Log.d("TAG", "Name field does not exist in this duLieuNap.");
                    }
                } else {
                    Log.w("FirestoreExample", "Error getting documents.", task.getException());
                }
            }
        });

//        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                if(task.isSuccessful()){
//                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()){
//                        if (documentSnapshot.exists()) {
//                            Map<String, Object> tienRutInfo = documentSnapshot.getData();
//                            for (String key : tienRutInfo.keySet()) {
//                                Object value = tienRutInfo.get(key);
//                                Log.d("Key id .",key);
//                            }
//                    }
//                }
//            }}
//        });

        //Chuyển màn hình sang incomeActivity và spendingActivity
        ConstraintLayout incomeView = view.findViewById(R.id.incomeLayout);
        incomeView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), IncomeActivity.class);
                int ptramThietYeu = Integer.parseInt(String.valueOf(txtPhanTramThietYeu.getText()).replaceAll("%", ""));
                int ptramGiaoDuc= Integer.parseInt(String.valueOf(txtPhanTramGiaoDuc.getText()).replaceAll("%", ""));
                int ptramTietKiem = Integer.parseInt(String.valueOf(txtPhanTramTietKiem.getText()).replaceAll("%", ""));
                int ptramHuongThu = Integer.parseInt(String.valueOf(txtPhanTramHuongThu.getText()).replaceAll("%", ""));
                int ptramDauTu  = Integer.parseInt(String.valueOf(txtPhanTramHuongThu.getText()).replaceAll("%", ""));
                int ptramThienTam = Integer.parseInt(String.valueOf(txtPhanTramThienTam.getText()).replaceAll("%", ""));
                intent.putExtra("ptramThietYeu", ptramThietYeu);
                intent.putExtra("ptramGiaoDuc", ptramGiaoDuc);
                intent.putExtra("ptramTietKiem", ptramTietKiem);
                intent.putExtra("ptramHuongThu", ptramHuongThu);
                intent.putExtra("ptramDauTu", ptramDauTu);
                intent.putExtra("ptramThienTam", ptramThienTam);
                Log.d("ptramThietYeu ", String.valueOf(ptramThietYeu));
                startActivity(intent);
            }
        });
        ConstraintLayout spendingView = view.findViewById(R.id.spendingLayout);
        spendingView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), SpendingActivity.class);
                startActivity(intent);
            }
        });




        // Lấy dữ liệu trong firestore vào biểu đồ tròn
        pieChart = view.findViewById(R.id.pieChart);
        List<PieEntry> entries = new ArrayList<>();
        userDocRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    Map<String, Object> data = documentSnapshot.getData();
                    if (data != null) {
                        for (String fieldName : data.keySet()) {
                            //Lấy tên hũ và tỷ lệ
                            Map<String, Object> hu = (Map<String, Object>) data.get(fieldName);
                            long tyLe = (Long) hu.get("tyLe");
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
                            entries.add(new PieEntry(tyLe, tenHu));
                        }
                        ArrayList<Integer> colors = new ArrayList<>();
                        for (int color : ColorTemplate.MATERIAL_COLORS) {
                            colors.add(color);
                        }
                        for (int color : ColorTemplate.VORDIPLOM_COLORS) {
                            colors.add(color);
                        }
                        PieDataSet dataSet = new PieDataSet(entries, "Tỷ Lệ");
                        dataSet.setColors(colors);
                        PieData pieData = new PieData(dataSet);
                        pieData.setDrawValues(true);
                        pieChart.setUsePercentValues(true);
                        pieData.setValueFormatter(new PercentFormatter());
                        pieData.setValueFormatter(new IValueFormatter() {
                            @Override
                            public String getFormattedValue(float value, Entry entry, int dataSetIndex, ViewPortHandler viewPortHandler) {
                                return (int) value + "%";
                            }
                        });
                        pieData.setValueTextSize(12f);
                        pieData.setValueTextColor(Color.BLACK);
                        pieChart.getLegend().setEnabled(false);
                        pieChart.setData(pieData);
                        pieChart.setDrawHoleEnabled(true);
                        pieChart.setEntryLabelTextSize(12);
                        pieChart.setEntryLabelColor(Color.BLACK);
                        pieChart.setCenterText("Tỷ lệ các hũ");
                        pieChart.setCenterTextSize(20);
                        pieChart.getDescription().setEnabled(false);
                        pieChart.invalidate();

                    } else {
                        Log.d(TAG, "Document does not exist");
                    }
                }
            }
        });

//        buttonChinhHu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getActivity(),ChinhHuActivity.class);
//                startActivity(intent);
//            }
//        });
//        buttonChinhHu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                //Get user uid
//                String uid = currentUser.getUid();
//                //Retrieve data from fire store documents and collections
//                CollectionReference useRef = firestore.collection("users");
//                DocumentReference userDocRef = useRef.document(uid).collection("duLieuHu").document("duLieuTien");
//                userDocRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
//                    @Override
//                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
//                        if (error != null) {
//                            Log.w(TAG, "Listen failed.", error);
//                            return;
//                        }
//                        if (value != null && value.exists()) {
//                            Map<String, Object> mapObject = value.getData();
//
//                            Object obj = mapObject.get("HuThietYeu");
//                            Map<String, Object> huDauTu = (Map<String, Object>) mapObject.get("HuDauTu");
//                            Map<String, Object> huGiaoDuc = (Map<String, Object>) mapObject.get("HuGiaoDuc");
//                            Map<String, Object> huHuongThu = (Map<String, Object>) mapObject.get("HuHuongThu");
//                            Map<String, Object> huThienTam = (Map<String, Object>) mapObject.get("HuThienTam");
//                            Map<String, Object> huThietYeu = (Map<String, Object>) mapObject.get("HuThietYeu");
//                            Map<String, Object> huTietKiem = (Map<String, Object>) mapObject.get("HuTietKiem");
//
//                            Integer tyLeThietYeu = ((Long) huThietYeu.get("tyLe")).intValue();
//                            Integer tyLeDauTu = ((Long) huDauTu.get("tyLe")).intValue();
//                            Integer tyLeGiaoDuc = ((Long) huGiaoDuc.get("tyLe")).intValue();
//                            Integer tyLeHuongThu = ((Long) huHuongThu.get("tyLe")).intValue();
//                            Integer tyLeThienTam = ((Long) huThienTam.get("tyLe")).intValue();
//                            Integer tyLeTietKiem = ((Long) huTietKiem.get("tyLe")).intValue();
//                            Intent intent = new Intent(getActivity(), ChinhHuActivity.class);
//                            intent.putExtra("tyLeThietYeu", tyLeThietYeu);
//                            intent.putExtra("tyLeDauTu", tyLeDauTu);
//                            intent.putExtra("tyLeGiaoDuc", tyLeGiaoDuc);
//                            intent.putExtra("tyLeHuongThu", tyLeHuongThu);
//                            intent.putExtra("tyLeThienTam", tyLeThienTam);
//                            intent.putExtra("tyLeTietKiem", tyLeTietKiem);
//
//                            startActivity(intent);
//                        } else {
//                            Log.d("TAG_DATA_NULL", "Current data: null");
//                        }
//
//
//                    }
//                });
//
//            }
//        });


        getValueForBarChart(new FirestoreCallback() {
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
                    getValueForBarChart(new FirestoreCallback() {
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

        Calendar calendar = Calendar.getInstance();
        int currentMonth = calendar.get(Calendar.MONTH) + 1; // add 1 because January is 0
        int currentYear = calendar.get(Calendar.YEAR);
        txtThangNam.setText("Tháng " + currentMonth + " " + currentYear);
        txtThangNam1.setText("Tháng " + currentMonth + " " + currentYear);
        String monthYearText = txtThangNam.getText().toString();
        String[] parts = monthYearText.split(" ");
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);

        layoutThangNam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonthYearPicker();
            }
        });

        layoutThangNam1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showMonthYearPicker1();
            }
        });

        List<GiaoDichNap> list = new ArrayList<>();
        CollectionReference collectionReference2 = firestore.collection("users").document(uid).collection("duLieuHu").document("lichSuGiaoDich").collection("subCollectionGiaoDich");
        collectionReference2.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot documentSnapshot : task.getResult()) {
                        if (documentSnapshot.exists()) {
                            Map<String, Object> lichSuGiaoDichInfo = documentSnapshot.getData();
                            for (String key : lichSuGiaoDichInfo.keySet()) {
                                Object value = lichSuGiaoDichInfo.get(key);
                                if (value instanceof Map) {
                                    Map<String, Object> mapValue = (Map<String, Object>) value;
                                    Timestamp getDate = (Timestamp) mapValue.get("ngayNap");
                                    String id = (String) mapValue.get("uuid");
                                    String tenHu = (String) mapValue.get("tenHu");
                                    Double soTien = (Double) mapValue.get("tienNap");
                                    String loaiGiaoDich = (String) mapValue.get("loaiGiaoDich");
                                    String moTa = (String) mapValue.get("moTa");
                                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MM");
                                    GiaoDichNap giaoDichNap = new GiaoDichNap(id,tenHu,soTien,getDate.toDate(),moTa,loaiGiaoDich);
                                    list.add(giaoDichNap);
                                }
                            }
                        }
                    }
                    Comparator<GiaoDichNap> comparator = Comparator.comparing(GiaoDichNap::getNgayNap);
                    Comparator<GiaoDichNap> reverseSort = comparator.reversed();
                    list.sort(Comparator.comparing(obj->Math.abs(System.currentTimeMillis()-obj.getNgayNap().getTime())));
                    Collections.sort(list,reverseSort);
                    List<GiaoDichNap> sortedList = new ArrayList<>();
                    if(!list.isEmpty()){
                        if(list.size() >= 3){
                            sortedList.addAll(list.subList(0,3));
                        }
                        if(list.size() == 2){
                            sortedList.addAll(list.subList(0,2));
                        }
                        if(list.size() ==1){
                            sortedList.addAll(list.subList(0,1));
                        }

                    }
                    recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                    recyclerView.setAdapter(new TransactionAdapter(getActivity(), sortedList));
                }
            }

        });


        buttonTatCaGiaoDich.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),LichSuGiaoDichActivity.class);
                startActivity(intent);
            }
        });

//        imgeMenu.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                PopupMenu popupMenu = new PopupMenu(getActivity(),v);
//                popupMenu.getMenuInflater().inflate(R.menu.main_menu,popupMenu.getMenu());
//                popupMenu.show();
//            }
//        });

        huThietYeuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HuThietYeuActivity.class);
                startActivity(intent);
            }
        });
        huGiaoDucLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HuGiaoDucActivity.class);
                startActivity(intent);
            }
        });
        huTietKiemLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HuTietKiemActivity.class);
                startActivity(intent);
            }
        });
        huHuongThuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HuHuongThuActivity.class);
                startActivity(intent);
            }
        });
        huDauTuLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HuDauTuActivity.class);
                startActivity(intent);
            }
        });
        huThienTamLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),HuThienTamActivity.class);
                startActivity(intent);
            }
        });


        CollectionReference collectionReference3 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuRut").collection("subCollectionNap");
        collectionReference3.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {

            Double tienRutValue;
            String tongTienRut = null;

            double tongTienRutTrongThang = 0;
            LocalDate currentDate = LocalDate.now();
            Month m = currentDate.getMonth();
            int y = currentDate.getYear();
            String month = String.valueOf(m).toLowerCase();

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
                                    Log.d("Month nap", monthName);
                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền rút
                                    if (month.equals(monthName.toLowerCase()) && year == y) {
                                        tienRutValue = (Double) mapValue.get("tienRut");
                                    } else {
                                        tienRutValue = 0.0;
                                    }

                                    //Log.d("FirestoreExample", "User " + documentSnapshot.getId() + " - Map Value: " + tienRutValue);
                                }
                            }
                            tongTienRutTrongThang += tienRutValue;
//                            Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
//                            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
//                            tongTienRut = currencyFormat.format(tongTienRutTrongThang);

                        }
                    }

                    if (tongTienRutTrongThang >= 0.0) {
                        Log.d("TAG", "Name tiền rút: " + tongTienRutTrongThang);

                        CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuNap").collection("subCollectionNap");
                        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                            Double tienNapValue;
                            String tongTienNap = null;
                            double tongTienNapTrongThang = 0;
                            LocalDate currentDate = LocalDate.now();
                            Month m = currentDate.getMonth();
                            int y = currentDate.getYear();
                            String month = String.valueOf(m).toLowerCase();

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

                                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                                    if (month.equals(monthName.toLowerCase()) && year == y) {
                                                        tienNapValue = (Double) mapValue.get("tienNap");
                                                    } else {
                                                        tienNapValue = 0.0;
                                                    }
                                                }
                                            }
                                            tongTienNapTrongThang += tienNapValue;
                                            Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                                            NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                                            tongTienNap = currencyFormat.format(tongTienNapTrongThang);

                                        }
                                    }

                                    if (tongTienNapTrongThang >= 0) {
                                        Double soDu = tongTienNapTrongThang - tongTienRutTrongThang;
                                        Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
                                        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN); // Create a currency formatter for the locale
                                        String soDuThang = currencyFormat.format(soDu);
//                                        txtSoDuThang.setText(soDuThang);

                                    } else {
                                        Log.d("TAG", "Name field does not exist in this duLieuNap.1");
                                    }
                                } else {
                                    Log.w("FirestoreExample", "Error getting documents.", task.getException());
                                }
                            }
                        });

                    } else {
                        Log.d("TAG", "Name field does not exist in this document.");
                    }
                } else {
                    Log.w("FirestoreExample", "Error getting documents.", task.getException());
                }
            }
        });
        return view;
    }


    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showMonthYearPicker() {
        // Create a BottomSheetDialog to display the number picker
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
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

                if(chckThongKeTheoThang.isChecked()){
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
                }
                else {
                    getValueForBarChart(new FirestoreCallback() {
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
                }
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
    public void onItemClick(GiaoDichNap giaoDichNap) {
        ItemOptionsBottomSheet itemOptionsBottomSheet = new ItemOptionsBottomSheet();
        itemOptionsBottomSheet.setListener(this);
        itemOptionsBottomSheet.setGiaoDichNap(giaoDichNap);
        itemOptionsBottomSheet.show(getActivity().getSupportFragmentManager(), "option_dialog");
    }

    @Override
    public void onUpdate(GiaoDichNap giaoDichNap) {

    }

    @Override
    public void onDelete(GiaoDichNap giaoDichNap) {

    }

    public interface FirestoreCallback {
        void onCallback(List<BarEntry> incomeList, List<BarEntry> spendingList);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getValueForBarChart(final FirestoreCallback firestoreCallback) {
        final List<BarEntry> incomeEntries = new ArrayList<>();
        final List<BarEntry> spendingEntries = new ArrayList<>();
        String uid = currentUser.getUid();
        CollectionReference collectionReference1 = firestore.collection("users").document(uid).collection("duLieuHu").document("duLieuNap").collection("subCollectionNap");
        collectionReference1.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            Double tienNapValue;
            LocalDate currentDate = LocalDate.now();

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
                                    Date date = getDate.toDate();
                                    Calendar calendar = Calendar.getInstance(new Locale("vi", "VN"));
                                    calendar.setFirstDayOfWeek(Calendar.MONDAY); // set Monday as the first day of the week
                                    calendar.set(Calendar.DAY_OF_MONTH, 1);
                                    calendar.add(Calendar.DAY_OF_MONTH, 27);
                                    //calendar.setDayOfMonth( 1 );
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
                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                    if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker) {
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
                                                Date date = getDate.toDate();
//                                                Calendar calendar = Calendar.getInstance(new Locale("vi", "VN"));
//                                                calendar.setFirstDayOfWeek(Calendar.MONDAY); // set Monday as the first day of the week
                                                Calendar calendar = Calendar.getInstance(new Locale("vi", "VN"));
                                                calendar.setFirstDayOfWeek(Calendar.MONDAY); // set Monday as the first day of the week
                                                calendar.set(Calendar.DAY_OF_MONTH, 1);
                                                calendar.add(Calendar.DAY_OF_MONTH, 27);
                                                calendar.setTime(date);
                                                int year = calendar.get(Calendar.YEAR);
                                                int weekOfMonth = calendar.get(Calendar.WEEK_OF_MONTH);
                                                String monthName = new SimpleDateFormat("MMMM", Locale.ENGLISH).format(date);
                                                //
                                                String monthYearText = txtThangNam.getText().toString();
                                                String[] parts = monthYearText.split(" ");
                                                int month = Integer.parseInt(parts[1]);
                                                int yearPicker = Integer.parseInt(parts[2]);
                                                String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December"};
                                                String monthWord = months[month].toLowerCase();
                                                // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền rút
                                                if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker) {
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


    private Hu castObjectToHu(Object o) throws IllegalArgumentException, IllegalAccessException {
        Class<? extends Object> clazz = o.getClass();
        Hu huEntity = new Hu();
        for (Field f : clazz.getDeclaredFields()) {
            f.setAccessible(true);
            if (f.isSynthetic()) {
                continue;
            } else {
                System.out.println(f.getName() + " = " + f.get(o));
                Log.d("MAP", f.getName() + " = " + f.get(o));
                Log.d("FIELD_NAME", f.getName());
                switch (f.getName().trim()) {
                    case "tyLe":
                        String txtTyLe = f.get(o).toString();
                        huEntity.setTyLe(Integer.parseInt(txtTyLe));
                        break;
                    case "soTien":
                        String txtSoTien = f.get(o).toString();
                        huEntity.setSoTien(Double.parseDouble(txtSoTien));
                        break;
                    default:
                        System.out.println("Default case");
                        break;
                }
            }

        }
        return huEntity;
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    private void showMonthYearPicker1() {
        // Create a BottomSheetDialog to display the number picker
        BottomSheetDialog dialog = new BottomSheetDialog(getActivity());
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
                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker) {
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
                                                            if (monthWord.equals(monthName.toLowerCase()) && year == yearPicker) {
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
                                    String monthYearText = txtThangNam.getText().toString();
                                    String[] parts = monthYearText.split(" ");
                                    int month = Integer.parseInt(parts[1]);
                                    int yearPicker = Integer.parseInt(parts[2]);
                                    String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December", "Tháng 13"};
                                    String monthWord = months[month].toLowerCase();
                                    // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền nạp
                                    if (year == yearPicker ) {
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
                                                String monthYearText = txtThangNam.getText().toString();
                                                String[] parts = monthYearText.split(" ");
                                                int month = Integer.parseInt(parts[1]);
                                                int yearPicker = Integer.parseInt(parts[2]);
                                                String[] months = new String[]{"", "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", "November", "December", "Tháng 13"};
                                                String monthWord = months[month].toLowerCase();
                                                // so sánh tháng và năm trong giao dịch nạp nếu trùng sẽ cộng toàn bộ số tiền rút
                                                if (year == yearPicker ) {
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

