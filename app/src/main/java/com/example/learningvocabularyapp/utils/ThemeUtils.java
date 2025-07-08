package com.example.learningvocabularyapp.utils;

import android.app.Activity;
import android.content.Context;
import android.content.res.Configuration;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.example.learningvocabularyapp.R;

public class ThemeUtils {
    
    public static void applyThemeToActivity(Activity activity, boolean isDarkMode) {
        // Apply system theme
        if (isDarkMode) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
        
        // Apply theme to current views without recreating
        applyThemeToViews(activity.findViewById(android.R.id.content), activity, isDarkMode);
    }
    
    private static void applyThemeToViews(View view, Context context, boolean isDarkMode) {
        if (view instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) view;
            
            // Apply theme to ViewGroup itself
            applyThemeToSingleView(view, context, isDarkMode);
            
            // Apply theme to all children
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                applyThemeToViews(viewGroup.getChildAt(i), context, isDarkMode);
            }
        } else {
            // Apply theme to single view
            applyThemeToSingleView(view, context, isDarkMode);
        }
    }
    
    private static void applyThemeToSingleView(View view, Context context, boolean isDarkMode) {
        // Update background colors
        if (view.getId() == R.id.mainContainer || 
            view instanceof LinearLayout && view.getBackground() != null) {
            
            String tag = (String) view.getTag();
            if (tag != null) {
                switch (tag) {
                    case "background":
                        view.setBackgroundColor(ContextCompat.getColor(context, R.color.background_color));
                        break;
                    case "surface":
                        view.setBackgroundColor(ContextCompat.getColor(context, R.color.surface_color));
                        break;
                    case "header":
                        view.setBackgroundColor(ContextCompat.getColor(context, R.color.header_background));
                        break;
                }
            }
        }
        
        // Update text colors
        if (view instanceof TextView) {
            TextView textView = (TextView) view;
            
            // Don't change button text colors or specific colored text
            if (!(view.getBackground() != null && 
                  (view.getId() == R.id.btn_save ||
                   view.getId() == R.id.createProjectButton))) {
                textView.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            }
        }
        
        // Update EditText colors
        if (view instanceof EditText) {
            EditText editText = (EditText) view;
            editText.setTextColor(ContextCompat.getColor(context, R.color.text_color));
            editText.setHintTextColor(ContextCompat.getColor(context, R.color.text_hint_color));
        }
    }
    
    public static boolean isSystemInDarkMode(Context context) {
        int nightModeFlags = context.getResources().getConfiguration().uiMode & 
                           Configuration.UI_MODE_NIGHT_MASK;
        return nightModeFlags == Configuration.UI_MODE_NIGHT_YES;
    }
}