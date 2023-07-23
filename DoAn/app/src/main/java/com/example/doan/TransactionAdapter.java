package com.example.doan;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.doan.entity.GiaoDichNap;
import com.example.doan.entity.LichSuGiaoDich;
import com.google.firebase.Timestamp;

import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class TransactionAdapter extends RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>{

    Context context;
    List<GiaoDichNap> items;
    private int[] imgView = {R.drawable.jar1,R.drawable.jar2,R.drawable.jar3,R.drawable.jar4,R.drawable.jar5,R.drawable.jar6};
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(GiaoDichNap giaoDichNap);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }


    public TransactionAdapter(Context context, List<GiaoDichNap> items) {
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public TransactionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new TransactionViewHolder(LayoutInflater.from(context).inflate(R.layout.transaction_view,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull TransactionViewHolder holder, int position) {

        holder.imgHu.setImageResource(imgView[position % imgView.length]);
        holder.txtId.setText(items.get(position).getUuid());
        Date date = items.get(position).getNgayNap();
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy ");
        String formattedDate = dateFormat.format(date);
        holder.txtThoiGian.setText(formattedDate);
        holder.txtLoaiGiaodich.setText(items.get(position).getLoaiGiaoDich());
        holder.txtTenHu.setText(items.get(position).getTenHu());
        Locale localeVN = new Locale("vi", "VN"); // Create a locale for Vietnam
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(localeVN);
        String tien = currencyFormat.format(items.get(position).getTienNap());
        holder.txtTienGiaoDich.setText(tien);
        if (items.get(position).getLoaiGiaoDich().equals("Thu nhập")){
            holder.txtTienGiaoDich.setTextColor(Color.GREEN);
        }else if(items.get(position).getLoaiGiaoDich().equals("Chi tiêu")){
            holder.txtTienGiaoDich.setTextColor(Color.RED);
        }
        holder.txtMoTa.setText(items.get(position).getMoTa());
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    public class TransactionViewHolder extends RecyclerView.ViewHolder {
        ImageView imgHu;
        TextView txtThoiGian,txtLoaiGiaodich,txtTenHu,txtTienGiaoDich,txtId,txtMoTa;
        public TransactionViewHolder(@NonNull View itemView) {
            super(itemView);
            txtId = itemView.findViewById(R.id.txtID);
            imgHu = itemView.findViewById(R.id.listImage);
            txtThoiGian = itemView.findViewById(R.id.textThoiGian);
            txtLoaiGiaodich = itemView.findViewById(R.id.textLoaiGiaoDich);
            txtTenHu = itemView.findViewById(R.id.textTenHu);
            txtTienGiaoDich = itemView.findViewById(R.id.textTienGiaoDich);
            txtMoTa = itemView.findViewById(R.id.txtMota);
            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION && listener != null) {
                        listener.onItemClick(items.get(position));
                    }
                }
            });
        }
    }
}
