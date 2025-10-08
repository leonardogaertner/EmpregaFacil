package com.example.facilemprega;

import android.content.Intent; // Importe o Intent para redirecionamento
import android.os.Bundle;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.facilemprega.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth; // Importe o FirebaseAuth
import com.google.firebase.auth.FirebaseUser; // Importe o FirebaseUser

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding binding;
    private FirebaseAuth mAuth; // Variável para o Firebase Auth

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 1. Inicializa o Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Código de inicialização da UI (BottomNavigationView e Navigation Component)
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        BottomNavigationView navView = findViewById(R.id.nav_view);

        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_home, R.id.navigation_dashboard, R.id.navigation_notifications)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_main);
        NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);
    }

    /**
     * 2. Checa o estado da autenticação sempre que a Activity for iniciada.
     */
    @Override
    public void onStart() {
        super.onStart();

        // Obtém o usuário atualmente logado
        FirebaseUser currentUser = mAuth.getCurrentUser();

        // Se o usuário for nulo (não logado), redireciona para a LoginActivity
        if (currentUser == null) {
            Intent intent = new Intent(MainActivity.this, LoginActivity.class);
            startActivity(intent);

            // É crucial chamar 'finish()' para que o usuário não possa voltar
            // para a MainActivity (protegida) ao apertar o botão "Voltar".
            finish();
        }
    }
}