package com.example.j940549.cassaforte_md;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;


public class SplashActivity extends AppCompatActivity {
    LinearLayout sfondocassafortechiusa;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity);
        /*token = getIntent().getExtras().getString("user");
        isffpp=getIntent().getExtras().getString("isffpp");
       */
        sfondocassafortechiusa= (LinearLayout) findViewById(R.id.sfondo_chiusa);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //cassaforteaperta.setVisibility(View.GONE);
        Animation animation_fade_in= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in_vel);
        sfondocassafortechiusa.startAnimation(animation_fade_in);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vaiaMenu();}
        },1000);


    }

    private void vaiaMenu(){
        Intent vaiaLogin = new Intent(this, Login_Activity.class);
        startActivity(vaiaLogin);
        finish();

    }
}

