package com.example.j940549.cassaforte_md;

import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.FingerprintDialog.BiometricCallback;
import com.example.j940549.cassaforte_md.FingerprintDialog.BiometricManager;
import com.example.j940549.cassaforte_md.FingerprintDialog.BiometricUtils;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableEntryException;
import java.security.cert.CertificateException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;

/**
 * A login screen that offers login via email/password.
 */
public class Login_Activity extends AppCompatActivity implements BiometricCallback {

    private SharedPreferences sharedPref;
    private EditText editPassword;
    private EditText editnomeUser;
    private ImageButton fp_button;
    private CheckBox chkboxRicordami;
    private boolean siFingerprint;
    private String token, user;
    private String SECURITYKEY;

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR2)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        editnomeUser = (EditText) findViewById(R.id.user);
        editPassword = (EditText) findViewById(R.id.password);
        chkboxRicordami = (CheckBox) findViewById(R.id.chkboxRicordami);
        sharedPref = getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);

        leggiPreferenze();

        chkboxRicordami.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    salvaUtenteinPreferenze();
                }
                if (!isChecked) {
                    salvaUtenteinPreferenze();
                }
            }
        });

        fp_button = (ImageButton) findViewById(R.id.purchase_button);
        fp_button.setVisibility(View.VISIBLE);
        fp_button.setEnabled(true);
        fp_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                new BiometricManager.BiometricBuilder(Login_Activity.this)
                        .setTitle(getString(R.string.biometric_title))
                        .setSubtitle(getString(R.string.biometric_subtitle))
                        .setDescription(getString(R.string.biometric_description))
                        .setNegativeButtonText(getString(R.string.biometric_negative_button_text))
                        .build()
                        .authenticate(Login_Activity.this);
            }
        });
        if(siFingerprint){
            Log.i("fingerprint",""+siFingerprint);

            fp_button.performClick();
        }



    }

    private void leggiPreferenze(){
        //SharedPreferences mSharedPreferences= getPreferences(Context.MODE_PRIVATE);

        String utente= sharedPref.getString("nomeUtente", "");
        siFingerprint=sharedPref.getBoolean("chkFingerprint", false);
        Log.i("si fingerprint login",""+siFingerprint);

        boolean check=sharedPref.getBoolean("chkbox", false);

        if(check||!utente.equals("")){
            editnomeUser.setText(utente);
            chkboxRicordami.setChecked(check);
            editPassword.requestFocus();
        }
        if(!check){
            editnomeUser.setText("");
            chkboxRicordami.setChecked(check);
        }

    }
    private void salvaUtenteinPreferenze() {

        editnomeUser = (EditText) findViewById(R.id.user);
        String user = editnomeUser.getText().toString();
        Boolean cheked=false;
        if(chkboxRicordami.isChecked()){
            cheked=true;
        }else{
            cheked=false;
        }
        if(!user.equals("")&& cheked) {
            //SharedPreferences mSharedPreferences= getPreferences(Context.MODE_PRIVATE);
            String utente= sharedPref.getString("nomeUtente", "");
            if(utente.equals("")) {
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putString("nomeUtente", user);
                editor.putBoolean("chkbox",cheked);
                editor.commit();
            }
        }else if (user.equals("")&& cheked) {
            Toast.makeText(this, "inserisci prima il nome Utente", Toast.LENGTH_SHORT).show();
            chkboxRicordami.setChecked(false);
        }else if (!user.equals("")&& !cheked){
            //SharedPreferences mSharedPreferences= getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("nomeUtente", "");
            editor.putBoolean("chkbox",cheked);
            editor.commit();
        }else if(user.equals("")&& !cheked){
            //SharedPreferences mSharedPreferences= getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("nomeUtente", "");
            editor.putBoolean("chkbox",cheked);
            editor.commit();
        }
    }


    @Override
    public void onSdkVersionNotSupported() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_sdk_not_supported), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationNotSupported() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_hardware_not_supported), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationNotAvailable() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_fingerprint_not_available), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationPermissionNotGranted() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_error_permission_not_granted), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onBiometricAuthenticationInternalError(String error) {
        Toast.makeText(getApplicationContext(), error, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationFailed() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_failure), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationCancelled() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_cancelled), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationSuccessful() {
        Toast.makeText(getApplicationContext(), getString(R.string.biometric_success), Toast.LENGTH_LONG).show();
        failogin();
    }

    @Override
    public void onAuthenticationHelp(int helpCode, CharSequence helpString) {
        Toast.makeText(getApplicationContext(), helpString, Toast.LENGTH_LONG).show();
    }

    @Override
    public void onAuthenticationError(int errorCode, CharSequence errString) {
        Toast.makeText(getApplicationContext(), errString, Toast.LENGTH_LONG).show();
    }

    public void failogin() {
        //Log.d("getpw login user", getUser());
        String token="";
        String isffpp="";


        DBLayer dbLayer = null;

        String user = editnomeUser.getText().toString();
        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();
            Cursor cursor = dbLayer.getPassword(user);

            if(cursor.getCount()>0) {
                cursor.moveToPosition(0);
                do{
                    //  PWcontrollo = cursor.getString(0);
                    token=(cursor.getString(1));

                }  while (cursor.moveToNext());
            }


            if(token.length()>0) {

                isffpp = token.substring((token.indexOf("-") + 1), token.length());
               // SharedPreferences sharedPref = this.getPreferences(Context.MODE_PRIVATE);
                //mSharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);

                String key=sharedPref.getString("securityKey", "");
                //String decoded=new String(Base64.decode(decryptedValue,Base64.DEFAULT));
                Log.i("securKEY_code", key);
                SECURITYKEY=new String(Base64.decode(key,Base64.DEFAULT));
                Log.i("securKEY_decode", SECURITYKEY);
                if(!key.equals("")) {
                    Intent vaiaMenu = new Intent(this, MainActivity.class);
                    vaiaMenu.putExtra("user", token);
                    vaiaMenu.putExtra("isffpp", isffpp);
                    startActivity(vaiaMenu);
                    this.finish();
                }else{
                    Toast.makeText(this, "la prima volta devi accedere con la password", Toast.LENGTH_SHORT).show();
                }
            }else{
                Toast.makeText(this, "utente non registrato", Toast.LENGTH_SHORT).show();
            }
        } catch (SQLException ex) {
            Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
    }


    public void login(View v) {

        editnomeUser = (EditText) findViewById(R.id.user);
        editPassword = (EditText) findViewById(R.id.password);

        String user = editnomeUser.getText().toString();
        String password = editPassword.getText().toString();
        token="";
        if(!user.equals("")&&!password.equals("")) {

            SharedPreferences sharedPref = getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);
            String keyEncrypted=sharedPref.getString("securityKey", "");
            Log.i("securKEY_code", keyEncrypted);

            /*
            String ivString=sharedPref.getString("iv", "");
            byte []iv=ivString.getBytes();

            KeyStore keyStore= null;

            try {
                keyStore = KeyStore.getInstance("AndroidKeyStore");
                keyStore.load(null);
                 KeyStore.SecretKeyEntry secretKeyEntry= (KeyStore.SecretKeyEntry) keyStore.getEntry("CassaforteKey", null);
                 SecretKey secretKey= secretKeyEntry.getSecretKey();
                 Cipher cipher= Cipher.getInstance("AES/GCM/NoPadding");
                 GCMParameterSpec spec= new GCMParameterSpec(128,iv);
                cipher.init(Cipher.DECRYPT_MODE, secretKey, spec);
                byte [] decodedData= cipher.doFinal(keyEncrypted.getBytes());
                SECURITYKEY=new String(decodedData, "UTF-8");

            } catch (KeyStoreException e) {
                e.printStackTrace();
            } catch (CertificateException e) {
                e.printStackTrace();
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (UnrecoverableEntryException e) {
                e.printStackTrace();
            } catch (NoSuchPaddingException e) {
                e.printStackTrace();
            } catch (InvalidKeyException e) {
                e.printStackTrace();
            } catch (BadPaddingException e) {
                e.printStackTrace();
            } catch (IllegalBlockSizeException e) {
                e.printStackTrace();
            } catch (InvalidAlgorithmParameterException e) {
                e.printStackTrace();
            }
*/
            SECURITYKEY=new String(Base64.decode(keyEncrypted,Base64.DEFAULT));
            Log.i("securKEY", SECURITYKEY);

            String PWcontrollo = "";
            String TKcontrollo="";
            String TKorig="";
            String isffpp="";
            DBLayer dbLayer = null;

            try {
                dbLayer = new DBLayer(this);
                dbLayer.open();
                Cursor cursor = dbLayer.getPassword(user);

                if(cursor.getCount()>0) {
                    cursor.moveToPosition(0);
                    Log.d("login curor", ""+cursor.getCount() + "-" + token);

                    do{
                        PWcontrollo = cursor.getString(0);
                        token=(cursor.getString(1));
                        Log.d("login encript", PWcontrollo + "-" + token);

                    }  while (cursor.moveToNext());

                    Log.d("login encript", PWcontrollo + "-" + token);

                    Crypto crypto=new Crypto(SECURITYKEY.getBytes());

                    PWcontrollo=crypto.decrypt(PWcontrollo.getBytes());
                    Log.d("login decript", PWcontrollo + "-" + token);
                    TKcontrollo=dbLayer.creaToken(user,PWcontrollo);
                    String TKcontrollo_base64=Base64.encodeToString(TKcontrollo.getBytes(),Base64.DEFAULT);

                    //Log.d("login decript", PWcontrollo + "-" + token);

                    if(token.length()>0) {

                        TKorig = token.substring(0, token.indexOf("-"));
                        isffpp=token.substring((token.indexOf("-")+1),token.length());
                    }
                    Log.d("login decrypto",PWcontrollo+"-"+TKcontrollo+"-??"+isffpp);
                    if (PWcontrollo.equals(password)&&TKorig.equalsIgnoreCase(TKcontrollo_base64)) {

                        Log.d("login decrypto","login OK");
                        Intent vaiaSplash = new Intent(this, SplashActivity2.class);
                        vaiaSplash .putExtra("user", token);
                        vaiaSplash .putExtra("isffpp", isffpp);
                        startActivity(vaiaSplash );

                        finish();
                    } else {
                        Toast.makeText(this, "utente o password errati", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(this, "utente non registrato", Toast.LENGTH_SHORT).show();
                }
            } catch (SQLException ex) {
                Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }else{
            Toast.makeText(this, "inserisci i dati per il login", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate( R.menu.menu_registrati,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem){
        boolean esito=false;
        switch(menuItem.getItemId()){
            case R.id.optionRegistrati:{

                Intent vaiaRegistrati = new Intent(this, RegistratiActivity.class);

                startActivity(vaiaRegistrati);
                finish();
                esito= true;
                break;
            }

        }
        return esito;
    }

}

