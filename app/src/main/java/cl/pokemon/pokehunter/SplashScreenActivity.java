package cl.pokemon.pokehunter;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import cl.pokemon.pokehunter.util.NetworkUtil;

public class SplashScreenActivity extends AppCompatActivity {

    private static final String TAG = "SplashScreen";
    private static final int SPLASH_DELAY = 2500;
    private AlertDialog noConnectionDialog;

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

        new Handler(Looper.getMainLooper()).postDelayed(this::checkConnection, SPLASH_DELAY);
    }

    private void checkConnection() {
        if (isFinishing() || isDestroyed()) return;

        if (NetworkUtil.isConnected(this)) {
            dismissDialog();
            goToMain();
        } else {
            showNoConnectionDialog();
        }
    }

    private void showNoConnectionDialog() {
        if (noConnectionDialog != null && noConnectionDialog.isShowing()) return;

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_no_connection, null);

        noConnectionDialog = new AlertDialog.Builder(this, R.style.TransparentDialog)
                .setView(dialogView)
                .setCancelable(false)
                .create();

        TextView btnRetry = dialogView.findViewById(R.id.btn_retry);
        btnRetry.setOnClickListener(v -> {
            if (NetworkUtil.isConnected(SplashScreenActivity.this)) {
                dismissDialog();
                goToMain();
            } else {
                Toast.makeText(this, "Sigue sin conexión", Toast.LENGTH_SHORT).show();
            }
        });

        noConnectionDialog.show();
    }

    private void dismissDialog() {
        if (noConnectionDialog != null && noConnectionDialog.isShowing()) {
            noConnectionDialog.dismiss();
        }
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dismissDialog();
    }
}
