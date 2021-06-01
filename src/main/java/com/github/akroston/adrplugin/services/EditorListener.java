package com.github.akroston.adrplugin.services;

import com.intellij.openapi.diagnostic.Logger;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.event.CaretEvent;
import com.intellij.openapi.editor.event.CaretListener;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerEvent;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.util.messages.MessageBusConnection;
import org.jetbrains.annotations.NotNull;

/**
 * @author <a href="mailto:andrew_armstrong@bigpond.com">Andrew Armstrong</a>
 * @version $Revision: 1.1 $
 */
public class EditorListener implements FileEditorManagerListener, CaretListener {


    private static final Logger LOG = Logger.getInstance(EditorListener.class);

    private final Project myProject;
    //private final PsiTreeChangeListener myTreeChangeListener;
    private Editor myCurrentEditor;
    private MessageBusConnection myMessageBus;

    public EditorListener(Project project) {
        myProject = project;
        /*
        myTreeChangeListener = new PsiTreeChangeAdapter() {
            public void childrenChanged(@NotNull final PsiTreeChangeEvent event) {
                updateTreeFromPsiTreeChange(event);
            }

            public void childAdded(@NotNull PsiTreeChangeEvent event) {
                updateTreeFromPsiTreeChange(event);
            }

            public void childMoved(@NotNull PsiTreeChangeEvent event) {
                updateTreeFromPsiTreeChange(event);
            }

            public void childRemoved(@NotNull PsiTreeChangeEvent event) {
                updateTreeFromPsiTreeChange(event);
            }

            public void childReplaced(@NotNull PsiTreeChangeEvent event) {
                updateTreeFromPsiTreeChange(event);
            }

            public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
                updateTreeFromPsiTreeChange(event);
            }
        };

         */
    }


    public void fileOpened(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        debug("source = [" + source + "], file = [" + file + "]");
    }

    public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
        debug("source = [" + source + "], file = [" + file + "]");
    }

    public void selectionChanged(@NotNull FileEditorManagerEvent event) {
        debug("selection changed " + event.toString());

        if (event.getNewFile() == null || myCurrentEditor == null) return;

        Editor newEditor = event.getManager().getSelectedTextEditor();

        if (myCurrentEditor != newEditor) myCurrentEditor.getCaretModel().removeCaretListener(this);

        //myViewer.selectElementAtCaret();

        if (newEditor != null)
            myCurrentEditor = newEditor;

        //myCurrentEditor.getCaretModel().addCaretListener(this, PsiViewerProjectService.getInstance(myProject));
    }


    public void caretPositionChanged(CaretEvent event) {
        final Editor editor = event.getEditor();

        debug("caret moved to " + editor.getCaretModel().getOffset() + " in editor " + editor);

        //myViewer.selectElementAtCaret();
    }

    public void start() {
        /*
        PsiViewerProjectService pluginDisposable = PsiViewerProjectService.getInstance(myProject);
        myMessageBus = myProject.getMessageBus().connect(pluginDisposable);
        myMessageBus.subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, this);

        PsiManager.getInstance(myProject).addPsiTreeChangeListener(myTreeChangeListener, pluginDisposable);

        myCurrentEditor = FileEditorManager.getInstance(myProject).getSelectedTextEditor();
        if (myCurrentEditor != null)
            myCurrentEditor.getCaretModel().addCaretListener(this, pluginDisposable);
            */
    }

    public void stop() {
        if (myMessageBus != null) {
            myMessageBus.disconnect();
            myMessageBus = null;
        }

        //PsiManager.getInstance(myProject).removePsiTreeChangeListener(myTreeChangeListener);
    }

    private static void debug(String message) {
        if (LOG.isDebugEnabled()) {
            LOG.debug(message);
        }
    }

}