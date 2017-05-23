package org.seedstack.intellij.config.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import static org.seedstack.intellij.config.util.CoffigUtil.isCoffigMethod;
import static org.seedstack.intellij.config.util.CoffigUtil.isLiteralOfConfigurationAnnotation;

public class CoffigJavaReferenceContributor extends CoffigBaseReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element, @NotNull ProcessingContext context) {
                        if (isLiteralOfConfigurationAnnotation(element) || isCoffigMethod(element)) {
                            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                            String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
                            return resolvePsiReferences(element, value, 1);
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}
