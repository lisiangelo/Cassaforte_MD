package com.example.j940549.cassaforte_md.Model;

import java.io.Serializable;

/**
 * Created by J940549 on 23/04/2017.
 */

public class RowGen implements Serializable {
    String id;
    String nomeApp;
    int resourceImage=0;
    String patchImage="";
    String tipo="pwPersonali";
    public RowGen(){

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNomeApp() {
        return nomeApp;
    }

    public void setNomeApp(String nomeApp) {
        this.nomeApp = nomeApp;
    }

    public int getResourceImage() {
        return resourceImage;
    }

    public void setResourceImage(int resourceImage) {
        this.resourceImage = resourceImage;
    }

    public String getPatchImage() {
        return patchImage;
    }

    public void setPatchImage(String patchImage) {
        this.patchImage = patchImage;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
}
