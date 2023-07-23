package com.example.doan;

import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.entity.GiaoDichNap;
import com.example.doan.entity.LichSuGiaoDich;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
public class ItemOptionsBottomSheet extends BottomSheetDialogFragment {
    private GiaoDichNap giaoDichNap;
    private OnUserActionListener listener;
    private AlertDialog.Builder builder;

    public interface OnUserActionListener {
        void onUpdate(GiaoDichNap giaoDichNap);
        void onDelete(GiaoDichNap giaoDichNap);
    }

//    public void setLichSuGiaoDich(LichSuGiaoDich lichSuGiaoDich) {
//        this.lichSuGiaoDich = lichSuGiaoDich;
//    }

    public void setGiaoDichNap(GiaoDichNap giaoDichNap) {
        this.giaoDichNap = giaoDichNap;
    }

    public void setListener(OnUserActionListener listener) {
        this.listener = listener;
    }

    //    public ItemOptionsBottomSheet(RecyclerView.ViewHolder viewHolder) {
//        this.viewHolder = viewHolder;
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.option_dialog, container, false);

        Button btnUpdate = view.findViewById(R.id.btn_update);
        Button btnDelete = view.findViewById(R.id.btn_delete);
        builder = new AlertDialog.Builder(view.getContext());

        btnUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement update action here
                if (listener != null) {
                    listener.onUpdate(giaoDichNap);
                }
                dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Implement delete action here
                if (listener != null) {
                    listener.onDelete(giaoDichNap);
                }
                dismiss();
            }
        });

        return view;
    }
}
