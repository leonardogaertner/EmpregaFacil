package com.example.facilemprega.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.facilemprega.LoginActivity;
import com.example.facilemprega.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProfileFragment extends Fragment {

    private static final String TAG = "ProfileFragment";
    private TextView textViewUserName, textViewUserEmail, textViewDisconnect, textViewCvStatus;
    private Button buttonChangePassword, buttonUploadCv;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;
    private StorageReference storageRef;

    // Launcher para o seletor de ficheiros
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    if (fileUri != null) {
                        uploadCvToStorage(fileUri);
                    }
                }
            });

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_profile, container, false);

        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
        storageRef = FirebaseStorage.getInstance().getReference();

        // ... (findViewById para os outros componentes)
        buttonUploadCv = root.findViewById(R.id.button_upload_cv);
        textViewCvStatus = root.findViewById(R.id.text_view_cv_status);
        textViewDisconnect = root.findViewById(R.id.text_view_disconnect);
        textViewUserName = root.findViewById(R.id.text_view_user_name);
        textViewUserEmail = root.findViewById(R.id.text_view_user_email);


        loadUserProfile();

        textViewDisconnect.setOnClickListener(v -> disconnectUser());

        buttonUploadCv.setOnClickListener(v -> openFilePicker());

        return root;
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        filePickerLauncher.launch(intent);
    }

    private void uploadCvToStorage(Uri fileUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        textViewCvStatus.setText("A carregar...");
        // Cria uma referência para o ficheiro no Storage: curriculos/[UID do utilizador]/curriculo.pdf
        StorageReference userCvRef = storageRef.child("curriculos/" + user.getUid() + "/curriculo.pdf");

        userCvRef.putFile(fileUri)
                .addOnSuccessListener(taskSnapshot -> userCvRef.getDownloadUrl().addOnSuccessListener(this::updateUserCvUrl))
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Falha no upload: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    Log.e(TAG, "Falha no upload do CV", e);
                    textViewCvStatus.setText("Falha ao carregar.");
                });
    }

    private void updateUserCvUrl(Uri downloadUri) {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user == null) return;

        db.collection("users").document(user.getUid())
                .update("curriculumUrl", downloadUri.toString())
                .addOnSuccessListener(aVoid -> {
                    Toast.makeText(getContext(), "Currículo carregado com sucesso!", Toast.LENGTH_SHORT).show();
                    textViewCvStatus.setText("Currículo carregado.");
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(getContext(), "Erro ao guardar URL do currículo.", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Erro ao atualizar o Firestore com a URL do CV", e);
                });
    }

    private void loadUserProfile() {
        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null) {
            String uid = user.getUid();
            textViewUserEmail.setText(user.getEmail());

            db.collection("users").document(uid).get().addOnSuccessListener(documentSnapshot -> {
                if (documentSnapshot.exists()) {
                    String name = documentSnapshot.getString("name");
                    textViewUserName.setText(name != null ? name : documentSnapshot.getString("responsibleName"));

                    // Verifica se já existe um currículo
                    if (documentSnapshot.contains("curriculumUrl")) {
                        textViewCvStatus.setText("Currículo carregado.");
                    } else {
                        textViewCvStatus.setText("Nenhum currículo carregado.");
                    }
                }
            });
        }
    }

    private void disconnectUser() {
        mAuth.signOut();
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        if (getActivity() != null) {
            getActivity().finish();
        }
    }
}