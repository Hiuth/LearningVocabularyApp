package com.example.learningvocabularyapp.manager;

import android.content.Context;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.learningvocabularyapp.R;
import com.example.learningvocabularyapp.model.Project;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class LanguageTabManager {
    
    private Context context;
    private LinearLayout languageTabsContainer;
    private String currentSelectedLanguage = "Tất cả";
    
    public interface TabClickListener {
        void onTabClicked(String language);
    }
    
    private TabClickListener tabClickListener;
    
    public LanguageTabManager(Context context, LinearLayout languageTabsContainer) {
        this.context = context;
        this.languageTabsContainer = languageTabsContainer;
    }
    
    public void setTabClickListener(TabClickListener listener) {
        this.tabClickListener = listener;
    }
    
    public void setupLanguageTabs(List<Project> allProjects) {
        System.out.println("=== SETUP LANGUAGE TABS DEBUG ===");
        System.out.println("All projects: " + (allProjects != null ? allProjects.size() : 0));
        
        // Clear existing tabs
        languageTabsContainer.removeAllViews();
        
        // FIX: Use LinkedHashSet to maintain insertion order
        Set<String> uniqueLanguages = new LinkedHashSet<>();
        if (allProjects != null) {
            for (Project project : allProjects) {
                uniqueLanguages.add(project.getLearningLanguage());
                System.out.println("Found language: " + project.getLearningLanguage());
            }
        }

        System.out.println("Unique languages in order: " + uniqueLanguages);
        
        // Always add "All" tab first
        addLanguageTab("Tất cả");

        // Add language tabs in consistent order
        for (String language : uniqueLanguages) {
            addLanguageTab(language);
            System.out.println("Added tab for: " + language);
        }
        
        System.out.println("Total tabs created: " + languageTabsContainer.getChildCount());
        
        // Debug: Print all tab texts in order
        System.out.println("Tabs in container order:");
        for (int i = 0; i < languageTabsContainer.getChildCount(); i++) {
            TextView tab = (TextView) languageTabsContainer.getChildAt(i);
            System.out.println("  [" + i + "] " + tab.getText().toString());
        }
        
        System.out.println("==================================");
    }
    
    private void addLanguageTab(String language) {
        TextView tabText = new TextView(context);
        tabText.setText(language);
        tabText.setTextSize(14);
        tabText.setPadding(16, 12, 16, 12);
        tabText.setClickable(true);
        tabText.setFocusable(true);
        
        // Set layout parameters
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.WRAP_CONTENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        );
        params.setMargins(8, 0, 8, 0);
        tabText.setLayoutParams(params);

        // Set style based on selection
        updateTabStyle(tabText, language);

        // Set click listener with debug
        tabText.setOnClickListener(v -> {
            System.out.println("=== TAB CLICKED DEBUG ===");
            System.out.println("Clicked tab: " + language);
            System.out.println("Current selected: " + currentSelectedLanguage);
            
            // FIX: Add tab position debug
            int tabPosition = -1;
            for (int i = 0; i < languageTabsContainer.getChildCount(); i++) {
                if (languageTabsContainer.getChildAt(i) == v) {
                    tabPosition = i;
                    break;
                }
            }
            System.out.println("Tab position in container: " + tabPosition);
            System.out.println("Tab text from view: " + ((TextView) v).getText().toString());
            
            // Prevent multiple clicks on same tab
            if (currentSelectedLanguage.equals(language)) {
                System.out.println("Same tab clicked, ignoring");
                return;
            }
            
            currentSelectedLanguage = language;
            updateAllTabStyles();
            
            System.out.println("Updated current language to: " + currentSelectedLanguage);
            
            if (tabClickListener != null) {
                System.out.println("Calling tabClickListener.onTabClicked() with: " + language);
                tabClickListener.onTabClicked(language);
            } else {
                System.out.println("ERROR: tabClickListener is null!");
            }
            
            System.out.println("========================");
        });

        languageTabsContainer.addView(tabText);
        System.out.println("Tab added to container: " + language + " (total: " + languageTabsContainer.getChildCount() + ")");
    }
    
    public void updateAllTabStyles() {
        System.out.println("=== UPDATE TAB STYLES ===");
        System.out.println("Current selected language: " + currentSelectedLanguage);
        
        for (int i = 0; i < languageTabsContainer.getChildCount(); i++) {
            TextView tabView = (TextView) languageTabsContainer.getChildAt(i);
            String tabLanguage = tabView.getText().toString();
            
            System.out.println("Updating tab [" + i + "]: " + tabLanguage + 
                (tabLanguage.equals(currentSelectedLanguage) ? " (SELECTED)" : ""));
            
            updateTabStyle(tabView, tabLanguage);
        }
        System.out.println("=========================");
    }
    
    private void updateTabStyle(TextView tabView, String language) {
        if (language.equals(currentSelectedLanguage)) {
            tabView.setTextColor(context.getResources().getColor(R.color.tab_selected_text));
            tabView.setBackground(context.getResources().getDrawable(R.drawable.tab_selected_underline));
        } else {
            tabView.setTextColor(context.getResources().getColor(R.color.tab_unselected_text));
            tabView.setBackground(context.getResources().getDrawable(R.drawable.tab_unselected_underline));
        }
    }
    
    public String getCurrentSelectedLanguage() {
        return currentSelectedLanguage;
    }
    
    public void setCurrentSelectedLanguage(String language) {
        System.out.println("Setting current selected language: " + currentSelectedLanguage + " -> " + language);
        this.currentSelectedLanguage = language;
        updateAllTabStyles();
    }
}