package com.example.doan;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.doan.entity.SpinnerItem;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class SpinnerAdapter extends BaseAdapter {
    private Context context;
    private List<SpinnerItem> spinnerItem;
    private int[] imgView = {R.drawable.jar1,R.drawable.jar2,R.drawable.jar3,R.drawable.jar4,R.drawable.jar5,R.drawable.jar6};

    public SpinnerAdapter(Context context, List<SpinnerItem> spinnerItem) {
        this.context = context;
        this.spinnerItem = spinnerItem;
    }


    @Override
    public int getCount() {
        return spinnerItem != null ? spinnerItem.size(): 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rootView = LayoutInflater.from(context).inflate(R.layout.spinner_layout,parent,false);
        SpinnerItem currentItem = spinnerItem.get(position);
        TextView  textView = rootView.findViewById(R.id.spinnerText);
        textView.setText(currentItem.getTenHu()+": ");
//        textView.setText((CharSequence) spinnerItem.get(position));
        TextView textView1 = rootView.findViewById(R.id.spinnerText1);
        textView1.setText(currentItem.getSoTien());
        ImageView imageView = rootView.findViewById(R.id.spinnerImage);
        imageView.setImageResource(imgView[position % imgView.length]);
        return rootView ;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        return super.getDropDownView(position, convertView, parent);
    }
}
