package com.github.akroston.adrplugin.actions;


import com.github.akroston.adrplugin.settings.AppSettingsState;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.command.WriteCommandAction;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiElementFilter;
import com.intellij.psi.util.PsiTreeUtil;
import org.intellij.markdown.flavours.MarkdownFlavourDescriptor;
import org.intellij.markdown.flavours.gfm.GFMFlavourDescriptor;
import org.intellij.plugins.markdown.lang.MarkdownElementType;
import org.intellij.plugins.markdown.lang.MarkdownElementTypes;
import org.intellij.plugins.markdown.lang.psi.MarkdownElementVisitor;
import org.intellij.plugins.markdown.lang.psi.MarkdownPsiElementFactory;
import org.intellij.plugins.markdown.lang.psi.MarkdownRecursiveElementVisitor;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownFile;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownLinkDefinitionImpl;
import org.intellij.plugins.markdown.lang.psi.impl.MarkdownParagraphImpl;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProcessMDLineAction extends AnAction {
    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {

        // Get all the required data from data keys
        final Editor editor = event.getRequiredData(CommonDataKeys.EDITOR);
        final Project project = event.getRequiredData(CommonDataKeys.PROJECT);
        final Document document = editor.getDocument();

        final VirtualFile file = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        PsiFile psiFile = event.getData(CommonDataKeys.PSI_FILE);

        if (editor == null || psiFile == null) {
            return;
        }

        final MarkdownFlavourDescriptor flavour = new GFMFlavourDescriptor();

        int offset = editor.getCaretModel().getOffset();
        //editor.getCaretModel().getLogicalPosition();

        PsiElement element = psiFile.findElementAt(offset);

        //find the paragrasph
        PsiElement pgraph = element.getNextSibling();
        String lineName = pgraph.getFirstChild().getText();


        PsiElement ele1 = element.getParent().getLastChild();

        List<MarkdownParagraphImpl> paragraphs = new ArrayList<>();

        //((ASTWrapperPsiElement) pgraph.getFirstChild().getNextSibling().getNextSibling().getNextSibling()).getNode().getElementType()

        // PsiTreeUtil.findSiblingForward(pgraph,MarkdownElementTypes.SHORT_REFERENCE_LINK )

//PsiTreeUtil.findElementOfClassAtOffsetWithStopSet()

        List<MarkdownLinkDefinitionImpl> linkDefinitions = new ArrayList<>();
        pgraph.accept(new MarkdownRecursiveElementVisitor() {

            @Override
            public void visitElement(com.intellij.psi.PsiElement psiElement) {
                if (psiElement instanceof MarkdownLinkDefinitionImpl) {
                    linkDefinitions.add((MarkdownLinkDefinitionImpl) psiElement);
                }

            }
        });


        PsiElement[] elements = PsiTreeUtil.collectElements(element, new PsiElementFilter() {
            @Override
            public boolean isAccepted(@NotNull PsiElement element) {

                return true;
            }
        });
//        MarkdownPsiUtil.processContainer(element, new Consumer<PsiElement>() {
//            @Override
//            public void consume(PsiElement psiElement) {
//
//            }
//        },null);
//

        //PsiTreeUtil.findElementOfClassAtOffset(element,0, MarkdownParagraphImpl.class,true);
        /*
        Markdown file(0,124)
  ASTWrapperPsiElement(Markdown:Markdown:MARKDOWN_FILE)(0,124)
    MarkdownListImpl(Markdown:Markdown:UNORDERED_LIST)(0,124)
      MarkdownListItemImpl(Markdown:Markdown:LIST_ITEM)(0,124)
        PsiElement(Markdown:Markdown:LIST_BULLET)('- ')(0,2)
        MarkdownParagraphImpl(Markdown:PARAGRAPH)(2,124)
          PsiElement(Markdown:Markdown:TEXT)('Status')(2,8)
          PsiElement(Markdown:Markdown::)(':')(8,9)
          PsiWhiteSpace(' ')(9,10)
          ASTWrapperPsiElement(Markdown:Markdown:SHORT_REFERENCE_LINK)(10,106)
            ASTWrapperPsiElement(Markdown:Markdown:LINK_LABEL)(10,106)
              PsiElement(Markdown:Markdown:[)('[')(10,11)
              PsiElement(Markdown:Markdown:TEXT)('draft')(11,16)
              PsiWhiteSpace(' ')(16,17)
              PsiElement(Markdown:Markdown:TEXT)('|')(17,18)
              PsiWhiteSpace(' ')(18,19)
              PsiElement(Markdown:Markdown:TEXT)('proposed')(19,27)
              PsiWhiteSpace(' ')(27,28)
              PsiElement(Markdown:Markdown:TEXT)('|')(28,29)
              PsiWhiteSpace(' ')(29,30)
              PsiElement(Markdown:Markdown:TEXT)('rejected')(30,38)
              PsiWhiteSpace(' ')(38,39)
              PsiElement(Markdown:Markdown:TEXT)('|')(39,40)
              PsiWhiteSpace(' ')(40,41)
              PsiElement(Markdown:Markdown:TEXT)('accepted')(41,49)
              PsiWhiteSpace(' ')(49,50)
              PsiElement(Markdown:Markdown:TEXT)('|')(50,51)
              PsiWhiteSpace(' ')(51,52)
              PsiElement(Markdown:Markdown:TEXT)('deprecated')(52,62)
              PsiWhiteSpace(' ')(62,63)
              PsiElement(Markdown:Markdown:TEXT)('|')(63,64)
              PsiWhiteSpace(' ')(64,65)
              PsiElement(Markdown:Markdown:TEXT)('…')(65,66)
              PsiWhiteSpace(' ')(66,67)
              PsiElement(Markdown:Markdown:TEXT)('|')(67,68)
              PsiWhiteSpace(' ')(68,69)
              PsiElement(Markdown:Markdown:TEXT)('superseded by')(69,82)
              PsiWhiteSpace(' ')(82,83)
              ASTWrapperPsiElement(Markdown:Markdown:INLINE_LINK)(83,105)
                ASTWrapperPsiElement(Markdown:Markdown:LINK_TEXT)(83,88)
                  PsiElement(Markdown:Markdown:[)('[')(83,84)
                  PsiElement(Markdown:Markdown:TEXT)('xxx')(84,87)
                  PsiElement(Markdown:Markdown:])(']')(87,88)
                PsiElement(Markdown:Markdown:()('(')(88,89)
                MarkdownLinkDestinationImpl(Markdown:Markdown:LINK_DESTINATION)(89,104)
                  PsiElement(Markdown:Markdown:TEXT)('yyyymmdd-xxx.md')(89,104)
                PsiElement(Markdown:Markdown:))(')')(104,105)
              PsiElement(Markdown:Markdown:])(']')(105,106)
          PsiWhiteSpace(' ')(106,107)
          PsiElement(Markdown:Markdown:HTML_TAG)('<!-- optional -->')(107,124)
HtmlFile:Dummy.md(0,124)
  PsiElement(HTML_DOCUMENT)(0,0)
    PsiElement(XML_PROLOG)(0,0)
      <empty list>
  PsiElement(MARKDOWN_OUTER_BLOCK)('- Status: [draft | proposed | rejected | accepted | deprecated | … | superseded by [xxx](yyyymmdd-xxx.md)] <!-- optional -->')(0,124)

         */

            /*
            Find which line it is (Like Status:), load the model for that
             */
        AppSettingsState settings = ServiceManager.getService(AppSettingsState.class);
        ArrayList<String> deciders = settings.getDeciders();

        String[] opts = deciders.stream().toArray(String[]::new);

        int chosen = Messages.showDialog(project, "Enter Status", "title", opts, 0, null);

        //String entry = Messages.showMultilineInputDialog(project, "enter value", "title here", "", null, null);
        PsiFile f = MarkdownPsiElementFactory.createFile(project, deciders.get(chosen));
        WriteCommandAction.runWriteCommandAction(project, new

                Runnable() {
                    public void run() {
                        ele1.replace(f);
                    }
                });


        //Messages.showMessageDialog(event.getProject(), infoBuilder.toString(), "PSI Info", null);
    }


    @Override
    public boolean isDumbAware() {
        return super.isDumbAware();
    }
}
