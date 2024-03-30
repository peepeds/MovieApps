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
import com.example.movie.models.viewmodels.PopularMovieListViewModel;
import com.example.movie.models.viewmodels.SearchMovieListViewModel;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class TopListActivity extends AppCompatActivity implements OnMovieListener {

    private PopularMovieListViewModel popularMovieListViewModel;

    private RecyclerView recyclerView;
    private MovieRecycleView recycleViewAdapter;
    private boolean isPopular = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_top_list);

        // Initialize RecyclerView and Toolbar
        recyclerView = findViewById(R.id.recycleView);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottomNavigation);
        bottomNavigationView.setSelectedItemId(R.id.popular);

        bottomNavigationView.setOnItemSelectedListener(item ->{
            int id = item.getItemId();
            if (id == R.id.now) {
                // Start NowListActivity when "Now" is selected
                startActivity(new Intent(this, MovieListActivity.class));
                finish(); // Finish the current activity to prevent going back
                return true;
            }
            return false;
        });

        // Initialize ViewModels
        popularMovieListViewModel = new ViewModelProvider(this).get(PopularMovieListViewModel.class);


        // Observe changes in popular movie list
        observingAnyChangesPopularMovie();

        configureRecyclerView();

        // Load popular movies
        popularMovieListViewModel.getPopularMovie(1);
    }
    // Setup search

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
                    if (isPopular) {
                        popularMovieListViewModel.popularMovieNextPage();
                    }
                }
            }
        });
    }

    // Observe changes in popular movie list
    private void observingAnyChangesPopularMovie() {
        popularMovieListViewModel.getPopularMovie().observe(this, movieModels -> {
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
