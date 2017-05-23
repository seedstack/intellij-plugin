package org.seedstack.intellij.config.annotator;

import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.lang.annotation.Annotation;
import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.search.searches.ReferencesSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.seedstack.intellij.config.util.CoffigResolver;

import static org.seedstack.intellij.config.util.CoffigUtil.isConfigFile;
import static org.seedstack.intellij.config.util.CoffigUtil.isYamlLeaf;
import static org.seedstack.intellij.config.util.CoffigUtil.resolvePath;

public class CoffigYamlAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (isConfigFile(psiElement) && psiElement instanceof YAMLKeyValue && isYamlLeaf(psiElement)) {
            if (hasNoConfigurationObject(psiElement) && hasNoExplicitReference(psiElement)) {
                TextRange range = new TextRange(psiElement.getTextRange().getStartOffset(), psiElement.getTextRange().getStartOffset() + ((YAMLKeyValue) psiElement).getKeyText().length());
                Annotation warningAnnotation = annotationHolder.createWarningAnnotation(range, "Unused configuration property");
                warningAnnotation.setHighlightType(ProblemHighlightType.LIKE_UNUSED_SYMBOL);
            }
        }
    }

    private boolean hasNoExplicitReference(@NotNull PsiElement psiElement) {
        return ReferencesSearch.search(psiElement).findFirst() == null;
    }

    private boolean hasNoConfigurationObject(@NotNull PsiElement psiElement) {
        return !CoffigResolver.from(psiElement.getProject()).find(resolvePath(psiElement)).isPresent();
    }
}
