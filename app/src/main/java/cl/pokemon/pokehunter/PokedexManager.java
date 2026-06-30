package cl.pokemon.pokehunter;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import cl.pokemon.pokehunter.model.Pokemon;

public class PokedexManager {

    private static final String PREFS_NAME = "pokedex_prefs";
    private static final String KEY_CAPTURED = "captured_pokemon";
    private static final Gson gson = new Gson();

    public static void savePokemon(Context context, Pokemon pokemon) {
        List<Pokemon> captured = getCapturedPokemon(context);
        for (Pokemon p : captured) {
            if (p.getId() == pokemon.getId()) return;
        }
        captured.add(pokemon);
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        prefs.edit().putString(KEY_CAPTURED, gson.toJson(captured)).apply();
    }

    public static List<Pokemon> getCapturedPokemon(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        String json = prefs.getString(KEY_CAPTURED, null);
        if (json == null) return new ArrayList<>();
        Type type = new TypeToken<List<Pokemon>>() {}.getType();
        List<Pokemon> list = gson.fromJson(json, type);
        return list != null ? list : new ArrayList<>();
    }

    public static boolean isCaptured(Context context, int pokemonId) {
        List<Pokemon> captured = getCapturedPokemon(context);
        for (Pokemon p : captured) {
            if (p.getId() == pokemonId) return true;
        }
        return false;
    }
}
