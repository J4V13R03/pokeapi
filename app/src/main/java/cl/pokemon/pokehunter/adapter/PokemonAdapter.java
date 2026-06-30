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

public class PokemonAdapter extends RecyclerView.Adapter<PokemonAdapter.PokemonViewHolder> {

    public interface OnCaptureClickListener {
        void onCaptureClick(Pokemon pokemon, int position);
    }

    private final Context context;
    private final List<Pokemon> pokemonList;
    private final OnCaptureClickListener listener;

    public PokemonAdapter(Context context, List<Pokemon> pokemonList, OnCaptureClickListener listener) {
        this.context = context;
        this.pokemonList = pokemonList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public PokemonViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false);
        return new PokemonViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PokemonViewHolder holder, int position) {
        Pokemon pokemon = pokemonList.get(position);
        holder.tvName.setText(pokemon.getFormattedName());

        Glide.with(context)
                .load(pokemon.getSpriteUrl())
                .placeholder(R.drawable.ic_pokeball_small)
                .error(R.drawable.ic_pokeball_small)
                .into(holder.ivSprite);

        holder.btnCapture.setOnClickListener(v -> {
            if (listener != null) {
                listener.onCaptureClick(pokemon, holder.getAdapterPosition());
            }
        });
    }

    @Override
    public int getItemCount() {
        return pokemonList != null ? pokemonList.size() : 0;
    }

    static class PokemonViewHolder extends RecyclerView.ViewHolder {
        ImageView ivSprite;
        TextView tvName;
        MaterialButton btnCapture;

        public PokemonViewHolder(@NonNull View itemView) {
            super(itemView);
            ivSprite = itemView.findViewById(R.id.iv_sprite);
            tvName = itemView.findViewById(R.id.tv_name);
            btnCapture = itemView.findViewById(R.id.btn_capture);
        }
    }
}
