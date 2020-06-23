package com.example.organizze_clone.config;

import com.google.firebase.auth.FirebaseAuth;

public class FirebaseConfig {
    private static FirebaseAuth auth;

    public static FirebaseAuth getFireBaseAuth() {
        if(auth == null) auth = FirebaseAuth.getInstance();
        return auth;
    }


}
