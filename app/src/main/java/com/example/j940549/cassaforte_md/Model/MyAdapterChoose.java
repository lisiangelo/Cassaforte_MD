package com.example.j940549.cassaforte_md.Model;

import android.content.Context;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.j940549.cassaforte_md.R;

/**
 * Created by J940549 on 23/04/2017.
 */

public class MyAdapterChoose extends ArrayAdapter {
    int [] imageArray;
    String [] applicazioni;

    public MyAdapterChoose(Context context,  String[]applicazione, int[]image ){
        super(context, R.layout.list_view_chooseapp, R.id.titoloApplicChoose,applicazione);
        imageArray=image;

        applicazioni=applicazione;

    }
    @NonNull
    @Override
    public View getView(int position, View convertView, ViewGroup parent){
        //inflate layout
        LayoutInflater inflater= (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View row=inflater.inflate(R.layout.list_view_chooseapp,parent,false);
        //prendi i reference agli oggetti della View
        ImageView imageView= (ImageView) row.findViewById(R.id.idimageChoose);
        //TextView textViewtit= (TextView) row.findViewById(R.id.titoloApplic);
        TextView textViewapp= (TextView) row.findViewById(R.id.titoloApplicChoose);

        //sostituisci gli elementi con l'array specificato alla propria posizione
        imageView.setImageResource(imageArray[position]);
        //textViewtit.setText(titoli[position]);
        textViewapp.setText(applicazioni[position]);
        return row;
    }
}
