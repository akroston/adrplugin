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

    private final JBList<String> jList = new JBList<>();
    private final CollectionListModel<String> deciderModel;

    public AppSettingsComponent() {
        AppSettingsState settings = ServiceManager.getService(AppSettingsState.class);

        jList.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);
        deciderModel = new CollectionListModel<String>(settings.getState().deciders);
        jList.setModel(deciderModel);
        ToolbarDecorator deciderDecorator = ToolbarDecorator.createDecorator(jList);
        deciderDecorator.setAddAction(anActionButton -> {
            //jList.add(Messages.showInputDialog());
            //myListModel.add("hello");
            String foo = Messages.showInputDialog("enter a thing", "foo", null);
            deciderModel.add(foo);
        });

        myMainPanel = FormBuilder.createFormBuilder()
                // .addLabeledComponent(new JBLabel("Enter user name: "), myUserNameText, 1, false)
                // .addComponent(myIdeaUserStatus, 1)
                //.addComponent(jList)
                .addComponent(deciderDecorator.createPanel())
                .addComponentFillVertically(new JPanel(), 0)
                .getPanel();
    }
    public JPanel getPanel() {
        return myMainPanel;
    }
    public JComponent getPreferredFocusedComponent() {
        return jList;
    }
}
