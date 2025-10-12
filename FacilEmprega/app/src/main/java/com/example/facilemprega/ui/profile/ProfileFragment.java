// Pacote: com.example.facilemprega.ui.profile
package com.example.facilemprega.ui.profile;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.facilemprega.ui.auth.LoginActivity;
import com.example.facilemprega.R;

public class ProfileFragment extends Fragment {

    private ProfileViewModel viewModel;
    private TextView textViewUserName, textViewUserEmail, textViewDisconnect, textViewCvStatus;
    private Button buttonUploadCv;

    // O launcher permanece na View, pois lida diretamente com Intents e resultados da Activity.
    private final ActivityResultLauncher<Intent> filePickerLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                if (result.getResultCode() == Activity.RESULT_OK && result.getData() != null) {
                    Uri fileUri = result.getData().getData();
                    if (fileUri != null) {
                        // A única responsabilidade aqui é passar a Uri para o ViewModel.
                        viewModel.uploadCv(fileUri);
                    }
                }
            });

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_profile, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        viewModel = new ViewModelProvider(this).get(ProfileViewModel.class);

        setupUI(view);
        setupClickListeners();
        observeViewModel();
    }

    private void setupUI(View root) {
        textViewUserName = root.findViewById(R.id.text_view_user_name);
        textViewUserEmail = root.findViewById(R.id.text_view_user_email);
        textViewDisconnect = root.findViewById(R.id.text_view_disconnect);
        textViewCvStatus = root.findViewById(R.id.text_view_cv_status);
        buttonUploadCv = root.findViewById(R.id.button_upload_cv);
    }

    private void setupClickListeners() {
        // Notifica o ViewModel sobre a intenção de logout
        textViewDisconnect.setOnClickListener(v -> viewModel.logout());

        // Abre o seletor de arquivos
        buttonUploadCv.setOnClickListener(v -> openFilePicker());
    }

    private void observeViewModel() {
        // Observa os dados do perfil do usuário
        viewModel.userProfile.observe(getViewLifecycleOwner(), documentSnapshot -> {
            if (documentSnapshot != null && documentSnapshot.exists()) {
                String name = documentSnapshot.getString("name");
                textViewUserName.setText(name != null ? name : documentSnapshot.getString("responsibleName"));
                textViewUserEmail.setText(documentSnapshot.getString("email"));

                if (documentSnapshot.contains("curriculumUrl")) {
                    textViewCvStatus.setText("Currículo carregado.");
                } else {
                    textViewCvStatus.setText("Nenhum currículo carregado.");
                }
            }
        });

        // Observa o status do upload do currículo
        viewModel.getCvUploadStatus().observe(getViewLifecycleOwner(), status -> {
            if (status == null) return;
            switch (status) {
                case "Carregando...":
                    textViewCvStatus.setText("A carregar...");
                    buttonUploadCv.setEnabled(false);
                    break;
                case "SUCESSO":
                    Toast.makeText(getContext(), "Currículo carregado com sucesso!", Toast.LENGTH_SHORT).show();
                    textViewCvStatus.setText("Currículo carregado.");
                    buttonUploadCv.setEnabled(true);
                    break;
                default: // Trata qualquer status de erro
                    Toast.makeText(getContext(), "Falha no upload.", Toast.LENGTH_LONG).show();
                    textViewCvStatus.setText("Falha ao carregar.");
                    buttonUploadCv.setEnabled(true);
                    break;
            }
        });

        // Observa o evento de logout
        viewModel.getUserLoggedOut().observe(getViewLifecycleOwner(), loggedOut -> {
            if (loggedOut != null && loggedOut) {
                goToLoginActivity();
            }
        });
    }

    private void openFilePicker() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("application/pdf");
        filePickerLauncher.launch(intent);
    }

    private void goToLoginActivity() {
        if (getActivity() == null) return;
        Intent intent = new Intent(getActivity(), LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        getActivity().finish();
    }
}