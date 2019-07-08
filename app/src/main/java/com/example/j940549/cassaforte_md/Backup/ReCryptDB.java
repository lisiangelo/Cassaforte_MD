package com.example.j940549.cassaforte_md.Backup;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.util.Log;
import android.widget.Toast;
import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class ReCryptDB {
    private Context context;
    private String user;
    Map <String,String[]> allFinanza= new HashMap<String, String[]>();
    Map <String,String[]> allBancheDati= new HashMap<String, String[]>();
    Map <String,String[]> allGestori= new HashMap<String, String[]>();
    Map <String,String[]> allPersonali= new HashMap<String, String[]>();
    Map <String,String[]> allLavoroGen= new HashMap<String, String[]>();
    private String NEW_SECURITYKEY;
    private String OLD_SECURITYKEY;

    public ReCryptDB ( Context context, String user, String new_Securitykey, String old_securitykey){
        this.context=context;
        this.user=user;
        NEW_SECURITYKEY=new_Securitykey;
        OLD_SECURITYKEY=old_securitykey;
    }

    public int rencryptDB() {
        int result;
        try {
            rencryptDBFinanza();
            rencryptDBBancheDati();
            rencryptDBGestori();
            rencryptDBPersonali();
            rencryptDBLavoroGen();
            result=1;
        }catch (Exception e){
         result=-1;
        }
        return result;
    }



    private void rencryptDBFinanza() throws Exception {
        caricaDatiFinanza();
        modificaDatiFinanza();
    }


    private void caricaDatiFinanza(){
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
                    Crypto crypto= new Crypto(OLD_SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String iban = cursor.getString(3);
                    Log.i("dato da controllare","iban "+iban);

                        Log.i("crypt_decrypt","decrypt");

                        if(iban.equals("-")){
                            ibanDaInserire=iban;
                        }else{
                            ibanDaInserire = crypto.decrypt(iban.getBytes());}

                    String password=cursor.getString(5);
                    Log.i("dato da controllare","pword "+password);

                        Log.i("crypt_decrypt","decrypt");

                        passwordDaInserire = crypto.decrypt(password.getBytes());

                    String nrcarta = cursor.getString(7);
                    Log.i("dato da controllare","nrcar "+nrcarta);

                        if(nrcarta.equals("-")){
                            nrCartaDaInserire=nrcarta;
                        }else{
                            nrCartaDaInserire = crypto.decrypt(nrcarta.getBytes());}


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

    public void modificaDatiFinanza() throws Exception {

        Set<String> keys = allFinanza.keySet();
        for (String key : keys) {
            String[] valori = allFinanza.get(key);
            if (!valori[1].equals("") && !valori[2].equals("") && !valori[3].equals("")) {
                Crypto crypto= new Crypto(NEW_SECURITYKEY.getBytes());

                ContentValues contentValues = new ContentValues();
                contentValues.put("iban", crypto.encrypt(valori[1].getBytes()));
                contentValues.put("password", crypto.encrypt(valori[2].getBytes()));
                contentValues.put("pin2", crypto.encrypt(valori[3].getBytes()));


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

    private void rencryptDBBancheDati() throws Exception {
        caricaDatiBancheDati();
        modificaDatiBancheDati();
    }


    private void caricaDatiBancheDati(){
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
                    Crypto crypto= new Crypto(OLD_SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String password=cursor.getString(4);
                    Log.i("dato da controllare","pwbanc "+password);

                        Log.i("crypt_decrypt","decrypt");

                        passwordDaInserire = crypto.decrypt(password.getBytes());

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

    public void modificaDatiBancheDati() throws Exception {

        Set<String> keys = allBancheDati.keySet();
        for (String key : keys) {
            String[] valori = allBancheDati.get(key);

            if (!valori[1].equals("")) {

                Crypto crypto= new Crypto(NEW_SECURITYKEY.getBytes());


                ContentValues contentValues = new ContentValues();
                contentValues.put("password", crypto.encrypt(valori[1].getBytes()));

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

    private void rencryptDBGestori() throws Exception {
        caricaDatiGestori();
        modificaDatiGestori();
    }

    private void caricaDatiGestori(){
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
                    Crypto crypto= new Crypto(OLD_SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String password=cursor.getString(4);
                    Log.i("dato da controllare","pwgest "+password);


                        passwordDaInserire = crypto.decrypt(password.getBytes());

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

    public void modificaDatiGestori() throws Exception {

        Set<String> keys = allGestori.keySet();
        for (String key : keys) {
            String[] valori = allGestori.get(key);
            if (!valori[1].equals("")) {

                Crypto crypto= new Crypto(NEW_SECURITYKEY.getBytes());

                ContentValues contentValues = new ContentValues();
                contentValues.put("password", crypto.encrypt(valori[1].getBytes()));


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

    private void rencryptDBPersonali() throws Exception {
        caricaDatiPersonali();
        modificaDatiPersonali();
    }


    private void caricaDatiPersonali(){
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
                    Crypto crypto= new Crypto(OLD_SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String password=cursor.getString(5);
                    Log.i("dato da controllare","pwper "+password);


                        passwordDaInserire = crypto.decrypt(password.getBytes());

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

    public void modificaDatiPersonali() throws Exception {

        Set<String> keys = allPersonali.keySet();
        for (String key : keys) {
            String[] valori = allPersonali.get(key);

            if (!valori[1].equals("")) {

                Crypto crypto= new Crypto(NEW_SECURITYKEY.getBytes());

                ContentValues contentValues = new ContentValues();
                contentValues.put("password", crypto.encrypt(valori[1].getBytes()));

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

    private void rencryptDBLavoroGen() throws Exception {
        caricaDatiLavoroGen();
        modificaDatiLavoroGen();
    }


    private void caricaDatiLavoroGen(){
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
                    Crypto crypto= new Crypto(OLD_SECURITYKEY.getBytes());
                    id= cursor.getString(0);
                    String password=cursor.getString(4);
                    Log.i("dato da controllare","pwlav "+password);

                        Log.i("crypt_decrypt","decrypt");

                        passwordDaInserire = crypto.decrypt(password.getBytes());

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

    public void modificaDatiLavoroGen() throws Exception {

        Set<String> keys = allLavoroGen.keySet();
        for (String key : keys) {
            String[] valori = allLavoroGen.get(key);
            if (!valori[1].equals("")) {
                Crypto crypto= new Crypto(NEW_SECURITYKEY.getBytes());

                ContentValues contentValues = new ContentValues();
                contentValues.put("password", crypto.encrypt(valori[1].getBytes()));


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




}
