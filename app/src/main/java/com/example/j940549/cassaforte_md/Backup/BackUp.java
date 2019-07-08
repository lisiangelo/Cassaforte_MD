package com.example.j940549.cassaforte_md.Backup;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.support.v4.content.FileProvider;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.BuildConfig;
import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.LoginActivity;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;


/**
 * Created by J940549 on 24/06/2017.
 */

public class BackUp {
    private Context context;
    private String user;
    private String mailUser;
    Map <String,String[]> allUtenti= new HashMap<String, String[]>();
    Map <String,String[]> allFinanza= new HashMap<String, String[]>();
    Map <String,String[]> allBancheDati= new HashMap<String, String[]>();
    Map <String,String[]> allGestori= new HashMap<String, String[]>();
    Map <String,String[]> allPersonali= new HashMap<String, String[]>();
    Map <String,String[]> allLavoroGen= new HashMap<String, String[]>();
    private File backupDB;
    private String SECURITYKEY;


    public BackUp( Context context, String user){
        this.context=context;
        this.user=user;
        mailUser=caricaMail();
        SharedPreferences sharedPref = context.getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);

//        SharedPreferences sharedPref =PreferenceManager.getDefaultSharedPreferences(context);
        String key=sharedPref.getString("securityKey", "");
        Log.i("securKEY_code", key);
        SECURITYKEY=new String(Base64.decode(key,Base64.DEFAULT));
        Log.i("securKEY_decode", SECURITYKEY);
    }

    public boolean exportDb(){

        boolean execute=false;
        try {
            File sd = Environment.getExternalStorageDirectory();
            //File sd = Environment.getExternalStorageDirectory();//new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/Cassaforte");

            File data = Environment.getDataDirectory();
            Log.i("SD", sd.toString());
            Log.i("data", data.toString());


            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                //  DBLayer dbLayer=new DBLayer(context);
                // dbLayer.getDB().execSQL();
                String currentDBPath = context.getDatabasePath("password.db").getAbsolutePath();//"//data//it.android.j940549.cassaforte_md//databases//password.db";
                Log.i("currenteDBpathexport", currentDBPath);
                String backupDBPath = "password.db";

                File currentDB = new File(currentDBPath);//(data, currentDBPath);
                backupDB = new File(sd, backupDBPath);
                if (currentDB.exists()) {
                    FileChannel src = new FileInputStream(currentDB).getChannel();
                    FileChannel dst = new FileOutputStream(backupDB).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();
                    Toast.makeText(context, "Backup Successfull!", Toast.LENGTH_SHORT).show();
                    execute=true;

                }

                //invia backup alla mail dell'utente
                if(inviaMail(backupDB)){
                    Toast.makeText(context, "invio il Backup alla tua mail!", Toast.LENGTH_SHORT).show();
                }else{
                    Toast.makeText(context, "Backup NON INVIATO alla tua mail!", Toast.LENGTH_SHORT).show();

                }
            }else{
                Log.i("data sd canwrite", ""+sd.canWrite());
                execute=false;
            }
        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("errore", e.toString());
            execute=false;
        }
        return execute;
    }

    public  boolean importDB(String backupDBPath) {
        boolean execute=false;
        //controllaPermessi();
        try {
            //File sd = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS)+"/Cassaforte");
            File sd = Environment.getExternalStorageDirectory();
            File data = Environment.getDataDirectory();
            if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {

                // DBLayer dbLayer=new DBLayer(context);
                String currentDBPath = context.getDatabasePath("password.db").getAbsolutePath();//"//data//com.example.j940549.cassaforte_md//databases//password.db";
                Log.i("currenteDBpathimport", backupDBPath);

               // String backupDBPath = "password.db";
                File currentDB = new File( currentDBPath);//(data, currentDBPath);
                File backupDB = new File( sd, backupDBPath);


                FileChannel src = new FileInputStream(backupDB).getChannel();
                FileChannel dst = new FileOutputStream(currentDB).getChannel();
                dst.transferFrom(src, 0, src.size());
                src.close();
                dst.close();

                Toast.makeText(context, "Import Successful!", Toast.LENGTH_SHORT).show();
                execute=true;
            }

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            Log.i("currentdbimport", e.toString());

            execute=false;
        }
        return execute;
    }
/*
    public void decryptDB() {
        decryptDBUtente();
        decryptDBFinanza();
        decryptDBBancheDati();
        decryptDBGestori();
        decryptDBPersonali();
        decryptDBLavoroGen();

    }*/

    public void encryptDB(){
        encryptDBUtente();
        encryptDBFinanza();
        encryptDBBancheDati();
        encryptDBGestori();
        encryptDBPersonali();
        encryptDBLavoroGen();

    }

    private void decryptDBUtente() {
        caricaDatiUtente("decrypt");
        modificaDatiUtente();
    }
    private void encryptDBUtente() {
        caricaDatiUtente("encrypt");
        modificaDatiUtente();
    }

    private void caricaDatiUtente(String decrypt_encrypt){
        allUtenti.clear();
        String id="";
        String pwDaInserire="";

        DBLayer dbLayer=null;
        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getAllUtente();
            Log.i("dato cursore alluser",""+cursor.toString());

            if (cursor.getCount() > 0) {

                cursor.moveToPosition(0);
                do {
                    Log.i("dato cursore do",""+cursor.getCount());

                    Crypto crypto= new Crypto(SECURITYKEY.getBytes());
                    Log.i("dato cursore cryp",""+cursor.getCount());

                    id= cursor.getString(0);
                    Log.i("dato da controllare id",id);
                    String pw = cursor.getString(2);
                    Log.i("dato da controllare","pw "+pw);

                    if(decrypt_encrypt.equals("decrypt")){
                        Log.i("crypt_decrypt","decrypt");

                        pwDaInserire = crypto.decrypt(pw.getBytes());
                        }
                    if(decrypt_encrypt.equals("encrypt")){
                        Log.i("crypt_decrypt","crypt");

                        pwDaInserire = crypto.encrypt(pw.getBytes());
                    }


                    Log.d("dato da inserire",pwDaInserire);

                    allUtenti.put(id,new String[]{id,pwDaInserire});
                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(context, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbLayer.close();

    }

    public void modificaDatiUtente() {

        Set<String> keys=allUtenti.keySet();
        for (String key: keys) {
            String[] valori = allUtenti.get(key);
            if (!valori[1].equals("")) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("password", valori[1]);

                DBLayer dbLayer = null;
                try {
                    dbLayer = new DBLayer(context);
                    dbLayer.open();

                    int ris = dbLayer.modificaDBUtente(valori[0], contentValues);
                    if (ris == 1) {
                        Log.d("dato users modificato", valori[1]);

                    } else {
                        Log.d("dato user NON modif", valori[1]);

                    }
                } catch (Exception ex) {
                    Log.e("errore modificaDB user", ex.toString());

                }
                dbLayer.close();

            }else {
                Log.d("dato user NON modif", valori[1]);
            }
        }
    }



    private void decryptDBFinanza() {
        caricaDatiFinanza("decrypt");
        modificaDatiFinanza();
    }

    private void encryptDBFinanza() {
        caricaDatiFinanza("encrypt");
        modificaDatiFinanza();
    }

    private void caricaDatiFinanza(String decrypt_encrypt){
        allFinanza.clear();
        String id="";
        String ibanDaInserire="";
        String passwordDaInserire="";
        String nrCartaDaInserire="";
        DBLayer dbLayer=null;
        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getAllDataFinanza(user);

            Log.i("dato cursore alldataFin",""+cursor.getCount());

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    Crypto crypto= new Crypto(SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String iban = cursor.getString(3);
                    Log.i("dato da controllare","iban "+iban);

                        if(decrypt_encrypt.equals("decrypt")){
                            Log.i("crypt_decrypt","decrypt");

                            if(iban.equals("-")){
                                ibanDaInserire=iban;
                            }else{
                        ibanDaInserire = crypto.decrypt(iban.getBytes());}
                        }
                        if(decrypt_encrypt.equals("encrypt")){
                            Log.i("crypt_decrypt","crypt");

                            if(iban.equals("-")){
                                ibanDaInserire=iban;
                            }else{
                            ibanDaInserire=crypto.encrypt(iban.getBytes());}
                        }

                    String password=cursor.getString(5);
                    Log.i("dato da controllare","pword "+password);

                    if(decrypt_encrypt.equals("decrypt")) {
                        Log.i("crypt_decrypt","decrypt");

                        passwordDaInserire = crypto.decrypt(password.getBytes());
                    }
                    if(decrypt_encrypt.equals("encrypt")){

                        Log.i("crypt_decrypt","crypt");
                        passwordDaInserire=crypto.encrypt(password.getBytes());
                    }
                    String nrcarta = cursor.getString(7);
                    Log.i("dato da controllare","nrcar "+nrcarta);

                    if(decrypt_encrypt.equals("decrypt")){
                        if(nrcarta.equals("-")){
                            nrCartaDaInserire=nrcarta;
                        }else{
                            nrCartaDaInserire = crypto.decrypt(nrcarta.getBytes());}
                        }
                        if(decrypt_encrypt.equals("encrypt")){
                            if(nrcarta.equals("-")) {
                                nrCartaDaInserire=nrcarta;
                            }else{
                                nrCartaDaInserire = crypto.encrypt(nrcarta.getBytes());
                            }
                        }


                    Log.d("dato da inserire",passwordDaInserire);

                    allFinanza.put(id,new String[]{id,ibanDaInserire,passwordDaInserire,nrCartaDaInserire});
                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(context, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbLayer.close();

    }

    public void modificaDatiFinanza() {

        Set<String> keys = allFinanza.keySet();
        for (String key : keys) {
            String[] valori = allFinanza.get(key);
            if (!valori[1].equals("") && !valori[2].equals("") && !valori[3].equals("")) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("iban", valori[1]);
                contentValues.put("password", valori[2]);
                contentValues.put("pin2", valori[3]);


                DBLayer dbLayer = null;
                try {
                    dbLayer = new DBLayer(context);
                    dbLayer.open();

                    int ris = dbLayer.modificaPwFinanza(valori[0], contentValues);
                    if (ris == 1) {
                        Log.d("dato fin.modificato", valori[2]);

                    } else {
                        Log.d("dato fin NON modificato", valori[2]);

                    }
                } catch (Exception ex) {
                    Log.e("errore modificaDB fin", ex.toString());

                }
                dbLayer.close();


            } else {
                Log.d("dato fin NON modificato", valori[2]);

            }
        }
    }

    private void decryptDBBancheDati() {
        caricaDatiBancheDati("decrypt");
        modificaDatiBancheDati();
    }

    private void encryptDBBancheDati() {
        caricaDatiBancheDati("encrypt");
        modificaDatiBancheDati();
    }

    private void caricaDatiBancheDati(String decrypt_encrypt){
        allBancheDati.clear();
        String id="";
        String passwordDaInserire="";
        DBLayer dbLayer=null;
        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getAllDataLavoroBancheDati(user);

            Log.i("dato cursore allBdLav",""+cursor.getCount());

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    Crypto crypto= new Crypto(SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String password=cursor.getString(4);
                    Log.i("dato da controllare","pwbanc "+password);

                    if(decrypt_encrypt.equals("decrypt")) {
                        Log.i("crypt_decrypt","decrypt");

                        passwordDaInserire = crypto.decrypt(password.getBytes());
                    }
                    if(decrypt_encrypt.equals("encrypt")){
                        Log.i("crypt_decrypt","crypt");

                        passwordDaInserire=crypto.encrypt(password.getBytes());
                    }

                    Log.d("dato da inserire",passwordDaInserire);
                    allBancheDati.put(id,new String[]{id,passwordDaInserire});
                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(context, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbLayer.close();

    }

    public void modificaDatiBancheDati() {

        Set<String> keys = allBancheDati.keySet();
        for (String key : keys) {
            String[] valori = allBancheDati.get(key);

            if (!valori[1].equals("")) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("password", valori[1]);

                DBLayer dbLayer = null;
                try {
                    dbLayer = new DBLayer(context);
                    dbLayer.open();

                    int ris = dbLayer.modificaPwLavoro(valori[0], contentValues);
                    if (ris == 1) {
                        Log.d("dato badt.modificato", valori[1]);

                    } else {
                        Log.d("dato bdt NON modificato", valori[1]);

                    }
                } catch (Exception ex) {
                    Log.e("errore modificaDB bdt", ex.toString());

                }
                dbLayer.close();

            } else {
                Log.d("dato bdt NON modificato", valori[1]);

            }
        }
    }

    private void decryptDBGestori() {
        caricaDatiGestori("decrypt");
        modificaDatiGestori();
    }
    private void encryptDBGestori() {
        caricaDatiGestori("encrypt");
        modificaDatiGestori();
    }

    private void caricaDatiGestori(String decrypt_encrypt){
        allGestori.clear();
        String id="";
        String passwordDaInserire="";
        DBLayer dbLayer=null;
        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getAllDataLavoroGestori(user);

            Log.i("dato cursore allGest",""+cursor.getCount());

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    Crypto crypto= new Crypto(SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String password=cursor.getString(4);
                    Log.i("dato da controllare","pwgest "+password);

                    if(decrypt_encrypt.equals("decrypt")) {
                        Log.i("crypt_decrypt","decrypt");

                        passwordDaInserire = crypto.decrypt(password.getBytes());
                    }
                    if(decrypt_encrypt.equals("encrypt")){
                        Log.i("crypt_decrypt","crypt");

                        passwordDaInserire=crypto.encrypt(password.getBytes());
                    }

                    Log.d("dato da inserire",passwordDaInserire);

                    allGestori.put(id,new String[]{id,passwordDaInserire});
                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(context, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbLayer.close();

    }

    public void modificaDatiGestori() {

        Set<String> keys = allGestori.keySet();
        for (String key : keys) {
            String[] valori = allGestori.get(key);
            if (!valori[1].equals("")) {

                ContentValues contentValues = new ContentValues();
                contentValues.put("password", valori[1]);


                DBLayer dbLayer = null;
                try {
                    dbLayer = new DBLayer(context);
                    dbLayer.open();

                    int ris = dbLayer.modificaPwLavoro(valori[0], contentValues);
                    if (ris == 1) {
                        Log.d("dato ges.modificato", valori[1]);

                    } else {
                        Log.d("dato ges NON modificato", valori[1]);

                    }
                } catch (Exception ex) {
                    Log.e("errore modificaDB ges", ex.toString());

                }
                dbLayer.close();

            } else {
                Log.d("dato ges NON modificato", valori[1]);

            }
        }
    }

    private void decryptDBPersonali() {
        caricaDatiPersonali("decrypt");
        modificaDatiPersonali();
    }

    private void encryptDBPersonali() {
        caricaDatiPersonali("encrypt");
        modificaDatiPersonali();
    }

    private void caricaDatiPersonali(String decrypt_encrypt){
        allPersonali.clear();
        String id="";
        String passwordDaInserire="";
        DBLayer dbLayer=null;
        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getAllDataPersona(user);

            Log.i("dato cursore allPer",""+cursor.getCount());

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    Crypto crypto= new Crypto(SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String password=cursor.getString(5);
                    Log.i("dato da controllare","pwper "+password);

                    if(decrypt_encrypt.equals("decrypt")) {
                        Log.i("crypt_decrypt","decrypt");

                        passwordDaInserire = crypto.decrypt(password.getBytes());
                    }
                    if(decrypt_encrypt.equals("encrypt")){
                        Log.i("crypt_decrypt","crypt");

                        passwordDaInserire=crypto.encrypt(password.getBytes());
                    }
                    Log.d("dato da inserire",passwordDaInserire);

                    allPersonali.put(id,new String[]{id,passwordDaInserire});
                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(context, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbLayer.close();

    }

    public void modificaDatiPersonali() {

        Set<String> keys = allPersonali.keySet();
        for (String key : keys) {
            String[] valori = allPersonali.get(key);

            if (!valori[1].equals("")) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("password", valori[1]);

                DBLayer dbLayer = null;
                try {
                    dbLayer = new DBLayer(context);
                    dbLayer.open();

                    int ris = dbLayer.modificaPwPersona(valori[0], contentValues);
                    if (ris == 1) {
                        Log.d("dato per.modificato", valori[1]);

                    } else {
                        Log.d("dato per NON modificato", valori[1]);

                    }
                } catch (Exception ex) {
                    Log.e("errore modificaDB per", ex.toString());

                }
                dbLayer.close();

            } else {
                Log.d("dato per NON modificato", valori[1]);

            }
        }
    }

    private void decryptDBLavoroGen() {
        caricaDatiLavoroGen("decrypt");
        modificaDatiLavoroGen();
    }

    private void encryptDBLavoroGen() {
        caricaDatiLavoroGen("encrypt");
        modificaDatiLavoroGen();
    }

    private void caricaDatiLavoroGen(String decrypt_encrypt){
        allLavoroGen.clear();
        String id="";
        String passwordDaInserire="";
        DBLayer dbLayer=null;
        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getAllDataLavoroAziendali(user);

            Log.i("dato cursore allLavgen",""+cursor.getCount());

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    Crypto crypto= new Crypto(SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String password=cursor.getString(4);
                    Log.i("dato da controllare","pwlav "+password);

                    if(decrypt_encrypt.equals("decrypt")) {
                        Log.i("crypt_decrypt","decrypt");

                        passwordDaInserire = crypto.decrypt(password.getBytes());
                    }
                    if(decrypt_encrypt.equals("encrypt")){
                        Log.i("crypt_decrypt","crypt");

                        passwordDaInserire=crypto.encrypt(password.getBytes());
                    }

                    Log.d("dato da inserire",passwordDaInserire);

                    allLavoroGen.put(id,new String[]{id,passwordDaInserire});
                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(context, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
        }
        dbLayer.close();

    }

    public void modificaDatiLavoroGen() {

        Set<String> keys = allLavoroGen.keySet();
        for (String key : keys) {
            String[] valori = allLavoroGen.get(key);
            if (!valori[1].equals("")) {
                ContentValues contentValues = new ContentValues();
                contentValues.put("password", valori[1]);


                DBLayer dbLayer = null;
                try {
                    dbLayer = new DBLayer(context);
                    dbLayer.open();

                    int ris = dbLayer.modificaPwLavoro(valori[0], contentValues);
                    if (ris == 1) {
                        Log.d("dato ges.modificato", valori[1]);

                    } else {
                        Log.d("dato ges NON modificato", valori[1]);

                    }
                } catch (Exception ex) {
                    Log.e("errore modificaDB ges", ex.toString());

                }
                dbLayer.close();

            } else {
                Log.d("dato ges NON modificato", valori[1]);

            }
        }
    }

    public  boolean inviaMail(File fileBackup) {
        boolean execute=false;
        Log.i("mail to", fileBackup.getAbsolutePath());
        //controllaPermessi();
        try {
            String backupDBPath = "password.db";

            File filelocation = new File(Environment.getExternalStorageDirectory().getAbsolutePath(), backupDBPath);
            //Uri patch= Uri.fromFile(filelocation);
            Uri patch = FileProvider.getUriForFile(context, BuildConfig.APPLICATION_ID + ".provider",filelocation);
                Intent emailintent= new Intent((Intent.ACTION_SEND));
                // impostiamo il tipo di mail
                emailintent.setType("vnd.android.cursor.dir/email");
                //String to[]={utente.getMail();};
                String to[]={mailUser};
                emailintent.putExtra(Intent.EXTRA_EMAIL, to);
                // allegato
            emailintent.putExtra(Intent.EXTRA_STREAM,patch);
            //oggetto della mail
            emailintent.putExtra(Intent.EXTRA_SUBJECT,"backup cassaforte");


            context.startActivity(Intent.createChooser(emailintent,"invia mail...."));
            Log.i("mail try", fileBackup.getAbsolutePath());

                execute=true;
            Log.i("mail try", String.valueOf(execute));

        } catch (Exception e) {
            Toast.makeText(context, e.toString(), Toast.LENGTH_SHORT).show();
            execute=false;
        }
        return execute;
    }

    private String caricaMail(){
        String mailUser="";
        DBLayer dbLayer=null;
        try {
            dbLayer = new DBLayer(context);
            dbLayer.open();
            Cursor cursor = dbLayer.getMailRecupero(user);

            Log.i("dato cursore MAIL",""+cursor.getCount());

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    mailUser= cursor.getString(0);

                    Log.i("mail User",mailUser);

                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(context, ""+ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();
return mailUser;
    }

}
