package com.example.movie.repositories;

import androidx.lifecycle.LiveData;

import com.example.movie.models.MovieModel;
import com.example.movie.request.PlayingMovieApiClient;

import java.util.List;

public class PlayingMovieRepository {
    private static PlayingMovieRepository instance;
    private PlayingMovieApiClient playingMovieApiClient;

    private int page;

    public static PlayingMovieRepository getInstance() {
        if (instance == null) {
            instance = new PlayingMovieRepository();
        }

        return instance;
    }

    private PlayingMovieRepository() {
        playingMovieApiClient = PlayingMovieApiClient.getInstance();
    }

    public LiveData<List<MovieModel>> getPlayingMovie() {
        return playingMovieApiClient.getPlayingMovie();
    }

    public void getPlayingMovie(int page) {
        this.page = page;
        playingMovieApiClient.getPlayingMovie(page);
    }

    // next page
    public void playingMovieNextPage() {
        getPlayingMovie(page+1);
    }
}
