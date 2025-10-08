package com.example.facilemprega;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterUserActivity extends AppCompatActivity {

    private EditText emailEditText, passwordEditText;
    private Button registerButton;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // You need to create a layout file named 'activity_register_user.xml'
        // for this screen.
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();
        emailEditText = findViewById(R.id.edit_text_email_register);
        passwordEditText = findViewById(R.id.edit_text_password_register);
        registerButton = findViewById(R.id.button_register_action);

        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }

    private void registerUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(RegisterUserActivity.this, "Por favor, preencha e-mail e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(RegisterUserActivity.this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show();
                            // After successful registration, go to the main screen
                            Intent intent = new Intent(RegisterUserActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Finish this activity and LoginActivity so user cannot go back
                        } else {
                            Toast.makeText(RegisterUserActivity.this, "Falha no registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }
}
