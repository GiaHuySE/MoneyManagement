package com.example.doan;

import static androidx.constraintlayout.helper.widget.MotionEffect.TAG;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.doan.entity.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.Map;

public class PersonFragment extends Fragment {
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    ImageView imgDangXuat;
    FirebaseFirestore firestore;
    FirebaseUser currentUser = mAuth.getCurrentUser();
    TextView txtUserName;
    LinearLayout linearLayoutChinhHu;
    @SuppressLint("MissingInflatedId")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_person, container, false);

        txtUserName = view.findViewById(R.id.txtUserName);
        imgDangXuat = view.findViewById(R.id.imgDangXuat);
        linearLayoutChinhHu= view.findViewById(R.id.layOutChinhHu);
//        Button btnDangXuat = view.findViewById(R.id.btnDangXuat);
//        btnDangXuat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                mAuth.signOut();
//                Intent intentSignOut = new Intent(getActivity(), LogInActivity.class);
//                startActivity(intentSignOut);
//            }
//        });
        // Inflate the layout for this fragment
        imgDangXuat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAuth.signOut();
                Intent intentSignOut = new Intent(getActivity(), LogInActivity.class);
                startActivity(intentSignOut);
            }
        });
        firestore = FirebaseFirestore.getInstance();
        String uid = currentUser.getUid();
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

                    txtUserName.setText(tenNguoiDung);
                }
                if(value == null || !value.exists()) {
                    txtUserName.setText("Radical Beam");
                }
            }
        });

        linearLayoutChinhHu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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

                            Object obj = mapObject.get("HuThietYeu");
                            Map<String, Object> huDauTu = (Map<String, Object>) mapObject.get("HuDauTu");
                            Map<String, Object> huGiaoDuc = (Map<String, Object>) mapObject.get("HuGiaoDuc");
                            Map<String, Object> huHuongThu = (Map<String, Object>) mapObject.get("HuHuongThu");
                            Map<String, Object> huThienTam = (Map<String, Object>) mapObject.get("HuThienTam");
                            Map<String, Object> huThietYeu = (Map<String, Object>) mapObject.get("HuThietYeu");
                            Map<String, Object> huTietKiem = (Map<String, Object>) mapObject.get("HuTietKiem");

                            Integer tyLeThietYeu = ((Long) huThietYeu.get("tyLe")).intValue();
                            Integer tyLeDauTu = ((Long) huDauTu.get("tyLe")).intValue();
                            Integer tyLeGiaoDuc = ((Long) huGiaoDuc.get("tyLe")).intValue();
                            Integer tyLeHuongThu = ((Long) huHuongThu.get("tyLe")).intValue();
                            Integer tyLeThienTam = ((Long) huThienTam.get("tyLe")).intValue();
                            Integer tyLeTietKiem = ((Long) huTietKiem.get("tyLe")).intValue();
                            Intent intent = new Intent(getActivity(), ChinhHuActivity.class);
                            intent.putExtra("tyLeThietYeu", tyLeThietYeu);
                            intent.putExtra("tyLeDauTu", tyLeDauTu);
                            intent.putExtra("tyLeGiaoDuc", tyLeGiaoDuc);
                            intent.putExtra("tyLeHuongThu", tyLeHuongThu);
                            intent.putExtra("tyLeThienTam", tyLeThienTam);
                            intent.putExtra("tyLeTietKiem", tyLeTietKiem);

                            startActivity(intent);
                        } else {
                            Log.d("TAG_DATA_NULL", "Current data: null");
                        }


                    }
                });
            }
        });
        return view;
    }
}