/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.completion;

import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiType;

import java.util.stream.Stream;

public interface ValueCompletionResolver {
    boolean canHandle(PsiClass rawType);

    Stream<LookupElementBuilder> resolveCompletions(String propertyName, PsiClass rawType, PsiType[] parameterTypes);
}
