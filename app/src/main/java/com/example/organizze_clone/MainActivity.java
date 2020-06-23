package com.example.organizze_clone;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.example.organizze_clone.activity.LoginActivity;
import com.example.organizze_clone.activity.RegisterActivity;
import com.heinrichreimersoftware.materialintro.app.IntroActivity;
import com.heinrichreimersoftware.materialintro.slide.FragmentSlide;

public class MainActivity extends IntroActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);

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
     *
     */
    public void register(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    /**
     *
     */
    public void login(View view) {
        startActivity(new Intent(this, LoginActivity.class));
    }


}