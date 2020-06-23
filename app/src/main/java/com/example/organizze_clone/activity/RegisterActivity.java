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
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;

public class RegisterActivity extends AppCompatActivity {

    // screen manipulation
    TextInputEditText nameField, emailField, passwordField;
    Button buttonRegister;

    private FirebaseAuth auth; // used to register user on Firebase
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

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
                    Toast.makeText(getApplicationContext(), "Fill all fields", Toast.LENGTH_LONG).show();
                } else {
                    registerUser(new User(textName, textEmail, textPassword));

                }
            }
        });
    }

    public void registerUser(User user) {
        auth = FirebaseConfig.getFireBaseAuth();
        auth.createUserWithEmailAndPassword(
                user.getEmail(),
                user.getPassword()
        ).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()) {
                    Toast.makeText(getApplicationContext(), "Sucess to register user", Toast.LENGTH_LONG).show();
                } else {
                    // treating thrown exceptions
                    String exceptionText = "";
                    try {
                        throw task.getException();
                    } catch (FirebaseAuthWeakPasswordException e) {
                        exceptionText = "Type a stronger password! (numbers and letters at least)";
                    } catch (FirebaseAuthInvalidCredentialsException e) {
                        exceptionText = "Please, type a valid email";
                    } catch (FirebaseAuthUserCollisionException e) {
                        exceptionText = "This email is already registered";
                    } catch (Exception e) {
                        exceptionText = "Error to register user: " + e.getMessage();
                        e.printStackTrace();
                    }
                    Toast.makeText(getApplicationContext(), exceptionText, Toast.LENGTH_LONG).show();
                    exceptionText = "";
                }

            }
        });
    }
}