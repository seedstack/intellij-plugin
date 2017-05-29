/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.completion.value;

import com.intellij.codeInsight.completion.JavaLookupElementBuilder;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiWildcardType;
import com.intellij.psi.search.searches.ClassInheritorsSearch;
import org.seedstack.intellij.config.completion.ValueCompletionResolver;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class ClassCompletionResolver implements ValueCompletionResolver {
    private static final String JAVA_LANG_CLASS = Class.class.getName();

    @Override
    public boolean canHandle(PsiClass rawType) {
        return JAVA_LANG_CLASS.equals(rawType.getQualifiedName());
    }

    @Override
    public Stream<LookupElementBuilder> resolveCompletions(String propertyName, PsiClass rawType, PsiType[] parameterTypes) {
        Stream<PsiClass> psiClassStream = null;
        if (parameterTypes.length == 1 && parameterTypes[0] instanceof PsiWildcardType) {
            PsiWildcardType psiWildcardType = ((PsiWildcardType) parameterTypes[0]);
            if (psiWildcardType.isBounded()) {
                if (psiWildcardType.isExtends()) {
                    psiClassStream = classesExtending((PsiClassType) psiWildcardType.getExtendsBound()).stream();
                } else if (psiWildcardType.isSuper()) {
                    psiClassStream = Arrays.stream(psiWildcardType.getSuperBound().getSuperTypes())
                            .map(psiType -> (PsiClassType) psiType)
                            .map(PsiClassType::resolve);
                }
            }
        }
        if (psiClassStream != null) {
            return psiClassStream.map(this::buildClassLookup).filter(Optional::isPresent).map(Optional::get);
        } else {
            return Stream.empty();
        }
    }

    private Set<PsiClass> classesExtending(PsiClassType psiClassReferenceType) {
        Set<PsiClass> results = new HashSet<>();
        Optional.of(psiClassReferenceType)
                .map(PsiClassType::resolve)
                .map(ClassInheritorsSearch::search)
                .ifPresent(psiClasses -> psiClasses.forEach(psiClass -> {
                    if (!psiClass.hasModifierProperty("abstract")) {
                        results.add(psiClass);
                    }
                }));
        return results;
    }

    private Optional<LookupElementBuilder> buildClassLookup(PsiClass psiClass) {
        String qualifiedName = psiClass.getQualifiedName();
        String name = psiClass.getName();
        if (qualifiedName != null && name != null) {
            return Optional.of(JavaLookupElementBuilder.forClass(psiClass, qualifiedName, true).withPresentableText(name));
        } else {
            return Optional.empty();
        }
    }
}
