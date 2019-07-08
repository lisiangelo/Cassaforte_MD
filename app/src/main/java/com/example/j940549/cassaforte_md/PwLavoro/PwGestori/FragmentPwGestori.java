package com.example.j940549.cassaforte_md.PwLavoro.PwGestori;

import android.content.Context;
import android.content.Intent;
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
import com.example.j940549.cassaforte_md.PwFinanza.FragmentChooseAppFin;
import com.example.j940549.cassaforte_md.PwPersonale.ViewRowPwPersonale;
import com.example.j940549.cassaforte_md.PwPersonale.ViewRowPwPersonaleGen;
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPwGestori.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPwGestori#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPwGestori extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static Set<String> imageAggiunte=new HashSet<>();
    ArrayList<RowGen> myDataset =new ArrayList<>();
    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;
    private  String user;


    public FragmentPwGestori() {
        // Required empty public constructor
    }


    public static FragmentPwGestori newInstance(String user) {
        FragmentPwGestori fragment = new FragmentPwGestori();
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
        View view= inflater.inflate(R.layout.fragment_pw_gestori, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_PwGestori);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a Linear grid layout manager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // specify an adapter (see also next example)

        caricaImageAggiunteGestori();
        caricaDatiLavoroGestori();


        mAdapter = new MyAdapter(myDataset, getActivity(), user);

        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment


        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apriChooseNewAppGestori();
            }
        });

        return view;

    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        /*if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }*/
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
/*        if (context instanceof OnFragmentInteractionListener) {
           // mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
        //mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
/*                Intent vaiaGestori=new Intent(getActivity(),ViewGestori.class);
                vaiaGestori.putExtra("user",user);
                startActivity(vaiaGestori);
                finish();*/
                return true;
            }

        });
        popupMenu.show();
    }

    /* private String getAlbumName() {
         return  getString(R.string.album_name)+"/GESTORI";
     }*/
    public void caricaImageAggiunteGestori(){
        // File storageDir =  new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),getAlbumName());
        File storageDir= getActivity().getExternalFilesDir(null);
        File[] file = new File[0];
        file=storageDir.listFiles();

        if(file!=null) {
            for (File f : file) {
                if (f.isDirectory()) {
                    File[] innerFiles = f.listFiles();

                    for (int i = 0; i < innerFiles.length; i++) {
                        Log.i("Name", innerFiles[i].getPath() + "");
                    }
                }
                if (f.isFile()) {
                    imageAggiunte.add(f.getAbsolutePath());
                }


            }
        }
    }

    private void apriChooseNewAppGestori() {

        Fragment fragment=FragmentChooseAppGestori.newInstance(user);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
    }



    /*private void vaiaRowLavoro(TextView unidApplicazione, int imageControll) {
        if (imageControll == R.drawable.luccetto||imageControll == 0) {
            Intent vaiaPwLavoroGen = new Intent(getActivity(), ViewRowPwGestoriGen.class);
            //Toast.makeText(this, ""+unidApplicazione.getText().toString(), Toast.LENGTH_SHORT).show();
            vaiaPwLavoroGen.putExtra("user",user);
            vaiaPwLavoroGen.putExtra("idApplicazione", unidApplicazione.getText().toString());
            //finish();
            startActivity(vaiaPwLavoroGen);
        } else {
            Intent vaiaPwLavoro = new Intent(getActivity(), ViewRowPwGestori.class);

            vaiaPwLavoro.putExtra("idApplicazione", unidApplicazione.getText().toString());
            vaiaPwLavoro.putExtra("user",user);
            startActivity(vaiaPwLavoro);
        }
    }
*/

    public void caricaDatiLavoroGestori(){

        DBLayer dbLayer=null;

        try {
            dbLayer=new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getAllDataLavoroGestori(user);

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);

                do {
                    RowGen rowGen = new RowGen();
                    rowGen.setTipo("pwLavoro-tel");
                    rowGen.setId(cursor.getString(0));
                    rowGen.setNomeApp(cursor.getString(2));
                    if (cursor.getString(2).equalsIgnoreCase("Tim")) {
                        rowGen.setResourceImage(R.drawable.tim);
                    } else if (cursor.getString(2).equalsIgnoreCase("Vodafone")) {
                        rowGen.setResourceImage(R.drawable.vodafone);
                    } else if (cursor.getString(2).equalsIgnoreCase("Tre-Wind")) {
                        rowGen.setResourceImage(R.drawable.tre_wind);
                    } else if (cursor.getString(2).equalsIgnoreCase("FastWeb")) {
                        rowGen.setResourceImage(R.drawable.fastweb);
                    } else if (cursor.getString(2).equalsIgnoreCase("ItaliaOnline")) {
                        rowGen.setResourceImage(R.drawable.italiaonline);
                    }else {
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
/*        if (rows.size() > 0) {
            applicazioni = new String[rows.size()];
            image = new int[rows.size()];
            imagesPatch=new String[rows.size()];
            id = new String[rows.size()];

            int i = 0;
            for (RowGen row : rows) {
                applicazioni[i] = row.getNomeApp();
                image[i] = row.getImg();
                imagesPatch[i]=row.getPatchImage();
                id[i] = row.getId();
                i++;
            }
        }*/

    }

}
