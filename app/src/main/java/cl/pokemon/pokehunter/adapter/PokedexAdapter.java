package cl.pokemon.pokehunter.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;

import java.util.List;

import cl.pokemon.pokehunter.R;
import cl.pokemon.pokehunter.model.Pokemon;

public class PokedexAdapter extends RecyclerView.Adapter<PokedexAdapter.PokedexViewHolder> {

    public interface OnPokedexActionListener {
        void onPlaySound(Pokemon pokemon);
        void onViewStats(Pokemon pokemon);
    }

    private final Context context;
    private final List<Pokemon> capturedList;
    private final OnPokedexActionListener listener;

    public PokedexAdapter(Context context, List<Pokemon> capturedList, OnPokedexActionListener listener) {
        this.context = context;
        this.capturedList = capturedList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PokedexViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pokedex, parent, false);
        return new PokedexViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokedexViewHolder holder, int position) {
        Pokemon pokemon = capturedList.get(position);
        holder.tvName.setText(pokemon.getFormattedName());

        Glide.with(context)
                .load(pokemon.getSpriteUrl())
                .placeholder(R.drawable.ic_pokeball_small)
                .error(R.drawable.ic_pokeball_small)
                .into(holder.ivSprite);

        holder.btnSound.setOnClickListener(v -> {
            if (listener != null) listener.onPlaySound(pokemon);
        });

        holder.btnStats.setOnClickListener(v -> {
            if (listener != null) listener.onViewStats(pokemon);
        });
    }

    @Override
    public int getItemCount() {
        return capturedList != null ? capturedList.size() : 0;
    }

    static class PokedexViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSprite;
        TextView tvName;
        MaterialButton btnSound;
        MaterialButton btnStats;

        public PokedexViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSprite = itemView.findViewById(R.id.iv_sprite);
            tvName = itemView.findViewById(R.id.tv_name);
            btnSound = itemView.findViewById(R.id.btn_sound);
            btnStats = itemView.findViewById(R.id.btn_stats);
        }
    }
}
