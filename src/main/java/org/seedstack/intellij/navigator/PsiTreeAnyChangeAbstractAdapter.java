/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator;

import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiTreeChangeAdapter;
import com.intellij.psi.PsiTreeChangeEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

abstract class PsiTreeAnyChangeAbstractAdapter extends PsiTreeChangeAdapter {
    @Override
    public void childAdded(@NotNull PsiTreeChangeEvent event) {
        onChange(event.getFile());
    }

    @Override
    public void childRemoved(@NotNull PsiTreeChangeEvent event) {
        onChange(event.getFile());
    }

    @Override
    public void childReplaced(@NotNull PsiTreeChangeEvent event) {
        onChange(event.getFile());
    }

    @Override
    public void childMoved(@NotNull PsiTreeChangeEvent event) {
        onChange(event.getFile());
    }

    @Override
    public void childrenChanged(@NotNull PsiTreeChangeEvent event) {
        onChange(event.getFile());
    }

    @Override
    public void propertyChanged(@NotNull PsiTreeChangeEvent event) {
        onChange(event.getFile());
    }

    protected abstract void onChange(@Nullable PsiFile file);
}