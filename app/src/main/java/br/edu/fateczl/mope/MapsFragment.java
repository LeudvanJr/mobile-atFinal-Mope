package br.edu.fateczl.mope;


import static br.edu.fateczl.mope.control.BitmapHelper.drawableToBitmap;

import  androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import android.Manifest;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.sql.SQLException;
import java.util.List;

import br.edu.fateczl.mope.control.BitmapHelper;
import br.edu.fateczl.mope.control.ControladorParada;
import br.edu.fateczl.mope.control.ControladorPlace;
import br.edu.fateczl.mope.control.OnParadaEncontradaListener;
import br.edu.fateczl.mope.model.ParadaUsuario;
import br.edu.fateczl.mope.persistence.ParadaDao;

public class MapsFragment extends Fragment {

    private View view;
    private GoogleMap gMap;
    private Location localizacaoUsuario;
    private MarkerOptions marcadorUsuario;

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            gMap = googleMap;
            LatLng brasil = new LatLng(-15,-54);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(brasil,3.8f));
            inicializarMarcadores();
        }
    };

    private ActivityResultLauncher<String[]> pedidoPermissao =
            registerForActivityResult(new ActivityResultContracts.RequestMultiplePermissions(),
                    resposta -> {

                        Boolean fineLocation = resposta.getOrDefault(
                                Manifest.permission.ACCESS_FINE_LOCATION, false);

                        if(fineLocation == null || !fineLocation){
                            Toast.makeText(getContext(),
                                    getString(R.string.localizacao_precisa_necessaria),
                                    Toast.LENGTH_LONG).show();
                        }
                    }
            );


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_maps, container, false);

        inicializarMarcadorUsuario();

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if(gMap!=null){
            gMap.clear();
            inicializarMarcadores();
        }
    }

    /*
        Verifica e obtem a permissao de localizacao, se concedida, marca a posicao
        atual do usuario no mapa, alem de inicializar uma busca pelas paradas ao redor.
    */
    public void atulizarLocalizacaoAtual() {
        if(!temPermissaoLocalizacao()){
            if(!pedirPermissaoLocalizao()){
                return;
            }
        }
        try {
            obterLocalizacaoAtual();
            LatLng latLngUsuario = new LatLng(
                    localizacaoUsuario.getLatitude(),
                    localizacaoUsuario.getLongitude());

            if(marcadorUsuario == null)
                marcadorUsuario = new MarkerOptions().position(latLngUsuario);
            else
                marcadorUsuario = marcadorUsuario.position(latLngUsuario);

            gMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLngUsuario,16f));
            gMap.addMarker(marcadorUsuario);

            marcarParadasAoRedor(latLngUsuario);
        }catch (NullPointerException e){
            Toast.makeText(getContext(),
                    getString(R.string.ativar_localizacao),
                    Toast.LENGTH_LONG).show();
        }
    }

    private boolean temPermissaoLocalizacao() {
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Log.i("Map", "temPermissaoLocalizacao: false");
            return false;
        }
        return true;
    }

    private boolean pedirPermissaoLocalizao() {
        pedidoPermissao.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION});
        return temPermissaoLocalizacao();
    }

    @SuppressLint("MissingPermission")
    private void obterLocalizacaoAtual() throws NullPointerException {
        LocationManager locationManager =
                (LocationManager)getContext().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationManager.getLastKnownLocation(LocationManager.FUSED_PROVIDER);

        localizacaoUsuario = location;
    }

    private void inicializarMarcadorUsuario() {
        BitmapDescriptor iconeUsuario = BitmapHelper.getLocalAtualBitmapDesc(getContext());
        marcadorUsuario = new MarkerOptions().icon(iconeUsuario).title("Você");
    }

    /*
        Marca no mapa, com icone especifico, paradas de onibus ao redor,
        levando em consideracao a latitude e longitude passada como parametro.
        Todas as paradas encontradas sao guardadas no banco de dados.
    */
    private void marcarParadasAoRedor(LatLng latLngUsuario) {
        ControladorPlace cPlace = new ControladorPlace(getContext());
        if(!cPlace.iniciarPlacesApi()){
            Toast.makeText(getContext(),
                    getString(R.string.falha_iniciar_places_api),
                    Toast.LENGTH_LONG).show();
            return;
        }

        BitmapDescriptor bitmapDescOnibus = BitmapHelper.getOnibusBitmapDesc(this.getContext());
        ControladorParada cParada = new ControladorParada(new ParadaDao(getContext()));

        cPlace.buscarParadasAoRedor(latLngUsuario, 800,
                new OnParadaEncontradaListener() {

                    @Override
                    public void onParadaEncontrada(ParadaUsuario... paradas) {
                        for (ParadaUsuario parada : paradas) {
                            MarkerOptions marker = new MarkerOptions();
                            marker.title(parada.getNome() + " - " + parada.getTipo().getNome());
                            marker.position(parada.getLatLng());
                            marker.snippet(parada.getDescricao());
                            marker.icon(bitmapDescOnibus);
                            gMap.addMarker(marker);
                            try {
                                cParada.inserir(parada);
                            } catch (SQLException e) {
                                Log.e("DAO error",
                                        "onParadaEncontrada: "
                                                +getString(R.string.sql_erro_insercao)
                                                +"\n"
                                                + e.getMessage());
                            }
                        }
                    }

                    @Override
                    public void onFalha(String mensagem) {
                        Log.e("Map", "onFalha: " + mensagem);
                        Toast.makeText(getContext(),
                                getString(R.string.falha_iniciar_places_api),
                                Toast.LENGTH_LONG).show();
                    }
                }
        );
    }


    /*
        Recupera as paradas salvas no Banco de Dados, e marca elas no mapa
        conforme suas características.
    */
    private void inicializarMarcadores(){
        ControladorParada cp = new ControladorParada(new ParadaDao(getContext()));
        List<ParadaUsuario> listaParadas;
        try {
            Log.i("Map", "onMapReady: "+"carregando lista de paradas do banco");
            listaParadas = cp.listar();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        Log.i("Map", "onMapReady: Paradas recuperadas - "+listaParadas.size());
        for (ParadaUsuario parada : listaParadas) {
            MarkerOptions marker = new MarkerOptions();
            marker.title(parada.getNome() + " - " + parada.getTipo().getNome());
            marker.position(parada.getLatLng());
            marker.snippet(parada.getDescricao());
            if(parada.getTipo().getId() == -1)
                marker.icon(BitmapHelper.getOnibusBitmapDesc(this.getContext()));
            else
                marker.icon(BitmapDescriptorFactory.defaultMarker(parada.getTipo().getMatiz()));

            gMap.addMarker(marker);
        }
    }

}