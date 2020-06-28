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
import com.google.firebase.auth.FirebaseAuthInvalidUserException;


public class LoginActivity extends AppCompatActivity implements ShowLongToast {
    private FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();

    private TextInputEditText emailField, passwordField;
    private Button buttonLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getSupportActionBar().setTitle("Log In");

        emailField = findViewById(R.id.textInputEditTextEmail);
        passwordField = findViewById(R.id.textInputEditTextPassword);

        buttonLogin = findViewById(R.id.buttonLogin);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emailText = emailField.getText().toString();
                String passwordText = passwordField.getText().toString();

                // Validate if fields are filled
                if(emailText.isEmpty() || passwordText.isEmpty()) {
                    showLongToast("Preencha todos os campos");
                } else {
                    loginUser(new User(emailText, passwordText));
                }

            }
        });
    }

    public void loginUser(User user) {
        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    showLongToast("Usuário logado");
                    startActivity(new Intent(getApplicationContext(), HomeActivity.class)); //navigate to home
                    finish(); // to end this activity
                } else {

                    String exceptionText;
                    try {
                        throw (task.getException());
                    } catch (FirebaseAuthInvalidUserException e) {
                        exceptionText = "Usuário não cadastrado";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exceptionText = "Wrong email or password";
                    } catch (Exception e) { // default exception
                        exceptionText = "Erro ao efetuar o login";
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