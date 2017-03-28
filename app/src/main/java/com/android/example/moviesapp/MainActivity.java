package com.android.example.moviesapp;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.example.moviesapp.utilities.GlobalVariables;
import com.android.example.moviesapp.utilities.NetworkUtils;

import java.io.IOException;
import java.net.URL;


public class MainActivity extends AppCompatActivity implements PostersAdapter.PostersAdapterOnCLickHandler {

    /*  Definimos de antemano el contexto de la actividad principal, informaci'on para el RecyclerView
     */
    public final Context context = MainActivity.this;

    /* Todos los Views que se utilizan en la plantilla (Layout/activity_main.xml) de la MainActivity
     */
    private RecyclerView   mRecyclerView;
    private PostersAdapter mPostersAdapter;
    private ProgressBar    mProgressBar;
    private TextView       mQueryType;
    private TextView       mErrorMessage;

    // Override para cargar nuestra plantilla en vez de la plantilla por defecto.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Asociar las Views de la plantilla xml con las variables de java
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_posters);
        mQueryType    = (TextView)     findViewById(R.id.tv_query_type);
        mProgressBar  = (ProgressBar)  findViewById(R.id.progress_bar);
        mErrorMessage = (TextView)     findViewById(R.id.tv_error_message);

        // BUSCAR, descargar y almacenar la base de datos.
        makeTmdbSearchQuery();

        // Definir el estilo del RecyclerView.

        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(gridLayoutManager);
        mPostersAdapter = new PostersAdapter( this, context );
        mRecyclerView.setAdapter(mPostersAdapter);
    }

    /* Override para definir nuestro propio OnClick, este m'etodo lo heredamos en la MainActivity
     * cuando le damos implement PostersAdapter.PostersAdapterOnClickHandler.
     * handler = manejador
     * B'asicamente necesita como par'ametro de entrada la posici'on donde estamos haciendo click,
     * esta la devuelve como un n'umero entero autom'aticamente el adaptador del RecyclerView.
     *
     * Para iniciar la DETAIL ACTIVITY usaremos como 'unico par'ametro la posici'on.
     *
     * Como toda la informaci'on de posterURL, titulo, fecha de lanzamiento, puntuaci'on y dem'as
     * ya est'an guardadas en la clase GlobalVariables, solo necesitamos la posici'on donde se hace
     * click para consultar los arrays en GlobalVariables.
     *
     * Cuando se hace click se inicia una nueva actividad para ver los detalles de cada pelicula
     * startActivity(intentToStartDetailActivity)
     */
    @Override
    public void onClick ( int adapterPosition ) {
        Context context = this;
        Class destinationClass = DetailedPoster.class;
        Intent intentToStartDetailActivity = new Intent(context, destinationClass);
        intentToStartDetailActivity.putExtra("adapterPosition", Integer.toString(adapterPosition)  );
        startActivity(intentToStartDetailActivity);
    }



    /* Hacer la b'usqueda.
     *  1) Crear la URL con un m'etodo en NetworkUtils
     *  2) Consultar la URL y guardar los datos, de nuevo, con un m'etodo de NetworkUtils
     *  Esta b'usqueda se realiza en segundo plano pues toma tiempos largos (1 segundo como m'aximo)
     */
    private void makeTmdbSearchQuery() {
        URL queryUrl = NetworkUtils.buildUrl();
        new theMovieDataBaseQueryTask().execute(queryUrl);
    }

    /*  Para hacer las cosas en segundo plano traemos propiedades de la clase TareasAsincr'onicas
     *  AsyncTasks.
     *  El proceso se divide en tres partes:
     *  1) Antes de ejecutar (onPreExecute)
     *  2) Ejecutar en segundo plano ( doInBackground )
     *  3) Qu'e hacer cuando termina la b'usqueda en internet (onPostExcecute)
     *
     *  Se han inclu'ido funciones que hacen ver m'as bonita la aplicaci'on cuando se hace la
     *  b'usqueda mostrando una barra de progreso (showProgressBar), cuando hay un error en la
     *  conexi'on (showErrorMessage) y mostrar los resultados si el proceso anterior termin'o bien
     *  (showMoviePosters)
     */
    public class theMovieDataBaseQueryTask extends AsyncTask<URL, Void, String> {

        @Override
        protected void onPreExecute(){
             ShowProgressBar();
             super.onPreExecute();
         }

        // Importante: Usar catch(IOException e) en caso que suceda un error de conexi'on.
        @Override
        protected String doInBackground(URL... params) {
            URL searchURL = params[0];
            String queryRawResults = null;

            try {
                queryRawResults = NetworkUtils.getResponseFromHttpUrl(searchURL);
            } catch (IOException e) {
                e.printStackTrace();
            }
            return queryRawResults;
        }

        /* Ac'a se toma el string con la informaci'on en formato JSON, se convierte en
         * un objeto JSONObject y se extraen los datos para guardarlos en los arrays
         * definidos em GlobalVariables.
         * Revisar en detalle las funciones guardadas en NetworkUtils.
         * getSimpleMovieStringsFromJson
         *
         * Si el m'etodo funcion'o, entonces mostrar posters.
         *
         * Si el m'etodo fall'o, entonces mostrar mensaje de error.
         */
        @Override
        protected void onPostExecute (String queryRawResults) {
            if (queryRawResults != null && !queryRawResults.equals("")) {

                try {
                    GlobalVariables.rawData =
                            NetworkUtils.getSimpleMovieStringsFromJson(context, queryRawResults);
                    mPostersAdapter.setPostersData( GlobalVariables.all_postersPaths );
                    ShowMoviePosters();
                } catch (Exception e) {
                    ShowErrorMessage();
                    e.printStackTrace();

                    return;
                }
            } else {
                mPostersAdapter.setPostersData(null);
                ShowErrorMessage();
                return;
            }
        }
    }


    /* Esta parte se encarga de crear e inflar el men'u para organizar los datos.
     *
     * Override OnCreateOptionsMenu para omitir el m'etodo por defecto y especificar c'omo queremos
     * que infle los men'us.
     *
     * Override onOptionsItemSelected para realizar acciones seg'un el bot'on que uno haya seleccionad.
     * B'asicamente se cambia el par'ametro de b'usqueda en GlobalVariables, se genera una
     * nueva URL de b'usqueda y se hace la b'usqueda.
     *
     * Se especifican las dos b'usquedas distintas en SortByRating y SortByPopularity
     * */
    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();

        if (id == R.id.action_sort_by_rating) {
            SortByRating();
            return true;
        }

        if (id == R.id.action_sort_by_popularity) {
            SortByPopularity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    // Organizar por Puntuaci'on
    public void SortByRating() {
        mQueryType.setText(R.string.search_by_rating);
        GlobalVariables.sort_by = "vote_average.desc";
        GlobalVariables.all_postersPaths=null;
        mPostersAdapter.setPostersData(null);
        makeTmdbSearchQuery();
    }

    // Organizar por Popularidad
    public void SortByPopularity() {
        mQueryType.setText(R.string.search_by_popular);
        GlobalVariables.sort_by = "popularity.desc";
        GlobalVariables.all_postersPaths=null;
        mPostersAdapter.setPostersData(null);
        makeTmdbSearchQuery();
    }


    /* Funciones auxiliares que se encargan de mostrar y ocultar las diferentes Views en los
     * casos normal, durante la b'usqueda y en caso de error.
     */

    public void ShowMoviePosters() {
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
        mQueryType.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);

    }

    public void ShowProgressBar() {
        mQueryType.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.VISIBLE);
        mErrorMessage.setVisibility(View.INVISIBLE);
    }

    public void ShowErrorMessage() {
        mQueryType.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
        mErrorMessage.setVisibility(View.VISIBLE);
    }
}