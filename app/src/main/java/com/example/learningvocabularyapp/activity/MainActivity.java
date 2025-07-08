package com.example.learningvocabularyapp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.RecyclerView;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.activity.CreateProjectActivity;
import com.example.learningvocabularyapp.database.vocabularyAppDatabase;
import com.example.learningvocabularyapp.manager.LanguageTabManager;
import com.example.learningvocabularyapp.manager.ProjectDisplayManager;
import com.example.learningvocabularyapp.manager.SearchManager;
import com.example.learningvocabularyapp.manager.ThemeManager;
import com.example.learningvocabularyapp.model.Project;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    // UI Components
    private Button createProjectButton;
    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private SearchView searchView;
    private ImageButton buttonSearch;
    private LinearLayout languageTabsContainer;
    private ImageButton buttonToggleTheme;

    // Managers
    private SearchManager searchManager;
    private ThemeManager themeManager;
    private LanguageTabManager languageTabManager;
    private ProjectDisplayManager projectDisplayManager;

    // Database
    private vocabularyAppDatabase db;
    private List<Project> allProjects;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // FIX: Tạo ThemeManager với dummy để apply theme trước
        ThemeManager.applyThemeFromPreferences(this);
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        initViews();
        setupManagers();
        setupClickListeners();
        
        // Load initial data
        loadProjectList();
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadProjectList();
    }

    private void initViews() {
        createProjectButton = findViewById(R.id.createProjectButton);
        recyclerView = findViewById(R.id.recyclerViewProjects);
        textViewEmpty = findViewById(R.id.textViewEmpty);
        searchView = findViewById(R.id.searchView);
        buttonSearch = findViewById(R.id.buttonSearch);
        languageTabsContainer = findViewById(R.id.languageTabsContainer);
        buttonToggleTheme = findViewById(R.id.buttonToggleTheme);
        
        db = new vocabularyAppDatabase(this);
    }

    private void setupManagers() {
        System.out.println("=== SETUP MANAGERS DEBUG ===");
        
        // Initialize managers
        searchManager = new SearchManager(this, searchView, buttonSearch);
        themeManager = new ThemeManager(this, buttonToggleTheme, new ThemeManager.ThemeChangeListener() {
            @Override
            public void onThemeChanged(boolean isDarkMode) {
                // Refresh tab styles after theme change
                if (languageTabManager != null) {
                    languageTabManager.updateAllTabStyles();
                }
                
                // Update any other UI elements that need manual refresh
                updateUIForTheme(isDarkMode);
            }
        });
        languageTabManager = new LanguageTabManager(this, languageTabsContainer);
        projectDisplayManager = new ProjectDisplayManager(this, recyclerView, textViewEmpty);

        System.out.println("All managers initialized");

        // Set up manager listeners
        searchManager.setSearchListener(new SearchManager.SearchListener() {
            @Override
            public void onSearchQueryChanged(String query) {
                System.out.println("Search query changed: " + query);
                performSearch(query);
            }

            @Override
            public void onSearchStateChanged(boolean isVisible) {
                System.out.println("Search state changed: " + isVisible);
            }
        });

        languageTabManager.setTabClickListener(language -> {
            System.out.println("=== TAB CLICK LISTENER CALLED ===");
            System.out.println("Language: " + language);
            
            // Clear search when switching tabs
            if (!searchManager.getCurrentSearchQuery().isEmpty()) {
                System.out.println("Clearing search query");
                searchManager.clearSearch();
            }
            
            System.out.println("Calling filterAndDisplayProjects()");
            filterAndDisplayProjects(language, "");
            System.out.println("=================================");
        });
        
        System.out.println("Manager listeners set up");
        System.out.println("============================");
    }

    private void setupClickListeners() {
        createProjectButton.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, CreateProjectActivity.class);
            startActivity(intent);
        });
    }

    @Override
    public void onBackPressed() {
        if (searchManager != null && searchManager.isSearchViewVisible()) {
            // Back button vẫn clear search như cũ
            searchManager.hideSearchView();
            searchManager.clearSearch();
        } else {
            super.onBackPressed();
        }
    }

    private void loadProjectList() {
        allProjects = db.getAllProject();
        if (languageTabManager != null) {
            languageTabManager.setupLanguageTabs(allProjects);
        }
        if (projectDisplayManager != null) {
            String currentLanguage = languageTabManager != null ? 
                languageTabManager.getCurrentSelectedLanguage() : "Tất cả";
            String currentQuery = searchManager != null ? 
                searchManager.getCurrentSearchQuery() : "";
            filterAndDisplayProjects(currentLanguage, currentQuery);
        }
    }

    private void performSearch(String query) {
        if (query.isEmpty()) {
            filterAndDisplayProjects(languageTabManager.getCurrentSelectedLanguage(), "");
        } else {
            // Get projects for current language first
            List<Project> languageFilteredProjects = projectDisplayManager.filterProjectsByLanguage(
                allProjects, languageTabManager.getCurrentSelectedLanguage());
            
            // Then apply search filter
            List<Project> searchResults = searchManager.searchInProjects(languageFilteredProjects, query);
            projectDisplayManager.displayProjects(searchResults, query);
        }
    }

    private void filterAndDisplayProjects(String language, String searchQuery) {
        System.out.println("=== FILTER AND DISPLAY DEBUG ===");
        System.out.println("Language: " + language);
        System.out.println("Search query: " + searchQuery);
        System.out.println("All projects: " + (allProjects != null ? allProjects.size() : 0));
        
        if (projectDisplayManager == null) {
            System.out.println("ERROR: projectDisplayManager is null!");
            return;
        }
        
        if (searchManager == null) {
            System.out.println("ERROR: searchManager is null!");
            return;
        }
        
        List<Project> filteredProjects = projectDisplayManager.filterProjectsByLanguage(allProjects, language);
        System.out.println("After language filter: " + filteredProjects.size());
        
        if (!searchQuery.isEmpty()) {
            filteredProjects = searchManager.searchInProjects(filteredProjects, searchQuery);
            System.out.println("After search filter: " + filteredProjects.size());
        }
        
        System.out.println("Calling displayProjects()...");
        projectDisplayManager.displayProjects(filteredProjects, searchQuery);
        System.out.println("=================================");
    }

    // Public method for ProjectAdapter to refresh after changes
    public void refreshAfterProjectChange() {
        runOnUiThread(() -> {
            loadProjectList();
        });
    }

    private void updateUIForTheme(boolean isDarkMode) {
        // Update any specific UI elements that need manual theme updates
        // For example, update RecyclerView adapter if needed
        if (projectDisplayManager != null && projectDisplayManager.getProjectAdapter() != null) {
            projectDisplayManager.getProjectAdapter().notifyDataSetChanged();
        }
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            if (searchManager != null && searchManager.isSearchViewVisible()) {
                // Lấy vị trí touch
                float x = ev.getRawX();
                float y = ev.getRawY();
                
                // Kiểm tra xem có touch vào search view không
                if (!isTouchInsideView(searchView, x, y) && 
                    !isTouchInsideView(buttonSearch, x, y) &&
                    !isTouchInsideView(languageTabsContainer, x, y) &&
                    !isTouchInsideView(recyclerView, x, y) &&
                    !isTouchInsideView(createProjectButton, x, y)) {
                    
                    // Touch vào vùng trống - chỉ ẩn search, không clear
                    searchManager.hideSearchViewOnly();
                    return true; // Consume the touch event
                }
            }
        }
        return super.dispatchTouchEvent(ev);
    }

    private boolean isTouchInsideView(View view, float x, float y) {
        if (view == null || view.getVisibility() != View.VISIBLE) {
            return false;
        }
        
        int[] location = new int[2];
        view.getLocationOnScreen(location);
        
        return x >= location[0] && 
               x <= location[0] + view.getWidth() && 
               y >= location[1] && 
               y <= location[1] + view.getHeight();
    }
}
