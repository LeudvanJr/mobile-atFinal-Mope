package br.edu.fateczl.mope.model;

import com.google.android.gms.maps.model.LatLng;

public abstract class Parada implements Localizavel {
    int id;
    String nome;
    String endereco;
    LatLng latLng;
    TipoParada tipo;

    public abstract TipoParada getTipo();

    public abstract void setTipo(TipoParada tipo);
}