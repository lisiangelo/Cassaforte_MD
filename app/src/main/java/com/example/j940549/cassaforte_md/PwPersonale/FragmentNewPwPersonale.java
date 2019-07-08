package com.example.j940549.cassaforte_md.PwPersonale;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.example.j940549.cassaforte_md.MainActivity;
import com.example.j940549.cassaforte_md.PwFinanza.FragmentPwFinanziarie;
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentNewPwPersonale.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentNewPwPersonale#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentNewPwPersonale extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    EditText editnomeApplicazione,editEmail;
    EditText editnomeUtente;
    EditText editpassword;
    EditText editpin;
    EditText editurl;
    EditText editnote;
    String nomeApp;
    private String user;
    private String SECURITYKEY;
    // TODO: Rename and change types of parameters


    public static FragmentNewPwPersonale newInstance(String user, String nomeApp) {
        FragmentNewPwPersonale fragment = new FragmentNewPwPersonale();
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("nomeApp", nomeApp);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString("user");
            nomeApp= getArguments().getString("nomeApp");
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
        final View view= inflater.inflate(R.layout.fragment_new_pw_personale, container, false);

        editnomeApplicazione= (EditText) view.findViewById(R.id.nome_applicazionenewPer);
        if(!nomeApp.equals("Nuova Applicazione")) {
            editnomeApplicazione.setText(nomeApp);
        }

        BottomNavigationView btnindietro = view.findViewById(R.id.bottom_navigation);
        btnindietro.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_backnavigation:
                        Fragment fragment = FragmentPwPersonali.newInstance(user);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
                        break;
                    case R.id.action_inserisci:
                        try {
                            inserisciNewPwPersonale(view);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }



                return true;
            }

        });

        /*Button buttonInsert= (Button) view.findViewById(R.id.inserisciNewPwPersonale);
        buttonInsert.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    inserisciNewPwPersonale(view);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        Button buttonCanc= (Button) view.findViewById(R.id.annullaNewPersonale);
        buttonCanc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Fragment fragment=FragmentPwPersonali.newInstance(user);
                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
*/
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


    private void inserisciNewPwPersonale(View view) throws Exception {
        editnomeApplicazione= (EditText) view.findViewById(R.id.nome_applicazionenewPer);
        editEmail= (EditText) view.findViewById(R.id.emailnewPer);
        editnomeUtente= (EditText) view.findViewById(R.id.nome_utentenewPer);
        editpassword= (EditText) view.findViewById(R.id.passwordnewPer);
        editpin= (EditText) view.findViewById(R.id.pinnewPer);
        editurl= (EditText) view.findViewById(R.id.urlnewPer);
        editnote= (EditText) view.findViewById(R.id.notenewPer);
        String nomeApplicazione=editnomeApplicazione.getText().toString();
        String email=editEmail.getText().toString();
        if (email.equals("")){email="-";}
        String nomeUtente=editnomeUtente.getText().toString();
        String passwordinchiaro=editpassword.getText().toString();
        Crypto crypto= new Crypto(SECURITYKEY.getBytes());
        String password = crypto.encrypt(passwordinchiaro.getBytes());
        String pin=editpin.getText().toString();
        if (pin.equals("")){pin="-";}
        String url=editurl.getText().toString();
        if (url.equals("")){url="-";}
        String note=editnote.getText().toString();
        if (note.equals("")){note="-";}
        if (!nomeApplicazione.equals("")&&!nomeUtente.equals("")&&!password.equals("")) {

            DBLayer dbLayer=new DBLayer(getActivity());
            dbLayer.open();

            boolean inserito=dbLayer.inserisciNewPwPersonale(user,nomeApplicazione,email, nomeUtente, password,
                    pin, url, note);
            if(inserito) {
                Toast.makeText(getActivity(), "Nuovo dato inserito", Toast.LENGTH_SHORT).show();
                Fragment fragment=FragmentPwPersonali.newInstance(user);
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
