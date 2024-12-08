package br.edu.fateczl.mope.control;

import android.util.Log;

import java.sql.SQLException;
import java.util.List;

import br.edu.fateczl.mope.model.TipoParada;
import br.edu.fateczl.mope.persistence.TipoParadaDao;

public class ControladorTipoParada implements IControlador<TipoParada> {

    private final TipoParadaDao tipoParadaDao;

    public ControladorTipoParada(TipoParadaDao dao){
        this.tipoParadaDao = dao;
        inserirTipoGoogleMaps();
    }

    @Override
    public void inserir(TipoParada tipoParada) throws SQLException {
        if(tipoParadaDao.open() == null)
            tipoParadaDao.open();

        tipoParadaDao.insert(tipoParada);
        tipoParadaDao.close();
    }

    @Override
    public void modificar(TipoParada tipoParada) throws SQLException {
        if(tipoParadaDao.open() == null)
            tipoParadaDao.open();

        tipoParadaDao.update(tipoParada);
        tipoParadaDao.close();
    }

    @Override
    public void deletar(TipoParada tipoParada) throws SQLException {
        if(tipoParadaDao.open() == null)
            tipoParadaDao.open();

        tipoParadaDao.delete(tipoParada);
        tipoParadaDao.close();
    }

    @Override
    public TipoParada buscar(TipoParada tipoParada) throws SQLException {
        if(tipoParadaDao.open() == null)
            tipoParadaDao.open();

        tipoParada = tipoParadaDao.findOne(tipoParada);
        tipoParadaDao.close();
        return tipoParada;
    }

    @Override
    public List<TipoParada> listar() throws SQLException {
        if(tipoParadaDao.open() == null)
            tipoParadaDao.open();

        List<TipoParada> tipos = tipoParadaDao.findAll();
        tipoParadaDao.close();
        return tipos;
    }

    private TipoParada inserirTipoGoogleMaps(){
        TipoParada tipoGoogleMaps = new TipoParada();
        tipoGoogleMaps.setId(-1);
        tipoGoogleMaps.setNome("Google Maps");
        tipoGoogleMaps.setDesc("Gerado pelo Google Maps");
        tipoGoogleMaps.setMatiz(0);

        try {
            this.inserir(tipoGoogleMaps);
        } catch (SQLException e) {
            Log.e("DAO Error", "inserirTipoGoogleMaps: "+e.getMessage());
        }

        return tipoGoogleMaps;
    }

    public TipoParada getTipoGoogleMaps(){
        TipoParada tipoGoogleMaps = new TipoParada();
        tipoGoogleMaps.setId(-1);
        try {
            tipoGoogleMaps = this.buscar(tipoGoogleMaps);
        } catch (SQLException e) {
            Log.e("DAO error", "getTipoGoogleMaps: "+e.getMessage());
        }

        if(tipoGoogleMaps.getNome() == null){
            tipoGoogleMaps = this.inserirTipoGoogleMaps();
        }

        return tipoGoogleMaps;
    }
}
