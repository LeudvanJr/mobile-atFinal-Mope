package br.edu.fateczl.mope.persistence;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import br.edu.fateczl.mope.model.TipoParada;

public class TipoParadaDao implements ICRUDDao<TipoParada>, ITipoParadaDao {

    private final Context context;
    private GenericDao gDao;
    private SQLiteDatabase database;

    public TipoParadaDao(Context context) {
        this.context = context;
    }

    @Override
    public TipoParadaDao open() throws SQLException {
        gDao = new GenericDao(this.context);
        database = gDao.getWritableDatabase();
        return this;
    }

    @Override
    public void close() {
        gDao.close();
    }

    @Override
    public void insert(TipoParada tipoParada) throws SQLException {
        ContentValues contentValues = getContentValues(tipoParada);
        database.insert("tipo_parada",null,contentValues);
    }

    @Override
    public int update(TipoParada tipoParada) throws SQLException {
        ContentValues contentValues = getContentValues(tipoParada);
        return database.update("tipo_parada",contentValues,
                "id_tipo = "+tipoParada.getId(),null);
    }

    @Override
    public void delete(TipoParada tipoParada) throws SQLException {
        database.delete("tipo_parada",
                "id_tipo = "+tipoParada.getId(),null);
    }

    @SuppressLint("Range")
    @Override
    public TipoParada findOne(TipoParada tipoParada) throws SQLException {
        String querry =
                "SELECT * FROM tipo_parada " +
                "WHERE id_tipo = "+tipoParada.getId();

        Cursor cursor = database.rawQuery(querry, null);

        if(cursor != null)
            cursor.moveToFirst();
        if(!cursor.isAfterLast()){
            tipoParada.setNome(cursor.getString(cursor.getColumnIndex("nome_tipo")));
            tipoParada.setDesc(cursor.getString(cursor.getColumnIndex("descricao_tipo")));
            tipoParada.setMatiz(cursor.getFloat(cursor.getColumnIndex("matiz")));
        }
        cursor.close();
        return tipoParada;
    }

    @SuppressLint("Range")
    @Override
    public List<TipoParada> findAll() throws SQLException {
        List<TipoParada> tiposParada = new ArrayList<>();
        String querry =
                "SELECT * FROM tipo_parada WHERE id_tipo <> -1";

        Cursor cursor = database.rawQuery(querry, null);

        if(cursor != null)
            cursor.moveToFirst();
        while(!cursor.isAfterLast()){
            TipoParada tipo = new TipoParada();

            tipo.setId(cursor.getInt(cursor.getColumnIndex("id_tipo")));
            tipo.setNome(cursor.getString(cursor.getColumnIndex("nome_tipo")));
            tipo.setDesc(cursor.getString(cursor.getColumnIndex("descricao_tipo")));
            tipo.setMatiz(cursor.getFloat(cursor.getColumnIndex("matiz")));

            tiposParada.add(tipo);
            cursor.moveToNext();
        }
        cursor.close();
        return tiposParada;
    }

    public ContentValues getContentValues(TipoParada tipoParada){
        ContentValues contentValues = new ContentValues();

        contentValues.put("id_tipo", tipoParada.getId());
        contentValues.put("nome_tipo", tipoParada.getNome());
        contentValues.put("descricao_tipo", tipoParada.getDesc());
        contentValues.put("matiz", tipoParada.getMatiz());

        return contentValues;
    }
}
