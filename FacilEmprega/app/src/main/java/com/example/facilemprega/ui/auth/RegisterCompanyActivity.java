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

public class RegisterCompanyActivity extends AppCompatActivity {

    private RegisterCompanyViewModel viewModel;
    private EditText userNameEditText, companyNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerCompanyButton;
    private TextView registerUserLink, loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);

        viewModel = new ViewModelProvider(this).get(RegisterCompanyViewModel.class);

        setupUI();
        setupListeners();
    }

    private void setupUI() {
        userNameEditText = findViewById(R.id.edit_text_name_company_user);
        companyNameEditText = findViewById(R.id.edit_text_company_name);
        emailEditText = findViewById(R.id.edit_text_email_company);
        passwordEditText = findViewById(R.id.edit_text_password_company);
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password_company);
        registerCompanyButton = findViewById(R.id.button_register_company_action);
        registerUserLink = findViewById(R.id.text_view_register_user_link);
        loginLink = findViewById(R.id.text_view_login_link_company);
    }

    private void setupListeners() {
        registerCompanyButton.setOnClickListener(v -> attemptRegister());
        registerUserLink.setOnClickListener(v -> startActivity(new Intent(this, RegisterUserActivity.class)));
        loginLink.setOnClickListener(v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void attemptRegister() {
        String responsibleName = userNameEditText.getText().toString().trim();
        String companyName = companyNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        String validationError = viewModel.validateInput(responsibleName, companyName, email, password, confirmPassword);
        if (validationError != null) {
            Toast.makeText(this, validationError, Toast.LENGTH_SHORT).show();
            return;
        }

        setLoading(true);

        viewModel.register(responsibleName, companyName, email, password).observe(this, result -> {
            setLoading(false);
            if (result.isSuccess) {
                Toast.makeText(this, "Cadastro de empresa bem-sucedido!", Toast.LENGTH_SHORT).show();
                goToMainActivity();
            } else {
                Toast.makeText(this, "Falha no cadastro: " + result.errorMessage, Toast.LENGTH_LONG).show();
            }
        });
    }

    private void setLoading(boolean isLoading) {
        registerCompanyButton.setEnabled(!isLoading);
    }

    private void goToMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}