// Pacote: com.example.facilemprega.ui.home
package com.example.facilemprega.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facilemprega.R;
import com.example.facilemprega.VagaAdapter;
import com.example.facilemprega.model.Vaga;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;
import java.util.HashSet;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private VagaAdapter vagaAdapter;
    private FloatingActionButton fabAddVaga;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        setupUI(view);
        observeViewModel();
    }

    private void setupUI(View root) {
        recyclerView = root.findViewById(R.id.recycler_view_vagas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // Passa listas vazias inicialmente. O adapter será atualizado pelo LiveData.
        vagaAdapter = new VagaAdapter(new ArrayList<>(), new HashSet<>());
        recyclerView.setAdapter(vagaAdapter);

        fabAddVaga = root.findViewById(R.id.fab_add_vaga);
        fabAddVaga.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment_to_cadastrarVagaFragment));
    }

    private void observeViewModel() {
        homeViewModel.userRole.observe(getViewLifecycleOwner(), role -> {
            if ("empresa".equals(role)) {
                fabAddVaga.setVisibility(View.VISIBLE);
            } else {
                fabAddVaga.setVisibility(View.GONE);
            }
            // Depois de saber o perfil, carrega as vagas correspondentes
            homeViewModel.getVagasComBaseNoPerfil(role).observe(getViewLifecycleOwner(), vagas -> {
                vagaAdapter.setVagas(vagas); // Um novo método no adapter para atualizar a lista
            });
        });
    }
}