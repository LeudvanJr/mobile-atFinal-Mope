package br.edu.fateczl.mope.control;

import java.sql.SQLException;
import java.util.List;

import br.edu.fateczl.mope.model.ParadaUsuario;
import br.edu.fateczl.mope.persistence.ParadaDao;

public class ControladorParada implements IControlador<ParadaUsuario> {

    private final ParadaDao paradaDao;

    public ControladorParada(ParadaDao dao){
        this.paradaDao = dao;
    }

    @Override
    public void inserir(ParadaUsuario parada) throws SQLException {
        if(paradaDao.open() == null)
            paradaDao.open();

        paradaDao.insert(parada);
        paradaDao.close();
    }

    @Override
    public void modificar(ParadaUsuario parada) throws SQLException {
        if(paradaDao.open() == null)
            paradaDao.open();

        paradaDao.update(parada);
        paradaDao.close();
    }

    @Override
    public void deletar(ParadaUsuario parada) throws SQLException {
        if(paradaDao.open() == null)
            paradaDao.open();

        paradaDao.delete(parada);
        paradaDao.close();
    }

    @Override
    public ParadaUsuario buscar(ParadaUsuario parada) throws SQLException {
        if(paradaDao.open() == null)
            paradaDao.close();

        parada = paradaDao.findOne(parada);
        paradaDao.close();
        return parada;
    }

    @Override
    public List<ParadaUsuario> listar() throws SQLException {
        if(paradaDao.open() == null)
            paradaDao.open();

        List<ParadaUsuario> paradas = paradaDao.findAll();
        paradaDao.close();
        return paradas;
    }
}
