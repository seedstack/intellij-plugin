/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.reference;

import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiReferenceContributor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public abstract class CoffigBaseReferenceContributor extends PsiReferenceContributor {
    @NotNull
    protected List<PsiReference> resolvePsiReferences(@NotNull PsiElement element, String value, int rangeOffset) {
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
                psiReferences.add(new CoffigReference(element, new TextRange(rangeOffset + startIndex, rangeOffset + sb.length()), sb.toString()));
            }
            return psiReferences;
        } else {
            return new ArrayList<>();
        }
    }
}
