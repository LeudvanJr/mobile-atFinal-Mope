package br.edu.fateczl.mope.model;

import androidx.annotation.NonNull;

public class TipoParada {
    private int id;
    private String nome;
    private String desc;
    private float matiz;

    public TipoParada(int id, String nome, String desc, float matiz) {
        this.id = id;
        this.nome = nome;
        this.desc = desc;
        this.matiz = matiz;
    }

    public TipoParada(){
        super();
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public float getMatiz() {
        return matiz;
    }

    public void setMatiz(float matiz) {
        this.matiz = matiz;
    }

    @NonNull
    @Override
    public String toString() {
        return this.id + " - "
                +this.nome + " - "
                +this.desc;
    }

}
