package com.example.j940549.cassaforte_md.Model;


import android.app.Activity;
import android.content.Intent;
import android.database.SQLException;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.j940549.cassaforte_md.MainActivity;
import com.example.j940549.cassaforte_md.PwFinanza.ViewRowPwFinanza;
import com.example.j940549.cassaforte_md.PwFinanza.ViewRowPwFinanzaGen;
import com.example.j940549.cassaforte_md.PwLavoro.PwBancheDati.ViewRowPwBancheDati;
import com.example.j940549.cassaforte_md.PwLavoro.PwBancheDati.ViewRowPwBancheDatiGen;
import com.example.j940549.cassaforte_md.PwLavoro.PwGestori.ViewRowPwGestori;
import com.example.j940549.cassaforte_md.PwLavoro.PwGestori.ViewRowPwGestoriGen;
import com.example.j940549.cassaforte_md.PwLavoro.PwLavoroGen.ViewRowPwLavoro;
import com.example.j940549.cassaforte_md.PwLavoro.PwLavoroGen.ViewRowPwLavoroGen;
import com.example.j940549.cassaforte_md.PwPersonale.ViewRowPwPersonale;
import com.example.j940549.cassaforte_md.PwPersonale.ViewRowPwPersonaleGen;
import com.example.j940549.cassaforte_md.R;
import com.example.j940549.cassaforte_md.SQLite.DBLayer;

import java.io.File;
import java.util.ArrayList;


public class MyAdapter extends RecyclerView.Adapter<MyAdapter.MyViewHolder> {

    private ArrayList<RowGen> passwList;
    private Activity myActivity;
    private String user;


    public class MyViewHolder extends RecyclerView.ViewHolder {//implements View.OnClickListener{
        public TextView nomeApp,  idApp, tipoApp, resourceImg;

        public ImageView imageApp;

        public MyViewHolder(View view) {
            super(view);
            idApp= (TextView) view.findViewById(R.id.idApplic);
            nomeApp = (TextView) view.findViewById(R.id.titoloApplic);
            imageApp=(ImageView)view.findViewById(R.id.idimage);
            tipoApp=(TextView) view.findViewById(R.id.tipoApp);
            resourceImg=(TextView) view.findViewById(R.id.resourceimg);
        }

      /*  @Override
        public void onClick(View view) {
            Toast.makeText(view.getContext(), "This is my Toast message!",
                    Toast.LENGTH_LONG).show();
        }*/
    }


    public MyAdapter(ArrayList<RowGen> passwList, Activity myActivity, String user) {
        this.passwList = passwList;
        this.myActivity=myActivity;
        this.user=user;


        Log.i("list passw personale","dataset.size--"+getItemCount());
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.cardlayout_rows, parent, false);

        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView idApp= view.findViewById(R.id.idApplic);

                String id_App=idApp.getText().toString();
                TextView tipoApp=(TextView) view.findViewById(R.id.tipoApp);
                String tipo_App= tipoApp.getText().toString();
                TextView rscImg=(TextView) view.findViewById(R.id.resourceimg);
                    String resourceImg= rscImg.getText().toString();
                String resourceImgLucchetto="res/drawable-v24/luccetto.jpg";
                Intent vaiaDettaglio=null;
                switch (tipo_App) {

                    case "pwPersonali":
                        if(resourceImg.equals(resourceImgLucchetto)) {
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwPersonaleGen.class);
                        }else{
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwPersonale.class);
                        }
                    break;

                    case "pwLavoro-az":
                        if(resourceImg.equals(resourceImgLucchetto)) {
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwLavoroGen.class);
                        }else{
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwLavoro.class);
                        }
                    break;

                    case "pwFinanziarie":
                        if(resourceImg.equals(resourceImgLucchetto)) {
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwFinanzaGen.class);
                        }else{
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwFinanza.class);
                            }
                    break;

                    case "pwLavoro-tel":
                        if(resourceImg.equals(resourceImgLucchetto)) {
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwGestoriGen.class);
                        }else{
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwGestori.class);
                        }
                    break;
                    case "pwLavoro-bd":
                        if(resourceImg.equals(resourceImgLucchetto)) {
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwBancheDatiGen.class);
                        }else{
                            vaiaDettaglio = new Intent(view.getContext(), ViewRowPwBancheDati.class);

                        }
                    break;
                }

                if(vaiaDettaglio!=null) {

                    vaiaDettaglio.putExtra("idApplicazione", id_App);
                    vaiaDettaglio.putExtra("user", user);
                }else{
                    Toast.makeText(myActivity, "errore su dettaglio", Toast.LENGTH_SHORT).show();
                }

                myActivity.startActivity(vaiaDettaglio);
                myActivity.finish();

            }
        });

        itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                TextView idApplicazione= (TextView) view.findViewById(R.id.idApplic);
                String id_applicazione= idApplicazione.getText().toString();

                TextView titoloApp= (TextView) view.findViewById(R.id.titoloApplic);
                String titolo_App= titoloApp.getText().toString();

                TextView tipoApp=(TextView) view.findViewById(R.id.tipoApp);
                String tipo_App= tipoApp.getText().toString();

                apriPopup(view,id_applicazione,titolo_App,tipo_App);
                return true;
            }
        });


        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        RowGen pwApp= passwList.get(position);
        holder.idApp.setText(pwApp.getId());
        holder.nomeApp.setText(pwApp.getNomeApp());
        if(pwApp.getResourceImage()!=0){
            holder.imageApp.setImageResource(pwApp.getResourceImage());
        }else if(!pwApp.getPatchImage().equals("")) {
            holder.imageApp.setImageBitmap(BitmapFactory.decodeFile(pwApp.getPatchImage()));
        }else{
            holder.imageApp.setImageResource(R.drawable.luccetto);
        }
        holder.tipoApp.setText(pwApp.getTipo());
        try {
            holder.resourceImg.setText(pwApp.getResourceImage());
        }catch (Exception e){
            Log.e("errore risorsa immagine", e.toString());
        }
    }

    @Override
    public int getItemCount() {
        return passwList.size();
    }

    public void apriPopup(View v, final String idApp,final String nomeApp, final String tipo_App  ){
        android.support.v7.widget.PopupMenu popupMenu=new android.support.v7.widget.PopupMenu(myActivity,v);
        MenuInflater menuInflater=popupMenu.getMenuInflater();
        menuInflater.inflate(R.menu.menu_popup,popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new android.support.v7.widget.PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                DBLayer dbLayer=null;

                try {
                    dbLayer=new DBLayer(myActivity);
                    dbLayer.open();
                    switch (tipo_App) {

                        case "pwPersonali":
                            dbLayer.deleteDataPersona(idApp);
                            break;

                        case "pwLavoro-az":
                            dbLayer.deleteDataLavoro(idApp);
                            break;

                        case "pwFinanziarie":
                            dbLayer.deleteDataFinanza(idApp);
                            break;

                        case "pwLavoro-tel":
                            dbLayer.deleteDataLavoro(idApp);
                            break;
                        case "pwLavoro-bd":
                            dbLayer.deleteDataLavoro(idApp);
                            break;
                    }
                    ricreateActivity(tipo_App);

                }catch (SQLException ex){
                    Toast.makeText(myActivity, ""+ex.toString(), Toast.LENGTH_SHORT).show();
                    return false;
                }

                Toast.makeText(myActivity, "dato cancellato!", Toast.LENGTH_SHORT).show();


                dbLayer.close();
                try {
                    File file = new File(myActivity.getExternalFilesDir(null), nomeApp + ".jpg");
                    if (file != null) {
                        file.delete();
                        /*if(imageAggiunte.contains(nomeApp+".jpg")){
                            imageAggiunte.remove(nomeApp+".jpg");
                        }*/
                    }
                }catch (Exception e){
                    Toast.makeText(myActivity, e.toString(), Toast.LENGTH_SHORT).show();
                }

                return true;
            }

        });


        popupMenu.show();

    }

    public void ricreateActivity(String tipo_App){
        if(tipo_App.contains("-")){
            String[] split=tipo_App.split("-");
            tipo_App=split[0];
        }
        Intent ricreaMainActivity=new Intent(myActivity,MainActivity.class);
        ricreaMainActivity.putExtra("user", user);
        ricreaMainActivity.putExtra("qualeFragment", tipo_App);
        ricreaMainActivity.putExtra("isffpp",MainActivity.isffpp);

        myActivity.startActivity(ricreaMainActivity);
        myActivity.finish();

    }


}
