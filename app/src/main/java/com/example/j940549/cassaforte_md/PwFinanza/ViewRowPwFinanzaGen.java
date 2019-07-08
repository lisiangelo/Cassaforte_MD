package com.example.j940549.cassaforte_md.PwFinanza;

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
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;
import com.example.j940549.cassaforte_md.UsoPhotoCamera.PhotoIntentActivity;


/**
 * Created by J940549 on 22/04/2017.
 */

public class ViewRowPwFinanzaGen extends AppCompatActivity {
    String nomeUtente="", idbnca, banca;
    String iban="";
    String password="";
    String pin="";
    String pin2="";
    String note="";String bancomat="";
    Button btnPhotoActivity;
    EditText editBanca,editIban;
    EditText editnomeUtente;
    EditText editpassword;
    EditText editpin;
    EditText editNrcarta;
    EditText editbancomat;
    EditText editnote;
    Cursor cursor;
    private String user;
    private String SECURITYKEY;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.row_finanza_icon_gen);
       idbnca = getIntent().getExtras().getString("idApplicazione");
        user = getIntent().getExtras().getString("user");
        btnPhotoActivity= (Button) findViewById(R.id.btnCreaIconaFin);
        editBanca= (EditText) findViewById(R.id.rownome_banca);
        //editBanca.setText(banca);
        editIban= (EditText) findViewById(R.id.rowiban);
        editnomeUtente= (EditText) findViewById(R.id.rowfnome_utente);
        editpassword= (EditText) findViewById(R.id.rowfpassword);
        editpin= (EditText) findViewById(R.id.rowfpin);
        editNrcarta = (EditText) findViewById(R.id.rowf_pin2);
        editbancomat= (EditText) findViewById(R.id.rowbancomat);
        editnote= (EditText) findViewById(R.id.rowfnote);

        BottomNavigationView btnindietro = findViewById(R.id.bottom_navigation);
        btnindietro.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_backnavigation:
                        Intent intent = new Intent(ViewRowPwFinanzaGen.this, MainActivity.class);
                        intent.putExtra("user", user);
                        intent.putExtra("isffpp", MainActivity.isffpp);
                        intent.putExtra("qualeFragment", "pwFinanziarie");
                        startActivity(intent);
                        finish();

                        break;
                    case R.id.action_modifica:
                        try {
                            modificaPwFinanza();
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
        caricaDati(idbnca);
    }
    public void apriPhotoIntent(View view) {
        Intent vaiaPhotoIntentActivity=new Intent(this,PhotoIntentActivity.class);
        vaiaPhotoIntentActivity.putExtra("tipoPassword","FINANZA");
        vaiaPhotoIntentActivity.putExtra("nomeApplicazione",editBanca.getText().toString());
        vaiaPhotoIntentActivity.putExtra("user",user);
        startActivity(vaiaPhotoIntentActivity);
        finish();
    }
    private void caricaDati(String unaBanca) {
        DBLayer dbLayer=null;
        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            cursor = dbLayer.getOneDataFinanza(unaBanca);


            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    Crypto crypto= new Crypto(SECURITYKEY.getBytes());
                    banca = cursor.getString(2);
                    String ibanCryptata = cursor.getString(3);
                    if(ibanCryptata.equals("-")){
                        iban=ibanCryptata;
                    }else{
                        iban = crypto.decrypt(ibanCryptata.getBytes());
                    }
                    nomeUtente = cursor.getString(4);
                    String passwordcryptata=cursor.getString(5);
                    password = crypto.decrypt(passwordcryptata.getBytes());
                    pin = cursor.getString(6);
                    String nrcarta = cursor.getString(7);
                    if(nrcarta.equals("-")){
                        pin2=nrcarta;
                    }else{
                        pin2=crypto.decrypt(nrcarta.getBytes());
                    }
                    bancomat = cursor.getString(8);
                    note = cursor.getString(9);
                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(this, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbLayer.close();
        editBanca.setText(banca);
        editIban.setText(iban);
        editnomeUtente.setText(nomeUtente);
        editpassword.setText(password);
        editpin.setText(pin);
        editNrcarta.setText(pin2);
        editbancomat.setText(bancomat);
        editnote.setText(note);

    }

    public void modificaPwFinanza() throws Exception {
        Crypto crypto= new Crypto(SECURITYKEY.getBytes());
        banca=editBanca.getText().toString();
        Toast.makeText(this, ""+banca, Toast.LENGTH_SHORT).show();
        String ibainchiaro=editIban.getText().toString();
        if (ibainchiaro.equals("")){iban="-";}
        else{
            iban = crypto.encrypt(ibainchiaro.getBytes());
        }
        nomeUtente=editnomeUtente.getText().toString();
        String passwordinchiaro=editpassword.getText().toString();
        password = crypto.encrypt(passwordinchiaro.getBytes());
        pin=editpin.getText().toString();
        String nrcarta= editNrcarta.getText().toString();
        if (nrcarta.equals("")){pin2="-";}else{
            pin2=crypto.encrypt(nrcarta.getBytes());
        }
        bancomat=editbancomat.getText().toString();
        note =editnote.getText().toString();
        DBLayer dbLayer=null;
        try {
            dbLayer=new DBLayer(this);
            dbLayer.open();


            int ris=dbLayer.modificaPwFinanza(idbnca,banca, iban, nomeUtente, password, pin, pin2,bancomat, note);
            if(ris==1){
                Toast.makeText(this, " dato modificato", Toast.LENGTH_SHORT).show();
                Intent vaiaFinanza=new Intent(this, MainActivity.class);
                vaiaFinanza.putExtra("user",user);
                vaiaFinanza.putExtra("isffpp", MainActivity.isffpp);
                vaiaFinanza.putExtra("qualeFragment","pwFinanziarie");
                startActivity(vaiaFinanza);
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
        intent.putExtra("qualeFragment", "pwFinanziarie");
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
                intent.putExtra("qualeFragment", "pwFinanziarie");
                startActivity(intent);
                finish();
                return true;
            }
        }
        return super.onOptionsItemSelected(menuItem);
    }
}
