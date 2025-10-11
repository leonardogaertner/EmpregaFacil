package com.example.facilemprega.ui.empresa;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import com.example.facilemprega.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CadastrarVagaFragment extends Fragment {

    private static final String TAG = "CadastrarVagaFragment";

    private EditText editTextNomeEmpresa, editTextNomeCargo, editTextDescricao, editTextLocalizacao, editTextSalario, editTextLink;
    private Button buttonCadastrar;

    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_cadastrar_vaga, container, false);

        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        editTextNomeEmpresa = root.findViewById(R.id.edit_text_nome_empresa);
        editTextNomeCargo = root.findViewById(R.id.edit_text_nome_cargo);
        editTextDescricao = root.findViewById(R.id.edit_text_descricao_vaga);
        editTextLocalizacao = root.findViewById(R.id.edit_text_localizacao);
        editTextSalario = root.findViewById(R.id.edit_text_salario);
        editTextLink = root.findViewById(R.id.edit_text_link_vaga);
        buttonCadastrar = root.findViewById(R.id.button_cadastrar_vaga);

        buttonCadastrar.setOnClickListener(v -> cadastrarVaga());

        return root;
    }

    private void cadastrarVaga() {
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            Toast.makeText(getContext(), "Erro: Utilizador não autenticado.", Toast.LENGTH_SHORT).show();
            return;
        }

        String nomeEmpresa = editTextNomeEmpresa.getText().toString().trim();
        String nomeCargo = editTextNomeCargo.getText().toString().trim();
        String descricao = editTextDescricao.getText().toString().trim();
        String localizacao = editTextLocalizacao.getText().toString().trim();
        String salarioStr = editTextSalario.getText().toString().trim();
        String link = editTextLink.getText().toString().trim();
        String empresaId = currentUser.getUid();

        if (TextUtils.isEmpty(nomeEmpresa) || TextUtils.isEmpty(nomeCargo) || TextUtils.isEmpty(salarioStr)) {
            Toast.makeText(getContext(), "Por favor, preencha os campos obrigatórios.", Toast.LENGTH_SHORT).show();
            return;
        }

        double salario = 0;
        try {
            salario = Double.parseDouble(salarioStr);
        } catch (NumberFormatException e) {
            Toast.makeText(getContext(), "Por favor, insira um valor de salário válido.", Toast.LENGTH_SHORT).show();
            return;
        }

        Map<String, Object> vaga = new HashMap<>();
        vaga.put("nomeEmpresa", nomeEmpresa);
        vaga.put("cargo", nomeCargo);
        vaga.put("descricao", descricao);
        vaga.put("localizacao", localizacao);
        vaga.put("salario", salario);
        vaga.put("link", link);
        vaga.put("empresaId", empresaId); // Campo crucial para filtrar os anúncios da empresa

        db.collection("vagas").add(vaga)
                .addOnSuccessListener(documentReference -> {
                    Toast.makeText(getContext(), "Vaga cadastrada com sucesso!", Toast.LENGTH_SHORT).show();
                    // Volta para a tela anterior (Meus Anúncios)
                    NavHostFragment.findNavController(CadastrarVagaFragment.this).navigateUp();
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao cadastrar vaga.", Toast.LENGTH_SHORT).show();
                    Log.w(TAG, "Erro ao adicionar documento", e);
                });
    }
}