package br.edu.fateczl.mope.persistence;

import java.sql.SQLException;

import br.edu.fateczl.mope.model.TipoParada;

public interface ITipoParadaDao {
    TipoParadaDao open() throws SQLException;
    void close();
}
