package com.example.j940549.cassaforte_md.PwPersonale;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentPwPersonali.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentPwPersonali#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentPwPersonali extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static Set<String> imageAggiunte=new HashSet<>();
    ArrayList<RowGen> myDataset =new ArrayList<>();
    private SearchView searchView;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;

    private  String user;


    public FragmentPwPersonali() {
        // Required empty public constructor
    }


    public static FragmentPwPersonali newInstance(String user) {
        FragmentPwPersonali fragment = new FragmentPwPersonali();
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
        View view= inflater.inflate(R.layout.fragment_pw_personali, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_PwPersonale);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a Linear grid layout manager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // specify an adapter (see also next example)

        caricaImageAggiuntePersonale();
        caricaDatiPersonale();

        mAdapter = new MyAdapter(myDataset, getActivity(), user);

        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment


        FloatingActionButton fab= view.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                apriChooseNewAppPersonale();
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
//        void onFragmentInteraction(Uri uri);
    }

    public void caricaImageAggiuntePersonale(){
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
                    Log.i("Name File", f.getPath() + "");
                }


            }
        }
    }




    private void apriChooseNewAppPersonale() {

        Fragment fragment=FragmentChooseAppPers.newInstance(user);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
        getActivity().setTitle("scegli un titolo");
    }

/*    private void vaiaRowPersonale(TextView unidApplicazione, int imageControll) {
        if (imageControll == R.drawable.luccetto||imageControll == 0) {
            Intent vaiaPwPersonaleGen = new Intent(getActivity(), ViewRowPwPersonaleGen.class);
            //Toast.makeText(this, ""+unidApplicazione.getText().toString(), Toast.LENGTH_SHORT).show();
            vaiaPwPersonaleGen.putExtra("user",user);
            vaiaPwPersonaleGen.putExtra("idApplicazione", unidApplicazione.getText().toString());
            getActivity().startActivity(vaiaPwPersonaleGen);
           // getActivity().finish();
        } else {
            Intent vaiaPwPersonale = new Intent(getActivity(), ViewRowPwPersonale.class);
            // Toast.makeText(this, ""+unidApplicazione.getText().toString(), Toast.LENGTH_SHORT).show();

            vaiaPwPersonale.putExtra("idApplicazione", unidApplicazione.getText().toString());
            vaiaPwPersonale.putExtra("user",user);
            startActivity(vaiaPwPersonale);
            //finish();
        }
    }
*/


    public void caricaDatiPersonale(){

        DBLayer dbLayer=null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor cursor = dbLayer.getAllDataPersona(user);

            if (cursor.getCount() > 0) {
                cursor.moveToPosition(0);
                do {
                    RowGen rowGen = new RowGen();
                    rowGen.setTipo("pwPersonali");
                    rowGen.setId(cursor.getString(0));
                    rowGen.setNomeApp(cursor.getString(2));
                    if (cursor.getString(2).equalsIgnoreCase("Libero Mail")) {
                        rowGen.setResourceImage(R.drawable.liberomail);
                    } else if (cursor.getString(2).equalsIgnoreCase("GDF Mail")) {
                        rowGen.setResourceImage(R.drawable.gdfmai);
                    } else if (cursor.getString(2).equalsIgnoreCase("Registro Elet")) {
                        rowGen.setResourceImage(R.drawable.regelet);
                    } else if (cursor.getString(2).equalsIgnoreCase("INPS")) {
                        rowGen.setResourceImage(R.drawable.inps);
                    } else if (cursor.getString(2).equalsIgnoreCase("TIM Vision")) {
                        rowGen.setResourceImage(R.drawable.timvision);
                    } else if (cursor.getString(2).equalsIgnoreCase("Polimi")) {
                        rowGen.setResourceImage(R.drawable.polimi);
                    } else if (cursor.getString(2).equalsIgnoreCase("Cassetto Trib.rio")) {
                        rowGen.setResourceImage(R.drawable.cassettotribuatrio);
                    }else if (cursor.getString(2).equalsIgnoreCase("E-mail")) {
                        rowGen.setResourceImage(R.drawable.email);
                    } else if (cursor.getString(2).equalsIgnoreCase("Facebook")) {
                        rowGen.setResourceImage(R.drawable.facebook);
                    }else if (cursor.getString(2).equalsIgnoreCase("Ebay")) {
                        rowGen.setResourceImage(R.drawable.ebay);
                    }else if (cursor.getString(2).equalsIgnoreCase("Amazon")) {
                        rowGen.setResourceImage(R.drawable.amazon);
                    }else if (cursor.getString(2).equalsIgnoreCase("Google")) {
                        rowGen.setResourceImage(R.drawable.google);
                    }else if (cursor.getString(2).equalsIgnoreCase("G.S.E.")) {
                        rowGen.setResourceImage(R.drawable.gse);
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
        }catch(SQLException ex){
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        }
        dbLayer.close();

/*        if(rows.size()>0) {
            applicazioni=new String[rows.size()];
            image=new int[rows.size()];
            imagesPatch=new String[rows.size()];
            id=new String[rows.size()];
            int i=0;
            for (RowGen row : rows) {
                applicazioni[i]=row.getNomeApp();
                image[i]=row.getImg();
                imagesPatch[i]=row.getPatchImage();
                id[i]=row.getId();
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
