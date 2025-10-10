package com.example.facilemprega;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.NumberFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public class VagaAdapter extends RecyclerView.Adapter<VagaAdapter.VagaViewHolder> {

    private static final String TAG = "VagaAdapter"; // Tag para os nossos logs
    private List<Vaga> vagas;
    private Set<String> vagasSalvasIds;

    public VagaAdapter(List<Vaga> vagas, Set<String> vagasSalvasIds) {
        this.vagas = vagas;
        this.vagasSalvasIds = vagasSalvasIds;
    }

    @NonNull
    @Override
    public VagaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vaga, parent, false);
        return new VagaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VagaViewHolder holder, int position) {
        Vaga vaga = vagas.get(position);

        holder.nomeEmpresa.setText(vaga.getNomeEmpresa());
        holder.cargo.setText(vaga.getCargo());

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(ptBr);
        holder.salario.setText(formatoMoeda.format(vaga.getSalario()));

        holder.saveButton.setSelected(vagasSalvasIds.contains(vaga.getId()));

        holder.saveButton.setOnClickListener(v -> {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            if (user == null || vaga.getId() == null) {
                Log.w(TAG, "Utilizador não logado ou ID da vaga é nulo. Ação de salvar cancelada.");
                Toast.makeText(holder.itemView.getContext(), "Faça login para salvar vagas", Toast.LENGTH_SHORT).show();
                return;
            }

            String userId = user.getUid();
            String vagaId = vaga.getId();
            Log.d(TAG, "Botão Salvar clicado para a vaga ID: " + vagaId);

            DocumentReference vagaSalvaRef = FirebaseFirestore.getInstance()
                    .collection("users").document(userId)
                    .collection("vagasSalvas").document(vagaId);

            vagaSalvaRef.get().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    if (task.getResult().exists()) {
                        Log.d(TAG, "A vaga já está salva. A remover...");
                        vagaSalvaRef.delete()
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Vaga removida com sucesso do Firestore."))
                                .addOnFailureListener(e -> Log.e(TAG, "Erro ao remover a vaga do Firestore.", e));
                    } else {
                        Log.d(TAG, "A vaga não está salva. A adicionar...");
                        Map<String, Object> data = new HashMap<>();
                        data.put("savedAt", new Date());
                        vagaSalvaRef.set(data)
                                .addOnSuccessListener(aVoid -> Log.d(TAG, "Vaga adicionada com sucesso ao Firestore."))
                                .addOnFailureListener(e -> Log.e(TAG, "Erro ao adicionar a vaga ao Firestore.", e));
                    }
                } else {
                    Log.e(TAG, "Falha ao verificar se a vaga está salva.", task.getException());
                }
            });

            // Atualização visual instantânea
            if (vagasSalvasIds.contains(vagaId)) {
                vagasSalvasIds.remove(vagaId);
                holder.saveButton.setSelected(false);
                Toast.makeText(holder.itemView.getContext(), "Vaga removida", Toast.LENGTH_SHORT).show();
            } else {
                vagasSalvasIds.add(vagaId);
                holder.saveButton.setSelected(true);
                Toast.makeText(holder.itemView.getContext(), "Vaga salva!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public int getItemCount() {
        return vagas.size();
    }

    public static class VagaViewHolder extends RecyclerView.ViewHolder {
        TextView nomeEmpresa, cargo, salario, acessarVaga;
        ImageView saveButton;

        public VagaViewHolder(@NonNull View itemView) {
            super(itemView);
            nomeEmpresa = itemView.findViewById(R.id.text_view_nome_empresa);
            cargo = itemView.findViewById(R.id.text_view_cargo);
            salario = itemView.findViewById(R.id.text_view_salario);
            acessarVaga = itemView.findViewById(R.id.text_view_acessar_vaga);
            saveButton = itemView.findViewById(R.id.image_view_save_vaga);
        }
    }
}