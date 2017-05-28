package org.seedstack.intellij.navigator.config;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.util.containers.MultiMap;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.config.util.CoffigUtil;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

class ConfigurationNode extends SeedStackGroupNode<ConfigFileNode> {
    private static final String NAME = "Configuration";

    ConfigurationNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.CONFIG);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    public MultiMap<PsiFile, ConfigFileNode> computeChildren(PsiFile psiFile) {
        Project project = getProject();
        MultiMap<PsiFile, ConfigFileNode> children = new MultiMap<>();
        if (project != null) {
            PsiManager psiManager = PsiManager.getInstance(project);
            for (VirtualFile virtualFile : CoffigUtil.findCoffigFiles(getProject())) {
                PsiFile file = psiManager.findFile(virtualFile);
                children.putValue(file, new ConfigFileNode(this, psiManager.findFile(virtualFile)));
            }
        }
        return children;
    }
}
