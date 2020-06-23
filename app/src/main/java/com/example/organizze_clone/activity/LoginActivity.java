package com.example.organizze_clone.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.organizze_clone.R;
import com.example.organizze_clone.config.FirebaseConfig;
import com.example.organizze_clone.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;


public class LoginActivity extends AppCompatActivity {

    TextInputEditText emailField, passwordField;
    Button buttonLogin;

    FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                    Toast.makeText(getApplicationContext(), "Fill both fields", Toast.LENGTH_LONG).show();
                } else {
                    loginUser(new User(emailText, passwordText));
                }

            }
        });
    }

    public void loginUser(User user) {
        auth = FirebaseConfig.getFireBaseAuth();


        auth.signInWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "User logged in", Toast.LENGTH_SHORT).show();
                } else {
                    try {
                        throw (task.getException());
                    } catch (FirebaseAuthInvalidUserException e) {
                        Toast.makeText(getApplicationContext(), "User not registered", Toast.LENGTH_LONG).show();
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        Toast.makeText(getApplicationContext(), "Wrong email or password", Toast.LENGTH_LONG).show();
                    } catch (Exception e) { // default exception
                        Toast.makeText(getApplicationContext(), "Error to log in: " + e.getMessage(), Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
    }
}