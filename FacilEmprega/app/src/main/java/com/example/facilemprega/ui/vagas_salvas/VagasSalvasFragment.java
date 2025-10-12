// Pacote: com.example.facilemprega.ui.vagas_salvas
package com.example.facilemprega.ui.vagas_salvas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facilemprega.R;
import com.example.facilemprega.VagaAdapter;
import com.example.facilemprega.model.Vaga;
import com.example.facilemprega.ui.home.HomeViewModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

// --- MUDANÇA 1: Implementa a interface ---
public class VagasSalvasFragment extends Fragment implements VagaAdapter.OnSaveClickListener {

    private VagasSalvasViewModel viewModel;
    private RecyclerView recyclerView;
    private VagaAdapter vagaAdapter;
    // --- MUDANÇA 2: Adiciona referência ao HomeViewModel ---
    private HomeViewModel homeViewModel;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vagas_salvas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(VagasSalvasViewModel.class);
        // --- MUDANÇA 3: Instancia o HomeViewModel para reutilizar a lógica de salvar/remover ---
        homeViewModel = new ViewModelProvider(requireActivity()).get(HomeViewModel.class);

        setupUI(view);
        observeViewModel();
    }

    private void setupUI(View root) {
        recyclerView = root.findViewById(R.id.recycler_view_vagas_salvas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        // --- MUDANÇA 4: Passa 'this' como listener ---
        vagaAdapter = new VagaAdapter(new ArrayList<>(), new HashSet<>(), this);
        recyclerView.setAdapter(vagaAdapter);
    }

    private void observeViewModel() {
        viewModel.vagasSalvas.observe(getViewLifecycleOwner(), vagas -> {
            vagaAdapter.setVagas(vagas);
            // Na tela de vagas salvas, todos os itens exibidos estão, por definição, salvos.
            Set<String> ids = vagas.stream().map(Vaga::getId).collect(Collectors.toSet());
            vagaAdapter.setVagasSalvasIds(ids);
        });
    }

    // --- MUDANÇA 5: Implementação do método da interface ---
    @Override
    public void onSaveClick(Vaga vaga) {
        // Na tela de vagas salvas, um clique sempre significa "remover".
        // O segundo parâmetro é 'true' para indicar que a vaga está atualmente salva.
        homeViewModel.toggleVagaSalva(vaga.getId(), true);
        Toast.makeText(getContext(), "Vaga removida.", Toast.LENGTH_SHORT).show();
    }
}