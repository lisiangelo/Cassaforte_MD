package com.example.j940549.cassaforte_md;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.j940549.cassaforte_md.PwFinanza.FragmentPwFinanziarie;
import com.example.j940549.cassaforte_md.PwLavoro.FragmentPwlavoro;
import com.example.j940549.cassaforte_md.PwPersonale.FragmentPwPersonali;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentStart.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentStart#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentStart extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private String user, qualefragment;

    public FragmentStart() {
        // Required empty public constructor
    }

    // TODO: Rename and change types and number of parameters
    public static FragmentStart newInstance(String user, String qualefragment ) {
        FragmentStart fragment = new FragmentStart();
        Bundle args = new Bundle();
        args.putString("user", user);
        args.putString("qualefragment", qualefragment);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            user= getArguments().getString("user");
            qualefragment=getArguments().getString("qualefragment");

        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view=inflater.inflate(R.layout.fragment_start, container, false);

        ImageButton buttonLav=view.findViewById(R.id.iconLavoro);
        ImageButton buttonPers=view.findViewById(R.id.iconPersonale);
        ImageButton buttonFin=view.findViewById(R.id.iconFinanziarie);

        buttonLav.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=FragmentPwlavoro.newInstance(user);
                qualefragment="pwLavoro";
                getActivity().setTitle("Passwords Lavoro");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
            }
        });

        buttonFin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=FragmentPwFinanziarie.newInstance(user);
                qualefragment="pwFinanziarie";
                getActivity().setTitle("Passwords Finanziarie");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
            }
        });
        buttonPers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Fragment fragment=FragmentPwPersonali.newInstance(user);
                qualefragment="pwPersonali";
                getActivity().setTitle("Passwords Personali");
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.flContent_utente, fragment).commit();
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
        void onFragmentInteraction(Uri uri);
    }
}
