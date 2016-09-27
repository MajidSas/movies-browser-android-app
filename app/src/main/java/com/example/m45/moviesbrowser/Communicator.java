package com.example.m45.moviesbrowser;

/**
 * Created by m45 on 8/5/16.
 */
public interface Communicator {
    void respond(String movie_id);
    void loadingError();
    void loadingSuccess();
}
