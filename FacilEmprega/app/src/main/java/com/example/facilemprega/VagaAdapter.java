package com.example.facilemprega;

import android.content.Intent;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.facilemprega.model.Vaga;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public class VagaAdapter extends RecyclerView.Adapter<VagaAdapter.VagaViewHolder> {

    private static final String TAG = "VagaAdapter";
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

        // --- LÓGICA DO BOTÃO "ACESSAR VAGA" ---
        holder.acessarVaga.setOnClickListener(v -> {
            String url = vaga.getLink();
            if (url != null && !url.isEmpty()) {
                // Adiciona "http://" se o link não tiver um protocolo
                if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    url = "http://" + url;
                }
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                try {
                    holder.itemView.getContext().startActivity(browserIntent);
                } catch (Exception e) {
                    Toast.makeText(holder.itemView.getContext(), "Não foi possível abrir o link.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao abrir o link da vaga: " + url, e);
                }
            } else {
                Toast.makeText(holder.itemView.getContext(), "Link da vaga não disponível.", Toast.LENGTH_SHORT).show();
            }
        });


        // --- Lógica do botão de salvar (continua igual) ---
        holder.saveButton.setSelected(vagasSalvasIds.contains(vaga.getId()));
        holder.saveButton.setOnClickListener(v -> {
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
    public void setVagas(List<Vaga> novasVagas) {
        this.vagas.clear();
        this.vagas.addAll(novasVagas);
        notifyDataSetChanged();
    }

    public void setVagasSalvasIds(Set<String> ids) {
        this.vagasSalvasIds.clear();
        this.vagasSalvasIds.addAll(ids);
        notifyDataSetChanged();
    }
}