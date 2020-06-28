package com.example.organizze_clone;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.example.organizze_clone.activity.LoginActivity;
import com.example.organizze_clone.activity.RegisterActivity;
import com.example.organizze_clone.activity.HomeActivity;
import com.example.organizze_clone.config.FirebaseConfig;
import com.google.firebase.auth.FirebaseAuth;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // setContentView(R.layout.activity_main); is not necessary since we're using addSlide

        // verifies if user is logged, so we don't need to show intro slides
        verifiyLoggedUser();


        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorWhite)
                .fragment(R.layout.intro_1)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorWhite)
                .fragment(R.layout.intro_2)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorWhite)
                .fragment(R.layout.intro_3)
                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorWhite)
                .fragment(R.layout.intro_4)

                .build());

        addSlide(new FragmentSlide.Builder()
                .background(R.color.colorWhite)
                .fragment(R.layout.intro_register)
                .canGoForward(false)
                .build());

    }

    /**
     * used to create new user
     */
    public void register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     * used to log user that already exists
     */
    public void login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }

    public void verifiyLoggedUser() {
        FirebaseAuth auth = FirebaseConfig.getFirebaseAuth();
        if( auth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), HomeActivity.class)); //starts home
            finish(); // end main activity
        }
    }


}