/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.config;

import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

class ConfigFileNode extends SeedStackSimpleNode {
    private final PsiFile psiFile;

    ConfigFileNode(SeedStackSimpleNode parent, PsiFile psiFile) {
        super(parent);
        this.psiFile = psiFile;
        setIcon(SeedStackIcons.CONFIG_FILE);
    }

    @Override
    public String getName() {
        return psiFile.getName();
    }

    @Nullable
    @Override
    public VirtualFile getVirtualFile() {
        return psiFile.getVirtualFile();
    }

    @NotNull
    @Override
    public Object[] getEqualityObjects() {
        return new Object[]{psiFile};
    }
}
