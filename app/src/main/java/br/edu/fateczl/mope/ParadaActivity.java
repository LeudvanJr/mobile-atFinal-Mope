package br.edu.fateczl.mope;

import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.android.libraries.places.api.model.Place;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import br.edu.fateczl.mope.control.ControladorParada;
import br.edu.fateczl.mope.control.ControladorPlace;
import br.edu.fateczl.mope.control.ControladorTipoParada;
import br.edu.fateczl.mope.control.OnParadaEncontradaListener;
import br.edu.fateczl.mope.model.ParadaUsuario;
import br.edu.fateczl.mope.model.TipoParada;
import br.edu.fateczl.mope.persistence.ParadaDao;
import br.edu.fateczl.mope.persistence.TipoParadaDao;

public class ParadaActivity extends AppCompatActivity {

    private ControladorParada controladorP;
    private EditText etIdParada;
    private EditText etNomeParada;
    private EditText etEnderecoParada;
    private Spinner spTipoParada;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_parada);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        ParadaDao pDao = new ParadaDao(this);
        controladorP = new ControladorParada(pDao);

        etIdParada = findViewById(R.id.etIdParada);
        etNomeParada = findViewById(R.id.etNomeParada);
        etEnderecoParada = findViewById(R.id.etEnderecoParada);
        spTipoParada = findViewById(R.id.spTipoParada);
        Button btnEncontrarParada = findViewById(R.id.btnEncontrarParada);
        Button btnAdicionarParada = findViewById(R.id.btnAdicionarParada);
        Button btnAtualizarParada = findViewById(R.id.btnAtualizarParada);
        Button btnRemoverParada = findViewById(R.id.btnRemoverParada);

        preencherSpinner();
        btnAdicionarParada.setOnClickListener(e -> adicionarParada());
        btnAtualizarParada.setOnClickListener(e -> atualizarParada());
        btnRemoverParada.setOnClickListener(e -> removerParada());
        btnEncontrarParada.setOnClickListener(e -> encontrarParada());
    }


    private void adicionarParada() {
            montarParada(new OnParadaEncontradaListener() {
                @Override
                public void onParadaEncontrada(ParadaUsuario... paradas) {
                    try {
                        controladorP.inserir(paradas[0]);
                        limparCampos();
                    } catch (SQLException e) {
                        onFalha(getString(R.string.sql_erro_insercao));
                    }
                }
                @Override
                public void onFalha(String mensagem) {
                    Log.e("ParadaActivity", "onFalha: " + mensagem);
                    Toast.makeText(getBaseContext(),
                            R.string.sem_internet,Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void atualizarParada() {
        montarParada(new OnParadaEncontradaListener() {
            @Override
            public void onParadaEncontrada(ParadaUsuario... paradas) {
                try {
                    controladorP.modificar(paradas[0]);
                    limparCampos();
                } catch (SQLException e) {
                    onFalha(getString(R.string.sql_erro_atualizacao));
                }
            }
            @Override
            public void onFalha(String mensagem) {
                Log.e("ParadaActivity", "onFalha: "+mensagem);
                Toast.makeText(getBaseContext(),
                        R.string.sem_internet,Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void removerParada() {
        try {
            ParadaUsuario parada = new ParadaUsuario();
            parada.setId(Integer.parseInt(etIdParada.getText().toString()));
            controladorP.deletar(parada);
            limparCampos();
        } catch (SQLException e) {
            Toast.makeText(this,
                    R.string.sql_erro_exclusao,Toast.LENGTH_LONG).show();
        }
    }

    private void encontrarParada() {
        try {
            ParadaUsuario parada = new ParadaUsuario();
            parada.setId(Integer.parseInt(etIdParada.getText().toString()));
            parada = controladorP.buscar(parada);

            if(parada.getNome() == null){
                Toast.makeText(this,
                        R.string.parada_nao_encontrada, Toast.LENGTH_LONG).show();
                return;
            }

            etNomeParada.setText(parada.getNome());
            etEnderecoParada.setText(parada.getDescricao());
            spTipoParada.setSelection(parada.getTipo().getId()-1,true);
        } catch (SQLException e) {
            Log.e("ParadaActivity", "encontrarParada: "+e.getMessage());
            Toast.makeText(this,
                    R.string.sql_erro_selecao,Toast.LENGTH_LONG).show();
        }
    }

    private void montarParada(OnParadaEncontradaListener listener){
        if(etIdParada.length() == 0 || etNomeParada.length() == 0 || etEnderecoParada.length() == 0){
            Toast.makeText(this,
                    R.string.preencher_campos,Toast.LENGTH_SHORT).show();
            return;
        }

        String endereco = etEnderecoParada.getText().toString();
        buscarEndereco(endereco, new OnParadaEncontradaListener() {
            @Override
            public void onParadaEncontrada(ParadaUsuario... paradas) {
                try {
                    ParadaUsuario parada = paradas[0];
                    parada.setId(Integer.parseInt(etIdParada.getText().toString()));
                    parada.setNome(etNomeParada.getText().toString());
                    parada.setTipo((TipoParada) spTipoParada.getSelectedItem());
                    listener.onParadaEncontrada(parada);
                }catch (Exception e){
                    listener.onFalha(getString(R.string.parada_nao_encontrada));
                }
            }

            @Override
            public void onFalha(String mensagem) {
                listener.onFalha(mensagem);
            }
        });
    }

    private void buscarEndereco (String endereco, OnParadaEncontradaListener listener){
        ParadaUsuario parada = new ParadaUsuario();

        ControladorPlace cPlace = new ControladorPlace(this);
        if(!cPlace.iniciarPlacesApi())
            Toast.makeText(this,
                    R.string.falha_iniciar_places_api,Toast.LENGTH_LONG).show();

        List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG, Place.Field.ADDRESS);

        cPlace.buscarPorEndereco(endereco, placeFields).thenAccept(place -> {
            if(place != null){
                try {
                    parada.setDescricao(place.getAddress());
                    parada.setLatLng(place.getLatLng());
                    listener.onParadaEncontrada(parada);
                }catch (Exception e){
                    listener.onFalha(getString(R.string.preencher_campos));
                }
            }
        }).exceptionally(excecao -> {
            listener.onFalha(getString(R.string.falha_iniciar_places_api));
            return null;
        });
    }

    private void preencherSpinner() {
        ControladorTipoParada cTipoParada = new ControladorTipoParada(new TipoParadaDao(this));

        try {
            List<TipoParada> tipos = cTipoParada.listar();
            ArrayAdapter<CharSequence> arrayAdapter = new ArrayAdapter(
                this,
                android.R.layout.simple_spinner_item,
                tipos);
            arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spTipoParada.setAdapter(arrayAdapter);
        } catch (SQLException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private void limparCampos(){
        etIdParada.setText("");
        etNomeParada.setText("");
        etEnderecoParada.setText("");
    }
}
