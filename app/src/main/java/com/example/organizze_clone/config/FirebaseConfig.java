package com.example.organizze_clone.config;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class FirebaseConfig {
    private static FirebaseAuth auth;
    private static DatabaseReference db;


    /**
     *  static method to keep FirebaseAuth as one instance on entire app
     * @return FirebaseAuth instance to control Firebase Authentication
     */
    public static FirebaseAuth getFirebaseAuth() {
        if(auth == null) auth = FirebaseAuth.getInstance();
        return auth;
    }

    /**
     *  static method do keep DatabaseReference as one instance on entire app
     * @return DatabaseReference to control Database communication
     */
    public static DatabaseReference getFirebaseDatabase() {
        if(db == null) db = FirebaseDatabase.getInstance().getReference();
        return db;
    }


}
