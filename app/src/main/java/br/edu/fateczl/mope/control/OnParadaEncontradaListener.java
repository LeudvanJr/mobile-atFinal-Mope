package br.edu.fateczl.mope.control;

import br.edu.fateczl.mope.model.ParadaUsuario;

public interface OnParadaEncontradaListener {
    void onParadaEncontrada(ParadaUsuario... paradas);
    void onFalha(String mensagem);
}
