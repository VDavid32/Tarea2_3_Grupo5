package com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;


import com.example.tarea2_3_grupo5.R;
import com.example.tarea2_3_grupo5.tarea2_3_Grupo5.Models.imageItem;

import java.util.List;

public class CustomAdapterImagenes extends RecyclerView.Adapter<CustomAdapterImagenes.CustomViewHolder> {
    private List<imageItem> dataList;
    private Context context;

    public CustomAdapterImagenes(Context context, List<imageItem> dataList) {
        this.context = context;
        this.dataList = dataList;
    }

    @Override
    public CustomViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.custom_list_item_imagenes, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(CustomViewHolder holder, int position) {
        imageItem data = dataList.get(position);

        // Bind data to views
        holder.descripcion.setText(data.getItemDescription());
        holder.image.setImageBitmap(data.getImageBitmap()); // Set the Bitmap to the ImageView
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        TextView descripcion;
        ImageView image;

        public CustomViewHolder(View itemView) {
            super(itemView);
            descripcion = itemView.findViewById(R.id.txtListItemDescription);
            image = itemView.findViewById(R.id.imageviewListItemImagenes);
        }
    }
}
