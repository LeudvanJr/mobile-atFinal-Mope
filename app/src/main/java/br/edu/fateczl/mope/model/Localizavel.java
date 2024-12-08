package br.edu.fateczl.mope.model;

import com.google.android.gms.maps.model.LatLng;

public interface Localizavel {
    int getId();
    void setId(int id);
    String getNome();
    void setNome(String nome);
    LatLng getLatLng();
    void setLatLng(LatLng latLng);
    String getDescricao();
    void setDescricao(String descricao);
}
