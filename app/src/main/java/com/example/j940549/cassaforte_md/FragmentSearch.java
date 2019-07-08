package com.example.j940549.cassaforte_md;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Model.MyAdapter;
import com.example.j940549.cassaforte_md.Model.RowGen;
import com.example.j940549.cassaforte_md.PwFinanza.FragmentChooseAppFin;
import com.example.j940549.cassaforte_md.PwFinanza.ViewRowPwFinanza;
import com.example.j940549.cassaforte_md.PwFinanza.ViewRowPwFinanzaGen;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSearch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearch extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static Set<String> imageAggiunte=new HashSet<>();
    ArrayList<RowGen> myDataset =new ArrayList<>();
    protected RecyclerView mRecyclerView;
    protected RecyclerView.Adapter mAdapter;
    private RecyclerView.LayoutManager mLinearLayoutManager;

    private  String user, query;

    public FragmentSearch() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentSearch newInstance(String user, String query) {
        FragmentSearch fragment = new FragmentSearch();
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("query", query);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user= getArguments().getString("user");
            query= getArguments().getString("query");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_search, container, false);

        mRecyclerView = (RecyclerView) view.findViewById(R.id.list_PwSearch);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a Linear grid layout manager
        mLinearLayoutManager = new LinearLayoutManager(getActivity());
        mRecyclerView.setLayoutManager(mLinearLayoutManager);

        // specify an adapter (see also next example)

        caricaImageAggiunteSearch();
        caricadatiSearch(query);

        mAdapter = new MyAdapter(myDataset, getActivity(), user);

        mRecyclerView.setAdapter(mAdapter);// Inflate the layout for this fragment


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
        /*if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }*/
    }

    @Override
    public void onDetach() {
        super.onDetach();
    //    mListener = null;
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
      //  void onFragmentInteraction(Uri uri);
    }


    public void caricaImageAggiunteSearch(){
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



    private void vaiaRowFinanza(TextView idbanca, int imageg) {

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
    public RecyclerView getmRecyclerView(){
        return mRecyclerView;
    }

    public void caricadatiSearch(String a_query) {
        myDataset.clear();
        DBLayer dbLayer = null;

        try {
            dbLayer = new DBLayer(getActivity());
            dbLayer.open();
            Cursor []cursors = dbLayer.getOneDataSearch(a_query);

            Log.d("query", "cursor .."+cursors.length);

//            if (cursors[0].getCount() > 0||cursors[1].getCount() > 0||cursors[2].getCount() > 0) {
                if (cursors.length > 0) {


                for (int i = 0; i <= cursors.length - 1; i++) {
                    Log.d("query", "cursor ..[" + i + "]");
                    Log.d("query", "cursor ..[" + i + "].length.." + cursors[i].getCount());
                    if (cursors[i].getCount() > 0) {
                        cursors[i].moveToPosition(0);
                        do {
                            RowGen rowGen = new RowGen();
                            switch (i) {
                                case  0:
                                rowGen.setTipo("pwLavoro-bd");
                                break;

                                case 1:

                                rowGen.setTipo("pwPersonali");
                                break;
                                case 2:

                                rowGen.setTipo("pwFinanziarie");
                                break;
                            }

                            rowGen.setId(cursors[i].getString(0));
                            rowGen.setNomeApp(cursors[i].getString(2));
                            if (cursors[i].getString(2).equalsIgnoreCase("B.N.L.")) {
                                rowGen.setResourceImage(R.drawable.bnl);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Mediolanum")) {
                                rowGen.setResourceImage(R.drawable.mediolanum);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Conto Arancio")) {
                                rowGen.setResourceImage(R.drawable.contoarancio);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("PostePay")) {
                                rowGen.setResourceImage(R.drawable.postepay);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Pay Pal")) {
                                rowGen.setResourceImage(R.drawable.paypal);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Unicredit")) {
                                rowGen.setResourceImage(R.drawable.unicredit);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Poste Italiane")) {
                                rowGen.setResourceImage(R.drawable.posteitaliane);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Unicredit")) {
                                rowGen.setResourceImage(R.drawable.unicredit);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("B.C.C.")) {
                                rowGen.setResourceImage(R.drawable.bcc);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Intesa S.Paolo")) {
                                rowGen.setResourceImage(R.drawable.intesa);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Banco Popolare")) {
                                rowGen.setResourceImage(R.drawable.banco_popolare);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("M.P.S.")) {
                                rowGen.setResourceImage(R.drawable.mps);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Telemaco")) {
                                rowGen.setResourceImage(R.drawable.telemaco);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Sister")) {
                                rowGen.setResourceImage(R.drawable.sister);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("SDI")) {
                                rowGen.setResourceImage(R.drawable.sdi);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("WebAT")) {
                                rowGen.setResourceImage(R.drawable.web_at);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("AT3270")) {
                                rowGen.setResourceImage(R.drawable.at3270);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Comune Anzio")) {
                                rowGen.setResourceImage(R.drawable.comune_anzio);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Comune Nettuno")) {
                                rowGen.setResourceImage(R.drawable.comune_nettuno);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Molecola")) {
                                rowGen.setResourceImage(R.drawable.molecola);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("NoiPa")) {
                                rowGen.setResourceImage(R.drawable.noipa);
                            }else if (cursors[i].getString(2).equalsIgnoreCase("Pec")) {
                                rowGen.setResourceImage(R.drawable.pec);
                            }else if (cursors[i].getString(2).equalsIgnoreCase("Iride_WebMail")) {
                                rowGen.setResourceImage(R.drawable.gdfmai);
                            }else if (cursors[i].getString(2).equalsIgnoreCase("Tim")) {
                                rowGen.setResourceImage(R.drawable.tim);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Vodafone")) {
                                rowGen.setResourceImage(R.drawable.vodafone);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Tre-Wind")) {
                                rowGen.setResourceImage(R.drawable.tre_wind);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("FastWeb")) {
                                rowGen.setResourceImage(R.drawable.fastweb);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("ItaliaOnline")) {
                                rowGen.setResourceImage(R.drawable.italiaonline);
                            }else  if (cursors[i].getString(2).equalsIgnoreCase("Libero Mail")) {
                                rowGen.setResourceImage(R.drawable.liberomail);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("GDF Mail")) {
                                rowGen.setResourceImage(R.drawable.gdfmai);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Registro Elet")) {
                                rowGen.setResourceImage(R.drawable.regelet);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("INPS")) {
                                rowGen.setResourceImage(R.drawable.inps);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("TIM Vision")) {
                                rowGen.setResourceImage(R.drawable.timvision);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Polimi")) {
                                rowGen.setResourceImage(R.drawable.polimi);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Cassetto Trib.rio")) {
                                rowGen.setResourceImage(R.drawable.cassettotribuatrio);
                            }else if (cursors[i].getString(2).equalsIgnoreCase("E-mail")) {
                                rowGen.setResourceImage(R.drawable.email);
                            } else if (cursors[i].getString(2).equalsIgnoreCase("Facebook")) {
                                rowGen.setResourceImage(R.drawable.facebook);
                            }else if (cursors[i].getString(2).equalsIgnoreCase("Ebay")) {
                                rowGen.setResourceImage(R.drawable.ebay);
                            }else if (cursors[i].getString(2).equalsIgnoreCase("Amazon")) {
                                rowGen.setResourceImage(R.drawable.amazon);
                            }else if (cursors[i].getString(2).equalsIgnoreCase("Google")) {
                                rowGen.setResourceImage(R.drawable.google);
                            }else if (cursors[i].getString(2).equalsIgnoreCase("G.S.E.")) {
                                rowGen.setResourceImage(R.drawable.gse);
                            }else {

                                for (String nomeApp : imageAggiunte) {
                                    Log.d("image aggiunta", nomeApp + "----" + cursors[i].getString(2));
                                    if (nomeApp.contains(cursors[i].getString(2))) {
                                        Log.d("image aggiunta", nomeApp + "----" + cursors[i].getString(2));
                                        rowGen.setPatchImage(nomeApp);
                                        break;
                                    }
                                }
                                if (rowGen.getPatchImage().equals("")) {
                                    rowGen.setResourceImage(R.drawable.luccetto);
                                }
                            }
                            myDataset.add(rowGen);
                        } while (cursors[i].moveToNext());
                    }
                }
            }
        } catch (SQLException ex) {
            Toast.makeText(getActivity(), "" + ex.toString(), Toast.LENGTH_SHORT).show();
        Log.d("query", ex.toString());
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
