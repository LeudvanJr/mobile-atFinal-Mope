package br.edu.fateczl.mope.persistence;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class GenericDao extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "mope.db";
        private static final int DATABASE_VER = 1;
        private static final String CREATE_TABLE_TIPO_PARADA =
                "CREATE TABLE tipo_parada( " +
                    "id_tipo INT PRIMARY KEY, " +
                    "nome_tipo VARCHAR(32), " +
                    "descricao_tipo VARCHAR(64), " +
                    "matiz NUMERIC(5,2)" +
                ");";
        private static final String CREATE_TABLE_PARADA =
                "CREATE TABLE parada( " +
                        "id_parada INT PRIMARY KEY, " +
                        "nome_parada VARCHAR(64), " +
                        "lat_lng VARCHAR(32) UNIQUE, " +
                        "descricao_parada VARCHAR(64), " +
                        "tipo INT, " +
                        "FOREIGN KEY (tipo) REFERENCES tipo_parada(id_tipo)" +
                ");";

    public GenericDao(Context context){
        super(context,DATABASE_NAME, null,DATABASE_VER);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_TIPO_PARADA);
        sqLiteDatabase.execSQL(CREATE_TABLE_PARADA);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int antigaVersao, int novaVersao) {
        if(antigaVersao < novaVersao){
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS parada");
            sqLiteDatabase.execSQL("DROP TABLE IF EXISTS tipo_parada");
            this.onCreate(sqLiteDatabase);
        }
    }
}
