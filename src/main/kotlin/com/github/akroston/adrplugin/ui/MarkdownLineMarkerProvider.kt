package com.github.akroston.adrplugin.ui

import com.github.akroston.adrplugin.psi.findChildElement
import com.github.akroston.adrplugin.settings.AppSettingsState
import com.intellij.codeInsight.daemon.LineMarkerInfo
import com.intellij.codeInsight.daemon.LineMarkerProvider
import com.intellij.openapi.actionSystem.*
import com.intellij.openapi.command.WriteCommandAction
import com.intellij.openapi.components.ServiceManager
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.editor.markup.GutterIconRenderer
import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.Messages
import com.intellij.openapi.util.IconLoader
import com.intellij.psi.PsiElement
import com.intellij.psi.PsiFile
import com.intellij.psi.tree.TokenSet
import com.intellij.util.Function
import org.intellij.plugins.markdown.lang.MarkdownElementTypes
import org.intellij.plugins.markdown.lang.MarkdownTokenTypeSets
import org.intellij.plugins.markdown.lang.psi.MarkdownPsiElementFactory
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import javax.swing.Icon


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
        val tooltipProvider =
            Function { element: PsiElement ->
                val current = LocalDateTime.now()
                val formatter = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                val formatted = current.format(formatter)
                buildString {
                    append("Tooltip calculated at ")
                    append(formatted)
                    append(", for URL: $linkDestination")
                }
            }
        return tooltipProvider
    }

    fun createActionGroup(inlineLinkElement: PsiElement): DefaultActionGroup {
        // val linkDestinationElement =
        //    findChildElement(inlineLinkElement, MarkdownTokenTypeSets.LINK_DESTINATION, null)
        //val linkDestination = linkDestinationElement?.text

        val label = findChildElement(inlineLinkElement, MarkdownTokenTypeSets.LINK_LABEL, null)
        val labelStr:String? =label?.text

        val group = DefaultActionGroup()
        group.add(OpenChooserAndReplaceAction(labelStr, inlineLinkElement))
        //group.add(OpenUrlAction(linkDestination))
//        group.add(ActionManager.getInstance().getAction("MyPlugin.OpenToolWindowAction"))
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
                return null
            }

            override fun isNavigateAction(): Boolean {
                return true
            }

            override fun getPopupMenuActions(): ActionGroup? {
                return myActionGroup
            }
        }
    }
}

class OpenChooserAndReplaceAction(val label: String?, val element: PsiElement) :
    AnAction(
        "Open Link", "Open URL destination in browser",
        IconLoader.getIcon("/icons/ic_extension.svg", OpenChooserAndReplaceAction::class.java)
    ) {
    override fun actionPerformed(event: AnActionEvent) {


        // Get all the required data from data keys
        val editor: Editor = event.getRequiredData<Editor>(CommonDataKeys.EDITOR)
        val project: Project = event.getRequiredData<Project>(CommonDataKeys.PROJECT)


        val settings = ServiceManager.getService(
            AppSettingsState::class.java
        )
        val deciders = settings.deciders

        val opts: Array<String> = deciders.toTypedArray()
        val chosen: Int = Messages.showDialog(project, "Enter Status", "title", opts, 0, null)

      //  val i:Int=Messages.showCheckboxMessageDialog("message", "title", opts,"checkbox string", true,0,1,null,null)
        //String entry = Messages.showMultilineInputDialog(project, "enter value", "title here", "", null, null);

       // Messages.showM
        //String entry = Messages.showMultilineInputDialog(project, "enter value", "title here", "", null, null);
        val f: PsiFile = MarkdownPsiElementFactory.createFile(project, deciders[chosen])
        WriteCommandAction.runWriteCommandAction(project
        ) { element.replace(f) }

    }

}
