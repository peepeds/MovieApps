package com.example.movie.request;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.movie.models.MovieModel;
import com.example.movie.response.PlayingMovieResponses;
import com.example.movie.response.PopularMovieResponses;
import com.example.movie.utils.AppExecutor;
import com.example.movie.utils.Credentials;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import retrofit2.Call;
import retrofit2.Response;

public class PlayingMovieApiClient {
    private static PlayingMovieApiClient instance;
    public static PlayingMovieApiClient getInstance() {
        if (instance == null) {
            instance = new PlayingMovieApiClient();
        }
        return instance;
    }

    private final MutableLiveData<List<MovieModel>> playingMovieLiveData;

    private PlayingRunnable playingRunnable;

    private PlayingMovieApiClient() {
        playingMovieLiveData = new MutableLiveData<>();
    }

    public LiveData<List<MovieModel>> getPlayingMovie() {
        return playingMovieLiveData;
    }

    public void getPlayingMovie(int page) {
        if (playingRunnable != null) {
            playingRunnable = null;
        }

        playingRunnable = new PlayingRunnable(page);

        final Future handler = AppExecutor.getInstance().getNetworkIO().submit(playingRunnable);

        AppExecutor.getInstance().getNetworkIO().schedule(() -> {
            // canceling retrofit call
            handler.cancel(true);
        },3000, TimeUnit.MILLISECONDS);
    }

    private class PlayingRunnable implements Runnable {

        private final int page;
        boolean cancleRequest;

        public PlayingRunnable(int page) {
            this.page = page;
            cancleRequest = false;
        }

        @Override
        public void run() {
            try {
                Response response = getPlayingMovie(page).execute();

                if (cancleRequest) {
                    return;
                }

                if (response.isSuccessful()) {
                    if (response.code() == 200) {
                        assert response.body() != null;
                        List<MovieModel> playingMovieList = new ArrayList<>(((PlayingMovieResponses) response.body()).getMovies());

                        if (page == 1) {
                            playingMovieLiveData.postValue(playingMovieList);
                        } else {
                            List<MovieModel> currentMovie = playingMovieLiveData.getValue();
                            assert currentMovie != null;
                            currentMovie.addAll(playingMovieList);
                            playingMovieLiveData.postValue(currentMovie);
                        }
                    } else {
                        assert response.errorBody() != null;
                        System.out.println(response.errorBody().string());
                        playingMovieLiveData.postValue(null);
                    }
                } else {
                    System.out.println("request isnt successful");
                }
            } catch (IOException e) {
                System.out.println(e);
                playingMovieLiveData.postValue(null);
            }
        }

        private Call<PlayingMovieResponses> getPlayingMovie(int page) {
            return ApiService.getMovieApi().getPlayingMovie(Credentials.KEY,page);
        }

    }
}
