package com.github.akroston.adrplugin.ui

import com.github.akroston.adrplugin.psi.findChildElement
import com.github.akroston.adrplugin.settings.AppSettingsState
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogWrapper
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.TokenSet
import com.intellij.ui.CollectionListModel
import com.intellij.ui.components.JBList
import com.intellij.ui.layout.panel
import com.intellij.util.Function
import org.intellij.plugins.markdown.lang.MarkdownElementTypes
import org.intellij.plugins.markdown.lang.MarkdownTokenTypeSets
import org.intellij.plugins.markdown.lang.psi.MarkdownPsiElementFactory
import org.jetbrains.annotations.Nullable
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.swing.*


class MarkdownLineMarkerProvider : LineMarkerProvider {

    private val myTokenSetOfMatchingElementTypes = TokenSet.create(MarkdownElementTypes.SHORT_REFERENCE_LINK)

    override fun getLineMarkerInfo(element: PsiElement): LineMarkerInfo<*>? {
        if (!myTokenSetOfMatchingElementTypes.contains(element.node.elementType)) return null
        return RunLineMarkerInfo(
            element,
            IconLoader.getIcon("/icons/ic_linemarkerprovider.svg", javaClass),
            createActionGroup(element),
            createToolTipProvider(element)
        )
    }

    private fun createToolTipProvider(inlineLinkElement: PsiElement): Function<in PsiElement, String> {
        val linkDestinationElement =
            findChildElement(inlineLinkElement, MarkdownTokenTypeSets.LINK_DESTINATION, null)
        val linkDestination = linkDestinationElement?.text
        return Function { element: PsiElement ->
            val current = LocalDateTime.now()
            val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            val formatted = current.format(formatter)
            buildString {
                append("Tooltip calculated at ")
                append(formatted)
                append(", for URL: $linkDestination")
            }
        }
    }

    fun createActionGroup(inlineLinkElement: PsiElement): DefaultActionGroup {
        val label = findChildElement(inlineLinkElement, MarkdownTokenTypeSets.LINK_LABEL, null)
        val labelStr: String? = label?.text

        val group = DefaultActionGroup()
        group.add(OpenChooserAndReplaceAction(labelStr, inlineLinkElement))
        return group
    }

}

class RunLineMarkerInfo(
    element: PsiElement,
    icon: Icon,
    private val myActionGroup: DefaultActionGroup,
    tooltipProvider: Function<in PsiElement, String>?
) : LineMarkerInfo<PsiElement>(
    element,
    element.textRange,
    icon,
    tooltipProvider,
    null,
    GutterIconRenderer.Alignment.CENTER
) {
    override fun createGutterRenderer(): GutterIconRenderer? {
        return object : LineMarkerGutterIconRenderer<PsiElement>(this) {

            override fun getClickAction(): AnAction? {
                // val label = findChildElement(element, MarkdownTokenTypeSets.LINK_LABEL, null)
                val labelStr: String? = element?.parent?.firstChild?.text
                return element?.let { OpenChooserAndReplaceAction(labelStr, it) }
            }

            override fun isNavigateAction(): Boolean {
                return true
            }

            override fun getPopupMenuActions(): ActionGroup? {
                return null
                //return myActionGroup
            }
        }
    }
}

class OpenChooserAndReplaceAction(private val label: String?, private val element: PsiElement) : AnAction() {

    override fun actionPerformed(event: AnActionEvent) {
        val project: Project = event.getRequiredData(CommonDataKeys.PROJECT)
        val dialog: DialogWrapper = when (label) {
            "Deciders", "Status" -> ChoicesDialogWrapper(label)
            else -> TextAreaDialogWrapper(label)
        }
        dialog.show()

        if (dialog.isOK) {
            var toShow: String = "";
            toShow = when (dialog) {
                is ChoicesDialogWrapper -> {
                    val sel = dialog.jbList.selectedValuesList
                    sel.joinToString(",").trim()
                }
                is TextAreaDialogWrapper -> {
                    dialog.textArea.text
                }
                else -> {
                    ""
                }
            }
            val f: PsiFile = MarkdownPsiElementFactory.createFile(project, toShow)
            WriteCommandAction.runWriteCommandAction(
                project
            ) { element.replace(f) }
        }
    }
}

class ChoicesDialogWrapper(private val label: String?) : DialogWrapper(true) {

    val jbList = JBList<String>()

    @Nullable
    override fun createCenterPanel(): JComponent? {

        val settings = ServiceManager.getService(AppSettingsState::class.java)
        val mod = when (label) {
            "Deciders" -> settings.deciders
            "Status" -> settings.status
            else -> null
        } ?: return JPanel()
        val model: ListModel<String> = CollectionListModel(mod)

        jbList.model = model
        //jbList.selectedIndices = intArrayOf(0, 1, 2)
        jbList.selectionMode = ListSelectionModel.MULTIPLE_INTERVAL_SELECTION

        val panel: JPanel = panel {
            row(label) {
                jbList()
            }
        }
        return panel
    }

    init {
        init()
        title = "Choose Wisely"
    }
}

class TextAreaDialogWrapper(private val label: String?) : DialogWrapper(true) {
    val textArea = JTextArea(20,60)

    @Nullable
    override fun createCenterPanel(): JComponent? {

        val panel: JPanel = panel {
            row(label) {
                textArea()
            }
        }
        return panel
    }

    init {
        init()
        title = "Choose Wisely"
    }
}