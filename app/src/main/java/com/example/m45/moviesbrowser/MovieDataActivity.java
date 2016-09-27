package com.example.m45.moviesbrowser;

import android.app.Activity;
import android.app.FragmentManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ViewSwitcher;

public class MovieDataActivity extends Activity implements Communicator {

    ViewSwitcher viewSwitcher;
    Button buttonRetry;
    int movieId = -1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_data);
        setTitle("Movie Data");

        buttonRetry = (Button) findViewById(R.id.buttonRetry);

        viewSwitcher = (ViewSwitcher) this.findViewById(R.id.viewSwitcherMovieDataActivity);
        Bundle extras = getIntent().getExtras();
        movieId = Integer.parseInt(extras.getString("movieId"));

        FragmentManager fm = getFragmentManager();
        MovieDataFragment fragment = (MovieDataFragment) fm.findFragmentById(R.id.dataFragment);
        fragment.selectMovie(movieId);

    }

    @Override
    public void respond(String movie_id) {

    }

    @Override
    public void loadingError() {
        if(viewSwitcher.getNextView().getId() != R.id.movieDataMainView)
            viewSwitcher.showNext();
        buttonRetry.setVisibility(View.VISIBLE);
    }
    @Override
    public void loadingSuccess() {
        if(viewSwitcher.getNextView().getId() == R.id.movieDataMainView)
            viewSwitcher.showNext();
    }

    public void retry(View view) {
        MovieDataFragment fragment = (MovieDataFragment) getFragmentManager().findFragmentById(R.id.dataFragment);
        fragment.loadData();
        view.setVisibility(View.INVISIBLE);
    }
}
