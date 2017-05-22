package org.seedstack.intellij.config.yaml;

import com.intellij.openapi.fileTypes.LanguageFileType;
import com.intellij.openapi.fileTypes.ex.FileTypeIdentifiableByVirtualFile;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;

import javax.swing.*;

public class CoffigYAMLFileType extends LanguageFileType implements FileTypeIdentifiableByVirtualFile {
    public static final CoffigYAMLFileType INSTANCE = new CoffigYAMLFileType();

    private CoffigYAMLFileType() {
        super(CoffigYAMLLanguage.INSTANCE);
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