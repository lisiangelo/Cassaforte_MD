package com.example.j940549.cassaforte_md.PwLavoro;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.SQLException;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
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
import com.example.j940549.cassaforte_md.PwLavoro.PwBancheDati.FragmentPwBancheDati;
import com.example.j940549.cassaforte_md.PwLavoro.PwGestori.FragmentPwGestori;
import com.example.j940549.cassaforte_md.PwPersonale.ViewRowPwPersonale;
import com.example.j940549.cassaforte_md.PwPersonale.ViewRowPwPersonaleGen;
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;



public class FragmentPwlavoro extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private SectionsPagerAdapter mSectionsPagerAdapter;
    private ViewPager mViewPager;
    private  String user;


    public FragmentPwlavoro() {
        // Required empty public constructor

    }


    public static FragmentPwlavoro newInstance(String user) {
        FragmentPwlavoro fragment = new FragmentPwlavoro();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_pw_lavoro,container,false);

        mSectionsPagerAdapter = new SectionsPagerAdapter(getChildFragmentManager(),user);

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) view.findViewById(R.id.container_pwlavoro);
        mViewPager.setAdapter(mSectionsPagerAdapter);

        TabLayout tabLayout = (TabLayout) view.findViewById(R.id.tabs_pwlavoro);
        tabLayout.setupWithViewPager(mViewPager);

        return view;
    }


    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {
        private String user;

        public SectionsPagerAdapter(FragmentManager fm, String user) {
            super(fm);
            this.user=user;

        }

        @Override
        public Fragment getItem(int position) {

            //Fragment fragment = null;
            switch (position) {
                case 0: {

                    FragmentPwBancheDati fragmentPwBancheDati = FragmentPwBancheDati.newInstance(user);

                    return fragmentPwBancheDati ;
                    //break;
                }
                case 1: {
                    FragmentPwGestori fragmentPwGestori =  FragmentPwGestori.newInstance(user);

                    return fragmentPwGestori ;
                    //break;
                }
                default:
                    Fragment fragment=null;
                    return fragment;

            }


        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {


                case 0:
                    return "BANCHE DATI";

                    case 1:
                    return "GESTORI";

            }
            return null;
        }
    }

}