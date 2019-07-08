package com.example.j940549.cassaforte_md.PwLavoro.PwGestori;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.TextView;

import com.example.j940549.cassaforte_md.Model.MyAdapterChoose;
import com.example.j940549.cassaforte_md.PwLavoro.FragmentPwlavoro;
import com.example.j940549.cassaforte_md.PwPersonale.FragmentNewPwPersonale;
import com.example.j940549.cassaforte_md.PwPersonale.FragmentPwPersonali;
import com.example.j940549.cassaforte_md.R;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentChooseAppGestori.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentChooseAppGestori#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentChooseAppGestori extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    String[] applicazioni={"Nuova Applicazione","Tim","Vodafone","Tre-Wind","Fastweb","ItaliOnLine"};

    int[] image={R.drawable.luccetto,R.drawable.tim,R.drawable.vodafone,R.drawable.tre_wind,R.drawable.fastweb,
            R.drawable.italiaonline};
    private String user;

    public FragmentChooseAppGestori() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentChooseAppGestori newInstance(String user) {
        FragmentChooseAppGestori fragment = new FragmentChooseAppGestori();
        Bundle args = new Bundle();
        args.putString("user", user);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user = getArguments().getString("user");

        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_choose_app_gestori, container, false);

        BottomNavigationView btnindietro = view.findViewById(R.id.bottom_navigation);
        btnindietro.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.action_backnavigation:
                        Fragment fragment = FragmentPwlavoro.newInstance(user);
                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
                        break;
                }
                return true;
            }

        });

        GridView gridView = (GridView) view.findViewById(R.id.ListView_chooseAppGestori);
        MyAdapterChoose myAdapter = new MyAdapterChoose(getActivity(), applicazioni,image);
        gridView.setAdapter(myAdapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView nomeApp= (TextView) view.findViewById(R.id.titoloApplicChoose);
                vaiaNewRowLavoro(nomeApp);

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
//        void onFragmentInteraction(Uri uri);
    }

    private void vaiaNewRowLavoro(TextView nomApp){

        Fragment fragment=FragmentNewPwGestori.newInstance(user,nomApp.getText().toString(),"tel");
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
    }


}
