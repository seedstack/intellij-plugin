package org.seedstack.intellij.navigator.config;

import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

class ConfigFileNode extends SeedStackSimpleNode {
    private final VirtualFile virtualFile;

    ConfigFileNode(SeedStackSimpleNode parent, VirtualFile virtualFile) {
        super(parent);
        this.virtualFile = virtualFile;
        setIcon(SeedStackIcons.CONFIG_FILE);
    }

    @Override
    public String getName() {
        return virtualFile.getName();
    }

    @Nullable
    @Override
    public VirtualFile getVirtualFile() {
        return virtualFile;
    }
}
