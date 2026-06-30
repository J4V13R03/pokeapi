package cl.pokemon.pokehunter;

import android.media.AudioAttributes;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import cl.pokemon.pokehunter.adapter.PokedexAdapter;
import cl.pokemon.pokehunter.model.Pokemon;

public class PokedexActivity extends AppCompatActivity implements PokedexAdapter.OnPokedexActionListener {

    private RecyclerView rvPokedex;
    private TextView tvEmpty;
    private PokedexAdapter adapter;
    private List<Pokemon> capturedList;
    private MediaPlayer mediaPlayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pokedex);

        rvPokedex = findViewById(R.id.rv_pokedex);
        tvEmpty = findViewById(R.id.tv_empty);

        findViewById(R.id.toolbar).setOnClickListener(v -> finish());

        capturedList = PokedexManager.getCapturedPokemon(this);

        rvPokedex.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PokedexAdapter(this, capturedList, this);
        rvPokedex.setAdapter(adapter);

        if (capturedList.isEmpty()) {
            tvEmpty.setVisibility(android.view.View.VISIBLE);
            rvPokedex.setVisibility(android.view.View.GONE);
        } else {
            tvEmpty.setVisibility(android.view.View.GONE);
            rvPokedex.setVisibility(android.view.View.VISIBLE);
        }
    }

    @Override
    public void onPlaySound(Pokemon pokemon) {
        if (pokemon.getCryUrl() == null || pokemon.getCryUrl().isEmpty()) {
            Toast.makeText(this, "Sonido no disponible", Toast.LENGTH_SHORT).show();
            return;
        }

        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }

        try {
            mediaPlayer = new MediaPlayer();
            mediaPlayer.setAudioAttributes(new AudioAttributes.Builder()
                    .setUsage(AudioAttributes.USAGE_MEDIA)
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .build());
            mediaPlayer.setDataSource(pokemon.getCryUrl());
            mediaPlayer.setOnPreparedListener(mp -> mp.start());
            mediaPlayer.setOnErrorListener((mp, what, extra) -> {
                Toast.makeText(this, "Error reproduciendo sonido", Toast.LENGTH_SHORT).show();
                mp.release();
                mediaPlayer = null;
                return true;
            });
            mediaPlayer.prepareAsync();
        } catch (Exception e) {
            Toast.makeText(this, "Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onViewStats(Pokemon pokemon) {
        String message = getString(R.string.hp) + ": " + pokemon.getHp() + "\n"
                + getString(R.string.attack) + ": " + pokemon.getAttack() + "\n"
                + getString(R.string.defense) + ": " + pokemon.getDefense();

        new AlertDialog.Builder(this)
                .setTitle(pokemon.getFormattedName())
                .setMessage(message)
                .setPositiveButton(R.string.ok, null)
                .show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
