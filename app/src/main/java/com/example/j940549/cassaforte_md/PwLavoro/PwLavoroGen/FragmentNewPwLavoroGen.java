package com.example.j940549.cassaforte_md.PwLavoro.PwLavoroGen;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.Crypto.Crypto;
import com.example.j940549.cassaforte_md.LoginActivity;
import com.example.j940549.cassaforte_md.PwLavoro.FragmentPwlavoro;
import com.example.j940549.cassaforte_md.PwLavoro.PwBancheDati.FragmentNewPwBancheDati;
import com.example.j940549.cassaforte_md.PwLavoro.PwGestori.FragmentPwGestori;
import com.example.j940549.cassaforte_md.PwPersonale.FragmentPwPersonali;
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;
import com.example.j940549.cassaforte_md.UsoPhotoCamera.PhotoIntentActivity;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentNewPwLavoroGen.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentNewPwLavoroGen#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNewPwLavoroGen extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    String nomeUtente="", idapp,nomeApplicazione;
    String password="";
    String pin="";
    String pin2="";
    String note="";String url="";
    Button btnPhotoActivity;
    EditText editnomeApplicazione;
    EditText editnomeUtente;
    EditText editpassword;
    EditText editpin;
    EditText editpin2;
    EditText editurl;
    EditText editnote;
    String nomeApp;
    private String user;
    private String tipo;
    private String SECURITYKEY;

    public FragmentNewPwLavoroGen() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentNewPwLavoroGen newInstance(String user, String applicazione, String tipo) {
        FragmentNewPwLavoroGen fragment = new FragmentNewPwLavoroGen();
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("applicazione", applicazione);
        args.putString("tipo", tipo);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString("user");
            nomeApp= getArguments().getString("applicazione");
            tipo = getArguments().getString("tipo");
        }
//SharedPreferences sharedPref = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences sharedPref = getContext().getSharedPreferences("CassafortePreference", Context.MODE_PRIVATE);
        String key=sharedPref.getString("securityKey", "");
        Log.i("securKEY_code", key);
        SECURITYKEY=new String(Base64.decode(key,Base64.DEFAULT));
        Log.i("securKEY_decode", SECURITYKEY);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        final View view=inflater.inflate(R.layout.fragment_new_pw_lavoro_gen, container, false);

        editnomeApplicazione= (EditText) view.findViewById(R.id.nome_applicazionenew);
        if(!nomeApp.equals("Nuova Applicazione")) {
            editnomeApplicazione.setText(nomeApp);
        }

        BottomNavigationView btnindietro = view.findViewById(R.id.bottom_navigation);
        btnindietro.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_backnavigation:
                        Fragment fragment=FragmentPwLavoroGen.newInstance(user);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
                        break;
                    case R.id.action_inserisci:
                        try {
                            inserisciNewPwLavoro(view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }



                return true;
            }

        });





        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
/*        if (mListener != null) {
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
        //void onFragmentInteraction(Uri uri);
    }

    public void inserisciNewPwLavoro(View view) throws Exception {
        editnomeApplicazione= (EditText) view.findViewById(R.id.nome_applicazionenew);
        editnomeUtente= (EditText) view.findViewById(R.id.nome_utentenew);
        editpassword= (EditText) view.findViewById(R.id.passwordnew);
        editpin= (EditText) view.findViewById(R.id.pinnew);
        editpin2= (EditText) view.findViewById(R.id._pin2new);
        editurl= (EditText) view.findViewById(R.id.rowurlnew);
        editnote= (EditText) view.findViewById(R.id.notenew);
        String nomeApplicazione=editnomeApplicazione.getText().toString();
        String nomeUtente=editnomeUtente.getText().toString();
        String passwordinchiaro=editpassword.getText().toString();
        Crypto crypto= new Crypto(SECURITYKEY.getBytes());
        String password = crypto.encrypt(passwordinchiaro.getBytes());
        String pin=editpin.getText().toString();
        if (pin.equals("")){pin="-";}
        String pin2=editpin2.getText().toString();
        if (pin2.equals("")){pin2="-";}
        String url= editurl.getText().toString();
        if (url.equals("")){url="-";}
        String note=editnote.getText().toString();
        if (note.equals("")){note="-";}
        if (!nomeApplicazione.equals("")&&!nomeUtente.equals("")&&!password.equals("")) {

            DBLayer dbLayer=new DBLayer(getActivity());
            dbLayer.open();

            boolean inserito=dbLayer.inserisciNewPwLavoro(user,nomeApplicazione, nomeUtente, password,
                    pin, pin2, url, note,tipo);
            if(inserito) {
                Toast.makeText(getActivity(), "Nuovo dato inserito", Toast.LENGTH_SHORT).show();
                /*Intent vaiaLavoro=new Intent(this, ViewBancheDati.class);
                vaiaLavoro.putExtra("user",user);
                startActivity(vaiaLavoro);
                finish();*/

                Fragment fragment=FragmentPwLavoroGen.newInstance(user);
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();


            }else {
                Toast.makeText(getActivity(), "Nessun nuovo dato inserito", Toast.LENGTH_SHORT).show();
            }
            dbLayer.close();
        }else{
            Toast.makeText(getActivity(), "completa  i dati essenziali", Toast.LENGTH_SHORT).show();
        }
    }

}


