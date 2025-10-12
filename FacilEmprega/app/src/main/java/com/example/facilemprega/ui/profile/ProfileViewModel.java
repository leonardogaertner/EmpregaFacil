// Pacote: com.example.facilemprega.ui.profile
package com.example.facilemprega.ui.profile;

import android.net.Uri;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.facilemprega.repository.VagaRepository;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;

public class ProfileViewModel extends ViewModel {

    private final VagaRepository vagaRepository;
    private final FirebaseAuth mAuth;

    // LiveData para os dados do perfil do usuário
    public final LiveData<DocumentSnapshot> userProfile;

    // LiveData para o status do upload do currículo
    private final MutableLiveData<String> cvUploadStatus = new MutableLiveData<>();

    // LiveData para sinalizar que o usuário foi desconectado
    private final MutableLiveData<Boolean> userLoggedOut = new MutableLiveData<>();

    public ProfileViewModel() {
        this.vagaRepository = new VagaRepository();
        this.mAuth = FirebaseAuth.getInstance();
        this.userProfile = vagaRepository.getUserProfile();
    }

    public LiveData<String> getCvUploadStatus() {
        return cvUploadStatus;
    }

    public LiveData<Boolean> getUserLoggedOut() {
        return userLoggedOut;
    }

    /**
     * Inicia o processo de upload do currículo.
     * @param fileUri A Uri do arquivo PDF selecionado.
     */
    public void uploadCv(Uri fileUri) {
        vagaRepository.uploadCurriculo(fileUri, cvUploadStatus);
    }

    /**
     * Desconecta o usuário atual.
     */
    public void logout() {
        mAuth.signOut();
        userLoggedOut.setValue(true); // Sinaliza para a View que o logout ocorreu
    }
}