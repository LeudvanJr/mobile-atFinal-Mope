package br.edu.fateczl.mope.control;

import java.sql.SQLException;
import java.util.List;

public interface IControlador<T> {
    void inserir(T t) throws SQLException;
    void modificar(T t) throws SQLException;
    void deletar(T t) throws SQLException;
    T buscar(T t) throws SQLException;
    List<T> listar() throws SQLException;
}
