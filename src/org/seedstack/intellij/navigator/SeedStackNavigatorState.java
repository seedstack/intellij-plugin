package org.seedstack.intellij.navigator;

import com.intellij.util.xmlb.annotations.Tag;
import org.jdom.Element;

public class SeedStackNavigatorState {
    @Tag("treeState")
    public Element treeState;
}