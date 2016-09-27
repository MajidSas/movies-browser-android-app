package com.example.m45.moviesbrowser;


import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ProgressBar;
import android.widget.Toast;

import org.apache.commons.lang3.text.StrBuilder;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.helpers.Util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import info.movito.themoviedbapi.TmdbApi;
import info.movito.themoviedbapi.TmdbMovies;
import info.movito.themoviedbapi.model.MovieDb;
import info.movito.themoviedbapi.tools.MovieDbException;


public class MoviesListFragment extends Fragment {

    GridView gridView;
    Communicator comm;
    DatabaseHelper databaseHelper;
    ArrayList<String> posters;
    ArrayList<String> movieIds;
    ThumbnailsAdapter adapter;
    ProgressBar listProgress;
    String page = "popular";
    int currentPage = 1, totalPages = 0;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movies_list,container,false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        comm = (Communicator) getActivity();
        databaseHelper = new DatabaseHelper(getActivity(), "movies", null, 1);
        gridView = (GridView) getActivity().findViewById(R.id.gridView);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                comm.respond(movieIds.get(i));
            }
        });

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int scrollState) {
                if (scrollState == SCROLL_STATE_IDLE) {
                    if (gridView.getLastVisiblePosition() >= gridView.getCount() - 1 - 20
                        && currentPage < totalPages) {
                        currentPage++;
                        //load more list items:
                        loadData();
                    }
                }
            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
            }
        });

        listProgress = (ProgressBar) (ProgressBar) getActivity().findViewById(R.id.listProgress);

        if(savedInstanceState != null) {
            page = savedInstanceState.getString("page");
            currentPage = savedInstanceState.getInt("currentPage");
            totalPages = savedInstanceState.getInt("totalPages");
            posters = savedInstanceState.getStringArrayList("posters");
            movieIds = savedInstanceState.getStringArrayList("movie_ids");
            adapter = new ThumbnailsAdapter(getActivity(), posters);
            gridView.setAdapter(adapter);
            gridView.onRestoreInstanceState(savedInstanceState.getParcelable("gridState"));
        }
        else {
            posters = new ArrayList<String>();
            movieIds = new ArrayList<String>();

            loadData();
        }

    }


    class DownloadData extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            listProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {

                JSONObject json = new JSONObject(Utils.getResponse(strings[0]));
                JSONArray jsonArray = json.getJSONArray("results");
                totalPages = json.getInt("total_pages");
                for(int i = 0, size = jsonArray.length(); i < size; i++) {
                    JSONObject movie = jsonArray.getJSONObject(i);
                    posters.add("http://image.tmdb.org/t/p/w185" + movie.getString("poster_path"));
                    movieIds.add(movie.getString("id"));
                }

                return "success";

            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return "failure";

        }

        @Override
        protected void onPostExecute(String s) {

            super.onPostExecute(s);
            if(s.equals("failure")) {
                comm.loadingError();
            }
            else {
                if(adapter == null) {
                    adapter = new ThumbnailsAdapter(getActivity(), posters);
                    gridView.setAdapter(adapter);
                }
                else {
                    adapter.notifyDataSetChanged();
                }

                listProgress.setVisibility(View.GONE);

                comm.loadingSuccess();
            }
        }
    }

    public void loadData() {
        if(page.equals("favorites")) {
            Cursor c = databaseHelper.getMovies();
            while(c.moveToNext()){
                posters.add(c.getString(7));
                movieIds.add(c.getString(1));
                totalPages = 0;
            }

            listProgress.setVisibility(View.GONE);
        }
        else {
            (new DownloadData()).execute("https://api.themoviedb.org/3/movie/" + page +
                    "?api_key=" + getResources().getString(R.string.api_key) + "&page=" + currentPage);
        }
    }

    public void changeSorting(String sortBy) {
        page = sortBy;
        posters = new ArrayList<String>();
        movieIds = new ArrayList<String>();
        adapter = new ThumbnailsAdapter(getActivity(), posters);
        currentPage = 1;
        loadData();
        gridView.setAdapter(adapter);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putStringArrayList("posters", posters);
        outState.putStringArrayList("movie_ids", movieIds);
        outState.putString("page", page);
        outState.putInt("currentPage", currentPage);
        outState.putParcelable("gridState", gridView.onSaveInstanceState());
        outState.putInt("totalPages", totalPages);


    }
}
