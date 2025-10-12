// Pacote: com.example.facilemprega.ui.vagas_salvas
package com.example.facilemprega.ui.vagas_salvas;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facilemprega.R;
import com.example.facilemprega.VagaAdapter;
import com.example.facilemprega.model.Vaga;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class VagasSalvasFragment extends Fragment {

    private VagasSalvasViewModel viewModel;
    private RecyclerView recyclerView;
    private VagaAdapter vagaAdapter;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_vagas_salvas, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(VagasSalvasViewModel.class);

        setupUI(view);
        observeViewModel();
    }

    private void setupUI(View root) {
        recyclerView = root.findViewById(R.id.recycler_view_vagas_salvas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        vagaAdapter = new VagaAdapter(new ArrayList<>(), new HashSet<>());
        recyclerView.setAdapter(vagaAdapter);
    }

    private void observeViewModel() {
        viewModel.vagasSalvas.observe(getViewLifecycleOwner(), vagas -> {
            vagaAdapter.setVagas(vagas);
            // Atualiza também os IDs para o botão de salvar funcionar corretamente
            vagaAdapter.setVagasSalvasIds(vagas.stream().map(Vaga::getId).collect(Collectors.toSet()));
        });
    }
}