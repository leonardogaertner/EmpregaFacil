package com.example.facilemprega;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log; // Importe a classe Log
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class RegisterUserActivity extends AppCompatActivity {

    private static final String TAG = "RegisterUserActivity"; // Tag para o Log
    private EditText nameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerButton;
    private TextView registerCompanyLink, loginLink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_user);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        nameEditText = findViewById(R.id.edit_text_name_user);
        emailEditText = findViewById(R.id.edit_text_email_register);
        passwordEditText = findViewById(R.id.edit_text_password_register);
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password_user);
        registerButton = findViewById(R.id.button_register_action);
        registerCompanyLink = findViewById(R.id.text_view_register_company_link);
        loginLink = findViewById(R.id.text_view_login_link_user);

        registerButton.setOnClickListener(v -> registerUser());
        registerCompanyLink.setOnClickListener(v -> startActivity(new Intent(RegisterUserActivity.this, RegisterCompanyActivity.class)));
        loginLink.setOnClickListener(v -> startActivity(new Intent(RegisterUserActivity.this, LoginActivity.class)));
    }

    private void registerUser() {
        String name = nameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            Toast.makeText(this, "Por favor, preencha todos os campos.", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!password.equals(confirmPassword)) {
            Toast.makeText(this, "As senhas não coincidem.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        FirebaseUser firebaseUser = mAuth.getCurrentUser();
                        if (firebaseUser != null) {
                            String uid = firebaseUser.getUid();
                            saveUserProfile(uid, name, email);
                        }
                    } else {
                        Toast.makeText(RegisterUserActivity.this, "Falha no registro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveUserProfile(String uid, String name, String email) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("role", "candidato");
        user.put("createdAt", new Date());

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterUserActivity.this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterUserActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // ESTA É A PARTE MAIS IMPORTANTE PARA O DEBUG
                    Toast.makeText(RegisterUserActivity.this, "Falha ao salvar perfil.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao salvar perfil no Firestore", e);
                });
    }
}