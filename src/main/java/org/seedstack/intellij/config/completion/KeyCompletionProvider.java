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
import org.seedstack.intellij.config.util.CoffigResolver;

import java.util.stream.Stream;

class KeyCompletionProvider implements CompletionResolver {
    @Override
    public Stream<LookupElementBuilder> resolve(String path, PsiElement position) {
        Project project = position.getProject();
        Stream<String> keys;
        if (path.isEmpty()) {
            keys = CoffigResolver.from(project)
                    .onlyAtTopLevel()
                    .classes()
                    .map(CoffigResolver.Match::getName);
        } else {
            keys = CoffigResolver.from(project)
                    .find(path)
                    .filter(CoffigResolver.Match::isFullyResolved)
                    .map(CoffigResolver.Match::allProperties)
                    .orElse(Stream.empty());
        }
        return keys.map(key -> key.split("\\.")[0]).map(LookupElementBuilder::create);
    }
}
