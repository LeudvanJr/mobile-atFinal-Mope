package br.edu.fateczl.mope.persistence;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.edu.fateczl.mope.control.LatLngHelper;
import br.edu.fateczl.mope.model.ParadaUsuario;
import br.edu.fateczl.mope.model.TipoParada;

public class ParadaDao implements ICRUDDao<ParadaUsuario>, IParadaDao{

    private GenericDao gDao;
    private final Context context;
    private SQLiteDatabase database;

    public ParadaDao(Context context){
        this.context = context;
    }

    @Override
    public ParadaDao open() throws SQLException {
        gDao = new GenericDao(this.context);
        database = gDao.getWritableDatabase();
        return this;
    }

    @Override
    public void close() {
        gDao.close();
    }

    @Override
    public void insert(ParadaUsuario parada) throws SQLException {
        ContentValues contentValues = getContentValues(parada);
        database.insert("parada", null, contentValues);
    }

    @Override
    public int update(ParadaUsuario parada) throws SQLException {
        ContentValues contentValues = getContentValues(parada);
        return database.update("parada",contentValues,
                "id_parada = "+ parada.getId(),null);
    }

    @Override
    public void delete(ParadaUsuario parada) throws SQLException {
        database.delete("parada",
                "id_parada = " + parada.getId(),null);
    }

    @SuppressLint("Range")
    @Override
    public ParadaUsuario findOne(ParadaUsuario parada) throws SQLException {
        String query = "SELECT id_parada, nome_parada, lat_lng, " +
                "descricao_parada , id_tipo, nome_tipo, descricao_tipo, matiz " +
                "FROM parada, tipo_parada " +
                "WHERE tipo = id_tipo " +
                "AND id_parada = "+parada.getId();
        Cursor cursor = database.rawQuery(query, null);

        if(cursor != null)
            cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            TipoParada tipo = new TipoParada();
            tipo.setId(cursor.getInt(cursor.getColumnIndex("id_tipo")));
            tipo.setNome(cursor.getString(cursor.getColumnIndex("nome_tipo")));
            tipo.setDesc(cursor.getString(cursor.getColumnIndex("descricao_tipo")));
            tipo.setMatiz(cursor.getFloat(cursor.getColumnIndex("matiz")));

            parada.setNome(cursor.getString(cursor.getColumnIndex("nome_parada")));
            String latLng = (cursor.getString(cursor.getColumnIndex("lat_lng")));
            parada.setLatLng(LatLngHelper.StringToLatLng(latLng));
            parada.setDescricao((cursor.getString(cursor.getColumnIndex("descricao_parada"))));
            parada.setTipo(tipo);
        }
        cursor.close();
        return parada;
    }

    @SuppressLint("Range")
    @Override
    public List<ParadaUsuario> findAll() throws SQLException {
        List<ParadaUsuario> paradas = new ArrayList<>();
        String query = "SELECT id_parada, nome_parada, lat_lng, " +
                "descricao_parada , id_tipo, nome_tipo, descricao_tipo, matiz " +
                "FROM parada " +
                "INNER JOIN tipo_parada " +
                "ON tipo = id_tipo";
        Cursor cursor = database.rawQuery(query, null);

        if(cursor != null)
            cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            TipoParada tipo = new TipoParada();
            tipo.setId(cursor.getInt(cursor.getColumnIndex("id_tipo")));
            tipo.setNome(cursor.getString(cursor.getColumnIndex("nome_tipo")));
            tipo.setDesc(cursor.getString(cursor.getColumnIndex("descricao_tipo")));
            tipo.setMatiz(cursor.getFloat(cursor.getColumnIndex("matiz")));

            ParadaUsuario parada = new ParadaUsuario();
            parada.setNome(cursor.getString(cursor.getColumnIndex("nome_parada")));
            String latLng = (cursor.getString(cursor.getColumnIndex("lat_lng")));
            parada.setLatLng(LatLngHelper.StringToLatLng(latLng));
            parada.setDescricao((cursor.getString(cursor.getColumnIndex("descricao_parada"))));
            parada.setTipo(tipo);

            paradas.add(parada);
            cursor.moveToNext();
        }
        cursor.close();
        return paradas;
    }

    private ContentValues getContentValues(ParadaUsuario parada){
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_parada",parada.getId());
        contentValues.put("nome_parada",parada.getNome());
        contentValues.put("lat_lng",
                LatLngHelper.latLngToString(parada.getLatLng()));
        contentValues.put("descricao_parada",parada.getDescricao());
        contentValues.put("tipo", parada.getTipo().getId());

        return contentValues;
    }
}
