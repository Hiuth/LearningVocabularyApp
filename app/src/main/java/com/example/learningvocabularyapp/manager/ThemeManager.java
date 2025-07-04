package com.example.learningvocabularyapp.manager;

import android.content.Context;
import android.content.SharedPreferences;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.example.learningvocabularyapp.R;

public class ThemeManager {
    
    private static final String PREFS_NAME = "app_preferences";
    private static final String KEY_DARK_MODE = "dark_mode";
    
    private Context context;
    private SharedPreferences sharedPreferences;
    private ImageButton buttonToggleTheme;
    
    public ThemeManager(Context context, ImageButton buttonToggleTheme) {
        this.context = context;
        this.buttonToggleTheme = buttonToggleTheme;
        this.sharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        setupThemeToggle();
    }
    
    // STATIC method to apply theme before setContentView
    public static void applyThemeFromPreferences(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        boolean isDarkMode = prefs.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    
    public void applyTheme() {
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
    }
    
    private void setupThemeToggle() {
        updateThemeIcon();
        
        buttonToggleTheme.setOnClickListener(v -> {
            toggleTheme();
        });
    }
    
    private void toggleTheme() {
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        boolean newDarkMode = !isDarkMode;
        
        // Save preference
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean(KEY_DARK_MODE, newDarkMode);
        editor.apply();
        
        // Apply theme
        applyTheme();
        
        // Recreate activity to apply theme
        if (context instanceof AppCompatActivity) {
            ((AppCompatActivity) context).recreate();
        }
    }
    
    private void updateThemeIcon() {
        boolean isDarkMode = sharedPreferences.getBoolean(KEY_DARK_MODE, false);
        if (isDarkMode) {
            buttonToggleTheme.setImageResource(R.drawable.ic_sun);
        } else {
            buttonToggleTheme.setImageResource(R.drawable.ic_moon);
        }
    }
    
    public boolean isDarkMode() {
        return sharedPreferences.getBoolean(KEY_DARK_MODE, false);
    }
}