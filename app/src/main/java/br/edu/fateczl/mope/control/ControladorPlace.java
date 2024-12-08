package br.edu.fateczl.mope.control;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.CircularBounds;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.api.net.SearchByTextRequest;
import com.google.android.libraries.places.api.net.SearchByTextResponse;
import com.google.android.libraries.places.api.net.SearchNearbyRequest;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import br.edu.fateczl.mope.BuildConfig;
import br.edu.fateczl.mope.model.Parada;
import br.edu.fateczl.mope.model.ParadaUsuario;
import br.edu.fateczl.mope.persistence.TipoParadaDao;

public class ControladorPlace {

    private Context context;

    public ControladorPlace(Context context){
        this.context = context;
    }

    public boolean iniciarPlacesApi() {
        if(Places.isInitialized())
            return true;

        String apiKey = BuildConfig.MAPS_API_KEY;
        if (TextUtils.isEmpty(apiKey) || apiKey.equals("DEFAULT_API_KEY")) {
            Log.e("Places API", "Sem chave da API");
            return false;
        }
        Places.initializeWithNewPlacesApiEnabled(context, apiKey);
        return Places.isInitialized();
    }

    public CompletableFuture<Place> buscarPorEndereco(String endereco, List<Place.Field> placeFields){
        if(!Places.isInitialized())
            return null;

        Parada parada = null;
        CompletableFuture<Place> future = new CompletableFuture<>();
        PlacesClient placesClient = Places.createClient(context);

        SearchByTextRequest searchByTextRequest = SearchByTextRequest
                .builder(endereco, placeFields)
                .setMaxResultCount(1).build();

        Log.i("TAG", "buscarPorEndereco: "+"buscando");
        Task<SearchByTextResponse> task = placesClient.searchByText(searchByTextRequest);

        task.addOnSuccessListener(response ->{
            future.complete(response.getPlaces().get(0));
        });
        task.addOnFailureListener(response -> {
            if(response instanceof ApiException){
                ApiException apiException = (ApiException) response;
                future.completeExceptionally(apiException);
            }
        });
        task.addOnCompleteListener(response -> {
            future.complete(null);
        });
        return future;
    }

    public void buscarParadasAoRedor(LatLng latlng, double raioEmMetros, OnParadaEncontradaListener listener){
        final List<Place.Field> placeFields = Arrays.asList(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG, Place.Field.ADDRESS);
        final List<String> includedTypes = Arrays.asList("bus_stop", "bus_station");
        CircularBounds limiteCircular = CircularBounds.newInstance(latlng, raioEmMetros);

        final SearchNearbyRequest searchNearbyRequest =
                SearchNearbyRequest.builder(limiteCircular, placeFields)
                        .setIncludedTypes(includedTypes)
                        .build();

        PlacesClient placesClient = Places.createClient(context);
        placesClient.searchNearby(searchNearbyRequest)
            .addOnSuccessListener(resposta -> {
                List<Place> places = resposta.getPlaces();
                if(places.isEmpty())
                   return;

                ControladorTipoParada cTipoParada = new ControladorTipoParada(
                        new TipoParadaDao(this.context));

                ParadaUsuario[] paradas = new ParadaUsuario[places.size()];
                for (int i=0; i < paradas.length; i++){
                    Place place = places.get(i);
                    ParadaUsuario parada = new ParadaUsuario();
                    parada.setId(place.getId().hashCode());
                    parada.setNome(place.getName());
                    parada.setDescricao(place.getAddress());
                    parada.setLatLng(place.getLatLng());
                    parada.setTipo(cTipoParada.getTipoGoogleMaps());

                    paradas[i] = parada;
                }
                listener.onParadaEncontrada(paradas);
            })
            .addOnFailureListener(resposta -> {
                if(resposta instanceof ApiException){
                    ApiException e = (ApiException) resposta;
                    Log.e("ApiException", "buscarParadasAoRedor: "+e.getMessage());
                    listener.onFalha(e.getMessage());
                }
            });
    }

}
