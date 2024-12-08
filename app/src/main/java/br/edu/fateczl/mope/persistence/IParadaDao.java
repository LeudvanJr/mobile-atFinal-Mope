package br.edu.fateczl.mope.persistence;

import java.sql.SQLException;

public interface IParadaDao {
    ParadaDao open()throws SQLException;
    void close();
}
