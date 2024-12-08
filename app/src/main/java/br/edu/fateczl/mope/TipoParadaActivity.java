package br.edu.fateczl.mope;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import java.sql.SQLException;

import br.edu.fateczl.mope.control.ControladorTipoParada;
import br.edu.fateczl.mope.model.TipoParada;
import br.edu.fateczl.mope.persistence.TipoParadaDao;

public class TipoParadaActivity extends AppCompatActivity {

    private ControladorTipoParada controladorTP;
    private float[] hueSatlig = {0, 10f, 10f};
    private Button btnTesteCor;
    private EditText etIdTipoParada;
    private EditText etNomeTipo;
    private EditText etDescricaoTipo;
    private SeekBar seekBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_tipo_parada);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        TipoParadaDao tpDao = new TipoParadaDao(this);
        controladorTP = new ControladorTipoParada(tpDao);

        etIdTipoParada = findViewById(R.id.etIdTipoParada);
        etNomeTipo = findViewById(R.id.etNomeTipo);
        etDescricaoTipo = findViewById(R.id.etDescricaoTipo);
        Button btnEncontrarTipo = findViewById(R.id.btnEncontrarTipo);
        Button btnAdicionarTipo = findViewById(R.id.btnAdicionarTipo);
        Button btnAtualizarTipo = findViewById(R.id.btnAtualizarTipo);
        Button btnRemoverTipo = findViewById(R.id.btnRemoverTipo);
        seekBar = findViewById(R.id.seekBar);
        btnTesteCor = findViewById(R.id.btnTesteCor);

        btnAdicionarTipo.setOnClickListener(e -> adicionarTipo());
        btnAtualizarTipo.setOnClickListener(e -> atualizarTipo());
        btnRemoverTipo.setOnClickListener(e -> removerTipo());
        btnEncontrarTipo.setOnClickListener(e -> encontrarTipo());

        btnTesteCor.setBackgroundColor(Color.HSVToColor(hueSatlig));
        seekBar.setOnSeekBarChangeListener(seekBarListen());
    }

    private void adicionarTipo() {
        try {
            controladorTP.inserir(montarTipoParada());
            limparCampos();
        } catch (SQLException e) {
            Log.e("TipoParadaActivity", "adicionarTipo: "+e.getMessage());
            Toast.makeText(this,
                    R.string.preencher_campos, Toast.LENGTH_SHORT).show();
        }
    }

    private void atualizarTipo() {
        try {
            controladorTP.modificar(montarTipoParada());
            limparCampos();
        } catch (SQLException e) {
            Log.e("TipoParadaActivity", "atualizarTipo: "+e.getMessage());
            Toast.makeText(this,
                    R.string.preencher_campos, Toast.LENGTH_SHORT).show();
        }
    }

    private void removerTipo() {
        try {
            controladorTP.deletar(montarTipoParada());
            limparCampos();
        } catch (SQLException e) {
            Toast.makeText(this,
                    getString(R.string.sql_erro_exclusao), Toast.LENGTH_LONG).show();
        }
    }

    private void encontrarTipo() {
        try {
            TipoParada tipoParada = controladorTP.buscar(montarTipoParada());
            etNomeTipo.setText(tipoParada.getNome());
            etDescricaoTipo.setText(tipoParada.getDesc());
            seekBar.setProgress((int)tipoParada.getMatiz(), true);
        } catch (SQLException e) {
            Toast.makeText(this,
                    getString(R.string.sql_erro_selecao), Toast.LENGTH_LONG).show();
        }
    }

    private SeekBar.OnSeekBarChangeListener seekBarListen() {
        return new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                hueSatlig[0] = seekBar.getProgress();
                btnTesteCor.setBackgroundColor(Color.HSVToColor(hueSatlig));
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                onProgressChanged(seekBar,0,true);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                onProgressChanged(seekBar,0,true);
            }
        };
    }

    private TipoParada montarTipoParada(){
        TipoParada tp = new TipoParada();
        try {
            tp.setId(Integer.parseInt(etIdTipoParada.getText().toString()));
            tp.setNome(etNomeTipo.getText().toString());
            tp.setDesc(etDescricaoTipo.getText().toString());
            tp.setMatiz(hueSatlig[0]);
        } catch (Exception e) {
            Toast.makeText(this,
                    getString(R.string.preencher_campos), Toast.LENGTH_LONG).show();
        }

        return tp;
    }

    private void limparCampos(){
        etIdTipoParada.setText("");
        etNomeTipo.setText("");
        etDescricaoTipo.setText("");
    }

}