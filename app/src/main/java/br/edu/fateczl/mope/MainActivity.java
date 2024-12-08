package br.edu.fateczl.mope;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.libraries.places.api.Places;

import br.edu.fateczl.mope.control.ControladorPlace;


public class MainActivity extends AppCompatActivity {

    private static final int ITEM_MENU_ID_PARADA = R.id.item_adicionarParada;
    private static final int ITEM_MENU_ID_TIPO_PARADA = R.id.item_adicionarTipo;
    private MapsFragment mapsFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        mapsFragment = new MapsFragment();
        ControladorPlace cPlace = new ControladorPlace(this);
        cPlace.iniciarPlacesApi();

        Button btnLocalizacao = findViewById(R.id.btnLocalizacaoAtual);
        btnLocalizacao.setOnClickListener(op -> mapsFragment.atulizarLocalizacaoAtual());

        FragmentManager fManager = getSupportFragmentManager();
        FragmentTransaction fTransaction = fManager.beginTransaction();
        fTransaction.replace(R.id.fragment,mapsFragment);
        fTransaction.commit();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent = null;
        if(item.getItemId() == ITEM_MENU_ID_PARADA)
            intent = new Intent(this, ParadaActivity.class);
        else if(item.getItemId() == ITEM_MENU_ID_TIPO_PARADA)
            intent = new Intent(this, TipoParadaActivity.class);
        
        if (intent != null)
            startActivity(intent);
        return super.onOptionsItemSelected(item);
    }

}