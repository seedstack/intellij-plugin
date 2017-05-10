package org.seedstack.intellij.config.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;

import static org.seedstack.intellij.config.util.CoffigUtil.isConfigFile;

public class CoffigAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (!isConfigFile(psiElement)) {
            return;
        }
    }
}
