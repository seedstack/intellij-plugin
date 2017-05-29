/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.completion.value;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;
import org.seedstack.intellij.config.completion.ValueCompletionResolver;

import java.util.stream.Stream;

public class BooleanCompletionResolver implements ValueCompletionResolver {
    private static final String JAVA_BOOLEAN_CLASS = Boolean.class.getName();
    private static final String ENABLED = "enabled";
    private static final String DISABLED = "disabled";

    @Override
    public boolean canHandle(PsiClass rawType) {
        return JAVA_BOOLEAN_CLASS.equals(rawType.getQualifiedName());
    }

    @Override
    public Stream<LookupElementBuilder> resolveCompletions(String propertyName, PsiClass rawType, PsiType[] parameterTypes) {
        if (ENABLED.equalsIgnoreCase(propertyName) || DISABLED.equalsIgnoreCase(propertyName)) {
            return Stream.of("yes", "no").map(LookupElementBuilder::create);
        }
        return Stream.of("true", "false").map(LookupElementBuilder::create);
    }
}
