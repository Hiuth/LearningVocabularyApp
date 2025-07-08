package com.example.learningvocabularyapp.manager;

import android.content.Context;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;

import androidx.appcompat.widget.SearchView;

import com.example.learningvocabularyapp.model.Project;

import java.util.ArrayList;
import java.util.List;

public class SearchManager {
    
    private Context context;
    private SearchView searchView;
    private ImageButton buttonSearch;
    private boolean isSearchViewVisible = false;
    private String currentSearchQuery = "";
    
    public interface SearchListener {
        void onSearchQueryChanged(String query);
        void onSearchStateChanged(boolean isVisible);
    }
    
    private SearchListener searchListener;
    
    public SearchManager(Context context, SearchView searchView, ImageButton buttonSearch) {
        this.context = context;
        this.searchView = searchView;
        this.buttonSearch = buttonSearch;
        setupSearchFunctionality();
    }
    
    public void setSearchListener(SearchListener listener) {
        this.searchListener = listener;
    }
    
    private void setupSearchFunctionality() {
        // Search button click listener
        buttonSearch.setOnClickListener(v -> showSearchView());

        // SearchView listeners
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                currentSearchQuery = query;
                if (searchListener != null) {
                    searchListener.onSearchQueryChanged(query);
                }
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                currentSearchQuery = newText;
                if (searchListener != null) {
                    searchListener.onSearchQueryChanged(newText);
                }
                return true;
            }
        });

        // SearchView close listener
        searchView.setOnCloseListener(() -> {
            hideSearchView();
            return false;
        });

        // SearchView focus change listener
        searchView.setOnQueryTextFocusChangeListener((v, hasFocus) -> {
            if (!hasFocus && currentSearchQuery.isEmpty()) {
                hideSearchView();
            }
        });
    }
    
    public void showSearchView() {
        isSearchViewVisible = true;
        buttonSearch.setVisibility(View.GONE);
        searchView.setVisibility(View.VISIBLE);
        
        // Focus and show keyboard
        searchView.setIconified(false);
        searchView.requestFocus();
        showKeyboard();
        
        if (searchListener != null) {
            searchListener.onSearchStateChanged(true);
        }
    }
    
    public void hideSearchView() {
        isSearchViewVisible = false;
        searchView.setVisibility(View.GONE);
        buttonSearch.setVisibility(View.VISIBLE);
        searchView.setQuery("", false);
        hideKeyboard();
        
        if (searchListener != null) {
            searchListener.onSearchStateChanged(false);
        }
    }
    
    public void hideSearchViewOnly() {
        isSearchViewVisible = false;
        searchView.setVisibility(View.GONE);
        buttonSearch.setVisibility(View.VISIBLE);
        hideKeyboard();
        
        // Không clear search query
        // Không call onSearchStateChanged để không trigger refresh
    }
    
    public void hideSearchViewIfVisible() {
        if (isSearchViewVisible) {
            hideSearchView();
        }
    }

    public void hideSearchViewWithoutClearingQuery() {
        isSearchViewVisible = false;
        searchView.setVisibility(View.GONE);
        buttonSearch.setVisibility(View.VISIBLE);
        hideKeyboard();
        
        if (searchListener != null) {
            searchListener.onSearchStateChanged(false);
        }
    }
    
    public void clearSearch() {
        currentSearchQuery = "";
        searchView.setQuery("", false);
        if (searchListener != null) {
            searchListener.onSearchQueryChanged("");
        }
    }
    
    public List<Project> searchInProjects(List<Project> projects, String query) {
        List<Project> searchResults = new ArrayList<>();
        String lowercaseQuery = query.toLowerCase().trim();
        
        for (Project project : projects) {
            // Search in project name
            if (project.getProjectName().toLowerCase().contains(lowercaseQuery)) {
                searchResults.add(project);
                continue;
            }
            
            // Search in learning language
            if (project.getLearningLanguage().toLowerCase().contains(lowercaseQuery)) {
                searchResults.add(project);
                continue;
            }
        }
        
        return searchResults;
    }
    
    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(searchView, InputMethodManager.SHOW_IMPLICIT);
        }
    }
    
    private void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(searchView.getWindowToken(), 0);
        }
    }
    
    // Getters
    public boolean isSearchViewVisible() {
        return isSearchViewVisible;
    }
    
    public String getCurrentSearchQuery() {
        return currentSearchQuery;
    }
}