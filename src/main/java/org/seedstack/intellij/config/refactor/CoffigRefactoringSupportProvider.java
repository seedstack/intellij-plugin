/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.refactor;

import com.intellij.lang.refactoring.RefactoringSupportProvider;
import com.intellij.psi.PsiElement;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.yaml.psi.YAMLKeyValue;

import static org.seedstack.intellij.config.util.CoffigUtil.isConfigFile;

public class CoffigRefactoringSupportProvider extends RefactoringSupportProvider {
    @Override
    public boolean isInplaceRenameAvailable(PsiElement element, PsiElement context) {
        return isConfigFile(element) && element instanceof YAMLKeyValue;
    }

    @Override
    public boolean isMemberInplaceRenameAvailable(PsiElement element, PsiElement context) {
        return isConfigFile(element) && element instanceof YAMLKeyValue;
    }

    @Override
    public boolean isSafeDeleteAvailable(@NotNull PsiElement element) {
        return isConfigFile(element);
    }
}