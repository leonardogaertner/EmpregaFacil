// Pacote: com.example.facilemprega.ui.auth
package com.example.facilemprega.ui.auth;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.example.facilemprega.MainActivity;
import com.example.facilemprega.R;

public class LoginActivity extends AppCompatActivity {

    private LoginViewModel viewModel;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        viewModel = new ViewModelProvider(this).get(LoginViewModel.class);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        emailEditText = findViewById(R.id.edit_text_email_login);
        passwordEditText = findViewById(R.id.edit_text_password_login);
        loginButton = findViewById(R.id.button_login_action);
        registerLink = findViewById(R.id.text_view_register_link);
    }

    private void setupListeners() {
        loginButton.setOnClickListener(v -> attemptLogin());
        registerLink.setOnClickListener(v ->
                startActivity(new Intent(LoginActivity.this, RegisterUserActivity.class))
        );
    }

    private void attemptLogin() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (!viewModel.isInputValid(email, password)) {
            Toast.makeText(this, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true); // Desabilita o botão

        viewModel.login(email, password).observe(this, result -> {
            setLoading(false); // Habilita o botão novamente
            if (result.isSuccess) {
                Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            } else {
                Toast.makeText(this, "Falha no Login: " + result.errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        // Apenas desabilita o botão para prevenir cliques múltiplos
        loginButton.setEnabled(!isLoading);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}