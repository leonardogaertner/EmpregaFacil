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
import java.util.Set;

// --- MUDANÇA 1: Implementa a interface ---
public class HomeFragment extends Fragment implements VagaAdapter.OnSaveClickListener {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private VagaAdapter vagaAdapter;
    private FloatingActionButton fabAddVaga;
    // --- MUDANÇA 2: Guarda o estado local dos IDs ---
    private Set<String> vagasSalvasIds = new HashSet<>();

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

        // --- MUDANÇA 3: Passa 'this' como o listener ---
        vagaAdapter = new VagaAdapter(new ArrayList<>(), vagasSalvasIds, this);
        recyclerView.setAdapter(vagaAdapter);

        fabAddVaga = root.findViewById(R.id.fab_add_vaga);
        fabAddVaga.setOnClickListener(v ->
                NavHostFragment.findNavController(HomeFragment.this)
                        .navigate(R.id.action_homeFragment_to_cadastrarVagaFragment));
    }

    private void observeViewModel() {
        // --- MUDANÇA 4: Novo observer para os IDs ---
        homeViewModel.getVagasSalvasIds().observe(getViewLifecycleOwner(), ids -> {
            this.vagasSalvasIds = ids;
            vagaAdapter.setVagasSalvasIds(ids);
        });

        homeViewModel.userRole.observe(getViewLifecycleOwner(), role -> {
            if (role == null) return;
            if ("empresa".equals(role)) {
                fabAddVaga.setVisibility(View.VISIBLE);
            } else {
                fabAddVaga.setVisibility(View.GONE);
            }

            homeViewModel.getVagasComBaseNoPerfil(role).observe(getViewLifecycleOwner(), vagas -> {
                vagaAdapter.setVagas(vagas);
            });
        });
    }

    // --- MUDANÇA 5: Implementação do método da interface ---
    @Override
    public void onSaveClick(Vaga vaga) {
        // Verifica se a vaga já está salva e chama o ViewModel
        boolean isCurrentlySaved = vagasSalvasIds.contains(vaga.getId());
        homeViewModel.toggleVagaSalva(vaga.getId(), isCurrentlySaved);
    }
}