package com.example.movie;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.movie.adapters.MovieRecycleView;
import com.example.movie.adapters.OnMovieListener;
import com.example.movie.models.viewmodels.PlayingMovieListViewModel;
import com.example.movie.models.viewmodels.SearchMovieListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MovieListActivity extends AppCompatActivity implements OnMovieListener {

    private PlayingMovieListViewModel playingMovieListViewModel;
    private SearchMovieListViewModel searchMovieListViewModel;
    private RecyclerView recyclerView;
    private MovieRecycleView recycleViewAdapter;
    private boolean isPlaying = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_movie_list);

        // Initialize RecyclerView and Toolbar
        recyclerView = findViewById(R.id.recycleView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.now);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            int id = item.getItemId();
            if (id == R.id.popular) {
                // Start NowListActivity when "Now" is selected
                startActivity(new Intent(this, TopListActivity.class));
                finish(); // Finish the current activity to prevent going back
                return true;
            }
            return false;
        });

        // Initialize ViewModels
        playingMovieListViewModel = new ViewModelProvider(this).get(PlayingMovieListViewModel.class);
        searchMovieListViewModel = new ViewModelProvider(this).get(SearchMovieListViewModel.class);

        // Setup search functionality
        searchSetup();

        // Observe changes in playing movie list
        observingAnyChangesPlayingMovie();

        // Observe changes in search movie list
        observingAnyChangesSearchMovie();
        configureRecyclerView();

        // Load playing movies
        playingMovieListViewModel.getPlayingMovie(1);
    }

    // Setup search
    private void searchSetup() {
        final SearchView searchView = findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                // Perform search only when user submits the query
                searchMovieListViewModel.getSearchMovie(query, 1);
                isPlaying = false;
                return true; // Return true to indicate that the query has been handled
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Remove search operation from here
                return false; // Return false to indicate that the text change event has not been handled
            }
        });
    }

    // Initialize RecyclerView
    private void configureRecyclerView() {
        recycleViewAdapter = new MovieRecycleView(this);
        recyclerView.setAdapter(recycleViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Pagination & loading next results
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                if (!recyclerView.canScrollVertically(1)) {
                    if (isPlaying) {
                        playingMovieListViewModel.playingMovieNextPage();
                    }
                }
            }
        });
    }

    // Observe changes in playing movie list
    private void observingAnyChangesPlayingMovie() {
        playingMovieListViewModel.getPlayingMovie().observe(this, movieModels -> {
            if (movieModels != null) {
                recycleViewAdapter.setMovie(movieModels);
            }
        });
    }

    // Observe changes in search movie list
    private void observingAnyChangesSearchMovie() {
        searchMovieListViewModel.getSearchMovie().observe(this, movieModels -> {
            if (movieModels != null) {
                recycleViewAdapter.setMovie(movieModels);
            }
        });
    }

    // Handle movie item click
    @Override
    public void onMovieClick(int pos) {
        Intent intent = new Intent(this, DetailMovieActivity.class);
        intent.putExtra("movie", recycleViewAdapter.getSelectedMovie(pos));
        startActivity(intent);
    }

    // Inflate menu with search functionality
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.bottom_nav_menu, menu);
        return true;
    }

    // Handle menu item selection
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.popular) {
            // Start NowListActivity when "Popular" is selected
            startActivity(new Intent(this, TopListActivity.class));
            finish(); // Finish the current activity to prevent going back
            return true;
        } else if (id == R.id.now) {
            // Start NowListActivity when "Now" is selected
            startActivity(new Intent(this, MovieListActivity.class));
            finish(); // Finish the current activity to prevent going back
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
