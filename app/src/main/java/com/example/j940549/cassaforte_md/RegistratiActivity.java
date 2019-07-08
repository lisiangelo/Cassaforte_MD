package com.example.j940549.cassaforte_md;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.SQLException;
import android.os.Bundle;
import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;

public class RegistratiActivity extends AppCompatActivity {
    private EditText editPassword, editEmail;
    private EditText editnomeUser;
    private EditText editPassword2;
    private ToggleButton optffpp;
    private String sonoffpp="no";
    boolean registrato=false;
    public static String SECURITYKEY="";
    public static byte [] iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrati);

        optffpp= (ToggleButton) findViewById(R.id.optffpp);
        editnomeUser = (EditText) findViewById(R.id.registrauser);
        editEmail=(EditText) findViewById(R.id.registraemail);
        editPassword = (EditText) findViewById(R.id.registrapassword);
        editPassword2 = (EditText) findViewById(R.id.registra2password);
        sonoFFPP(optffpp);


    }
    public void sonoFFPP(View v) {
        boolean check=((ToggleButton)v).isChecked();
        if(check){
            sonoffpp="si";
        }else{
            sonoffpp="no";
        }

    }


    public void registrati(View v) {

        String user = editnomeUser.getText().toString();
        String email=editEmail.getText().toString();
        String password = editPassword.getText().toString();
        String password2 = editPassword2.getText().toString();
        if(password.equals(password2)) {

            SECURITYKEY=creaSecurKey(user,password);

            DBLayer dbLayer = null;

            try {
                dbLayer = new DBLayer(this);
                dbLayer.open();
                Crypto crypto=new Crypto(SECURITYKEY.getBytes());
                password=crypto.encrypt(password.getBytes());
                Log.d("registrami",password);
                registrato = dbLayer.registra(user, password,email,sonoffpp);
            } catch (SQLException ex) {
                Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
            } catch (Exception e) {
                e.printStackTrace();
            }


            if (registrato == true) {
                Intent vaiaLogin = new Intent(this, LoginActivity.class);
                //vaiaMenu.putExtra("user", user);
                startActivity(vaiaLogin);
               // USER=user;
                finish();
            } else {
                Toast.makeText(this, "Ops! registrazione non avvenuta", Toast.LENGTH_SHORT).show();
            }
        }else{
            Toast.makeText(this, "le due password non coincidono", Toast.LENGTH_SHORT).show();
        }

    }

    public String creaSecurKey(String user,String password){
        String securKey;
        Log.d("crea key",user);
        Log.d("crea key",password);

        StringBuilder token=new StringBuilder();
        int x=user.length()-1;
        int y=password.length()-1;

        if(x<y){
            for(int i=0; i<=y;i++){
                if(i<=x){
                    token.append(user.charAt(i));}else{
                    token.append(user.charAt(x));
                }
                token.append(password.charAt(i));

            }

        }else{
            for(int i=0; i<=x;i++){

                token.append(user.charAt(i));
                if(i<=y){
                    token.append(password.charAt(i));}else{

                    token.append(password.charAt(y));
                }

            }

         }

        if(token.length()<=16) {
            while (token.length() < 16) {
                token.append("x");
            }
            securKey=token.toString();
        }else{

            securKey=token.substring(0,16);

        }
        Log.d("key",securKey);
        archiviaSecurityKey(securKey);

        return securKey;
    }

    public void archiviaSecurityKey(String plan_KEY) {
        Log.i("securkey_arch", plan_KEY);
        String SECURITYKEY="";

        SharedPreferences sharedPref = getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);
/*
        try {
            final KeyGenerator keyGenerator=  KeyGenerator.getInstance(KeyProperties.KEY_ALGORITHM_AES,"AndroidKeyStore");
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {
                 KeyGenParameterSpec keyGenParameterSpec=new KeyGenParameterSpec.Builder("CassaforteKey",KeyProperties.PURPOSE_ENCRYPT|KeyProperties.PURPOSE_DECRYPT)
                        .setBlockModes(KeyProperties.BLOCK_MODE_GCM)
                        .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_NONE)
                        .build();
                keyGenerator.init(keyGenParameterSpec);

                 SecretKey secretKey=keyGenerator.generateKey();
                 Cipher cipher=Cipher.getInstance("AES/GCM/NoPadding");
                cipher.init(Cipher.ENCRYPT_MODE,secretKey);
               iv=cipher.getIV();
               byte[]encryption= cipher.doFinal(plan_KEY.getBytes("UTF-8"));
                 SECURITYKEY= new String(encryption);
            }

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (NoSuchProviderException e) {
            e.printStackTrace();
        } catch (InvalidAlgorithmParameterException e) {
            e.printStackTrace();
        } catch (NoSuchPaddingException e) {
            e.printStackTrace();
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        } catch (BadPaddingException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IllegalBlockSizeException e) {
            e.printStackTrace();
        }
*/
        SECURITYKEY=Base64.encodeToString(plan_KEY.getBytes(),Base64.DEFAULT);
        Log.i("securkey_arch", SECURITYKEY);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("securityKey", SECURITYKEY);
        editor.putString("iv", String.valueOf(iv));
        editor.commit();


    }


}
