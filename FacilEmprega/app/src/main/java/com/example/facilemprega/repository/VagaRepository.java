// Pacote: com.example.facilemprega.repository
package com.example.facilemprega.repository;

import android.net.Uri;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.facilemprega.model.Vaga;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class VagaRepository {

    private static final String TAG = "VagaRepository";
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final StorageReference storageRef = FirebaseStorage.getInstance().getReference();

    // ... (outros métodos como getUserRole, cadastrarVaga, etc., continuam iguais)
    public LiveData<String> getUserRole() {
        MutableLiveData<String> userRole = new MutableLiveData<>();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(documentSnapshot -> {
                        if (documentSnapshot.exists()) {
                            userRole.setValue(documentSnapshot.getString("role"));
                        }
                    });
        }
        return userRole;
    }

    public LiveData<List<Vaga>> getVagasParaEmpresa(String empresaId) {
        MutableLiveData<List<Vaga>> vagasDaEmpresa = new MutableLiveData<>();
        db.collection("vagas").whereEqualTo("empresaId", empresaId).get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Vaga> vagaList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Vaga vaga = document.toObject(Vaga.class);
                            vaga.setId(document.getId());
                            vagaList.add(vaga);
                        }
                        vagasDaEmpresa.setValue(vagaList);
                    }
                });
        return vagasDaEmpresa;
    }

    public LiveData<List<Vaga>> getTodasAsVagas() {
        MutableLiveData<List<Vaga>> todasAsVagas = new MutableLiveData<>();
        db.collection("vagas").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Vaga> vagaList = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Vaga vaga = document.toObject(Vaga.class);
                            vaga.setId(document.getId());
                            vagaList.add(vaga);
                        }
                        todasAsVagas.setValue(vagaList);
                    }
                });
        return todasAsVagas;
    }

    public LiveData<DocumentSnapshot> getUserProfile() {
        MutableLiveData<DocumentSnapshot> userProfile = new MutableLiveData<>();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(userProfile::setValue);
        }
        return userProfile;
    }

    public LiveData<Boolean> cadastrarVaga(Map<String, Object> vaga) {
        MutableLiveData<Boolean> vagaCadastroStatus = new MutableLiveData<>();
        db.collection("vagas").add(vaga)
                .addOnSuccessListener(documentReference -> vagaCadastroStatus.setValue(true))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Erro ao adicionar documento", e);
                    vagaCadastroStatus.setValue(false);
                });
        return vagaCadastroStatus;
    }

    public void uploadCurriculo(Uri fileUri, MutableLiveData<String> uploadStatus) {
        // ... (código do upload continua o mesmo)
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            uploadStatus.setValue("ERRO: Usuário não autenticado.");
            return;
        }

        uploadStatus.setValue("Carregando...");
        StorageReference userCvRef = storageRef.child("curriculos/" + user.getUid() + "/curriculo.pdf");

        userCvRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> userCvRef.getDownloadUrl().addOnSuccessListener(downloadUri -> {
                    db.collection("users").document(user.getUid())
                            .update("curriculumUrl", downloadUri.toString())
                            .addOnSuccessListener(aVoid -> uploadStatus.setValue("SUCESSO"))
                            .addOnFailureListener(e -> uploadStatus.setValue("ERRO: Falha ao salvar URL."));
                }))
                .addOnFailureListener(e -> uploadStatus.setValue("ERRO: " + e.getMessage()));
    }
    public LiveData<List<Vaga>> getVagasSalvas() {
        MutableLiveData<List<Vaga>> vagasSalvas = new MutableLiveData<>();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            vagasSalvas.setValue(new ArrayList<>());
            return vagasSalvas;
        }

        // Troca o .get() por .addSnapshotListener para ouvir em tempo real
        db.collection("users").document(user.getUid()).collection("vagasSalvas")
                .addSnapshotListener((snapshots, error) -> {
                    if (error != null || snapshots == null) {
                        Log.w(TAG, "Listen failed.", error);
                        vagasSalvas.setValue(new ArrayList<>()); // Em caso de erro, retorna lista vazia
                        return;
                    }

                    // Se a lista de snapshots estiver vazia (nenhuma vaga salva), atualiza a UI
                    if (snapshots.isEmpty()) {
                        vagasSalvas.setValue(new ArrayList<>());
                        return;
                    }

                    List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                    for (QueryDocumentSnapshot document : snapshots) {
                        // Para cada ID de vaga salva, cria uma tarefa para buscar os detalhes completos
                        Task<DocumentSnapshot> vagaTask = db.collection("vagas").document(document.getId()).get();
                        tasks.add(vagaTask);
                    }

                    // Quando todas as buscas de detalhes terminarem...
                    Tasks.whenAllSuccess(tasks).addOnSuccessListener(list -> {
                        List<Vaga> vagaList = new ArrayList<>();
                        for (Object object : list) {
                            DocumentSnapshot doc = (DocumentSnapshot) object;
                            Vaga vaga = doc.toObject(Vaga.class);
                            if (vaga != null) {
                                vaga.setId(doc.getId());
                                vagaList.add(vaga);
                            }
                        }
                        // Atualiza o LiveData, que por sua vez, atualiza a UI
                        vagasSalvas.setValue(vagaList);
                    });
                });
        return vagasSalvas;
    }


    public void salvarVaga(String vagaId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || vagaId == null) return;

        db.collection("users").document(user.getUid())
                .collection("vagasSalvas").document(vagaId)
                .set(new java.util.HashMap<>());
    }

    public void removerVagaSalva(String vagaId) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null || vagaId == null) return;

        db.collection("users").document(user.getUid())
                .collection("vagasSalvas").document(vagaId)
                .delete();
    }
}