package com.example.facilemprega;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;

// Dentro de LoginActivity.java

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialização
        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.edit_text_email_login);
        passwordEditText = findViewById(R.id.edit_text_password_login);
        loginButton = findViewById(R.id.button_login_action);
        registerLink = findViewById(R.id.text_view_register_link);

        // Listener do botão de Login
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signInUser();
            }
        });

        // Listener para o link de Registro - ALTERADO
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Inicia a Activity de cadastro de usuário
                Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Registro OK! Redirecionando...", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Falha no Registro: " + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void signInUser() {
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(LoginActivity.this, "Login OK! Bem-vindo.", Toast.LENGTH_SHORT).show();
                            goToMainActivity();
                        } else {
                            Toast.makeText(LoginActivity.this, "Falha no Login: " + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void goToMainActivity() {
        // Redireciona o usuário para a MainActivity (sua tela com Bottom View)
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Finaliza a LoginActivity para que o usuário não possa voltar
    }
}