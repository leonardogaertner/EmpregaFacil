package com.example.facilemprega;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

public class RegisterCompanyActivity extends AppCompatActivity {

    private Button registerCompanyButton;
    private TextView registerUserLink, loginLink;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_company);

        registerCompanyButton = findViewById(R.id.button_register_company_action);
        registerUserLink = findViewById(R.id.text_view_register_user_link);
        loginLink = findViewById(R.id.text_view_login_link_company);

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
}