package com.example.learningvocabularyapp.manager;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.StaggeredGridLayoutManager;

import com.example.learningvocabularyapp.adapter.ProjectAdapter;
import com.example.learningvocabularyapp.model.Project;

import java.util.ArrayList;
import java.util.List;

public class ProjectDisplayManager {
    
    private Context context;
    private RecyclerView recyclerView;
    private TextView textViewEmpty;
    private StaggeredGridLayoutManager layoutManager;
    private ProjectAdapter projectAdapter;
    
    public ProjectDisplayManager(Context context, RecyclerView recyclerView, TextView textViewEmpty) {
        this.context = context;
        this.recyclerView = recyclerView;
        this.textViewEmpty = textViewEmpty;
        setupRecyclerView();
    }
    
    // FIX: Better setupRecyclerView
    private void setupRecyclerView() {
        System.out.println("=== SETUP RECYCLERVIEW ===");
        
        layoutManager = new StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL);
        layoutManager.setGapStrategy(StaggeredGridLayoutManager.GAP_HANDLING_MOVE_ITEMS_BETWEEN_SPANS);
        
        // FIX: Set layout manager explicitly
        recyclerView.setLayoutManager(layoutManager);
        
        // FIX: Enable nested scrolling and other optimizations
        recyclerView.setNestedScrollingEnabled(true);
        recyclerView.setHasFixedSize(false); // Important for StaggeredGrid
        
        System.out.println("RecyclerView setup complete");
        System.out.println("Layout manager set: " + (recyclerView.getLayoutManager() != null));
        System.out.println("===========================");
    }
    
    public void displayProjects(List<Project> projects, String searchQuery) {
        System.out.println("=== DISPLAY PROJECTS DEBUG ===");
        System.out.println("Projects to display: " + (projects != null ? projects.size() : 0));
        
        if (projects != null && !projects.isEmpty()) {
            System.out.println("Showing RecyclerView with projects:");
            for (Project p : projects) {
                System.out.println("- " + p.getProjectName());
            }
            
            // FIX: Force UI update on main thread
            if (context instanceof Activity) {
                ((Activity) context).runOnUiThread(() -> {
                    recyclerView.setVisibility(View.VISIBLE);
                    textViewEmpty.setVisibility(View.GONE);

                    // FIX: Clear existing adapter first
                    recyclerView.setAdapter(null);
                    
                    // Create new adapter with fresh data
                    projectAdapter = new ProjectAdapter(context, new ArrayList<>(projects));
                    
                    // FIX: Set adapter after a brief delay
                    recyclerView.post(() -> {
                        recyclerView.setAdapter(projectAdapter);
                        
                        // Force layout immediately
                        recyclerView.requestLayout();
                        layoutManager.requestLayout();
                        
                        System.out.println("Adapter set with " + projects.size() + " projects");
                        
                        // Check after longer delay
                        recyclerView.postDelayed(() -> {
                            System.out.println("=== DELAYED CHECK ===");
                            System.out.println("Child count: " + recyclerView.getChildCount());
                            System.out.println("Layout manager child count: " + layoutManager.getChildCount());
                            System.out.println("Adapter attached: " + (recyclerView.getAdapter() != null));
                            System.out.println("Layout manager: " + recyclerView.getLayoutManager());
                            
                            // Force measure and layout
                            recyclerView.measure(
                                View.MeasureSpec.makeMeasureSpec(recyclerView.getWidth(), View.MeasureSpec.EXACTLY),
                                View.MeasureSpec.makeMeasureSpec(recyclerView.getHeight(), View.MeasureSpec.EXACTLY)
                            );
                            recyclerView.layout(recyclerView.getLeft(), recyclerView.getTop(), 
                                              recyclerView.getRight(), recyclerView.getBottom());
                            
                            System.out.println("After force layout - Child count: " + recyclerView.getChildCount());
                            System.out.println("====================");
                        }, 500);
                    });
                    
                    resetRecyclerViewPosition();
                });
            }
            
        } else {
            System.out.println("No projects - showing empty state");
            recyclerView.setVisibility(View.GONE);
            textViewEmpty.setVisibility(View.VISIBLE);
            
            if (searchQuery != null && !searchQuery.isEmpty()) {
                textViewEmpty.setText("No projects found for \"" + searchQuery + "\"");
            } else {
                textViewEmpty.setText("No projects available");
            }
        }
        System.out.println("===============================");
    }
    
    public List<Project> filterProjectsByLanguage(List<Project> allProjects, String language) {
        System.out.println("=== FILTER BY LANGUAGE DEBUG ===");
        System.out.println("Language: " + language);
        System.out.println("All projects: " + (allProjects != null ? allProjects.size() : 0));
        
        List<Project> filteredProjects = new ArrayList<>();
        
        if (allProjects == null || allProjects.isEmpty()) {
            System.out.println("No projects to filter");
            return filteredProjects;
        }
        
        if (language.equals("Tất cả")) {
            filteredProjects.addAll(allProjects);
            System.out.println("Added all projects: " + filteredProjects.size());
        } else {
            for (Project project : allProjects) {
                System.out.println("Checking: " + project.getProjectName() + " - Language: " + project.getLearningLanguage());
                if (project.getLearningLanguage().equals(language)) {
                    filteredProjects.add(project);
                    System.out.println("Added to filtered list");
                }
            }
            System.out.println("Filtered by language '" + language + "': " + filteredProjects.size());
        }
        
        System.out.println("================================");
        return filteredProjects;
    }
    
    private void resetRecyclerViewPosition() {
        recyclerView.scrollToPosition(0);
        layoutManager.scrollToPositionWithOffset(0, 0);
        
        recyclerView.post(() -> {
            recyclerView.smoothScrollToPosition(0);
            layoutManager.scrollToPositionWithOffset(0, 0);
        });
        
        recyclerView.postDelayed(() -> {
            layoutManager.scrollToPositionWithOffset(0, 0);
        }, 100);
    }
    
    public ProjectAdapter getProjectAdapter() {
        return projectAdapter;
    }
}