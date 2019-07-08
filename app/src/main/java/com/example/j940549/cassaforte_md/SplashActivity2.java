package com.example.j940549.cassaforte_md;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;

public class SplashActivity2 extends AppCompatActivity {

    LinearLayout sfondocassaforteaperta;
    LinearLayout sfondocassafortechiusa;
    private String  token="", isffpp="";
//    private static String SECURITYKEY;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash_activity2);
        token = getIntent().getExtras().getString("user");
        isffpp=getIntent().getExtras().getString("isffpp");
  //      SECURITYKEY=getIntent().getExtras().getString("securityKey");
        sfondocassaforteaperta= (LinearLayout) findViewById(R.id.sfondo_aperta);
        sfondocassafortechiusa= (LinearLayout) findViewById(R.id.sfondo_chiusa);

    }

    @Override
    protected void onStart() {
        super.onStart();
        //cassaforteaperta.setVisibility(View.GONE);
        Animation animation_fade_out= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_out);
        sfondocassafortechiusa.startAnimation(animation_fade_out);

        Animation animation_fade_in= AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
        sfondocassaforteaperta.startAnimation(animation_fade_in);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                vaiaMenu();

            }
        },1000);

    }

    private void vaiaMenu(){
        Intent vaiaMenu = new Intent(this, MainActivity.class);
        vaiaMenu.putExtra("user", token);
        vaiaMenu.putExtra("isffpp", isffpp);
        vaiaMenu.putExtra("qualeFragment","scegliCat");
        startActivity(vaiaMenu);
        finish();

    }
}

