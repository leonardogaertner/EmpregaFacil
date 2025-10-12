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
    private final MutableLiveData<String> userRole = new MutableLiveData<>();
    private final MutableLiveData<DocumentSnapshot> userProfile = new MutableLiveData<>();
    private final MutableLiveData<Boolean> vagaCadastroStatus = new MutableLiveData<>();

    public LiveData<String> getUserRole() {
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

    public LiveData<List<Vaga>> getVagasSalvas() {
        MutableLiveData<List<Vaga>> vagasSalvas = new MutableLiveData<>();
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) {
            vagasSalvas.setValue(new ArrayList<>());
            return vagasSalvas;
        }

        db.collection("users").document(user.getUid()).collection("vagasSalvas").get()
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            Task<DocumentSnapshot> vagaTask = db.collection("vagas").document(document.getId()).get();
                            tasks.add(vagaTask);
                        }

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
                            vagasSalvas.setValue(vagaList);
                        });
                    }
                });
        return vagasSalvas;
    }

    public LiveData<Boolean> cadastrarVaga(Map<String, Object> vaga) {
        db.collection("vagas").add(vaga)
                .addOnSuccessListener(documentReference -> vagaCadastroStatus.setValue(true))
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Erro ao adicionar documento", e);
                    vagaCadastroStatus.setValue(false);
                });
        return vagaCadastroStatus;
    }

    public LiveData<DocumentSnapshot> getUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            db.collection("users").document(user.getUid()).get()
                    .addOnSuccessListener(userProfile::setValue);
        }
        return userProfile;
    }

    public void uploadCurriculo(Uri fileUri, MutableLiveData<String> uploadStatus) {
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
}