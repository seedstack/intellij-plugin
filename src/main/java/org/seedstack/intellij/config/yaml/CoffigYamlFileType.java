/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.yaml;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;

import javax.swing.*;

public class CoffigYamlFileType extends LanguageFileType implements FileTypeIdentifiableByVirtualFile {
    public static final CoffigYamlFileType INSTANCE = new CoffigYamlFileType();

    private CoffigYamlFileType() {
        super(CoffigYamlLanguage.INSTANCE);
    }

    @NotNull
    @Override
    public String getName() {
        return "Coffig YAML";
    }

    @NotNull
    @Override
    public String getDescription() {
        return "Coffig YAML file";
    }

    @NotNull
    @Override
    public String getDefaultExtension() {
        return "yaml";
    }

    @Nullable
    @Override
    public Icon getIcon() {
        return SeedStackIcons.LOGO;
    }

    @Override
    public boolean isMyFileType(@NotNull VirtualFile virtualFile) {
        // TODO: implement detection of location
        return false;
    }
}