package com.example.vipayee;


import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.shapes.OvalShape;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class SplashScreenActivity extends AppCompatActivity {

    View boxShape;
    View OvalShape;
    TextView logoText;
    RelativeLayout splashRoot;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_screen);

        getWindow().setStatusBarColor(Color.WHITE);

        boxShape = findViewById(R.id.boxshape);
        OvalShape = findViewById(R.id.ovalShape);
        logoText = findViewById(R.id.logotext);
        splashRoot = findViewById(R.id.splashroot);

        startAnimationSequence();
    }



    private void startAnimationSequence() {
        new Handler(Looper.getMainLooper()).postDelayed(() -> {

            boxShape.setVisibility(View.VISIBLE);
            boxShape.setScaleX(1f);
            boxShape.setScaleY(1f);
            boxShape.setRotation(0f);
            boxShape.setAlpha(1f);

            // Step 2: Rotate
            boxShape.animate().rotation(225f).setDuration(500).withEndAction(() -> {
                // Step 1: Grow
                boxShape.animate().scaleX(2.0f).scaleY(2.0f).setDuration(500).withEndAction(() -> {


                    // Step 3: Morph into circle
                    GradientDrawable drawable = (GradientDrawable) boxShape.getBackground();
                    ObjectAnimator cornerAnim = ObjectAnimator.ofFloat(drawable, "cornerRadius", 30f, 500f); // Increase corner radius
                    cornerAnim.setDuration(400);
                    cornerAnim.start();

//                    getWindow().setStatusBarColor(Color.parseColor("#FFA500"));

                    boxShape.animate()
                            .scaleX(0.1f)
                            .scaleY(0.1f)
                            .alpha(0f)
                            .setDuration(500);

                    // Step 4: Shrink & fade
                    new Handler(Looper.getMainLooper()).postDelayed(() -> {
                        boxShape.animate().scaleX(-15.0f).scaleY(-15.0f).alpha(0f).setDuration(500).withEndAction(() -> {
                            boxShape.setVisibility(View.GONE);
                            animateGradientBackground();
                            logoText.setVisibility(View.VISIBLE);
                            animateTypingText("Vipayee");
                        });
                    }, 400); // Wait until morph is done
                });
            });
        }, 300);
    }


    private void animateTypingText(String fullText) {
        final int[] index = {0};
        Handler handler = new Handler();

        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                logoText.setText(fullText.substring(0, index[0] + 1));
                index[0]++;


                if (index[0] < fullText.length()) {
                    handler.postDelayed(this, 150);
                }


            }
        };
        handler.post(runnable);
    }

    private void animateGradientBackground() {
        int[] colors = {0xFFFFA500, 0xFFFF4500}; // Orange to dark orange
        GradientDrawable gradient = new GradientDrawable(
                GradientDrawable.Orientation.TOP_BOTTOM, colors);
        splashRoot.setBackground(gradient);



        // Optional: Proceed to next activity after animation
        new Handler(Looper.getMainLooper()).postDelayed(() -> {
            getWindow().setStatusBarColor(Color.parseColor("#FFA500"));

            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                startActivity(new Intent(SplashScreenActivity.this, MainActivity.class));
                finish();
            }, 1000);

        }, -400);
    }
}