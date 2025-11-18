package com.example.carrishop;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adaptador para representar los botones táctiles del modo niños.
 */
public class KidsAudioButtonAdapter extends RecyclerView.Adapter<KidsAudioButtonAdapter.KidsButtonViewHolder> {

    public interface OnItemClickListener {
        void onItemClick(@NonNull KidsCategoryItem item);
    }

    private final List<KidsCategoryItem> items = new ArrayList<>();
    private final OnItemClickListener listener;

    public KidsAudioButtonAdapter(@NonNull OnItemClickListener listener) {
        this.listener = listener;
    }

    public void submitList(@NonNull List<KidsCategoryItem> newItems) {
        items.clear();
        items.addAll(newItems);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public KidsButtonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_kids_audio_button, parent, false);
        return new KidsButtonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull KidsButtonViewHolder holder, int position) {
        KidsCategoryItem item = items.get(position);
        holder.bind(item);
        holder.itemView.setOnClickListener(v -> listener.onItemClick(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class KidsButtonViewHolder extends RecyclerView.ViewHolder {
        private final TextView labelView;
        private final TextView symbolView;
        private final FrameLayout iconContainer;

        KidsButtonViewHolder(@NonNull View itemView) {
            super(itemView);
            labelView = itemView.findViewById(R.id.txtKidsLabel);
            symbolView = itemView.findViewById(R.id.txtKidsSymbol);
            iconContainer = itemView.findViewById(R.id.kidsIconContainer);
        }

        void bind(@NonNull KidsCategoryItem item) {
            labelView.setText(item.getLabel());
            symbolView.setText(item.getSymbol());
            Drawable background = iconContainer.getBackground();
            if (background != null) {
                Drawable wrapped = DrawableCompat.wrap(background.mutate());
                DrawableCompat.setTint(wrapped, item.getIconColor());
                iconContainer.setBackground(wrapped);
            }
            itemView.setContentDescription(item.getLabel());
        }
    }
}
