package cl.pokemon.pokehunter;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import cl.pokemon.pokehunter.adapter.PokemonAdapter;
import cl.pokemon.pokehunter.model.Pokemon;
import cl.pokemon.pokehunter.util.NetworkUtil;

public class MainActivity extends AppCompatActivity implements PokemonAdapter.OnCaptureClickListener {

    private static final Set<String> MY_POKEMON = new HashSet<>(Arrays.asList(
            "gardevoir", "dragapult", "mamoswine", "giratina-altered", "lopunny"
    ));

    private static final String LIST_URL = "https://pokeapi.co/api/v2/pokemon?limit=20";
    private static final String DETAIL_URL = "https://pokeapi.co/api/v2/pokemon/";

    private RecyclerView rvPokemon;
    private LinearProgressIndicator progressBar;
    private TextView tvEmpty;
    private FloatingActionButton fabPokedex;
    private RequestQueue requestQueue;
    private List<Pokemon> pokemonList;
    private PokemonAdapter adapter;
    private int requestsCompleted = 0;
    private int totalRequests = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        rvPokemon = findViewById(R.id.rv_pokemon);
        progressBar = findViewById(R.id.progress_bar);
        tvEmpty = findViewById(R.id.tv_empty);
        fabPokedex = findViewById(R.id.fab_pokedex);
        requestQueue = Volley.newRequestQueue(this);
        pokemonList = new ArrayList<>();

        rvPokemon.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PokemonAdapter(this, pokemonList, this);
        rvPokemon.setAdapter(adapter);

        fabPokedex.setOnClickListener(v ->
                startActivity(new Intent(this, PokedexActivity.class))
        );

        if (NetworkUtil.isConnected(this)) {
            fetchAllPokemon();
        } else {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_LONG).show();
            tvEmpty.setText(getString(R.string.no_connection));
            tvEmpty.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        adapter.notifyDataSetChanged();
    }

    private void fetchAllPokemon() {
        progressBar.setVisibility(View.VISIBLE);
        tvEmpty.setVisibility(View.GONE);

        JsonObjectRequest listRequest = new JsonObjectRequest(Request.Method.GET, LIST_URL, null,
                response -> {
                    try {
                        JSONArray results = response.getJSONArray("results");
                        List<String> namesToFetch = new ArrayList<>();
                        for (int i = 0; i < results.length(); i++) {
                            namesToFetch.add(results.getJSONObject(i).getString("name"));
                        }
                        for (String my : MY_POKEMON) {
                            if (!namesToFetch.contains(my)) {
                                namesToFetch.add(my);
                            }
                        }
                        totalRequests = namesToFetch.size();
                        requestsCompleted = 0;
                        for (String name : namesToFetch) {
                            fetchPokemonDetail(name);
                        }
                    } catch (Exception e) {
                        progressBar.setVisibility(View.GONE);
                        tvEmpty.setVisibility(View.VISIBLE);
                    }
                },
                error -> {
                    progressBar.setVisibility(View.GONE);
                    tvEmpty.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Error de red", Toast.LENGTH_SHORT).show();
                }
        );
        requestQueue.add(listRequest);
    }

    private void fetchPokemonDetail(String name) {
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET, DETAIL_URL + name, null,
                response -> {
                    try {
                        int id = response.getInt("id");
                        String spriteUrl = response.getJSONObject("sprites").getString("front_default");

                        Pokemon p = new Pokemon();
                        p.setId(id);
                        p.setName(name);
                        p.setSpriteUrl(spriteUrl);

                        JSONArray stats = response.getJSONArray("stats");
                        for (int j = 0; j < stats.length(); j++) {
                            JSONObject stat = stats.getJSONObject(j);
                            String statName = stat.getJSONObject("stat").getString("name");
                            int baseStat = stat.getInt("base_stat");
                            switch (statName) {
                                case "hp": p.setHp(baseStat); break;
                                case "attack": p.setAttack(baseStat); break;
                                case "defense": p.setDefense(baseStat); break;
                            }
                        }

                        JSONObject cries = response.optJSONObject("cries");
                        if (cries != null) {
                            p.setCryUrl(cries.optString("latest", null));
                        }

                        if (PokedexManager.isCaptured(this, id)) {
                            p.setCaptured(true);
                        }

                        pokemonList.add(p);
                    } catch (Exception e) { }

                    requestsCompleted++;
                    if (requestsCompleted >= totalRequests) {
                        progressBar.setVisibility(View.GONE);
                        if (pokemonList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                },
                error -> {
                    requestsCompleted++;
                    if (requestsCompleted >= totalRequests) {
                        progressBar.setVisibility(View.GONE);
                        if (pokemonList.isEmpty()) {
                            tvEmpty.setVisibility(View.VISIBLE);
                        } else {
                            adapter.notifyDataSetChanged();
                        }
                    }
                }
        );
        requestQueue.add(request);
    }

    @Override
    public void onCaptureClick(Pokemon pokemon, int position) {
        if (!NetworkUtil.isConnected(this)) {
            Toast.makeText(this, getString(R.string.no_connection), Toast.LENGTH_SHORT).show();
            return;
        }

        if (pokemon.isCaptured()) {
            Toast.makeText(this, pokemon.getFormattedName() + " ya esta en tu Pokedex", Toast.LENGTH_SHORT).show();
            return;
        }

        if (MY_POKEMON.contains(pokemon.getName().toLowerCase())) {
            pokemon.setCaptured(true);
            PokedexManager.savePokemon(this, pokemon);
            Toast.makeText(this, getString(R.string.captured) + " " + pokemon.getFormattedName() + "!", Toast.LENGTH_SHORT).show();
            adapter.notifyItemChanged(position);
        } else {
            Toast.makeText(this, getString(R.string.belongs_to_other), Toast.LENGTH_SHORT).show();
        }
    }
}
