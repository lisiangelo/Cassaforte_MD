package com.example.j940549.cassaforte_md;

import android.Manifest;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.SearchManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.MenuInflater;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Backup.BackUp;
import com.example.j940549.cassaforte_md.PwFinanza.FragmentPwFinanziarie;
import com.example.j940549.cassaforte_md.PwLavoro.FragmentPwlavoro;
import com.example.j940549.cassaforte_md.PwLavoro.PwLavoroGen.FragmentPwLavoroGen;
import com.example.j940549.cassaforte_md.PwPersonale.FragmentPwPersonali;

import java.io.File;
import java.util.Calendar;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private DrawerLayout mDrawer;
    String qualeFragment;
    private String user;
    public static String isffpp;
    private static final int CREA_BACKUP = 1;
    private static final int CARICA_BACKUP= 2;
    private boolean permesso=true;
    private static final int READ_REQUEST_CODE=42;
    private String backupPath;
    private Switch mSwitch, notifSwitch;
    private SharedPreferences mSharedPreferences;
    FragmentManager fragmentManager;
    // Notification ID.
    private static final int NOTIFICATION_ID = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(savedInstanceState != null){
            user= (String) savedInstanceState.get("user");
            isffpp=(String) savedInstanceState.get("isffpp");
            Log.d("isffpp",isffpp);
            qualeFragment= (String) savedInstanceState.get("qualeFragment");
        }else{
            user = getIntent().getExtras().getString("user");
            isffpp=getIntent().getExtras().getString("isffpp");
            Log.d("isffpp",isffpp);
            qualeFragment=getIntent().getExtras().getString("qualeFragment");
        }

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mDrawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, mDrawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        mDrawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        if(qualeFragment==null||qualeFragment.equals("")){
            qualeFragment="scegliCat";
        }
        caricaFragment(qualeFragment);

        navigationView.getMenu().findItem(R.id.checkOnFingerprint)
                .setActionView(new Switch(this));
        navigationView.getMenu().findItem(R.id.checkOnNotification)
                .setActionView(new Switch(this));

        // To set whether switch is on/off use:
        mSwitch = (Switch)
               navigationView.getMenu().findItem(R.id.checkOnFingerprint).getActionView();

//        aSwitch.setChecked(false);

        mSharedPreferences = getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);

        mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {

                if (ischecked) {
                    salvainPreferenze(ischecked);

                } else {
                    salvainPreferenze(ischecked);
                }

            }
        });


        notifSwitch = (Switch)
                navigationView.getMenu().findItem(R.id.checkOnNotification).getActionView();


//        aSwitch.setChecked(false);

        notifSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean ischecked) {

                if (ischecked) {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("notifSwitch",true);
                    editor.commit();

                    attivaNotifiche();
                    Log.i("switch", "on" );
                    Toast.makeText(getBaseContext(),"Notifiche abilitate",Toast.LENGTH_LONG).show();
                    //  startService(i);
                } else {
                    SharedPreferences.Editor editor = mSharedPreferences.edit();
                    editor.putBoolean("notifSwitch",false);
                    editor.commit();

                    disattivaNotifiche();
                    Log.i("switch", "off" );
                    Toast.makeText(getBaseContext(),"Notifiche disabilitate",Toast.LENGTH_LONG).show();

                }

            }
        });

        leggiPreferenze();


    }


    private void leggiPreferenze(){
        //SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        boolean siNotifiche= mSharedPreferences.getBoolean("notifSwitch", false);

        if(!siNotifiche){
            Toast.makeText(this, "ATTIVA LE NOTIFICHE PER BACKUP", Toast.LENGTH_SHORT).show();
            notifSwitch.setChecked(false);

        }else {
            notifSwitch.setChecked(true);

        }

        boolean siFingerprint=mSharedPreferences.getBoolean("chkFingerprint", false);
        Log.i("si fingerprint",""+siFingerprint);
            mSwitch.setChecked(siFingerprint);

    }

    private void salvainPreferenze(boolean isChecked) {

        Log.i("si fingerprint set..",""+isChecked);


                SharedPreferences.Editor editor = mSharedPreferences.edit();
                editor.putBoolean("chkFingerprint",isChecked);
                editor.commit();
            }


    private void attivaNotifiche(){


        Intent intent = new Intent(this, AllarmReceiver.class);
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

            PendingIntent pendingalarmIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent, 0);
            // se esiste prima lo cancello
            alarmManager.cancel(pendingalarmIntent);
            // Set the alarm to start and repet evert 2 hours
            Calendar calendar_ora_allarm= Calendar.getInstance();
            calendar_ora_allarm.set(Calendar.DAY_OF_WEEK,Calendar.MONDAY);
            calendar_ora_allarm.set(Calendar.HOUR_OF_DAY,9);
            calendar_ora_allarm.set(Calendar.MINUTE,0);

            Calendar calendarOra_adesso= Calendar.getInstance();

            long diff=calendar_ora_allarm.getTimeInMillis()-calendarOra_adesso.getTimeInMillis();
            Log.i("notifica ","dopo "+diff);

            if(diff<0){
                // sommo ai millisecondi di una settimana il tempo (negativo) in millisecondi passati dall'ultimo lunedi
                diff=604800000+diff;
                Log.i("notifica ","passata "+diff);

            }
            Log.i("notifica ","ora "+calendar_ora_allarm.getTimeInMillis());

            long repeatInterval = 7 * AlarmManager.INTERVAL_DAY;

            long triggerTime = SystemClock.elapsedRealtime()+diff;

            // If the Toggle is turned on, set the repeating alarm with
            // a 2 hours interval.

            if (alarmManager != null) {
                alarmManager.setInexactRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP,
                        triggerTime, repeatInterval,
                        pendingalarmIntent);
            }


    }

    private void disattivaNotifiche(){

        Intent intent = new Intent(this, AllarmReceiver.class);
        PendingIntent alarmIntent = PendingIntent.getBroadcast(this, NOTIFICATION_ID, intent,0);

        AlarmManager manager= (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        manager.cancel(alarmIntent);

    }


    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        outState.putString("user", user);
        outState.putString("isffpp",isffpp);
        outState.putString("qualeFragment", qualeFragment);

    }
    private void caricaFragment(String qualeFragment){
        Fragment fragment=null;

        switch(qualeFragment){
            case "pwLavoro":

                if(isffpp.equals("si")){
                fragment=FragmentPwlavoro.newInstance(user);}
                else{fragment=FragmentPwLavoroGen.newInstance(user);}
                setTitle("Passwords Lavoro");

                break;
            case "pwFinanziarie":

                fragment=FragmentPwFinanziarie.newInstance(user);
                setTitle("Passwords Finanziarie");

                break;
            case "pwPersonali":

                fragment=FragmentPwPersonali.newInstance(user);
                setTitle("Passwords Personali");

                break;

            case "scegliCat":

                if(isffpp.equals("si")){
                fragment=FragmentStart.newInstance(user,qualeFragment);}
                else{fragment=FragmentStart_Gen.newInstance(user,qualeFragment);}
                setTitle("Scegli una categoria");

                break;


        }

//        fragment=FragmentStart.newInstance(user);
        //inserisci il fragment rimpiazzando i frgment esitente
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent_utente, fragment).commit();

    }


    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener( new NavigationView.OnNavigationItemSelectedListener(){
            @Override
            public boolean onNavigationItemSelected(MenuItem item) {
                selectDrawerItem(item);
                return true;
            }
        });

    }

    public void selectDrawerItem(MenuItem item) {

        Fragment fragment=null;

        switch (item.getItemId()) {

            case R.id.nav_pwpersonale:

                qualeFragment="pwPersonali";
                fragment=FragmentPwPersonali.newInstance(user);

                break;

            case R.id.nav_pwlavoro:
                qualeFragment="pwLavoro";
                if(isffpp.equals("si")){
                    fragment=FragmentPwlavoro.newInstance(user);}
                else{fragment=FragmentPwLavoroGen.newInstance(user);}
                setTitle("Passwords Lavoro");
                break;

            case R.id.nav_pwfinanziarie:
                qualeFragment="pwFinanziarie";
                fragment = FragmentPwFinanziarie.newInstance(user);
                break;

            case R.id.nav_start:
                qualeFragment="scegliCat";
                if(isffpp.equals("si")){
                    fragment=FragmentStart.newInstance(user,qualeFragment);}
                else{fragment=FragmentStart_Gen.newInstance(user,qualeFragment);}
                setTitle("Scegli una categoria");
                break;

            case R.id.nav_backup:
                exportDB(this);
                break;

            case R.id.nav_caricabackup:
                importDB(this);
                break;

            case R.id.nav_cambiapassword:
                Intent vaiaCambioPassword =new Intent(this,CambiaPassword.class);
                vaiaCambioPassword.putExtra("user",user);
                vaiaCambioPassword.putExtra("isffpp",isffpp);

                startActivity(vaiaCambioPassword);
                finish();
                break;


            default:
                fragment =   FragmentStart.newInstance(user,qualeFragment);
                break;
        }

        if(fragment!=null) {
            //inserisci il fragment rimpiazzando i frgment esitente
            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent_utente, fragment).commit();
        }
        //evidenzio  l'item che è stato selezionato nel Navigationview
        item.setChecked(true);

        //imposto il titolo dellìaction bar
        setTitle(item.getTitle());
        //chiudo il navigationdrawer
        mDrawer.closeDrawers();

    }




    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {

//            super.onBackPressed();
            AlertDialog.Builder builder=new AlertDialog.Builder(this)
                    .setMessage("sei sicuro di voler uscire?")
                    .setCancelable(false)
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            MainActivity.this.onSuperBackPressed();
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.cancel();
                        }
                    });
            AlertDialog alert= builder.create();
            alert.show();

        }
    }

    public  void onSuperBackPressed(){
        super.onBackPressed();
    }




    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        selectDrawerItem(item);

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_search, menu);

        final MenuItem searchItem = menu.findItem(R.id.action_search);

        SearchManager searchManager = (SearchManager) MainActivity.this.getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView = null;
        if (searchItem != null) {
            searchView = (SearchView) searchItem.getActionView();
        }
        if (searchView != null) {
            searchView.setSearchableInfo(searchManager.getSearchableInfo(MainActivity.this.getComponentName()));
        }


        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Toast like print
                Log.i("SearchOnQueryTxtSubmit:", query);
                    Fragment fragmentSearch = FragmentSearch.newInstance(user, query);
                    //inserisci il fragment rimpiazzando i frgment esitente
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent_utente, fragmentSearch).commit();

               // searchItem.collapseActionView();
                return false;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                Log.i( "SearchOnQueryTxtChan:" , s);
                if (s.equals("")) {
                    Log.i("qualefragment:", qualeFragment);

                    caricaFragment(qualeFragment);
                } else {

                    Fragment fragmentSearch = FragmentSearch.newInstance(user, s);
                    //inserisci il fragment rimpiazzando i frgment esitente
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.flContent_utente, fragmentSearch).commit();
                }
                return false;
            }
        });
        return true;
    }






    @Override
    public boolean onOptionsItemSelected (MenuItem menuItem){
        boolean esito=false;
        switch(menuItem.getItemId()){
            case R.id.optionCambiaPW:{
                Intent vaiaCambioPassword =new Intent(this,CambiaPassword.class);
                vaiaCambioPassword.putExtra("user",user);
                startActivity(vaiaCambioPassword);
                esito= true;
                break;
            }
            case R.id.optionExport:{
                exportDB(this);
                break;
            }
            case R.id.optionImport:{
                importDB(this);
                break;
            }

            case R.id.optionEsci:{
                finish();
                break;
            }


        }
        return esito;
    }

    public  void importDB(Context context) {
        backupPath="";
        prendiPath();

    }


    public  void exportDB(Context context) {
        controllaPermessi();
        if(permesso) {
            BackUp backUp = new BackUp(context, user);
            //backUp.decryptDB();
            boolean fatto = backUp.exportDb();
            if (fatto) {
                Toast.makeText(context, "Backup Successful!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(context, "Backup non riuscito", Toast.LENGTH_SHORT).show();
            }

            //backUp.encryptDB();
        }
    }


    public void controllaPermessi(){
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {


            // No explanation needed, we can request the permission.

            ActivityCompat.requestPermissions(this,
                    new String[]{ Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    CREA_BACKUP);
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    CARICA_BACKUP);
            // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
            // app-defined int constant. The callback method gets the
            // result of the request.
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case CREA_BACKUP: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "permesso accordato\n riprova", Toast.LENGTH_SHORT).show();
                    permesso=true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Toast.makeText(this, "permesso negato", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    permesso=false;
                }
                break;
            }
            case CARICA_BACKUP: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    Toast.makeText(this, "permesso accordato \n riprova", Toast.LENGTH_SHORT).show();
                    permesso=true;
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.

                } else {

                    Toast.makeText(this, "permesso negato", Toast.LENGTH_SHORT).show();
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    permesso=false;
                }
                break;
            }

            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
    public void prendiPath(){
        Intent intent= new Intent(Intent.ACTION_OPEN_DOCUMENT);
        //intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("*/*");
        startActivityForResult(intent, READ_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent resultData){
        if(requestCode==READ_REQUEST_CODE && resultCode==Activity.RESULT_OK){
            Uri uri=null;
            if(resultData!=null){
                uri=resultData.getData();
                File file= new File(uri.getPath());
                String []split = file.getPath().split(":");

                Log.i("backupath", uri.toString());
                Log.i("backupath", file.getAbsolutePath());

                Log.i("backupath", split[1] );

              //  backupPath=uri.getPath();
                backupPath=split[1];
                controllaPermessi();

                if(permesso) {

                    if(backupPath!=null||!backupPath.equals("")) {
                        BackUp backUp = new BackUp(this, user);

                        boolean fatto = backUp.importDB(backupPath);

                        if (fatto) {
//                   backUp.encryptDB();

                            Toast.makeText(this, "Backup Successful!", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(this, "Backup non riuscito", Toast.LENGTH_SHORT).show();
                        }
                    }else{
                        Toast.makeText(this, "percorso Backup errato", Toast.LENGTH_SHORT).show();
                    }

                }
            }
        }
    }


}


