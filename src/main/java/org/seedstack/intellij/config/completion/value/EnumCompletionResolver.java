/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.completion.value;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.util.Iconable;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiEnumConstant;
import com.intellij.psi.PsiType;
import org.seedstack.intellij.config.completion.ValueCompletionResolver;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

public class EnumCompletionResolver implements ValueCompletionResolver {
    @Override
    public boolean canHandle(PsiClass rawType) {
        return rawType.isEnum();
    }

    @Override
    public Stream<LookupElementBuilder> resolveCompletions(String propertyName, PsiClass rawType, PsiType[] parameterTypes) {
        return Arrays.stream(rawType.getChildren())
                .filter(child -> child instanceof PsiEnumConstant)
                .map(child -> buildEnumLookup((PsiEnumConstant) child))
                .filter(Optional::isPresent)
                .map(Optional::get);
    }

    private Optional<LookupElementBuilder> buildEnumLookup(PsiEnumConstant psiEnumConstant) {
        return Optional.of(LookupElementBuilder.create(psiEnumConstant).withIcon(psiEnumConstant.getIcon(Iconable.ICON_FLAG_VISIBILITY)));
    }
}
