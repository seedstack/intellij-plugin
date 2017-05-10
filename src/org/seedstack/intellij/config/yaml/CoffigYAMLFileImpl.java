package org.seedstack.intellij.config.yaml;

import com.intellij.extapi.psi.PsiFileBase;
import com.intellij.lang.ASTNode;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.psi.FileViewProvider;
import com.intellij.psi.PsiElement;
import com.intellij.psi.tree.TokenSet;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.YAMLElementTypes;
import org.jetbrains.yaml.psi.YAMLDocument;
import org.jetbrains.yaml.psi.YAMLFile;
import org.jetbrains.yaml.psi.YAMLPsiElement;

import java.util.ArrayList;
import java.util.List;

/**
 * @author oleg
 */
class CoffigYAMLFileImpl extends PsiFileBase implements YAMLFile {
    CoffigYAMLFileImpl(FileViewProvider viewProvider) {
        super(viewProvider, CoffigYAMLLanguage.INSTANCE);
    }

    @NotNull
    public FileType getFileType() {
        return CoffigYAMLFileType.INSTANCE;
    }

    @Override
    public String toString() {
        return "Coffig YAML file";
    }

    public List<YAMLDocument> getDocuments() {
        final ArrayList<YAMLDocument> result = new ArrayList<>();
        for (ASTNode node : getNode().getChildren(TokenSet.create(YAMLElementTypes.DOCUMENT))) {
            result.add((YAMLDocument) node.getPsi());
        }
        return result;
    }

    public List<YAMLPsiElement> getYAMLElements() {
        final ArrayList<YAMLPsiElement> result = new ArrayList<>();
        for (ASTNode node : getNode().getChildren(null)) {
            final PsiElement psi = node.getPsi();
            if (psi instanceof YAMLPsiElement) {
                result.add((YAMLPsiElement) psi);
            }
        }
        return result;
    }
}
