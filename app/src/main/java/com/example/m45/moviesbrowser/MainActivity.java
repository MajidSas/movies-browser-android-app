package com.example.m45.moviesbrowser;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

public class MainActivity extends Activity implements Communicator {

    String page = "popular";
    ViewSwitcher switcher;
    Button buttonRetry;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if(Utils.isTablet(this)) {
            setContentView(R.layout.tablet_main_view);

        }
        else {
            setContentView(R.layout.activity_main);
        }

        buttonRetry = (Button) findViewById(R.id.buttonRetry);

        switcher = (ViewSwitcher) findViewById(R.id.viewSwitcher);


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);

        menu.add("Favorites");
        menu.add("Now Playing");
        menu.add("Popular");
        menu.add("Top Rated");

        return true;
    }

    @Override
    public void respond(String movieId) {
        if(Utils.isTablet(this)) {
            FragmentManager fm = getFragmentManager();
            MovieDataFragment fragment = (MovieDataFragment) fm.findFragmentById(R.id.dataFragment);
            fragment.selectMovie(Integer.parseInt(movieId));
        }
        else {
            Intent intent = new Intent(this, MovieDataActivity.class);
            intent.putExtra("movieId", movieId);
            startActivity(intent);
        }
    }

    @Override
    public void loadingError() {
        if(switcher.getNextView().getId() != R.id.mainView)
            switcher.showNext();
        buttonRetry.setVisibility(View.VISIBLE);
    }
    @Override
    public void loadingSuccess() {
        if(switcher.getNextView().getId() == R.id.mainView)
            switcher.showNext();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getTitle().toString()) {
            case "Popular":
                page = "popular";
                loadListItems();
                setTitle("Popular Movies");
                break;
            case "Top Rated":
                page = "top_rated";
                loadListItems();
                setTitle("Top Rated Movies");
                break;
            case "Now Playing":
                page = "now_playing";
                loadListItems();
                setTitle("Now Playing");
                break;
            case "Favorites":
                page = "favorites";
                loadListItems();
                setTitle("Favorite Movies");
                break;
        }


        return super.onOptionsItemSelected(item);
    }



    public void retry(View view) {
        loadListItems();
        switcher.showPrevious();
        view.setVisibility(View.INVISIBLE);
    }

    public void loadListItems() {
        FragmentManager fm = getFragmentManager();
        MoviesListFragment f = (MoviesListFragment) fm.findFragmentById(R.id.listFragment);
        f.changeSorting(page);
    }
}
