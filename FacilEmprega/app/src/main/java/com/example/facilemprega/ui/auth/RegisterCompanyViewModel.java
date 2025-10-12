// Pacote: com.example.facilemprega.ui.auth
package com.example.facilemprega.ui.auth;

import android.text.TextUtils;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.example.facilemprega.repository.AuthRepository;

public class RegisterCompanyViewModel extends ViewModel {

    private final AuthRepository authRepository;

    public RegisterCompanyViewModel() {
        this.authRepository = new AuthRepository();
    }

    public LiveData<AuthRepository.AuthResult> register(String responsibleName, String companyName, String email, String password) {
        return authRepository.registerCompany(responsibleName, companyName, email, password);
    }

    public String validateInput(String responsibleName, String companyName, String email, String password, String confirmPassword) {
        if (TextUtils.isEmpty(responsibleName) || TextUtils.isEmpty(companyName) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(confirmPassword)) {
            return "Por favor, preencha todos os campos.";
        }
        if (!password.equals(confirmPassword)) {
            return "As senhas não coincidem.";
        }
        return null; // Sem erros
    }
}