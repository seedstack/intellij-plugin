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
