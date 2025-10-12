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

public class RegisterUserActivity extends AppCompatActivity {

    private RegisterUserViewModel viewModel;
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView registerCompanyLink, loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        viewModel = new ViewModelProvider(this).get(RegisterUserViewModel.class);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        nameEditText = findViewById(R.id.edit_text_name_user);
        emailEditText = findViewById(R.id.edit_text_email_register);
        passwordEditText = findViewById(R.id.edit_text_password_register);
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password_user);
        registerButton = findViewById(R.id.button_register_action);
        registerCompanyLink = findViewById(R.id.text_view_register_company_link);
        loginLink = findViewById(R.id.text_view_login_link_user);
    }

    private void setupListeners() {
        registerButton.setOnClickListener(v -> attemptRegister());
        registerCompanyLink.setOnClickListener(v -> startActivity(new Intent(this, RegisterCompanyActivity.class)));
        loginLink.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void attemptRegister() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        String validationError = viewModel.validateInput(name, email, password, confirmPassword);
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        viewModel.register(name, email, password).observe(this, result -> {
            setLoading(false);
            if (result.isSuccess) {
                Toast.makeText(this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            } else {
                Toast.makeText(this, "Falha no registro: " + result.errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        registerButton.setEnabled(!isLoading);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}