package com.example.j940549.cassaforte_md;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Backup.ReCryptDB;
import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;


public class CambiaPassword extends AppCompatActivity {
    private EditText editUser;
    private EditText editOldPassword, editNewPassword, reditNewPassword;
    private String user, isffpp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cambia_password);
        SharedPreferences sharedPref = getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);
        user= sharedPref.getString("nomeUtente", "");
        editUser = (EditText) findViewById(R.id.cambiaPwuser);
        editUser.setText(user);
        //editUser.setVisibility(View.GONE);
        isffpp=getIntent().getExtras().getString("isffpp");

        editOldPassword = (EditText) findViewById(R.id.oldpassword);
        editNewPassword = (EditText) findViewById(R.id.newPassword);
        reditNewPassword = (EditText) findViewById(R.id.reNewPassword);

    }

    public void cambiaPw(View v) {


        String oldPassword = editOldPassword.getText().toString();
        String newPassword = editNewPassword.getText().toString();
        String renewPassword = reditNewPassword.getText().toString();
        SharedPreferences sharedPref = getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);
        String key = sharedPref.getString("securityKey", "");
        Log.i("securKEYOLD_code", key);
        String SECURITYKEY_OLD = new String(Base64.decode(key, Base64.DEFAULT));
        Log.i("securKEYOLD_decode", SECURITYKEY_OLD);

        if (newPassword.equals(renewPassword)) {

            if (controllaVecchipw(oldPassword,SECURITYKEY_OLD)) {
//carica securykey vecchia;


                String SECURITYKEY = creaSecurKey(user, newPassword);

                DBLayer dbLayer = null;

                try {
                    dbLayer = new DBLayer(this);
                    dbLayer.open();
                    //cripto la vecchia password pn la vecchia securitykey
                    Crypto crypto_old = new Crypto(SECURITYKEY_OLD.getBytes());
                    String oldPassword_crypto = crypto_old.encrypt(oldPassword.getBytes());

                    //cripto la nuova password con  la nuova securitykey

                    Crypto crypto = new Crypto(SECURITYKEY.getBytes());

                    String newPassword_crypto = crypto.encrypt(newPassword.getBytes());

                    String newToken = dbLayer.creaToken(user, newPassword);
                    String newtoken_base64 = Base64.encodeToString(newToken.getBytes(), Base64.DEFAULT);
                    String token = newtoken_base64 + "-" + isffpp;

                    //int resutl = 0;
                    int result = dbLayer.cambiaPassword(user, oldPassword_crypto, newPassword_crypto, token);

                    if (result != -1) {

                        int result2 = dbLayer.aggiornaToken(token);

                        if (result2 != -1) {
                            ReCryptDB reCryptDB=new ReCryptDB(getBaseContext(),token,SECURITYKEY,SECURITYKEY_OLD);
                            int result3 =reCryptDB.rencryptDB();

                            if (result3 != -1) {
                             /*   Intent vaiaLogin = new Intent(this, LoginActivity.class);
                                startActivity(vaiaLogin);
                                finish();*/
                                final Activity activity=this;
                                AlertDialog.Builder builder= new AlertDialog.Builder(this);
                                builder.setMessage("per rendere effettivi i cambiamenti \n devi riavviare l\'applicazione")
                                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialog, int which) {
                                           dialog.cancel();
                                           activity.finish();

                                            }
                                        });
                                builder.create();
                                builder.show();

                                //Toast.makeText(this, "per rendere effettivi i cambiamenti \n devi riavviare l\'APP", Toast.LENGTH_SHORT).show();

                            } else {
                                Toast.makeText(this, "Ops! si è erificato un errore interno \n password non cambiata ", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(this, "Ops! si è erificato un errore interno \n password non cambiata ", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(this, "Ops! si è erificato un errore interno \n password non cambiata ", Toast.LENGTH_SHORT).show();
                    }

                } catch (SQLException ex) {
                    Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            } else {

                Toast.makeText(this, "la vecchia password è errata", Toast.LENGTH_SHORT).show();

            }
        } else {

        Toast.makeText(this, "le due nuove password non coincidono", Toast.LENGTH_SHORT).show();

    }
    }

    public boolean controllaVecchipw(String vecchiaPW, String securkey) {
        boolean result=false;
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(this);
            dbLayer.open();

                Cursor cursor = dbLayer.getPassword(user);
            String old_Password="";
            if(cursor.getCount()>0) {
                cursor.moveToPosition(0);

                do {
                    old_Password = cursor.getString(0);

                } while (cursor.moveToNext());
            }

            Log.i("securPW da db", old_Password);//cripto la vecchia password pn la vecchia securitykey
            Crypto crypto_old = new Crypto(securkey.getBytes());
            String oldPassword_decrypto = crypto_old.decrypt(old_Password.getBytes());
            Log.i("securPW da db decry", oldPassword_decrypto);//cripto la vecchia password pn la vecchia securitykey

            if (vecchiaPW.equals(oldPassword_decrypto)) {
                result=true;
            } else {
                result=false;
            }

        } catch (SQLException ex) {
            Toast.makeText(this, "" + ex.toString(), Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
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
        SharedPreferences sharedPref = getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);
        String SECURITYKEY=Base64.encodeToString(plan_KEY.getBytes(),Base64.DEFAULT);
        Log.i("securkey_arch", SECURITYKEY);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("securityKey", SECURITYKEY);
        editor.commit();
    }



}
