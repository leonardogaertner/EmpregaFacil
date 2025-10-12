// Pacote: com.example.facilemprega.ui.home
package com.example.facilemprega.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.facilemprega.model.Vaga;
import com.example.facilemprega.repository.VagaRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

public class HomeViewModel extends ViewModel {

    private final VagaRepository vagaRepository;
    private final FirebaseAuth mAuth;
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
}