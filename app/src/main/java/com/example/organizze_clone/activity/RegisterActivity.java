package com.example.organizze_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.organizze_clone.R;
import com.example.organizze_clone.config.FirebaseConfig;
import com.example.organizze_clone.helper.ShowLongToast;
import com.example.organizze_clone.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity implements ShowLongToast {
    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();;

    private TextInputEditText nameField, emailField, passwordField;
    private Button buttonRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getSupportActionBar().setTitle("Cadastro");

        nameField = findViewById(R.id.textInputEditTextName);
        emailField = findViewById(R.id.textInputEditTextEmail);
        passwordField = findViewById(R.id.textInputEditTextPassword);

        buttonRegister = findViewById(R.id.buttonLogin);

        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String textName =  nameField.getText().toString();
                String textEmail = emailField.getText().toString();
                String textPassword = passwordField.getText().toString();

                // Validate if fields are filled
                if (textName.isEmpty() || textEmail.isEmpty() || textPassword.isEmpty()) {
                    showLongToast("Preencha todos os campos obrigatórios");
                } else {
                    registerUser(new User(textName, textEmail, textPassword));
                }
            }
        });
    }

    public void registerUser(final User user) {
        auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    user.setId(auth.getCurrentUser().getUid());
                    user.saveOnDatabase();

                    showLongToast("Sucesso ao registrar usuário");

                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);

                    startActivity(intent);
                    finish(); // to end this activity
                } else {
                    // treating thrown exceptions
                    String exceptionText = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        exceptionText = "Senha fraca, insira uma senha com letras e números pelo menos";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exceptionText = "Por favor, insira um email válido";
                    } catch (FirebaseAuthUserCollisionException e) {
                        exceptionText = "Esse email já está registrado";
                    } catch (Exception e) {
                        exceptionText = "Erro ao registrar o usuário: " + e.getMessage();
                        e.printStackTrace();
                    }
                    showLongToast(exceptionText);
                }

            }
        });
    }

    @Override
    public void showLongToast(String msg) {
        Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
    }
}