// Pacote: com.example.facilemprega.ui.auth
package com.example.facilemprega.ui.auth;

import android.text.TextUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.facilemprega.repository.AuthRepository;

public class LoginViewModel extends ViewModel {

    private final AuthRepository authRepository;

    public LoginViewModel() {
        this.authRepository = new AuthRepository();
    }

    public LiveData<AuthRepository.AuthResult> login(String email, String password) {
        return authRepository.loginUser(email, password);
    }

    public boolean isInputValid(String email, String password) {
        return !TextUtils.isEmpty(email) && !TextUtils.isEmpty(password);
    }
}