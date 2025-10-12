// Pacote: com.example.facilemprega.ui.home
package com.example.facilemprega.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.facilemprega.model.Vaga;
import com.example.facilemprega.repository.VagaRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class HomeViewModel extends ViewModel {

    private final VagaRepository vagaRepository;
    private final FirebaseAuth mAuth;
    // --- MUDANÇA AQUI ---
    private final FirebaseFirestore db = FirebaseFirestore.getInstance(); // Adicionado para o SnapshotListener
    public LiveData<String> userRole;
    public LiveData<List<Vaga>> vagas;


    public HomeViewModel() {
        vagaRepository = new VagaRepository();
        mAuth = FirebaseAuth.getInstance();
        userRole = vagaRepository.getUserRole();
    }

    public LiveData<List<Vaga>> getVagasComBaseNoPerfil(String role) {
        if ("empresa".equals(role)) {
            vagas = vagaRepository.getVagasParaEmpresa(mAuth.getCurrentUser().getUid());
        } else {
            vagas = vagaRepository.getTodasAsVagas();
        }
        return vagas;
    }

    // --- MUDANÇAS AQUI ---

    /**
     * Alterna o estado de uma vaga (salva/não salva).
     * @param vagaId O ID da vaga.
     * @param isCurrentlySaved true se a vaga já está salva, false caso contrário.
     */
    public void toggleVagaSalva(String vagaId, boolean isCurrentlySaved) {
        if (isCurrentlySaved) {
            vagaRepository.removerVagaSalva(vagaId);
        } else {
            vagaRepository.salvarVaga(vagaId);
        }
    }

    /**
     * Retorna um LiveData com um conjunto de IDs das vagas salvas pelo usuário.
     * Usa um SnapshotListener para atualizações em tempo real.
     */
    public LiveData<Set<String>> getVagasSalvasIds() {
        MutableLiveData<Set<String>> vagasSalvasIds = new MutableLiveData<>();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            vagasSalvasIds.setValue(new HashSet<>());
            return vagasSalvasIds;
        }

        db.collection("users").document(user.getUid()).collection("vagasSalvas")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        return;
                    }
                    Set<String> ids = new HashSet<>();
                    for (QueryDocumentSnapshot doc : snapshots) {
                        ids.add(doc.getId());
                    }
                    vagasSalvasIds.setValue(ids);
                });

        return vagasSalvasIds;
    }
}