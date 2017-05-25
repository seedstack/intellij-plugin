package org.seedstack.intellij.navigator.business;

import org.seedstack.intellij.navigator.NavigatorSectionProvider;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public class BusinessSectionProvider implements NavigatorSectionProvider {
    @Override
    public SeedStackSimpleNode getSectionNode(SeedStackGroupNode rootNode) {
        return new BusinessNode(rootNode);
    }
}

