package br.edu.fateczl.mope.model;

import com.google.android.gms.maps.model.LatLng;

public class ParadaUsuario extends Parada{

    @Override
    public int getId() {
        return this.id;
    }

    @Override
    public void setId(int id) {
        this.id = id;
    }

    @Override
    public String getNome() {
        return this.nome;
    }

    @Override
    public void setNome(String nome) {
        this.nome = nome;
    }

    @Override
    public LatLng getLatLng() {
        return this.latLng;
    }

    @Override
    public void setLatLng(LatLng latLng) {
        this.latLng = latLng;
    }

    @Override
    public String getDescricao() {
        return this.endereco;
    }

    @Override
    public void setDescricao(String descricao) {
        this.endereco = descricao;
    }

    @Override
    public TipoParada getTipo(){
        return this.tipo;
    }

    @Override
    public void setTipo(TipoParada tipo){
        this.tipo = tipo;
    }
}
