package org.seedstack.intellij.config.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

import static org.seedstack.intellij.config.util.CoffigUtil.isCoffigMethod;
import static org.seedstack.intellij.config.util.CoffigUtil.isLiteralOfConfigurationAnnotation;

public class CoffigReferenceContributor extends PsiReferenceContributor {
    @Override
    public void registerReferenceProviders(@NotNull PsiReferenceRegistrar registrar) {
        registrar.registerReferenceProvider(PlatformPatterns.psiElement(PsiLiteralExpression.class),
                new PsiReferenceProvider() {
                    @NotNull
                    @Override
                    public PsiReference[] getReferencesByElement(@NotNull PsiElement element,
                                                                 @NotNull ProcessingContext
                                                                         context) {
                        if (isLiteralOfConfigurationAnnotation(element) || isCoffigMethod(element)) {
                            PsiLiteralExpression literalExpression = (PsiLiteralExpression) element;
                            String value = literalExpression.getValue() instanceof String ? (String) literalExpression.getValue() : null;
                            if (value != null) {
                                String[] split = value.split("\\.");
                                List<PsiReference> psiReferences = new ArrayList<>();
                                StringBuilder sb = new StringBuilder();
                                int startIndex = 0;
                                for (int i = 0; i < split.length; i++) {
                                    if (i > 0) {
                                        sb.append(".");
                                        startIndex = sb.length();
                                    }
                                    sb.append(split[i]);
                                    psiReferences.add(new CoffigReference(element, new TextRange(startIndex + 1, sb.length() + 1), sb.toString()));
                                }
                                return psiReferences.toArray(new PsiReference[psiReferences.size()]);
                            }
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}
