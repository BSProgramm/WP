package ru.badsprogramm.wp.RV;


import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import ru.badsprogramm.wp.Category;
import ru.badsprogramm.wp.R;
import ru.badsprogramm.wp.Screen;

public class RVAcategory extends RecyclerView.Adapter<RVAcategory.ViewHolder> {

    List<Category> category = new ArrayList<>();

    public RVAcategory(List<Category> category) {
        this.category = category;
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        CardView cardView;
        TextView name;
        ImageView png;

        public ViewHolder(View itemView) {
            super(itemView);
            cardView = (CardView) itemView.findViewById(R.id.card);
            name = (TextView) itemView.findViewById(R.id.text);
            png = (ImageView) itemView.findViewById(R.id.png);
            cardView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            Intent intent = new Intent(itemView.getContext(),Screen.class);
            intent.putExtra("LINK", category.get(getAdapterPosition()).getLink());
            itemView.getContext().startActivity(intent);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.rv_category, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.name.setText(category.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return category.size();
    }
}