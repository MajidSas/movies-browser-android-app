package com.example.m45.moviesbrowser;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;

import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Created by m45 on 8/5/16.
 */
public class MovieDataFragment extends Fragment {


    int movieId = -1;

    Button btnFavorite, btnRemove;
    ImageView imageViewCover, imageViewPoster;
    TextView textViewTitle,textViewYear, textViewRuntime, textViewRating, textViewPlot;
    ListView listViewTrailers;
    ArrayList<String> trailers_titles, trailers_keys, thumbnails;
    Communicator comm;
    ViewSwitcher viewSwitcher;
    String cover, poster, title, year, runtime, rating, plot;
    ScrollView sv;
    DatabaseHelper databaseHelper;
    ProgressBar movieProgres;



    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        databaseHelper = new DatabaseHelper(getActivity(), "movies", null, 1);
        comm = (Communicator) getActivity();
        View fragmentView = inflater.inflate(R.layout.fragment_movie_data,container,false);

        sv = (ScrollView) fragmentView.findViewById(R.id.scrollViewMovie);

        imageViewCover = (ImageView) fragmentView.findViewById(R.id.imageViewCover);
        imageViewPoster = (ImageView) fragmentView.findViewById(R.id.imageViewPoster);

        textViewTitle = (TextView) fragmentView.findViewById(R.id.textViewTitle);
        textViewYear = (TextView) fragmentView.findViewById(R.id.textViewYear);
        textViewRuntime = (TextView) fragmentView.findViewById(R.id.textViewRuntime);
        textViewRating = (TextView) fragmentView.findViewById(R.id.textViewRating);
        textViewPlot = (TextView) fragmentView.findViewById(R.id.textViewPlot);

        listViewTrailers = (ListView) fragmentView.findViewById(R.id.listViewTrailers);

        btnFavorite = (Button) fragmentView.findViewById(R.id.buttonFavorite);
        btnFavorite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(movieId != -1
                        && imageViewPoster.getDrawable() != null
                        && imageViewCover.getDrawable() != null) {

                    Bitmap posterDrawable = ((BitmapDrawable)imageViewPoster.getDrawable()).getBitmap();
                    Bitmap coverDrawable = ((BitmapDrawable)imageViewCover.getDrawable()).getBitmap();

                    if(posterDrawable != null)
                        Utils.saveImage(getActivity(), posterDrawable, "poster_" + movieId + ".jpg");
                    if(coverDrawable != null)
                        Utils.saveImage(getActivity(), coverDrawable, "cover_" + movieId + ".jpg");


                    databaseHelper.saveMovie(movieId, textViewTitle.getText().toString(),
                            textViewYear.getText().toString(), textViewRuntime.getText().toString(),
                            textViewRating.getText().toString(), textViewPlot.getText().toString(),
                            poster, cover);

                    for(int i = 0, size = trailers_titles.size(); i < size; i++) {
                        databaseHelper.saveTrailer(movieId, trailers_titles.get(i), trailers_keys.get(i));
                    }

                    view.setVisibility(View.GONE);
                    btnRemove.setVisibility(View.VISIBLE);
                }
            }
        });

        btnRemove = (Button) fragmentView.findViewById(R.id.buttonRemove);
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(movieId != -1 && databaseHelper.isFavoriteMovie(movieId)) {
                    databaseHelper.deleteMovie(movieId);
                    view.setVisibility(View.GONE);
                    btnFavorite.setVisibility(View.VISIBLE);
                }
            }
        });



        listViewTrailers.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String video_url = "http://youtube.com/watch?v=" + trailers_keys.get(i);
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(video_url));
                startActivity(intent);
            }
        });

        trailers_titles = new ArrayList<String>();
        trailers_keys = new ArrayList<String>();
        thumbnails = new ArrayList<String>();

        movieProgres = (ProgressBar) fragmentView.findViewById(R.id.movieProgress);
        viewSwitcher = (ViewSwitcher)  fragmentView.findViewById(R.id.viewSwitcher2);

        return fragmentView;
    }



    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);


        if(savedInstanceState != null) {
            movieId = savedInstanceState.getInt("movie_id");
            if(movieId != -1) {
                textViewTitle.setText(savedInstanceState.getString("title"));
                textViewYear.setText(savedInstanceState.getString("year"));
                textViewRuntime.setText(savedInstanceState.getString("runtime"));
                textViewRating.setText(savedInstanceState.getString("rating"));
                textViewPlot.setText(savedInstanceState.getString("plot"));
                trailers_titles = savedInstanceState.getStringArrayList("trailers_titles");
                trailers_keys = savedInstanceState.getStringArrayList("trailers_keys");
                thumbnails = savedInstanceState.getStringArrayList("thumbnails");

                cover  = savedInstanceState.getString("cover");
                poster = savedInstanceState.getString("poster");
                Picasso.with(getActivity()).load(cover).into(imageViewCover);
                Picasso.with(getActivity()).load(poster).into(imageViewPoster);

                ArrayAdapter ad = new ArrayAdapter(getActivity(), android.R.layout.simple_list_item_1, trailers_titles);


                ViewGroup.LayoutParams params = listViewTrailers.getLayoutParams();
                params.height = savedInstanceState.getInt("listHeight");
                listViewTrailers.setLayoutParams(params);

                listViewTrailers.setAdapter(ad);
                listViewTrailers.onRestoreInstanceState(savedInstanceState.getParcelable("list_state"));
            }
            else {
                showEmpty(true);
            }
        }
        else if(Utils.isTablet(getActivity())) {
            showEmpty(true);
            movieProgres.setVisibility(View.GONE);
        }


    }

    class DownloadData extends AsyncTask<String,Void,String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            movieProgres.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... urls) {
           try {
                String response = Utils.getResponse(urls[0]);
                JSONObject movie = new JSONObject(response);

                cover   = "http://image.tmdb.org/t/p/w780" + movie.getString("backdrop_path");
                poster  = "http://image.tmdb.org/t/p/w185" + movie.getString("poster_path");
                title   = movie.getString("title");
                year    = movie.getString("release_date").substring(0,4);
                runtime = movie.getString("runtime") + "min";
                rating  = movie.getString("vote_average") + "/10";
                plot    = movie.getString("overview");
            } catch (JSONException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }


           try {
                String response = Utils.getResponse(urls[1]);
                JSONArray trailers = (new JSONObject(response)).getJSONArray("results");
                trailers_titles = new ArrayList<String>();
                trailers_keys = new ArrayList<String>();
                for(int i = 0, size = trailers.length(); i < size; i++) {
                    JSONObject trailer = trailers.getJSONObject(i);
                    if(trailer.getString("site").equals("YouTube")) {
                        trailers_titles.add(trailer.getString("name"));
                        trailers_keys.add(trailer.getString("key"));
                    }
                }
                return "success";
           } catch (JSONException e) {
                e.printStackTrace();
           } catch (IOException e) {
                e.printStackTrace();
           }

            return "failure";

        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            movieProgres.setVisibility(View.GONE);
            if(s.equals("failure")) {
                comm.loadingError();
            }
            else {

                textViewTitle.setText(title);
                textViewYear.setText(year);
                textViewRuntime.setText(runtime);
                textViewRating.setText(rating);
                textViewPlot.setText(plot);

                setTrailersList();

                comm.loadingSuccess();

                showEmpty(false);

                Picasso.with(getActivity()).load(cover).into(imageViewCover);
                Picasso.with(getActivity()).load(poster).into(imageViewPoster);

                Handler h = new Handler();

                h.postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        sv.scrollTo(0, 0);
                    }
                }, 1000);





            }

        }
    }

    public void setTrailersList() {
        if(trailers_titles != null && trailers_titles.size() > 0) {
            ArrayAdapter ad = new ArrayAdapter((Activity) comm, android.R.layout.simple_list_item_1, trailers_titles);

            int totalHeight = 0;
            int maxItemHeight = 0;
            for (int i = 0; i < ad.getCount(); i++) {
                View listItem = ad.getView(i, null, listViewTrailers);
                listItem.measure(0, 0);
                totalHeight += listItem.getMeasuredHeight();
                if (maxItemHeight < listItem.getMeasuredHeight())
                    maxItemHeight = listItem.getMeasuredHeight();
            }
            totalHeight += maxItemHeight;

            ViewGroup.LayoutParams params = listViewTrailers.getLayoutParams();
            params.height = totalHeight + (listViewTrailers.getDividerHeight() * (ad.getCount() - 1));
            listViewTrailers.setLayoutParams(params);

            listViewTrailers.setAdapter(ad);
        }
    }

    public void loadData() {
        (new DownloadData()).execute("https://api.themoviedb.org/3/movie/" + movieId
                + "?api_key=" + getResources().getString(R.string.api_key) ,
                "https://api.themoviedb.org/3/movie/" + movieId
                        + "/videos?api_key=" + getResources().getString(R.string.api_key));
    }


    public void selectMovie(int i) {

        movieId = i;

        if(movieId != -1 && databaseHelper.isFavoriteMovie(movieId)) {
            btnFavorite.setVisibility(View.GONE);
            btnRemove.setVisibility(View.VISIBLE);
            showEmpty(false);
            comm.loadingSuccess();
            Cursor c = databaseHelper.getMovie(movieId);

            if(c.moveToNext()) {
                textViewTitle.setText(c.getString(2));
                textViewYear.setText(c.getString(3));
                textViewRuntime.setText(c.getString(4));
                textViewRating.setText(c.getString(5));
                textViewPlot.setText(c.getString(6));
                poster = c.getString(7);
                cover = c.getString(8);
                Bitmap bitmap = Utils.loadImage(getActivity(), "poster_" + movieId + ".jpg");
                if(bitmap == null)
                    Picasso.with(getActivity()).load(poster).into(imageViewPoster);
                else
                    imageViewPoster.setImageBitmap(bitmap);

                bitmap = Utils.loadImage(getActivity(), "cover_" + movieId + ".jpg");
                if(bitmap == null)
                    Picasso.with(getActivity()).load(cover).into(imageViewCover);
                else
                    imageViewCover.setImageBitmap(bitmap);

                trailers_titles = new ArrayList<String>();
                trailers_keys   = new ArrayList<String>();
                c = databaseHelper.getTrailers(movieId);
                while(c.moveToNext()) {
                    trailers_titles.add(c.getString(2));
                    trailers_keys.add(c.getString(3));
                }

                setTrailersList();



            }



        } else {
            btnRemove.setVisibility(View.GONE);
            btnFavorite.setVisibility(View.VISIBLE);
            showEmpty(true);
            loadData();
        }
    }





    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putInt("movieId", movieId);
        if(movieId != -1) {
            outState.putString("title", textViewTitle.getText().toString());
            outState.putString("year", textViewYear.getText().toString());
            outState.putString("runtime", textViewRuntime.getText().toString());
            outState.putString("rating", textViewRating.getText().toString());
            outState.putString("plot", textViewPlot.getText().toString());
            outState.putStringArrayList("trailers_titles", trailers_titles);
            outState.putStringArrayList("trailers_keys", trailers_keys);
            outState.putString("cover", cover);
            outState.putString("poster", poster);

            outState.putParcelable("list_state", listViewTrailers.onSaveInstanceState());
            ViewGroup.LayoutParams params = listViewTrailers.getLayoutParams();
            outState.putInt("listHeight", params.height);
        }

    }

    public void showEmpty(boolean empty) {
        if (empty && viewSwitcher.getNextView().getId() == R.id.movieDataEmpty)
            viewSwitcher.showNext();
        else if(!empty && viewSwitcher.getNextView().getId() != R.id.movieDataEmpty)
            viewSwitcher.showNext();

    }



}
