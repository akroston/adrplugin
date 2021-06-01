package com.github.akroston.adrplugin.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.options.Configurable;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;

/**
 * Provides controller functionality for application settings.
 */
public class AppSettingsConfigurable implements Configurable {

    private AppSettingsComponent mySettingsComponent;
    private boolean isModified = false;

    // A default constructor with no arguments is required because this implementation
    // is registered as an applicationConfigurable EP

    @Nls(capitalization = Nls.Capitalization.Title)
    @Override
    public String getDisplayName() {
        return "ADR Plugin Settings";
    }

    @Override
    public JComponent getPreferredFocusedComponent() {
        return mySettingsComponent.getPreferredFocusedComponent();
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        mySettingsComponent = new AppSettingsComponent();
        return mySettingsComponent.getPanel();
    }

    @Override
    public boolean isModified() {
        AppSettingsState settings = ServiceManager.getService(AppSettingsState.class);
        boolean modified = mySettingsComponent.getDeciderModel().getSize() != 0;
        return modified;
    }

    @Override
    public void apply() {
        AppSettingsState settings = ServiceManager.getService(AppSettingsState.class);
        settings.getState().deciders = new ArrayList<>( mySettingsComponent.getDeciderModel().getItems());
    }

    @Override
    public void reset() {
        AppSettingsState settings = ServiceManager.getService(AppSettingsState.class);
        //settings.lm=mySettingsComponent.getListModel();
    }

    @Override
    public void disposeUIResources() {
        mySettingsComponent = null;
    }

}

