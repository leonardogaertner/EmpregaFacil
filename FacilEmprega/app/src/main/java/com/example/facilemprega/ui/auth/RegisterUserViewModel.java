// Pacote: com.example.facilemprega.ui.auth
package com.example.facilemprega.ui.auth;

import android.text.TextUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.facilemprega.repository.AuthRepository;

public class RegisterUserViewModel extends ViewModel {

    private final AuthRepository authRepository;

    public RegisterUserViewModel() {
        this.authRepository = new AuthRepository();
    }

    public LiveData<AuthRepository.AuthResult> register(String name, String email, String password) {
        return authRepository.registerUser(name, email, password);
    }

    public String validateInput(String name, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            return "Por favor, preencha todos os campos.";
        }
        if (!password.equals(confirmPassword)) {
            return "As senhas não coincidem.";
        }
        return null; // Sem erros
    }
}