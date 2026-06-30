package cl.pokemon.pokehunter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    private static final int SPLASH_DELAY = 2500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        try {
            setContentView(R.layout.activity_splash);
        } catch (Exception e) {
            Log.e(TAG, "Error setting content view", e);
            goToMain();
            return;
        }

        new Handler(Looper.getMainLooper()).postDelayed(this::goToMain, SPLASH_DELAY);
    }

    private void goToMain() {
        try {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        } catch (Exception e) {
            Log.e(TAG, "Error starting MainActivity", e);
            Toast.makeText(this, "Error al iniciar", Toast.LENGTH_SHORT).show();
        }
    }
}
