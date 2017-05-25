package org.seedstack.intellij.navigator.config;

import com.intellij.openapi.vfs.VirtualFile;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.config.util.CoffigUtil;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import java.util.ArrayList;
import java.util.List;

class ConfigurationNode extends SeedStackGroupNode {
    private static final String NAME = "Configuration";
    private final List<ConfigFileNode> configFileNodes = new ArrayList<>();

    ConfigurationNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.CONFIG);
        NavigatorUtil.runDumbAware(getProject(), this::updateConfigFiles);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected List<? extends SeedStackSimpleNode> doGetChildren() {
        return configFileNodes;
    }

    private void updateConfigFiles() {
        configFileNodes.clear();
        for (VirtualFile virtualFile : CoffigUtil.findCoffigFiles(getProject())) {
            configFileNodes.add(new ConfigFileNode(this, virtualFile));
        }
        sort(configFileNodes);
        childrenChanged();
    }
}
