package com.github.akroston.adrplugin.settings;

import com.intellij.openapi.components.*;
import com.intellij.openapi.project.Project;
import com.intellij.ui.CollectionListModel;
import com.intellij.util.xmlb.XmlSerializerUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Supports storing the application settings in a persistent way.
 * The {@link State} and {@link Storage} annotations define the name of the data and the file name where
 * these persistent application settings are stored.
 */
@State(
        name = "com.github.akroston.adrplugin.settings.AppSettingsState",
        storages = {@Storage("ADRPluginSettings.xml")}
)
public class AppSettingsState implements PersistentStateComponent<AppSettingsState.State> {


    static class State {
        public ArrayList<String> deciders = new ArrayList<String>(Arrays.asList("Adrian", "Zack", "Shan"));
    }

    public ArrayList<String> getDeciders(){
        return myState.deciders;
    }
    private State myState = new State();

    public State getState() {
        return myState;
    }

    public void loadState(State state) {
        myState = state;
    }


    /*
    @Override
    public void loadState(@NotNull AppSettingsState state) {
        XmlSerializerUtil.copyBean(state, this);
    }
*/
}

