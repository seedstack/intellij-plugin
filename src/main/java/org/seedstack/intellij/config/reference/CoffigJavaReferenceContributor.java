/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.reference;

import com.intellij.patterns.PlatformPatterns;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiLiteralExpression;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceProvider;
import com.intellij.psi.PsiReferenceRegistrar;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.List;

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
                            List<PsiReference> psiReferences = resolvePsiReferences(element, value, 1);
                            return psiReferences.toArray(new PsiReference[psiReferences.size()]);
                        }
                        return PsiReference.EMPTY_ARRAY;
                    }
                });
    }
}
