
package com.example.j940549.cassaforte_md.PwLavoro.PwBancheDati;

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
import android.widget.EditText;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.LoginActivity;
import com.example.j940549.cassaforte_md.MainActivity;
import com.example.j940549.cassaforte_md.PwFinanza.ViewRowPwFinanzaGen;
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;


public class ViewRowPwBancheDati extends AppCompatActivity {
    String nomeUtente="", idapp,nomeApplicazione;
    String password="";
    String pin="";
    String pin2="";
    String note="";String url="";
    EditText editnomeApplicazione;
    EditText editnomeUtente;
    EditText editpassword;
    EditText editpin;
    EditText editpin2;
    EditText editurl;
    EditText editnote;
    Cursor cursor;
    private String user;
    private String SECURITYKEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_lavoro);

        idapp = getIntent().getExtras().getString("idApplicazione");
        user = getIntent().getExtras().getString("user");

        //Toast.makeText(this, "ho passato idApplicazione= "+idapp, Toast.LENGTH_SHORT).show();
        editnomeApplicazione = (EditText) findViewById(R.id.rownome_applicazione);
        //editnomeApplicazione.setText(nomeApplicazione);
        editnomeUtente= (EditText) findViewById(R.id.rownome_utente);
        editpassword= (EditText) findViewById(R.id.rowpassword);
        editpin= (EditText) findViewById(R.id.rowpin);
        editpin2= (EditText) findViewById(R.id.row_pin2);
        editurl= (EditText) findViewById(R.id.rowurl);
        editnote= (EditText) findViewById(R.id.rownote);

        BottomNavigationView btnindietro = findViewById(R.id.bottom_navigation);
        btnindietro.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_backnavigation:
                        Intent intent = new Intent(ViewRowPwBancheDati.this, MainActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("isffpp", MainActivity.isffpp);
                        intent.putExtra("qualeFragment", "pwLavoro");
                        startActivity(intent);
                        finish();

                        break;
                    case R.id.action_modifica:
                        try {
                            modificaPwLavoro();
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
        caricaDati(idapp);
    }

    private void caricaDati(String unidApllicazione) {
      //  Toast.makeText(this, ""+unidApllicazione, Toast.LENGTH_SHORT).show();
        DBLayer dbLayer=null;
        try {
            dbLayer=new DBLayer(this);
            dbLayer.open();
            cursor = dbLayer.getOneDataLavoro(unidApllicazione);

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                nomeApplicazione=cursor.getString(2);
                nomeUtente = cursor.getString(3);
                String passwordcryptata=cursor.getString(4);
                Crypto crypto= new Crypto(SECURITYKEY.getBytes());
                password = crypto.decrypt(passwordcryptata.getBytes());
                pin = cursor.getString(5);
                pin2 = cursor.getString(6);
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
        editnomeUtente.setText(nomeUtente);
        editpassword.setText(password);
        editpin.setText(pin);
        editpin2.setText(pin2);
        editurl.setText(url);
        editnote.setText(note);

    }

    public void modificaPwLavoro() throws Exception {

        nomeApplicazione=editnomeApplicazione.getText().toString();
        Toast.makeText(this, ""+nomeApplicazione, Toast.LENGTH_SHORT).show();

        nomeUtente=editnomeUtente.getText().toString();
        String passwordinchiaro=editpassword.getText().toString();
        Crypto crypto= new Crypto(SECURITYKEY.getBytes());
        password = crypto.encrypt(passwordinchiaro.getBytes());
        pin=editpin.getText().toString();
        pin2=editpin2.getText().toString();
        note =editnote.getText().toString();
        DBLayer dbLayer=null;
        try {
            dbLayer=new DBLayer(this);
            dbLayer.open();


            int ris=dbLayer.modificaPwLavoro(idapp,nomeApplicazione, nomeUtente, password, pin, pin2,url, note);
        if(ris==1){
            Intent vaiaLavoro=new Intent(this, MainActivity.class);
            vaiaLavoro.putExtra("user",user);
            vaiaLavoro.putExtra("isffpp", MainActivity.isffpp);
            vaiaLavoro.putExtra("qualeFragment","pwLavoro");
            startActivity(vaiaLavoro);
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
        intent.putExtra("qualeFragment", "pwLavoro");
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
                intent.putExtra("qualeFragment", "pwLavoro");
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
