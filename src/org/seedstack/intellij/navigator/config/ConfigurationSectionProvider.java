package org.seedstack.intellij.navigator.config;

import org.seedstack.intellij.navigator.NavigatorSectionProvider;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public class ConfigurationSectionProvider implements NavigatorSectionProvider {
    @Override
    public SeedStackSimpleNode getSectionNode(SeedStackGroupNode rootNode) {
        return new ConfigurationNode(rootNode);
    }
}
