package org.seedstack.intellij.config.annotator;

import com.intellij.lang.annotation.AnnotationHolder;
import com.intellij.lang.annotation.Annotator;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;
import org.jetbrains.yaml.psi.YAMLValue;
import org.seedstack.intellij.config.util.CoffigUtil;

import java.util.Set;

import static org.seedstack.intellij.config.util.CoffigUtil.isCoffigMethod;
import static org.seedstack.intellij.config.util.CoffigUtil.isLiteralOfConfigurationAnnotation;

public class CoffigAnnotator implements Annotator {
    @Override
    public void annotate(@NotNull PsiElement psiElement, @NotNull AnnotationHolder annotationHolder) {
        if (psiElement instanceof PsiLiteralExpression) {
            if (isLiteralOfConfigurationAnnotation(psiElement) || isCoffigMethod(psiElement)) {
                PsiLiteralExpression literalExpression = (PsiLiteralExpression) psiElement;
                String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
                if (value != null) {
                    Project project = psiElement.getProject();
                    Set<YAMLKeyValue> keys = CoffigUtil.findCoffigKeys(project, value);
                    if (keys.size() == 1) {
                        YAMLValue yamlValue = keys.iterator().next().getValue();
                        if (yamlValue != null) {
                            TextRange range = new TextRange(psiElement.getTextRange().getStartOffset() + 1, psiElement.getTextRange().getEndOffset() - 1);
                            annotationHolder.createInfoAnnotation(range, yamlValue.getText());
                        }
                    } else if (keys.size() == 0) {
                        TextRange range = new TextRange(psiElement.getTextRange().getStartOffset() + 1, psiElement.getTextRange().getEndOffset() - 1);
                        annotationHolder.createErrorAnnotation(range, "Unresolved configuration property");
                        //.registerFix(new CreatePropertyQuickFix(key));
                    }
                }
            }
        }
    }
}
