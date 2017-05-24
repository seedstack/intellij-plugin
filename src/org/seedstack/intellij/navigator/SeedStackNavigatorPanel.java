package org.seedstack.intellij.navigator;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.SimpleTree;

public class SeedStackNavigatorPanel extends SimpleToolWindowPanel {
    private final Project project;
    private final SimpleTree tree;

    public SeedStackNavigatorPanel(Project project, SimpleTree tree) {
        super(true, true);
        this.project = project;
        this.tree = tree;

        final ActionManager actionManager = ActionManager.getInstance();
        ActionToolbar actionToolbar = actionManager.createActionToolbar(
                "SeedStack Navigator Toolbar",
                (DefaultActionGroup) actionManager.getAction("SeedStack.NavigatorActionsToolbar"),
                true
        );

        actionToolbar.setTargetComponent(tree);
        setToolbar(actionToolbar.getComponent());
        setContent(ScrollPaneFactory.createScrollPane(tree));
    }
}
