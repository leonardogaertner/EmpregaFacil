// Pacote: com.example.facilemprega
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
    // --- MUDANÇA 1: Interface de comunicação ---
    private OnSaveClickListener listener;

    /**
     * Interface para comunicar eventos de clique no botão 'salvar' de volta para o Fragment.
     */
    public interface OnSaveClickListener {
        void onSaveClick(Vaga vaga);
    }

    // --- MUDANÇA 2: Construtor atualizado ---
    public VagaAdapter(List<Vaga> vagas, Set<String> vagasSalvasIds, OnSaveClickListener listener) {
        this.vagas = vagas;
        this.vagasSalvasIds = vagasSalvasIds;
        this.listener = listener;
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
        if (vaga == null || vaga.getId() == null) return;

        holder.nomeEmpresa.setText(vaga.getNomeEmpresa());
        holder.cargo.setText(vaga.getCargo());

        Locale ptBr = new Locale("pt", "BR");
        NumberFormat formatoMoeda = NumberFormat.getCurrencyInstance(ptBr);
        holder.salario.setText(formatoMoeda.format(vaga.getSalario()));

        holder.acessarVaga.setOnClickListener(v -> {
            // ... (código para abrir link continua o mesmo)
            String url = vaga.getLink();
            if (url != null && !url.isEmpty()) {
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

        // --- MUDANÇA 3: Lógica do botão de salvar ---
        final boolean isSaved = vagasSalvasIds.contains(vaga.getId());
        holder.saveButton.setSelected(isSaved);

        holder.saveButton.setOnClickListener(v -> {
            // Notifica o listener (o Fragment) que o botão foi clicado
            if (listener != null) {
                listener.onSaveClick(vaga);
            }
        });
    }

    @Override
    public int getItemCount() {
        return vagas.size();
    }

    public static class VagaViewHolder extends RecyclerView.ViewHolder {
        // ... (ViewHolder continua o mesmo)
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

    // --- MUDANÇA 4: Método setVagasSalvasIds atualizado ---
    public void setVagas(List<Vaga> novasVagas) {
        this.vagas.clear();
        this.vagas.addAll(novasVagas);
        notifyDataSetChanged();
    }

    /**
     * Atualiza o conjunto de IDs de vagas salvas e notifica o adapter para
     * redesenhar os itens, atualizando o estado visual dos botões de salvar.
     */
    public void setVagasSalvasIds(Set<String> ids) {
        this.vagasSalvasIds = ids;
        notifyDataSetChanged();
    }
}