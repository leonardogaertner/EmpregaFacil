package com.example.facilemprega.ui.vagas_salvas;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facilemprega.R;
import com.example.facilemprega.Vaga;
import com.example.facilemprega.VagaAdapter;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class VagasSalvasFragment extends Fragment {

    private static final String TAG = "VagasSalvasFragment";
    private RecyclerView recyclerView;
    private VagaAdapter vagaAdapter;
    private List<Vaga> vagaList;
    private Set<String> vagasSalvasIds;
    private FirebaseFirestore db;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_vagas_salvas, container, false);

        db = FirebaseFirestore.getInstance();
        recyclerView = root.findViewById(R.id.recycler_view_vagas_salvas);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        vagaList = new ArrayList<>();
        vagasSalvasIds = new HashSet<>();
        vagaAdapter = new VagaAdapter(vagaList, vagasSalvasIds);
        recyclerView.setAdapter(vagaAdapter);

        carregarVagasSalvas();

        return root;
    }

    private void carregarVagasSalvas() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user == null) {
            return; // Usuário não logado, não há vagas salvas para mostrar
        }

        // 1. Pega a lista de IDs de vagas da subcoleção do usuário
        db.collection("users").document(user.getUid()).collection("vagasSalvas").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        vagasSalvasIds.clear();
                        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            vagasSalvasIds.add(document.getId());
                            // 2. Para cada ID, cria uma tarefa para buscar os detalhes da vaga
                            Task<DocumentSnapshot> vagaTask = db.collection("vagas").document(document.getId()).get();
                            tasks.add(vagaTask);
                        }

                        // 3. Executa todas as tarefas de busca de vagas em paralelo
                        Tasks.whenAllSuccess(tasks).addOnSuccessListener(list -> {
                            vagaList.clear();
                            for (Object object : list) {
                                DocumentSnapshot doc = (DocumentSnapshot) object;
                                try {
                                    Vaga vaga = doc.toObject(Vaga.class);
                                    if (vaga != null) {
                                        vaga.setId(doc.getId());
                                        vagaList.add(vaga);
                                    }
                                } catch (Exception e) {
                                    Log.e(TAG, "Erro ao converter vaga salva: " + doc.getId(), e);
                                }
                            }
                            vagaAdapter.notifyDataSetChanged();
                        });
                    } else {
                        Log.w(TAG, "Erro ao buscar IDs de vagas salvas.", task.getException());
                    }
                });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}