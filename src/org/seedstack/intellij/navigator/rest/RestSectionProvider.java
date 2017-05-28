package org.seedstack.intellij.navigator.rest;

import org.seedstack.intellij.navigator.NavigatorSectionProvider;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public class RestSectionProvider implements NavigatorSectionProvider {
    @Override
    public SeedStackSimpleNode getSectionNode(SeedStackGroupNode rootNode) {
        return new ResourcesNode(rootNode);
    }
}
