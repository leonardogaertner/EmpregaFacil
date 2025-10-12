// Pacote: com.example.facilemprega.repository
package com.example.facilemprega.repository;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class AuthRepository {

    private final FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private final FirebaseFirestore db = FirebaseFirestore.getInstance();

    // Classe interna para encapsular o resultado da operação
    public static class AuthResult {
        public Boolean isSuccess;
        public String errorMessage;

        public AuthResult(Boolean isSuccess, String errorMessage) {
            this.isSuccess = isSuccess;
            this.errorMessage = errorMessage;
        }
    }

    public LiveData<AuthResult> loginUser(String email, String password) {
        MutableLiveData<AuthResult> resultLiveData = new MutableLiveData<>();
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> resultLiveData.setValue(new AuthResult(true, null)))
                .addOnFailureListener(e -> resultLiveData.setValue(new AuthResult(false, e.getLocalizedMessage())));
        return resultLiveData;
    }

    public LiveData<AuthResult> registerUser(String name, String email, String password) {
        MutableLiveData<AuthResult> resultLiveData = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        saveUserProfile(firebaseUser.getUid(), name, email, resultLiveData);
                    }
                })
                .addOnFailureListener(e -> resultLiveData.setValue(new AuthResult(false, e.getLocalizedMessage())));
        return resultLiveData;
    }

    private void saveUserProfile(String uid, String name, String email, MutableLiveData<AuthResult> resultLiveData) {
        Map<String, Object> user = new HashMap<>();
        user.put("name", name);
        user.put("email", email);
        user.put("role", "candidato");
        user.put("createdAt", new Date());

        db.collection("users").document(uid).set(user)
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(new AuthResult(true, null)))
                .addOnFailureListener(e -> resultLiveData.setValue(new AuthResult(false, "Falha ao salvar perfil: " + e.getLocalizedMessage())));
    }

    public LiveData<AuthResult> registerCompany(String responsibleName, String companyName, String email, String password) {
        MutableLiveData<AuthResult> resultLiveData = new MutableLiveData<>();
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnSuccessListener(authResult -> {
                    FirebaseUser firebaseUser = mAuth.getCurrentUser();
                    if (firebaseUser != null) {
                        saveCompanyProfile(firebaseUser.getUid(), responsibleName, companyName, email, resultLiveData);
                    }
                })
                .addOnFailureListener(e -> resultLiveData.setValue(new AuthResult(false, e.getLocalizedMessage())));
        return resultLiveData;
    }

    private void saveCompanyProfile(String uid, String responsibleName, String companyName, String email, MutableLiveData<AuthResult> resultLiveData) {
        Map<String, Object> company = new HashMap<>();
        company.put("responsibleName", responsibleName);
        company.put("companyName", companyName);
        company.put("email", email);
        company.put("role", "empresa");
        company.put("createdAt", new Date());

        db.collection("users").document(uid).set(company)
                .addOnSuccessListener(aVoid -> resultLiveData.setValue(new AuthResult(true, null)))
                .addOnFailureListener(e -> resultLiveData.setValue(new AuthResult(false, "Falha ao salvar perfil da empresa: " + e.getLocalizedMessage())));
    }
}