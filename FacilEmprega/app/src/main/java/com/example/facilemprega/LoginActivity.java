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
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private FirebaseAuth mAuth;
    private FirebaseFirestore db; // Adicionado Firestore
    private EditText emailEditText, passwordEditText;
    private Button loginButton;
    private TextView registerLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Inicialização
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance(); // Inicializa o Firestore
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

        // Listener para o link de Registro
        registerLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterUserActivity.class);
                startActivity(intent);
            }
        });
    }

    private void signInUser() {
        String email = emailEditText.getText().toString().trim();
        String password = passwordEditText.getText().toString().trim();

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(LoginActivity.this, "Preencha e-mail e senha.", Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            FirebaseUser user = mAuth.getCurrentUser();
                            if (user != null) {
                                checkUserRole(user); // Chama a verificação de perfil
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Falha no Login: " + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
                });
    }

    private void checkUserRole(FirebaseUser user) {
        String uid = user.getUid();
        db.collection("users").document(uid).get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            DocumentSnapshot document = task.getResult();
                            if (document.exists()) {
                                String role = document.getString("role");
                                if ("empresa".equals(role)) {
                                    Toast.makeText(LoginActivity.this, "Bem-vinda, Empresa!", Toast.LENGTH_SHORT).show();
                                } else {
                                    Toast.makeText(LoginActivity.this, "Bem-vindo, Candidato!", Toast.LENGTH_SHORT).show();
                                }
                                goToMainActivity(); // Redireciona para a tela principal
                            } else {
                                // Este caso é raro, mas pode acontecer se o registro falhou ao salvar no Firestore
                                Toast.makeText(LoginActivity.this, "Perfil não encontrado.", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(LoginActivity.this, "Erro ao buscar perfil.", Toast.LENGTH_SHORT).show();
                            Log.w(TAG, "Error getting document", task.getException());
                        }
                    }
                });
    }

    private void goToMainActivity() {
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}