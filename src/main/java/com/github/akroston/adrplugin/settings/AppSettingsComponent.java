package com.github.akroston.adrplugin.settings;

import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.ui.CollectionListModel;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import com.intellij.util.ui.FormBuilder;

import javax.swing.*;

/**
 * Supports creating and managing a {@link JPanel} for the Settings Dialog.
 */
public class AppSettingsComponent {

    private final JPanel myMainPanel;
    //private final JBTextField myUserNameText = new JBTextField();

    public CollectionListModel<String> getDeciderModel() {
        return deciderModel;
    }

    public CollectionListModel<String> getStatusModel() {
        return statusModel;
    }

    private final JBList<String> deciderJList = new JBList<>();
    private final JBList<String> statusJList = new JBList<>();
    private final CollectionListModel<String> deciderModel;
    private final CollectionListModel<String> statusModel;

    public AppSettingsComponent() {
        AppSettingsState settings = ServiceManager.getService(AppSettingsState.class);

        deciderJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        deciderModel = new CollectionListModel<>(settings.getState().deciders);
        deciderJList.setModel(deciderModel);
        ToolbarDecorator deciderDecorator = ToolbarDecorator.createDecorator(deciderJList);
        deciderDecorator.setAddAction(anActionButton -> {
            String foo = Messages.showInputDialog("Enter deciders", "Deciders", null);
            deciderModel.add(foo);
        });

        statusJList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        statusModel = new CollectionListModel<>(settings.getState().status);
        statusJList.setModel(statusModel);
        ToolbarDecorator statusDecorator = ToolbarDecorator.createDecorator(statusJList);
        statusDecorator.setAddAction(anActionButton -> {
            String foo = Messages.showInputDialog("Enter Status", "Status", null);
            statusModel.add(foo);
        });

        myMainPanel = FormBuilder.createFormBuilder()
                .addComponent(deciderDecorator.createPanel())
                .addComponent(statusDecorator.createPanel())
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }

    public JPanel getPanel() {
        return myMainPanel;
    }

    public JComponent getPreferredFocusedComponent() {
        return deciderJList;
    }
}
