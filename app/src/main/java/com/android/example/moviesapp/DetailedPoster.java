package com.android.example.moviesapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import com.android.example.moviesapp.utilities.GlobalVariables;
import com.squareup.picasso.Picasso;


/* Clase asociada a la actividad secundaria que muestra los detalles de cada pel'icula. */
public class DetailedPoster extends AppCompatActivity {

    ImageView mPosterDisplay;
    TextView mTitleDisplay;
    TextView mOTitleDisplay;
    RatingBar mStars;           // Muestra las estrellas de calificaci'on
    TextView mRating;
    TextView mReleaseDate;
    TextView mOverview;

    /* Override onCreate
     * Cargar la plantilla activity_detailed_poster.xml en vez de la plantilla en blanco por defecto
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detailed_poster);

        mPosterDisplay = (ImageView) findViewById(R.id.iv_detail_posterPath);
        mTitleDisplay  = (TextView)  findViewById(R.id.tv_detail_title);
        mOTitleDisplay = (TextView)  findViewById(R.id.tv_detail_original_title);
        mRating        = (TextView)  findViewById(R.id.tv_detail_rating);
        mReleaseDate   = (TextView)  findViewById(R.id.tv_detail_releaseDate);
        mOverview      = (TextView)  findViewById(R.id.tv_detail_overview);
        mStars         = (RatingBar) findViewById(R.id.rb_rating);

        /* En el main act'ivity se hace click sobre un poster, este se idenifica y crea un
         * intent para lanzar esta actividad.
         * En el intent se recibe la posici'on ( int adapterPosition) del poster seleccionado,
         * con este n'umero busca en GlobalVariables los arrays con la informaci'on de todas las
         * pel'iculas y retorna en espec'ifico los detalles de la pel'icula de inter'es
         */
        Intent intentThatStartedThisActivity = getIntent();
        if (intentThatStartedThisActivity.hasExtra("adapterPosition")){
            String number = intentThatStartedThisActivity.getStringExtra("adapterPosition") ;

            // Se convierte el string de posici'on a un n'umero entero.
            int n = Integer.valueOf(number);

            Picasso.with(this).load(GlobalVariables.all_postersPaths[n]).into(mPosterDisplay);
            mTitleDisplay.setText( GlobalVariables.all_titles[n]);
            mOTitleDisplay.setText( "Original title: " + GlobalVariables.all_originalTitles[n]);
            mReleaseDate.setText( "Release Date: " + GlobalVariables.all_releaseDates[n]);

            // mostrar la puntuaci'on de la escala de 0 a 10 en una escala de estrellas de 0 a 5.
            float stars = Float.parseFloat(GlobalVariables.all_ratings[n]);
            stars = stars / 2 ;
            mStars.setRating( stars );

            mRating.setText( "Rating: " + GlobalVariables.all_ratings[n] + "/10");
            mOverview.setText( "Overview \n" + GlobalVariables.all_overviews[n]);

        }
    }
}