// Pacote: com.example.facilemprega.ui.empresa
package com.example.facilemprega.ui.empresa;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.facilemprega.repository.VagaRepository;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;
import java.util.Map;

public class CadastrarVagaViewModel extends ViewModel {

    private final VagaRepository vagaRepository;
    private final FirebaseAuth mAuth;

    public CadastrarVagaViewModel() {
        this.vagaRepository = new VagaRepository();
        this.mAuth = FirebaseAuth.getInstance();
    }

    public LiveData<Boolean> cadastrarVaga(String nomeEmpresa, String nomeCargo, String descricao, String localizacao, String salarioStr, String link) {
        String empresaId = mAuth.getCurrentUser().getUid();

        double salario = 0;
        try {
            salario = Double.parseDouble(salarioStr);
        } catch (NumberFormatException e) {
            // Pode retornar um LiveData de erro aqui se quiser tratar especificamente
            return new androidx.lifecycle.MutableLiveData<>(false);
        }

        Map<String, Object> vaga = new HashMap<>();
        vaga.put("nomeEmpresa", nomeEmpresa);
        vaga.put("cargo", nomeCargo);
        vaga.put("descricao", descricao);
        vaga.put("localizacao", localizacao);
        vaga.put("salario", salario);
        vaga.put("link", link);
        vaga.put("empresaId", empresaId);

        return vagaRepository.cadastrarVaga(vaga);
    }
}