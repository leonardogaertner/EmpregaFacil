package com.example.facilemprega;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterCompanyActivity extends AppCompatActivity {

    private EditText userNameEditText, companyNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerCompanyButton;
    private TextView registerUserLink, loginLink;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);

        mAuth = FirebaseAuth.getInstance();

        // Referências dos componentes da UI
        userNameEditText = findViewById(R.id.edit_text_name_company_user);
        companyNameEditText = findViewById(R.id.edit_text_company_name);
        emailEditText = findViewById(R.id.edit_text_email_company);
        passwordEditText = findViewById(R.id.edit_text_password_company);
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password_company);
        registerCompanyButton = findViewById(R.id.button_register_company_action);
        registerUserLink = findViewById(R.id.text_view_register_user_link);
        loginLink = findViewById(R.id.text_view_login_link_company);

        // Listener para o botão de registrar empresa
        registerCompanyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerCompany();
            }
        });

        // Listener para o link "Cadastrar como usuário"
        registerUserLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterCompanyActivity.this, RegisterUserActivity.class);
                startActivity(intent);
            }
        });

        // Listener para o link "Fazer Login"
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterCompanyActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerCompany() {
        String userName = userNameEditText.getText().toString().trim();
        String companyName = companyNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(companyName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(RegisterCompanyActivity.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterCompanyActivity.this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Aqui você pode adicionar a lógica para salvar os dados da empresa (userName, companyName) no Firebase Realtime Database ou Firestore, associado ao UID do usuário criado.
                            Toast.makeText(RegisterCompanyActivity.this, "Cadastro de empresa bem-sucedido!", Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(RegisterCompanyActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        } else {
                            Toast.makeText(RegisterCompanyActivity.this, "Falha no cadastro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}