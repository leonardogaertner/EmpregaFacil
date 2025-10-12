package com.example.facilemprega;

import android.content.Intent;
import android.os.Bundle;

import com.example.facilemprega.ui.auth.LoginActivity;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.facilemprega.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mAuth = FirebaseAuth.getInstance();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_vagas_salvas, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            goToLoginActivity();
        } else {
            // VERIFICA O TIPO DE UTILIZADOR E DIRECIONA
            checkUserRoleAndRedirect(currentUser.getUid());
        }
    }

    private void checkUserRoleAndRedirect(String uid) {
        FirebaseFirestore.getInstance().collection("users").document(uid).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("empresa".equals(role)) {
                            // Se for empresa, ajusta a UI para a empresa
                            setupCompanyUI();
                        } else {
                            // Se for candidato, ajusta a UI para o candidato
                            setupCandidateUI();
                        }
                    } else {
                        // Se o perfil não for encontrado, assume o padrão de candidato
                        setupCandidateUI();
                    }
                })
                .addOnFailureListener(e -> {
                    // Em caso de falha, também assume o padrão de candidato
                    setupCandidateUI();
                });
    }

    private void setupCompanyUI() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Esconde o item "Vagas salvas"
        navView.getMenu().findItem(R.id.navigation_vagas_salvas).setVisible(false);
        // Renomeia o item "Home" para "Meus anúncios"
        navView.getMenu().findItem(R.id.navigation_home).setTitle("Meus anúncios");
    }

    private void setupCandidateUI() {
        BottomNavigationView navView = findViewById(R.id.nav_view);
        // Garante que o item "Vagas salvas" está visível
        navView.getMenu().findItem(R.id.navigation_vagas_salvas).setVisible(true);
        // Garante que o título do item "Home" é "Empregos"
        navView.getMenu().findItem(R.id.navigation_home).setTitle("Empregos");
    }

    private void goToLoginActivity() {
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}