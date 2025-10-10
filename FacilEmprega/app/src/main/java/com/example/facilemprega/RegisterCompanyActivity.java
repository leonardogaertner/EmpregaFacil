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

public class RegisterCompanyActivity extends AppCompatActivity {

    private static final String TAG = "RegisterCompanyActivity"; // Tag para o Log
    private EditText userNameEditText, companyNameEditText, emailEditText, passwordEditText, confirmPasswordEditText;
    private Button registerCompanyButton;
    private TextView registerUserLink, loginLink;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        userNameEditText = findViewById(R.id.edit_text_name_company_user);
        companyNameEditText = findViewById(R.id.edit_text_company_name);
        emailEditText = findViewById(R.id.edit_text_email_company);
        passwordEditText = findViewById(R.id.edit_text_password_company);
        confirmPasswordEditText = findViewById(R.id.edit_text_confirm_password_company);
        registerCompanyButton = findViewById(R.id.button_register_company_action);
        registerUserLink = findViewById(R.id.text_view_register_user_link);
        loginLink = findViewById(R.id.text_view_login_link_company);

        registerCompanyButton.setOnClickListener(v -> registerCompany());
        registerUserLink.setOnClickListener(v -> startActivity(new Intent(RegisterCompanyActivity.this, RegisterUserActivity.class)));
        loginLink.setOnClickListener(v -> startActivity(new Intent(RegisterCompanyActivity.this, LoginActivity.class)));
    }

    private void registerCompany() {
        String userName = userNameEditText.getText().toString().trim();
        String companyName = companyNameEditText.getText().toString().trim();
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();
        String confirmPassword = confirmPasswordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(userName) || TextUtils.isEmpty(companyName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
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
                            saveCompanyProfile(uid, userName, companyName, email);
                        }
                    } else {
                        Toast.makeText(RegisterCompanyActivity.this, "Falha no cadastro: " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void saveCompanyProfile(String uid, String responsibleName, String companyName, String email) {
        Map<String, Object> company = new HashMap<>();
        company.put("responsibleName", responsibleName);
        company.put("companyName", companyName);
        company.put("email", email);
        company.put("role", "empresa");
        company.put("createdAt", new Date());

        db.collection("users").document(uid).set(company)
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(RegisterCompanyActivity.this, "Cadastro de empresa bem-sucedido!", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(RegisterCompanyActivity.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                })
                .addOnFailureListener(e -> {
                    // ESTA É A PARTE MAIS IMPORTANTE PARA O DEBUG
                    Toast.makeText(RegisterCompanyActivity.this, "Falha ao salvar perfil da empresa.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao salvar perfil da empresa no Firestore", e);
                });
    }
}