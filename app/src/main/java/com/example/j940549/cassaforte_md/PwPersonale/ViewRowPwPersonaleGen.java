package com.example.j940549.cassaforte_md.PwPersonale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.LoginActivity;
import com.example.j940549.cassaforte_md.MainActivity;
import com.example.j940549.cassaforte_md.PwLavoro.PwBancheDati.ViewRowPwBancheDati;
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;
import com.example.j940549.cassaforte_md.UsoPhotoCamera.PhotoIntentActivity;

/**
 * Created by J940549 on 22/04/2017.
 */

public class ViewRowPwPersonaleGen extends AppCompatActivity {
    String nomeUtente="", idApp,nomeApplicazione,email;
    String password="";
    String pin="";
    String note="";String url="";
    Button btnPhotoActivity;
    EditText editnomeApplicazione, editEmail;
    EditText editnomeUtente;
    EditText editpassword;
    EditText editpin;
    EditText editurl;
    EditText editnote;
    Cursor cursor;
    private String user;
    private String SECURITYKEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_personale_icon_gen);
        idApp = getIntent().getExtras().getString("idApplicazione");
        user = getIntent().getExtras().getString("user");

        btnPhotoActivity= (Button) findViewById(R.id.btnCreaIconaPer);
        editnomeApplicazione = (EditText) findViewById(R.id.rowpnome_applicazione);
        editEmail= (EditText) findViewById(R.id.rowemail);
        editnomeUtente= (EditText) findViewById(R.id.rowpnome_utente);
        editpassword= (EditText) findViewById(R.id.rowppassword);
        editpin= (EditText) findViewById(R.id.rowppin);
        editurl= (EditText) findViewById(R.id.rowpurl);
        editnote= (EditText) findViewById(R.id.rowpnote);
        BottomNavigationView btnindietro = findViewById(R.id.bottom_navigation);
        btnindietro.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_backnavigation:
                        Intent intent = new Intent(ViewRowPwPersonaleGen.this, MainActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("isffpp", MainActivity.isffpp);
                        intent.putExtra("qualeFragment", "pwPersonali");
                        startActivity(intent);
                        finish();

                        break;
                    case R.id.action_modifica:
                        try {
                            modificaPwPersonale();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }



                return true;
            }

        });

//SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);
        String key=sharedPref.getString("securityKey", "");
        Log.i("securKEY_code", key);
        SECURITYKEY=new String(Base64.decode(key,Base64.DEFAULT));
        Log.i("securKEY_decode", SECURITYKEY);

        caricaDati(idApp);
    }

    public void apriPhotoIntent(View view) {
        String nomeApp=editnomeApplicazione.getText().toString();
        Intent vaiaPhotoIntentActivity=new Intent(this,PhotoIntentActivity.class);
        vaiaPhotoIntentActivity.putExtra("tipoPassword","PERSONALE");
        vaiaPhotoIntentActivity.putExtra("nomeApplicazione",nomeApp);
        vaiaPhotoIntentActivity.putExtra("user",user);
        startActivity(vaiaPhotoIntentActivity);
        finish();
    }

    private void caricaDati(String unidApllicazione) {
        DBLayer dbLayer=null;
        try {
            dbLayer=new DBLayer(this);
            dbLayer.open();
            cursor = dbLayer.getOneDataPersona(unidApllicazione);

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                nomeApplicazione=cursor.getString(2);
                email=cursor.getString(3);
                nomeUtente = cursor.getString(4);
                String passwordcryptata=cursor.getString(5);
                Crypto crypto= new Crypto(SECURITYKEY.getBytes());
                password = crypto.decrypt(passwordcryptata.getBytes());
                pin = cursor.getString(6);
                url = cursor.getString(7);
                note = cursor.getString(8);
                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(this, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbLayer.close();

        editnomeApplicazione.setText(nomeApplicazione);
        editEmail.setText(email);
        editnomeUtente.setText(nomeUtente);
        editpassword.setText(password);
        editpin.setText(pin);
        editurl.setText(url);
        editnote.setText(note);

    }

    public void modificaPwPersonale() throws Exception {

        nomeApplicazione=editnomeApplicazione.getText().toString();
       // Toast.makeText(this, ""+nomeApplicazione, Toast.LENGTH_SHORT).show();

        nomeUtente=editnomeUtente.getText().toString();
        email=editEmail.getText().toString();
        String passwordinchiaro=editpassword.getText().toString();
        Crypto crypto= new Crypto(SECURITYKEY.getBytes());
        password = crypto.encrypt(passwordinchiaro.getBytes());
        pin=editpin.getText().toString();
        url=editurl.getText().toString();
        note =editnote.getText().toString();
        DBLayer dbLayer=null;
        try {
            dbLayer=new DBLayer(this);
            dbLayer.open();


            int ris=dbLayer.modificaPwPersona(idApp,nomeApplicazione,email, nomeUtente, password, pin, url, note);
            if(ris==1){
                Toast.makeText(this, " dato modificato", Toast.LENGTH_SHORT).show();
                Intent vaiaPersonale=new Intent(this, MainActivity.class);
                vaiaPersonale.putExtra("user",user);
                vaiaPersonale.putExtra("isffpp", MainActivity.isffpp);
                vaiaPersonale.putExtra("qualeFragment","pwPersonali");
                startActivity(vaiaPersonale);
                finish();
            }else{
                Toast.makeText(this, "nessun  dato modificato", Toast.LENGTH_SHORT).show();

            }
        }catch (Exception ex){
            Toast.makeText(this, ""+ex.toString(), Toast.LENGTH_SHORT).show();

        }
        dbLayer.close();

    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, MainActivity.class);
        intent.putExtra("user", user);
        intent.putExtra("isffpp", MainActivity.isffpp);
        intent.putExtra("qualeFragment", "pwPersonali");
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case android.R.id.home: {
                Intent intent = new Intent(this, MainActivity.class);
                intent.putExtra("user", user);
                intent.putExtra("isffpp", MainActivity.isffpp);
                intent.putExtra("qualeFragment", "pwPersonali");
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
}



