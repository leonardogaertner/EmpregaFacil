package com.example.facilemprega.ui.home;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facilemprega.R;
import com.example.facilemprega.Vaga;
import com.example.facilemprega.VagaAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeFragment extends Fragment {

    private static final String TAG = "HomeFragment";
    private RecyclerView recyclerView;
    private VagaAdapter vagaAdapter;
    private List<Vaga> vagaList;
    private FirebaseFirestore db;
    private Set<String> vagasSalvasIds;
    private FloatingActionButton fabAddVaga;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_home, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.recycler_view_vagas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        vagaList = new ArrayList<>();
        vagasSalvasIds = new HashSet<>();
        vagaAdapter = new VagaAdapter(vagaList, vagasSalvasIds);
        recyclerView.setAdapter(vagaAdapter);

        fabAddVaga = root.findViewById(R.id.fab_add_vaga);
        fabAddVaga.setOnClickListener(v -> {
            // Navega para a tela de adicionar vaga
            NavHostFragment.findNavController(HomeFragment.this)
                    .navigate(R.id.action_homeFragment_to_cadastrarVagaFragment);
        });

        checkUserRoleAndLoadData();

        return root;
    }

    private void checkUserRoleAndLoadData() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            fabAddVaga.setVisibility(View.GONE);
            return;
        }

        FirebaseFirestore.getInstance().collection("users").document(user.getUid()).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        String role = documentSnapshot.getString("role");
                        if ("empresa".equals(role)) {
                            fabAddVaga.setVisibility(View.VISIBLE);
                            carregarVagasDaEmpresa(user.getUid());
                        } else {
                            fabAddVaga.setVisibility(View.GONE);
                            carregarVagasSalvasEEmpregos(); // Função para o candidato
                        }
                    } else {
                        // Se não encontrar o perfil, assume que é candidato
                        fabAddVaga.setVisibility(View.GONE);
                        carregarVagasSalvasEEmpregos();
                    }
                });
    }

    private void carregarVagasDaEmpresa(String empresaUid) {
        db.collection("vagas").whereEqualTo("empresaId", empresaUid).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vagaList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Vaga vaga = document.toObject(Vaga.class);
                                vaga.setId(document.getId());
                                vagaList.add(vaga);
                            } catch (Exception e) {
                                Log.e(TAG, "Erro ao converter vaga da empresa: " + document.getId(), e);
                            }
                        }
                        vagaAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Erro ao buscar vagas da empresa.", task.getException());
                    }
                });
    }

    private void carregarVagasSalvasEEmpregos() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            carregarVagasDeEmprego();
            return;
        }

        db.collection("users").document(user.getUid()).collection("vagasSalvas").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vagasSalvasIds.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            vagasSalvasIds.add(document.getId());
                        }
                    }
                    carregarVagasDeEmprego();
                });
    }

    private void carregarVagasDeEmprego() {
        db.collection("vagas").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vagaList.clear();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            try {
                                Vaga vaga = document.toObject(Vaga.class);
                                vaga.setId(document.getId());
                                vagaList.add(vaga);
                            } catch (Exception e) {
                                Log.e(TAG, "Erro ao converter documento: " + document.getId(), e);
                            }
                        }
                        vagaAdapter.notifyDataSetChanged();
                    } else {
                        Log.w(TAG, "Erro ao buscar vagas.", task.getException());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}