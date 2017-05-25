package org.seedstack.intellij.navigator.tools;

import org.seedstack.intellij.navigator.NavigatorSectionProvider;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public class ToolsSectionProvider implements NavigatorSectionProvider {
    @Override
    public SeedStackSimpleNode getSectionNode(SeedStackGroupNode rootNode) {
        return new ToolsNode(rootNode);
    }
}
