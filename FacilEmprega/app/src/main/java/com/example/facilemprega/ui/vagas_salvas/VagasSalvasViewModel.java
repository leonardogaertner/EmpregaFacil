// Pacote: com.example.facilemprega.ui.vagas_salvas
package com.example.facilemprega.ui.vagas_salvas;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.facilemprega.model.Vaga;
import com.example.facilemprega.repository.VagaRepository;

import java.util.List;

public class VagasSalvasViewModel extends ViewModel {

    private final VagaRepository vagaRepository;
    public final LiveData<List<Vaga>> vagasSalvas;

    public VagasSalvasViewModel() {
        this.vagaRepository = new VagaRepository();
        this.vagasSalvas = vagaRepository.getVagasSalvas();
    }
}