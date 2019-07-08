package com.example.j940549.cassaforte_md.SQLite;

/**
 * Created by J940549 on 22/04/2017.
 */

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Base64;
import android.util.Log;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.RegistratiActivity;

/**
 * Created by J940549 on 22/04/2017.
 */

public class DBLayer {

    private static final String DATABASE_NAME = "password.db";
    private static final int DATABASE_VERSION = 1;

    private DbHelper ourHelper;
    private  static Context ourContext;
    private SQLiteDatabase ourDatabase;
    //private static Crypto crypto;

    public DBLayer(Context c){
        this.ourContext = c;
    }

    private static class DbHelper extends SQLiteOpenHelper {


        public DbHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
            //  crypto=new Crypto(context);
            // TODO Auto-generated constructor stub
        }

        @Override
        public void onCreate(SQLiteDatabase db) {

            try {
                db.execSQL("CREATE TABLE tableUsers (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        "nomeUser TEXT, password TEXT, token TEXT,email TEXT,ffpp TEXT);");
                db.execSQL("CREATE TABLE tablePwLavoro (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " user TEXT, nomeApplicazione TEXT, utente TEXT, password TEXT,pin TEXT, pin2 TEXT, url TEXT, note TEXT, tipo TEXT);");
                db.execSQL("CREATE TABLE tablePwFinanza (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " user TEXT,banca TEXT, iban TEXT, utente TEXT, password TEXT, pin TEXT, pin2 TEXT, codicebancomat TEXT, note TEXT);");

                db.execSQL("CREATE TABLE tablePwFamiglia (" +
                        " _id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        " user TEXT,nomeApplicazione TEXT,email TEXT, utente TEXT, password TEXT,pin TEXT,  url TEXT, note TEXT);");
            }catch (SQLException ex){
                Toast.makeText(ourContext, ""+ex.toString(), Toast.LENGTH_SHORT).show();
            }
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            db.execSQL("DROP TABLE IF EXISTS tableUsers;");
            db.execSQL("DROP TABLE IF EXISTS tablePwLavoro;");
            db.execSQL("DROP TABLE IF EXISTS tablePwFinanza;");
            db.execSQL("DROP TABLE IF EXISTS tablePwFamiglia;");
            onCreate(db);
        }
    }


    public DBLayer open() throws SQLException {
        this.ourHelper = new DbHelper(ourContext);
        this.ourDatabase = ourHelper.getWritableDatabase();
        return this;
    }

    public void close(){
        this.ourHelper.close();
    }

    public Cursor  getAllUtente(){

        Cursor c = ourDatabase.rawQuery("select * from tableUsers",null);

        return c;

    }

    public int modificaDBUtente(String id,ContentValues contentValues){

        int c=-1;

        try{
            c=ourDatabase.update("tableUsers",contentValues,"_id="+"\""+id+"\"",null);

        }catch (Exception e){

            Log.d("errore modicaDBUtente  ",e.toString());

        }

        return c;
    }


    public Cursor getPassword(String nomeUser ){
            nomeUser="\""+nomeUser+"\"";


            Cursor c= ourDatabase.rawQuery("select password, token from tableUsers where nomeUser= "+nomeUser,null);
Log.d("getpw indbLayer",nomeUser);
            return c;
    }

    public Cursor getMailRecupero(String token ){
        token="\""+token+"\"";

        Log.d("getemail indbLayer",token);
        Cursor c= ourDatabase.rawQuery("select email from tableUsers where token= "+token,null);

        return c;
    }
    public Cursor getUtente(String nomeUser ){
        nomeUser="\""+nomeUser+"\"";


        Cursor c= ourDatabase.rawQuery("select * from tableUsers where nomeUser= "+nomeUser,null);

        return c;
    }

    public boolean registra(String nomeUser, String unapassword, String unamail, String optFFPP) throws Exception {
       // String passwordCrypto=crypto.encrypt(unapassword);
     //   String tokenCrypto=crypto.encrypt(creaToken(nomeUser,unapassword));
        String user="\""+nomeUser+"\"";
        String password="\""+unapassword+"\"";
        String mail="\""+unamail+"\"";
        Crypto crypto=new Crypto(RegistratiActivity.SECURITYKEY.getBytes());
        String pwperToken=crypto.decrypt(unapassword.getBytes());

        String token_base64=Base64.encodeToString(creaToken(nomeUser,pwperToken).getBytes(),Base64.DEFAULT);
        String token="\""+token_base64+"-"+optFFPP+"\"";
        String ffpp="\""+optFFPP+"\"";
        boolean c = false;
      /*  Cursor nrUtenti=getUtente(nomeUser);
        nrUtenti.moveToFirst();
        if(nrUtenti.getCount()==0) {*/
            String Query = "insert into tableUsers (_id,nomeUser , password, token, email, ffpp )" +
                    "values (null," + user + "," + password + "," + token + "," + mail + "," + ffpp + ");";
            Log.i("query", Query);
            try {
                ourDatabase.execSQL(Query);
                c = true;
            } catch (Exception e) {
                c = false;
            }
     /*   }
        if(nrUtenti.getCount()>=1) {
            Toast.makeText(ourContext, "più alias", Toast.LENGTH_SHORT).show();
            String id=nrUtenti.getString(0);
            int res=ourDatabase.delete("tableUsers", "nomeUser="+user,null);
            //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
            if(res!=0){
                registra(nomeUser, unapassword, unamail, optFFPP);
                Toast.makeText(ourContext, "elimino alias", Toast.LENGTH_SHORT).show();

            }
            else{
                Toast.makeText(ourContext, "nessun alias eliminato", Toast.LENGTH_SHORT).show();
            }

        }*/
        return c;
    }

    public int cambiaPassword(String nomeUser, String  oldpassword,
                                String newPassword, String newtoken){


        nomeUser="\""+nomeUser+"\"";
        oldpassword="\""+oldpassword+"\"";
        newPassword="\""+newPassword+"\"";
        newtoken="\""+newtoken+"\"";
        String strSQL = "UPDATE tableUsers SET password = "+newPassword+", token=" +newtoken+ "  WHERE nomeUser = "+ nomeUser ;
        Log.i("cambiopw", strSQL);
        int c=0;

        try{


            ourDatabase.execSQL(strSQL);
            c=1;

        }catch (Exception e){
            Toast.makeText(ourContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
        c=-1;
        }

        return c;
    }


    public int aggiornaToken( String newtoken){

        newtoken="\""+newtoken+"\"";
        String strSQL_fam = "UPDATE tablePwFamiglia SET user="+newtoken;
        Log.i("cambiopw", strSQL_fam);
        String strSQL_fin = "UPDATE tablePwFinanza SET user="+newtoken;
        Log.i("cambiopw", strSQL_fin);
        String strSQL_lav = "UPDATE tablePwLavoro SET user="+newtoken;
        Log.i("cambiopw", strSQL_lav);


        int c=0;

        try{
            ourDatabase.execSQL(strSQL_fam);
            c=1;
        }catch (Exception e){
            Toast.makeText(ourContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
            c=-1;
        }
        try{
            ourDatabase.execSQL(strSQL_fin);
            c=1;
        }catch (Exception e){
            Toast.makeText(ourContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
            c=-1;
        }
        try{
            ourDatabase.execSQL(strSQL_lav);
            c=1;
        }catch (Exception e){
            Toast.makeText(ourContext, ""+e.toString(), Toast.LENGTH_SHORT).show();
            c=-1;
        }

        return c;
    }


    public boolean inserisciNewPwLavoro(String user,String nomeApplicazione, String nomeUtente, String password,
                                       String pin,String pin2, String url, String note,String tipo){
        user="\""+user+"\"";
        nomeApplicazione="\""+nomeApplicazione+"\"";
        nomeUtente="\""+nomeUtente+"\"";
    //    String passwordCrypto=crypto.encrypt(password);
        password="\""+password+"\"";
        pin="\""+pin+"\"";
        pin2="\""+pin2+"\"";
        url="\""+url+"\"";
        note="\""+note+"\"";
         tipo="\""+tipo+"\"";
        boolean c = false;
        String Query="insert into tablePwLavoro (_id,user,nomeApplicazione , utente , password ,pin ,pin2, url , note,tipo)" +
                "values (null,"+user+","+nomeApplicazione+","+nomeUtente+","+password+","+pin+","+pin2+","+url+","+note+","+tipo+");";
        Log.i("query",Query);
        try{
                    ourDatabase.execSQL(Query);
        c=true;
            Log.i("query succesfully",""+c);
        }catch (Exception e){
            c=false;
            Log.i("query errore",e.toString());
        }

        return c;
    }



    public int modificaPwLavoro(String id,String nomeApplicazione, String nomeUtente, String password,
                                        String pin, String pin2, String url, String note){
    //    String passwordCrypto=crypto.encrypt(password);

        ContentValues contentValues= new ContentValues();
        contentValues.put("nomeApplicazione",nomeApplicazione);
        contentValues.put("utente",nomeUtente);
        contentValues.put("password",password);
        contentValues.put("pin",pin);
        contentValues.put("pin2",pin2);
        contentValues.put("url",url);
        contentValues.put("note",note);
        int c=-1;

        try{
            c=ourDatabase.update("tablePWLavoro",contentValues,"_id="+"\""+id+"\"",null);

        }catch (Exception e){
            Log.d("errore modicaPwLavoro  ",e.toString());

        }

        return c;
    }

    public int modificaPwLavoro(String id,ContentValues contentValues){

        int c=-1;

        try{
            c=ourDatabase.update("tablePWLavoro",contentValues,"_id="+"\""+id+"\"",null);

        }catch (Exception e){
            Log.d("errore modicaPwLavoro  ",e.toString());

        }

        return c;
    }

    public int deleteDataLavoro(String id){
    id="\""+id+"\"";
    int res=ourDatabase.delete("tablePwLavoro", "_id="+id,null);
    //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
    return res;
}

    public Cursor  getOneDataLavoro(String id){
//        ArrayList<String> lstD = new ArrayList<String>();
    id="\""+id+"\"";


           /* Cursor c = ourDatabase.query(true, "tablePwLavoro",new String[]{"_id","nomeApplicazione" , "utente" , "password" ,"pin"
                    , "url" , "note"},"_id="+id,null,null,null,null,null);*/
        Cursor c= ourDatabase.rawQuery("select * from tablePwLavoro where _id= "+id,null);

return c;


        }

    public Cursor  getAllDataLavoroGestori(String user){
        user="\""+user+"\"";
        Cursor c = ourDatabase.rawQuery("select * from tablePwLavoro where user="+user+" and tipo=\"tel\" order by nomeApplicazione asc",null);
        Log.d("query all gestori nr..",""+c.getCount());
        return c;

    }

    public Cursor  getAllDataLavoroBancheDati(String user){
        user="\""+user+"\"";
        Cursor c = ourDatabase.rawQuery("select * from tablePwLavoro where user="+user+" and tipo=\"bd\" order by nomeApplicazione asc",null);
        Log.d("query all Bdati nr..",""+c.getCount());
        return c;

    }
    public Cursor  getAllDataLavoroAziendali(String user){
        user="\""+user+"\"";
        Cursor c = ourDatabase.rawQuery("select * from tablePwLavoro where user="+user+" and tipo=\"az\" order by nomeApplicazione asc",null);

        return c;

    }

    public boolean inserisciNewPwFinanza(String user, String banca, String iban, String nomeUtente, String password,
                                        String pin, String pin2, String bancomat,String note){
        user="\""+user+"\"";
        banca="\""+banca+"\"";
        iban="\""+iban+"\"";
        nomeUtente="\""+nomeUtente+"\"";
      //  String passwordCrypto=crypto.encrypt(password);
        password="\""+password+"\"";
        pin="\""+pin+"\"";
        pin2="\""+pin2+"\"";
        bancomat="\""+bancomat+"\"";
        note="\""+note+"\"";
//        String url="\"url\"";
        boolean c = false;
        String Query="insert into tablePwFinanza (_id,user, banca , iban , utente , password , pin , pin2 , codicebancomat , note )" +
                "values (null, "+user+","+banca+","+iban+","+nomeUtente+","+password+","+pin+","+pin2+","+bancomat+","+note+");";
        Log.i("query",Query);
        try{
            ourDatabase.execSQL(Query);
            c=true;
        }catch (Exception e){
            c=false;
        }

        return c;
    }

    public int modificaPwFinanza(String id, String banca, String iban, String nomeUtente, String password,
                                 String pin, String pin2, String bancomat,String note){
      //  String passwordCrypto=crypto.encrypt(password);

        ContentValues contentValues= new ContentValues();
        contentValues.put("banca",banca);
        contentValues.put("iban",iban);
        contentValues.put("utente",nomeUtente);
        contentValues.put("password",password);
        contentValues.put("pin",pin);
        contentValues.put("pin2",pin2);
        contentValues.put("codicebancomat",bancomat);
        contentValues.put("note",note);
        int c=-1;

        try{
            c=ourDatabase.update("tablePWFinanza",contentValues,"_id="+"\""+id+"\"",null);

        }catch (Exception e){
            Log.d("errore modicaPwFinanza ",e.toString());

        }

        return c;
    }
    public int modificaPwFinanza(String id, ContentValues contentValues){
        int c=-1;

        try{
            c=ourDatabase.update("tablePWFinanza",contentValues,"_id="+"\""+id+"\"",null);

        }catch (Exception e){
            Log.d("errore modPwFinanza  ",e.toString());

        }

        return c;
    }


    public int deleteDataFinanza(String id){
        id="\""+id+"\"";
        int res=ourDatabase.delete("tablePwFinanza", "_id="+id,null);
        //db.delete(DATABASE_TABLE, KEY_ROWID + "=" + row, null);
        return res;
    }
    public Cursor  getOneDataFinanza(String id){
//        ArrayList<String> lstD = new ArrayList<String>();
        id="\""+id+"\"";


       /* Cursor c = ourDatabase.query(true, "tablePwFinanza",new String[]{"_id","banca" , "iban","utente" , "password" ,"pin" , "pin2"
                , "codicebancomat" , "note"},"_id="+id,null,null,null,null,null);*/
        Cursor c= ourDatabase.rawQuery("select * from tablePwFinanza where _id= "+id,null);

        return c;

    }

    public Cursor  getAllDataFinanza(String user){
        user="\""+user+"\"";
        Cursor c = ourDatabase.rawQuery("select * from tablePwFinanza where user="+user+" order by banca asc",null);

        return c;

    }

    public boolean inserisciNewPwPersonale(String user, String nomeApplicazione, String email,String nomeUtente, String password,
                                           String pin, String url,String note){
        user="\""+user+"\"";
        nomeApplicazione="\""+nomeApplicazione+"\"";
        email="\""+email+"\"";
        nomeUtente="\""+nomeUtente+"\"";
  //      String passwordCrypto=crypto.encrypt(password);
        password="\""+password+"\"";
        pin="\""+pin+"\"";
        note="\""+note+"\"";
        url="\""+url+"\"";
//        String url="\"url\"";
        boolean c = false;
        String Query="insert into tablePwFamiglia  (_id, user, nomeApplicazione ,email, utente , password , pin , url , note )" +
                "values (null, "+user+","+nomeApplicazione+","+email+","+nomeUtente+","+password+","+pin+","+url+","+note+");";
        Log.i("query",Query);
        try{
            ourDatabase.execSQL(Query);
            c=true;
        }catch (Exception e){
            c=false;
        }

        return c;
    }

    public int modificaPwPersona(String id,String nomeApplicazione,String email, String nomeUtente, String password,
                                 String pin, String url,String note){
      //  String passwordCrypto=crypto.encrypt(password);

        ContentValues contentValues= new ContentValues();
        contentValues.put("nomeApplicazione",nomeApplicazione);
        contentValues.put("email",email);
        contentValues.put("utente",nomeUtente);
        contentValues.put("password",password);
        contentValues.put("pin",pin);
        contentValues.put("url",url);
        contentValues.put("note",note);
        int c=-1;

        try{
            c=ourDatabase.update("tablePWFamiglia",contentValues,"_id="+"\""+id+"\"",null);

        }catch (Exception e){
            Log.d("errore modPwPersona  ", e.toString());

        }

        return c;
    }

    public int modificaPwPersona(String id,ContentValues contentValues){
        int c=-1;

        try{
            c=ourDatabase.update("tablePWFamiglia",contentValues,"_id="+"\""+id+"\"",null);

        }catch (Exception e){
            Log.d("errore modPwPersona  ",e.toString());

        }

        return c;
    }


    public int deleteDataPersona(String id){
        id="\""+id+"\"";
        int res=ourDatabase.delete("tablePwFamiglia", "_id="+id,null);

        return res;
    }


    public Cursor  getOneDataPersona(String id){

        Cursor c= ourDatabase.rawQuery("select * from tablePwFamiglia where _id="+id,null);

        return c;
    }

    public Cursor  getAllDataPersona(String user){
        user="\""+user+"\"";
        Cursor c = ourDatabase.rawQuery("select * from tablePwFamiglia where user="+user+" order by nomeApplicazione asc",null);

        return c;

    }

    public Cursor [] getOneDataSearch(String query){
//        ArrayList<String> lstD = new ArrayList<String>();
        Cursor [] cursors= new Cursor[3];
        query="\"%"+query+"%\"";


           /* Cursor c = ourDatabase.query(true, "tablePwLavoro",new String[]{"_id","nomeApplicazione" , "utente" , "password" ,"pin"
                    , "url" , "note"},"_id="+id,null,null,null,null,null);*/
        cursors[0] = ourDatabase.rawQuery("select  * from tablePwLavoro where tablePwLavoro.nomeApplicazione like "+query ,null);

        cursors[1] = ourDatabase.rawQuery("select  * from tablePwFamiglia where tablePwFamiglia.nomeApplicazione like "+query,null);

        cursors[2] = ourDatabase.rawQuery("select  * from tablePwFinanza where banca like "+query ,null);


        return cursors;


    }



    public SQLiteDatabase getDB(){
    return this.ourDatabase;

}

public String creaToken(String user,String password){

    Log.d("crea token",user);
    Log.d("crea token",password);

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
        Log.d("token",token.toString());

    }else{
        for(int i=0; i<=x;i++){

                token.append(user.charAt(i));
            if(i<=y){
                token.append(password.charAt(i));}else{

                token.append(password.charAt(y));
            }

        }
        Log.d("token",token.toString());
    }
    return token.toString();
}
}