package com.zbadev.autenhuella;

import static androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG;
import static androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL;

import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.provider.Settings;
import android.util.Log;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.os.Bundle;

import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE = 101010;
    private Button btnAuthenticate;

    private Executor executor;
    private BiometricPrompt biometricPrompt;
    private BiometricPrompt.PromptInfo promptInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btnAuthenticate = findViewById(R.id.btn_authenticate);

        //START

        BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)) {
            case BiometricManager.BIOMETRIC_SUCCESS:
                Log.d("MY_APP_TAG", "App can authenticate using biometrics.");
                break;
            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                //Log.e("MY_APP_TAG", "No biometric features available on this device.");
                Toast.makeText(getApplicationContext(), "No hay hardware biométrico disponible", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                //Log.e("MY_APP_TAG", "Biometric features are currently unavailable.");
                Toast.makeText(getApplicationContext(), "Hardware biométrico no disponible actualmente", Toast.LENGTH_SHORT).show();
                break;
            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                // Prompts the user to create credentials that your app accepts.
                final Intent enrollIntent = new Intent(Settings.ACTION_BIOMETRIC_ENROLL);
                enrollIntent.putExtra(Settings.EXTRA_BIOMETRIC_AUTHENTICATORS_ALLOWED,
                        BIOMETRIC_STRONG | DEVICE_CREDENTIAL);
                startActivityForResult(enrollIntent, REQUEST_CODE);
                Toast.makeText(getApplicationContext(), "No hay huellas digitales registradas", Toast.LENGTH_SHORT).show();
                break;
        }

        executor = ContextCompat.getMainExecutor(this);
        biometricPrompt = new BiometricPrompt(MainActivity.this,
                executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode,
                                              @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(),
                                "Authentication error: " + errString, Toast.LENGTH_SHORT)
                        .show();
            }

            @Override
            public void onAuthenticationSucceeded(
                    @NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Intent intent = new Intent(MainActivity.this, PatternActivity.class);
                startActivity(intent);
                finish();
                Toast.makeText(getApplicationContext(),
                        "Authentication succeeded!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Authentication failed",
                                Toast.LENGTH_SHORT)
                        .show();
            }
        });

        promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Biometric login for my app: loco")
                .setSubtitle("Log in using your biometric credential")
                .setAllowedAuthenticators(BIOMETRIC_STRONG | DEVICE_CREDENTIAL)
                .build();

        // Prompt appears when user clicks "Log in".
        // Consider integrating with the keystore to unlock cryptographic operations,
        // if needed by your app.

        btnAuthenticate.setOnClickListener(view -> {
            biometricPrompt.authenticate(promptInfo);
        });

        //FIN

        /*// Configurar el executor
        Executor executor = ContextCompat.getMainExecutor(this);

        // Crear el callback para la autenticación
        BiometricPrompt.AuthenticationCallback callback = new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
                Toast.makeText(getApplicationContext(), "Error de autenticación: " + errString, Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                Toast.makeText(getApplicationContext(), "Autenticación exitosa!", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
                Toast.makeText(getApplicationContext(), "Autenticación fallida", Toast.LENGTH_SHORT).show();
            }
        };

        // Crear el prompt de autenticación biométrica
        final BiometricPrompt biometricPrompt = new BiometricPrompt(this, executor, callback);

        // Crear el prompt de información
        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Autenticación biométrica")
                .setSubtitle("Inicie sesión usando su huella digital")
                .setAllowedAuthenticators(BiometricManager.Authenticators.BIOMETRIC_STRONG | BiometricManager.Authenticators.DEVICE_CREDENTIAL)
                .setNegativeButtonText("Cancelar")
                .build();

        // Configurar el botón para iniciar la autenticación
        btnAuthenticate.setOnClickListener(v -> {
            BiometricManager biometricManager = BiometricManager.from(this);
            switch (biometricManager.canAuthenticate()){
                case BiometricManager.BIOMETRIC_SUCCESS:
                    biometricPrompt.authenticate(promptInfo);
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                    Toast.makeText(getApplicationContext(), "No hay hardware biométrico disponible", Toast.LENGTH_SHORT).show();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                    Toast.makeText(getApplicationContext(), "Hardware biométrico no disponible actualmente", Toast.LENGTH_SHORT).show();
                    break;
                case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                    Toast.makeText(getApplicationContext(), "No hay huellas digitales registradas", Toast.LENGTH_SHORT).show();
                    break;
            }
        });
*/
        /*// Configurar el botón para mostrar el diálogo de opciones de autenticación
        btnAuthenticate.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle("Seleccione el método de autenticación")
                    .setItems(new CharSequence[]{"Huella Digital", "Patrón"}, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (which == 0) {
                                // Autenticación con huella digital
                                BiometricManager biometricManager = BiometricManager.from(MainActivity.this);
                                switch (biometricManager.canAuthenticate()) {
                                    case BiometricManager.BIOMETRIC_SUCCESS:
                                        biometricPrompt.authenticate(promptInfo);
                                        break;
                                    case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:
                                        Toast.makeText(getApplicationContext(), "No hay hardware biométrico disponible", Toast.LENGTH_SHORT).show();
                                        break;
                                    case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:
                                        Toast.makeText(getApplicationContext(), "Hardware biométrico no disponible actualmente", Toast.LENGTH_SHORT).show();
                                        break;
                                    case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:
                                        Toast.makeText(getApplicationContext(), "No hay huellas digitales registradas", Toast.LENGTH_SHORT).show();
                                        break;
                                }
                            } else if (which == 1) {
                                // Autenticación con patrón
                                Intent intent = new Intent(MainActivity.this, PatternActivity.class);
                                startActivity(intent);
                            }
                        }
                    });
            builder.show();
        });*/

    }

    protected void onActivityResult(int requestCode, int resultCode, @NonNull Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE) {
            BiometricManager biometricManager = BiometricManager.from(this);
            if (biometricManager.canAuthenticate(BIOMETRIC_STRONG | DEVICE_CREDENTIAL) ==
                    BiometricManager.BIOMETRIC_SUCCESS) {
                Toast.makeText(getApplicationContext(), "Autenticación biométrica configurada con éxito", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getApplicationContext(), "No se pudo configurar la autenticación biométrica", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /*public void loco(){
        Intent intent = new Intent(MainActivity.this, PatternActivity.class);
        startActivity(intent);
    }*/
}