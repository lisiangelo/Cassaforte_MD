package com.example.j940549.cassaforte_md.PwFinanza;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Model.MyAdapter;
import com.example.j940549.cassaforte_md.Model.RowGen;
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPwFinanziarie.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPwFinanziarie#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPwFinanziarie extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static Set<String> imageAggiunte=new HashSet<>();
    ArrayList<RowGen> myDataset =new ArrayList<>();
    private SearchView searchView;
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;

    private  String user;


    public FragmentPwFinanziarie() {
        // Required empty public constructor
    }


    public static FragmentPwFinanziarie newInstance(String user) {
        FragmentPwFinanziarie fragment = new FragmentPwFinanziarie();
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
        View view= inflater.inflate(R.layout.fragment_pw_finanziarie, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_PwFinanziarie);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a Linear grid layout manager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // specify an adapter (see also next example)

        caricaImageAggiunteFinanza();
        caricadatiFinanza();

        mAdapter = new MyAdapter(myDataset, getActivity(), user);

        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment


        FloatingActionButton fab= view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apriChooseNewAppFinanza();
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

    public void caricaImageAggiunteFinanza(){
        File storageDir =  getActivity().getBaseContext().getExternalFilesDir(null);//,getAlbumName());
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


    private void apriChooseNewAppFinanza() {

        FragmentChooseAppFin fragmentChooseAppFin = FragmentChooseAppFin.newInstance(user);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragmentChooseAppFin).commit();
    }

    /*private void vaiaRowFinanza(TextView idbanca, int imageg) {

        Log.i("nr.image",""+imageg);
        Log.i("nr. LUCCHETTO",""+R.drawable.luccetto);

        if(imageg== R.drawable.luccetto||imageg== 0) {
            Intent vaiaPwFinanzaGen = new Intent(getActivity(), ViewRowPwFinanzaGen.class);
            //Toast.makeText(ViewFinanza.this, ""+idbanca.getText().toString(), Toast.LENGTH_SHORT).show();
            vaiaPwFinanzaGen.putExtra("idBanca", idbanca.getText().toString());
            vaiaPwFinanzaGen.putExtra("imageAggiunte", idbanca.getText().toString());
            vaiaPwFinanzaGen.putExtra("user",user);
            getActivity().startActivity(vaiaPwFinanzaGen);
            getActivity().finish();
        }else{
            Intent vaiaPwFinanza = new Intent(getActivity(), ViewRowPwFinanza.class);
            //Toast.makeText(ViewFinanza.this, ""+idbanca.getText().toString(), Toast.LENGTH_SHORT).show();
            vaiaPwFinanza.putExtra("idBanca", idbanca.getText().toString());
            vaiaPwFinanza.putExtra("user",user);
            getActivity().startActivity(vaiaPwFinanza);
            //finish();
        }
    }

    */

    public RecyclerView getmRecyclerView(){
        return mRecyclerView;
    }


    public void caricadatiFinanza() {

        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getAllDataFinanza(user);

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    RowGen rowGen = new RowGen();
                    rowGen.setTipo("pwFinanziarie");
                    rowGen.setId(cursor.getString(0));
                    rowGen.setNomeApp(cursor.getString(2));
                    if (cursor.getString(2).equalsIgnoreCase("B.N.L.")) {
                        rowGen.setResourceImage(R.drawable.bnl);
                    } else if (cursor.getString(2).equalsIgnoreCase("Mediolanum")) {
                        rowGen.setResourceImage(R.drawable.mediolanum);
                    } else if (cursor.getString(2).equalsIgnoreCase("Conto Arancio")) {
                        rowGen.setResourceImage(R.drawable.contoarancio);
                    } else if (cursor.getString(2).equalsIgnoreCase("PostePay")) {
                        rowGen.setResourceImage(R.drawable.postepay);
                    } else if (cursor.getString(2).equalsIgnoreCase("Pay Pal")) {
                        rowGen.setResourceImage(R.drawable.paypal);
                    } else if (cursor.getString(2).equalsIgnoreCase("Unicredit")) {
                        rowGen.setResourceImage(R.drawable.unicredit);
                    } else if (cursor.getString(2).equalsIgnoreCase("Poste Italiane")) {
                        rowGen.setResourceImage(R.drawable.posteitaliane);
                    } else if (cursor.getString(2).equalsIgnoreCase("Unicredit")) {
                        rowGen.setResourceImage(R.drawable.unicredit);
                    } else if (cursor.getString(2).equalsIgnoreCase("B.C.C.")) {
                        rowGen.setResourceImage(R.drawable.bcc);
                    } else if (cursor.getString(2).equalsIgnoreCase("Intesa S.Paolo")) {
                        rowGen.setResourceImage(R.drawable.intesa);
                    } else if (cursor.getString(2).equalsIgnoreCase("Banco Popolare")) {
                        rowGen.setResourceImage(R.drawable.banco_popolare);
                    } else if (cursor.getString(2).equalsIgnoreCase("M.P.S.")) {
                        rowGen.setResourceImage(R.drawable.mps);
                    } else {
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
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
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

    public void apriPopup(View v){
        PopupMenu popupMenu=new PopupMenu(getActivity(),v);
        MenuInflater menuInflater= (MenuInflater) popupMenu.getMenu();
        menuInflater.inflate(R.menu.menu_popup,popupMenu.getMenu());
        popupMenu.show();
    }



}
