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

public class RegisterUserActivity extends AppCompatActivity {

    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView registerCompanyLink, loginLink;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();

        // Referências dos componentes da UI
        nameEditText = findViewById(R.id.edit_text_name_user);
        emailEditText = findViewById(R.id.edit_text_email_register);
        passwordEditText = findViewById(R.id.edit_text_password_register);
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password_user);
        registerButton = findViewById(R.id.button_register_action);
        registerCompanyLink = findViewById(R.id.text_view_register_company_link);
        loginLink = findViewById(R.id.text_view_login_link_user);

        // Listener para o botão de registrar
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });

        // Listener para o link "Cadastrar como empresa"
        registerCompanyLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterUserActivity.this, RegisterCompanyActivity.class);
                startActivity(intent);
            }
        });

        // Listener para o link "Fazer Login"
        loginLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterUserActivity.this, LoginActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(RegisterUserActivity.this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(RegisterUserActivity.this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterUserActivity.this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show();
                            // Após o registro, redireciona para a tela principal
                            Intent intent = new Intent(RegisterUserActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Finaliza a atividade para que o usuário não possa voltar
                        } else {
                            Toast.makeText(RegisterUserActivity.this, "Falha no registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}