package com.example.movie.models.viewmodels;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.example.movie.models.MovieModel;
import com.example.movie.repositories.PlayingMovieRepository;

import java.util.List;

public class PlayingMovieListViewModel extends ViewModel {
    private PlayingMovieRepository playingMovieRepository;

    public PlayingMovieListViewModel() {
        playingMovieRepository = PlayingMovieRepository.getInstance();
    }

    public LiveData<List<MovieModel>> getPlayingMovie() {
        return playingMovieRepository.getPlayingMovie();
    }

    public void getPlayingMovie(int page) {
        playingMovieRepository.getPlayingMovie(page);
    }

    // next page
    public void playingMovieNextPage() {
        playingMovieRepository.playingMovieNextPage();
    }
}
