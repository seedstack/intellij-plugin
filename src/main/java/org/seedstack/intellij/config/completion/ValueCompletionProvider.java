/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.completion;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import org.seedstack.intellij.config.util.CoffigResolver;

import java.util.HashSet;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.stream.Stream;

class ValueCompletionProvider implements CompletionResolver {
    private Set<ValueCompletionResolver> completionResolvers = new HashSet<>();

    ValueCompletionProvider() {
        for (ValueCompletionResolver completionResolver : ServiceLoader.load(ValueCompletionResolver.class, ValueCompletionProvider.class.getClassLoader())) {
            completionResolvers.add(completionResolver);
        }
    }

    @Override
    public Stream<LookupElementBuilder> resolve(String joined, PsiElement position) {
        Project project = position.getProject();
        return CoffigResolver.from(project)
                .find(joined)
                .flatMap(match -> match.resolveField(match.getUnmatchedPath())
                        .map(PsiVariable::getType)
                        .map(psiType -> buildStream(match.getUnmatchedPath(), position, psiType)))
                .orElse(Stream.empty());
    }

    private Stream<LookupElementBuilder> buildStream(String propertyName, PsiElement position, PsiType psiType) {
        for (ValueCompletionResolver completionResolver : completionResolvers) {
            if (completionResolver.canHandle(psiType)) {
                return completionResolver.resolveCompletions(propertyName, psiType);
            }
        }
        return Stream.empty();
    }
}
