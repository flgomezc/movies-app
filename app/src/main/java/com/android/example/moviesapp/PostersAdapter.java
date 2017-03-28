
package com.android.example.moviesapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

/* Se crea la clase PoseterAdapter que hereda m'etodos y cositas de RecyclerView.Adapter, en
 * particular hereda el manejador de clicks (onClickListener o onClickHandler, seg'un como
 * uno quiera llamarlo).
 */

public class PostersAdapter extends RecyclerView.Adapter<PostersAdapter.PostersAdapterViewHolder> {

    /* Se guardar'a ac'a localmente la ruta URL de las im'agenes de los posters.
     * Hay que tener en cuenta que esta informaci'on y toda la dem'as (titulos, puntuaci'on, etc)
     * ya est'an guardados den GlobalVariables.
     */
    private String[] postersPath;

    // Vamos a guardar el contexto de la MainActivity ac'a. CONTEXTO EST'ATICO
    private Context context1;

    /* Crear un elemento mClickHandler (qu'e hacer cuando hay clicks) de la clase con propiedades
     * heredadas de RecyclerView.
     */
    private final PostersAdapterOnCLickHandler mClickHandler;

    // Constructor? de la interface que maneja Clicks.
    public interface PostersAdapterOnCLickHandler {
        void onClick(int adapterPosition );
    }

    // Consructor de los elementos de esta clase. Requiere un clickhandler y un contexto
    public PostersAdapter(PostersAdapterOnCLickHandler clickHandler, Context context){
        mClickHandler = clickHandler;
        context1 = context;
    }


    /* Ahora dentro de la superclase PostersAdapter se crea una clase llamada PostersAdapterViewHolder
     * hold=sostener/soportar, esta clase tendr'a los elementos que sostendr'an o posicionar'an
     * las View de la clase recyclerview.
     * Heredamos de RecyclerView las propiedades del ViewHolder y adem'as ac'a se debe especificar
      * qu'e diablos va a hacer el OnClickListener.
      * Como las RecyclerView son din'amicas utilic'e al principio una definici'on un poco a la brava
      * del contexto est'atico de la MainActivity, en ese contexto se pueden crear las RecyclerView.
     */
    public class PostersAdapterViewHolder extends RecyclerView.ViewHolder implements OnClickListener {

        // En la pantalla principal solo mostraremos posters, usamos ImageView
        public final ImageView mPostersImageView;


        /* Asociamos esta variable con la ImageView definida en la plantilla posters_list_item.xml
         * que es distinta a la plantilla de la actividad principal.
         * A;adimos de una vez el m'etodo setOnClickListener(this) sobre la view que acabamos de
         * definir.
          */
        public PostersAdapterViewHolder(View view) {
            super(view);
            mPostersImageView = (ImageView) view.findViewById(R.id.iv_PosterListItems);
            view.setOnClickListener(this);
        }

        /* Con Override omitimos la definici'on del m'etodo onClick que viene por defecto y
         * le especificamos qu'e nos indiquela posici'on de la ReciclerView.
         *
         * Esta posici'on se cuenta con enteros empezando en 0, 1, 2, ... n-1.
          *
         * n es el n'umero de posters a mostrar. (para el caso de TheMovieDataBase.com son 20 items
         * por p'agina.
         *
         * Cuando ya se tiene la posici'on, la pasamos como argumento al mClickHandler.
         */
        @Override
        public void onClick(View view){

            int adapterPosition = getAdapterPosition();

            mClickHandler.onClick( adapterPosition );
        }
    } // Ac'a terminamos de definir la clase PostersAdapterViewHolder //


    /* Ya definido el PostersAdapterViewHolder se define un m'etodo que se encargar'a de poblar
     * las RecyclerView con el contenido que nosotros le especifiquemos.
     *
     * Ni idea sobre que hace el ViewGroup espec'ificamente.
     */

    @Override
    public PostersAdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType){
        Context context = viewGroup.getContext();
        int layoutforListItem = R.layout.posters_list_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        boolean shouldAttachToParentImmediately = false;

        View view = inflater.inflate(layoutforListItem, viewGroup, shouldAttachToParentImmediately);
        return new PostersAdapterViewHolder(view);
    }


    /* Le asignamos a cada RecyclerView un poster del array posterPath
     *
     * Librer'ia Picasso
     * Lo raro ac'a es que utilizaremos una librer'ia para conectarnos a internet, descargar,
     * almacenar, cerrar la conexi'on  y mostrar las im'agenes desde internet.
     * Lo ch'evere es que se hace t'odo eso en una sola l'inea.
     */
    @Override
    public void onBindViewHolder( PostersAdapterViewHolder postersAdapterViewHolder, int position) {
        String posterUrl = postersPath[position];
        Picasso.with(context1).load(posterUrl).into(postersAdapterViewHolder.mPostersImageView);
    }

    /* Funci'on para saber qu'e tantos elementos vamos a mostrar. Retorna el taman;o del array
      * donde se tienen guardadas las direcciones de las im'agenes. posterPath.length;
      */
    @Override
    public int getItemCount() {
        if (null == postersPath) return 0;
        return postersPath.length;
    }

    /* IMPORTANTE:
     * Este m'etodo indica que hay una actualizaci'on en los datos, este m'etodo dispara de nuevo
     * los m'etodos que van a poblar la RecyclerView con nuevas im'agenes.
     * Si no se incluye, Blanca Ramos.
     */
    public void setPostersData (String[] newPostersData) {
        postersPath = newPostersData;
        notifyDataSetChanged();
    }
}