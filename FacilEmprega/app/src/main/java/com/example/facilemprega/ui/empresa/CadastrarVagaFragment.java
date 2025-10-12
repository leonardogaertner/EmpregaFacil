// Pacote: com.example.facilemprega.ui.empresa
package com.example.facilemprega.ui.empresa;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.fragment.NavHostFragment;

import com.example.facilemprega.R;

public class CadastrarVagaFragment extends Fragment {

    private CadastrarVagaViewModel viewModel;
    private EditText editTextNomeEmpresa, editTextNomeCargo, editTextDescricao, editTextLocalizacao, editTextSalario, editTextLink;
    private Button buttonCadastrar;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_cadastrar_vaga, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inicializa o ViewModel
        viewModel = new ViewModelProvider(this).get(CadastrarVagaViewModel.class);

        // Referências da UI
        editTextNomeEmpresa = view.findViewById(R.id.edit_text_nome_empresa);
        editTextNomeCargo = view.findViewById(R.id.edit_text_nome_cargo);
        editTextDescricao = view.findViewById(R.id.edit_text_descricao_vaga);
        editTextLocalizacao = view.findViewById(R.id.edit_text_localizacao);
        editTextSalario = view.findViewById(R.id.edit_text_salario);
        editTextLink = view.findViewById(R.id.edit_text_link_vaga);
        buttonCadastrar = view.findViewById(R.id.button_cadastrar_vaga);

        buttonCadastrar.setOnClickListener(v -> tentarCadastrarVaga());
    }

    private void tentarCadastrarVaga() {
        String nomeEmpresa = editTextNomeEmpresa.getText().toString().trim();
        String nomeCargo = editTextNomeCargo.getText().toString().trim();
        String descricao = editTextDescricao.getText().toString().trim();
        String localizacao = editTextLocalizacao.getText().toString().trim();
        String salarioStr = editTextSalario.getText().toString().trim();
        String link = editTextLink.getText().toString().trim();

        if (TextUtils.isEmpty(nomeEmpresa) || TextUtils.isEmpty(nomeCargo) || TextUtils.isEmpty(salarioStr)) {
            Toast.makeText(getContext(), "Por favor, preencha os campos obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        // Chama o método do ViewModel e observa o resultado
        viewModel.cadastrarVaga(nomeEmpresa, nomeCargo, descricao, localizacao, salarioStr, link)
                .observe(getViewLifecycleOwner(), sucesso -> {
                    if (sucesso) {
                        Toast.makeText(getContext(), "Vaga cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                        NavHostFragment.findNavController(CadastrarVagaFragment.this).navigateUp();
                    } else {
                        Toast.makeText(getContext(), "Erro ao cadastrar vaga.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}