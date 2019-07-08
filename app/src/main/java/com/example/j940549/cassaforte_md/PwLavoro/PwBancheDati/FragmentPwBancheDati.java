package com.example.j940549.cassaforte_md.PwLavoro.PwBancheDati;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Model.MyAdapter;
import com.example.j940549.cassaforte_md.Model.RowGen;
import com.example.j940549.cassaforte_md.PwLavoro.PwGestori.FragmentNewPwGestori;
import com.example.j940549.cassaforte_md.PwPersonale.ViewRowPwPersonale;
import com.example.j940549.cassaforte_md.PwPersonale.ViewRowPwPersonaleGen;
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public class FragmentPwBancheDati extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static Set<String> imageAggiunte=new HashSet<>();
    ArrayList<RowGen> myDataset =new ArrayList<>();
    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private  String user;

    public FragmentPwBancheDati() {
        // Required empty public constructor
    }


    public static FragmentPwBancheDati newInstance(String user) {
        FragmentPwBancheDati fragment = new FragmentPwBancheDati();
        Bundle args = new Bundle();
        args.putString("user", user);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user= getArguments().getString("user");
        }

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view= inflater.inflate(R.layout.fragment_pw_banchedati, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_PwBancheDati);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a Linear grid layout manager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // specify an adapter (see also next example)

        caricaImageAggiunteBancheDati();
        caricaDatiLavoroBancheDati();

        mAdapter = new MyAdapter(myDataset, getActivity(), user);

        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apriChooseNewAppBancheDati();
            }
        });


        return view;

    }

    public void apriPopup(View v, final String idApp, final String nomeApp){
        android.support.v7.widget.PopupMenu popupMenu=new android.support.v7.widget.PopupMenu(getActivity(),v);
        MenuInflater menuInflater=popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_popup,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DBLayer dbLayer=null;

                try {
                    dbLayer=new DBLayer(getActivity());
                    dbLayer.open();
                    dbLayer.deleteDataLavoro(idApp);
                }catch (SQLException ex){
                    Toast.makeText(getActivity(), ""+ex.toString(), Toast.LENGTH_SHORT).show();
                    return false;
                }
                Toast.makeText(getActivity(), "dato cancellato!", Toast.LENGTH_SHORT).show();

                dbLayer.close();

                try {
                    File file = new File(getActivity().getExternalFilesDir(null), nomeApp + ".jpg");
                    if (file != null) {
                        file.delete();
                        if(imageAggiunte.contains(nomeApp+".jpg")){
                            imageAggiunte.remove(nomeApp+".jpg");
                        }
                    }
                }catch (Exception e){
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_SHORT).show();
                }

                return true;
            }

        });
        popupMenu.show();
    }

    /*private String getAlbumName() {
        return  getString(R.string.album_name)+"/BANCHE_DATI";
    }*/
    public void caricaImageAggiunteBancheDati(){
        //File storageDir =  new File(getExternalFilesDir(Environment.DIRECTORY_PICTURES),getAlbumName());
        File storageDir= getActivity().getExternalFilesDir(null);
        File[] file=storageDir.listFiles();

        if(file!=null) {
            for (File f : file) {
                if (f.isDirectory()) {
                    File[] innerFiles = f.listFiles();

                    for (int i = 0; i < innerFiles.length; i++) {
                        Log.d("Name", innerFiles[i].getPath() + "");
                    }
                }
                if (f.isFile()) {
                    imageAggiunte.add(f.getAbsolutePath());
                }


            }
        }
    }


    private void apriChooseNewAppBancheDati() {
        Fragment fragment=FragmentChooceAppBancheDati.newInstance(user);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
    }



    private void vaiaRowLavoro(TextView unidApplicazione, int imageControll) {
        if (imageControll == R.drawable.luccetto||imageControll == 0) {
            Intent vaiaPwLavoroGen = new Intent(getActivity(), ViewRowPwBancheDatiGen.class);
            //Toast.makeText(this, ""+unidApplicazione.getText().toString(), Toast.LENGTH_SHORT).show();

            vaiaPwLavoroGen.putExtra("idApplicazione", unidApplicazione.getText().toString());
            vaiaPwLavoroGen.putExtra("user",user);
            startActivity(vaiaPwLavoroGen);
            getActivity().finish();
        } else {
            Intent vaiaPwLavoro = new Intent(getActivity(), ViewRowPwBancheDati.class);
            //Toast.makeText(this, ""+unidApplicazione.getText().toString(), Toast.LENGTH_SHORT).show();

            vaiaPwLavoro.putExtra("idApplicazione", unidApplicazione.getText().toString());
            vaiaPwLavoro.putExtra("user",user);
            getActivity().startActivity(vaiaPwLavoro);
        }
    }

    public void caricaDatiLavoroBancheDati(){

        DBLayer dbLayer=null;

        try {
            dbLayer=new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getAllDataLavoroBancheDati(user);

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);

                do {
                    RowGen rowGen = new RowGen();
                    rowGen.setTipo("pwLavoro-bd");
                    rowGen.setId(cursor.getString(0));
                    rowGen.setNomeApp(cursor.getString(2));
                    if (cursor.getString(2).equalsIgnoreCase("Telemaco")) {
                        rowGen.setResourceImage(R.drawable.telemaco);
                    } else if (cursor.getString(2).equalsIgnoreCase("Sister")) {
                        rowGen.setResourceImage(R.drawable.sister);
                    } else if (cursor.getString(2).equalsIgnoreCase("SDI")) {
                        rowGen.setResourceImage(R.drawable.sdi);
                    } else if (cursor.getString(2).equalsIgnoreCase("WebAT")) {
                        rowGen.setResourceImage(R.drawable.web_at);
                    } else if (cursor.getString(2).equalsIgnoreCase("AT3270")) {
                        rowGen.setResourceImage(R.drawable.at3270);
                    } else if (cursor.getString(2).equalsIgnoreCase("Comune Anzio")) {
                        rowGen.setResourceImage(R.drawable.comune_anzio);
                    } else if (cursor.getString(2).equalsIgnoreCase("Comune Nettuno")) {
                        rowGen.setResourceImage(R.drawable.comune_nettuno);
                    } else if (cursor.getString(2).equalsIgnoreCase("Molecola")) {
                        rowGen.setResourceImage(R.drawable.molecola);
                    } else if (cursor.getString(2).equalsIgnoreCase("NoiPa")) {
                        rowGen.setResourceImage(R.drawable.noipa);
                    }else if (cursor.getString(2).equalsIgnoreCase("Pec")) {
                        rowGen.setResourceImage(R.drawable.pec);
                    }else if (cursor.getString(2).equalsIgnoreCase("Iride_WebMail")) {
                        rowGen.setResourceImage(R.drawable.gdfmai);
                    }
                    else {
                        for (String nomeApp : imageAggiunte) {
                            Log.d("image aggiunta", nomeApp+"----"+cursor.getString(2));
                            if (nomeApp.contains(cursor.getString(2))) {
                                Log.d("image aggiunta", nomeApp+"----"+cursor.getString(2));
                                rowGen.setPatchImage(nomeApp);
                                break;
                            }
                        }
                        if (rowGen.getPatchImage().equals("")) {
                            rowGen.setResourceImage(R.drawable.luccetto);
                        }
                    }
                    myDataset.add(rowGen);
                } while (cursor.moveToNext());
            }
        }catch (SQLException ex){
            Toast.makeText(getActivity(), ""+ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();


    }



}
