package com.example.movie.utils;

import com.example.movie.response.PopularMovieResponses;
import com.example.movie.response.PlayingMovieResponses;
import com.example.movie.response.SearchMovieResponses;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface MovieApi {
    // popular movie
    @GET("movie/top_rated")
    Call<PopularMovieResponses> getPopularMovie(@Query("api_key") String key,
                                               @Query("page") int page);
    @GET("movie/now_playing")
    Call<PlayingMovieResponses> getPlayingMovie(@Query("api_key") String key,
                                                @Query("page") int page);


    // search movie
    @GET("search/movie")
    Call<SearchMovieResponses> searchMovie(@Query("api_key") String key,
                                           @Query("query") String query,
                                           @Query("page") int page);

}
