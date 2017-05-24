/*
 * Copyright 2000-2017 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.seedstack.intellij.navigator;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerAdapter;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.SimpleTree;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

public class SeedStackNavigator extends AbstractProjectComponent implements Disposable, ProjectComponent {
    public static final String TOOL_WINDOW_ID = "SeedStack";
    private SimpleTree tree;
    private ToolWindowEx toolWindow;

    public static SeedStackNavigator getInstance(Project project) {
        return project.getComponent(SeedStackNavigator.class);
    }

    public SeedStackNavigator(Project project) {
        super(project);
    }

    @Override
    public void initComponent() {
        NavigatorUtil.runWhenInitialized(myProject, (DumbAwareRunnable) () -> {
            if (myProject.isDisposed()) return;
            initToolWindow();
        });
    }

    @Override
    public void dispose() {
        toolWindow = null;
    }

    private void initToolWindow() {
        initTree();
        JPanel panel = new SeedStackNavigatorPanel(myProject, tree);

        final ToolWindowManagerEx manager = ToolWindowManagerEx.getInstanceEx(myProject);
        toolWindow = (ToolWindowEx) manager.registerToolWindow(TOOL_WINDOW_ID, false, ToolWindowAnchor.LEFT, myProject, true);
        toolWindow.setIcon(SeedStackIcons.LOGO);
        final ContentFactory contentFactory = ServiceManager.getService(ContentFactory.class);
        final Content content = contentFactory.createContent(panel, "", false);
        ContentManager contentManager = toolWindow.getContentManager();
        contentManager.addContent(content);
        contentManager.setSelectedContent(content, false);

        final ToolWindowManagerAdapter listener = new ToolWindowManagerAdapter() {
            boolean wasVisible = false;

            @Override
            public void stateChanged() {
                if (toolWindow.isDisposed()) return;
                boolean visible = toolWindow.isVisible();
                if (!visible || wasVisible) {
                    return;
                }
                // TODO update
                wasVisible = true;
            }
        };
        manager.addToolWindowManagerListener(listener, myProject);

//        ActionManager actionManager = ActionManager.getInstance();
//
//        DefaultActionGroup group = new DefaultActionGroup();
//        group.add(actionManager.getAction("Maven.GroupProjects"));
//        group.add(actionManager.getAction("Maven.ShowIgnored"));
//        group.add(actionManager.getAction("Maven.ShowBasicPhasesOnly"));
//        group.add(actionManager.getAction("Maven.AlwaysShowArtifactId"));
//        group.add(actionManager.getAction("Maven.ShowVersions"));
//
//        toolWindow.setAdditionalGearActions(group);
    }

    private void initTree() {
        tree = new SimpleTree() {
            private final JLabel myLabel = new JLabel("This is not a SeedStack project.");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                myLabel.setFont(getFont());
                myLabel.setBackground(getBackground());
                myLabel.setForeground(getForeground());
                Rectangle bounds = getBounds();
                Dimension size = myLabel.getPreferredSize();
                myLabel.setBounds(0, 0, size.width, size.height);

                int x = (bounds.width - size.width) / 2;
                Graphics g2 = g.create(bounds.x + x, bounds.y + 20, bounds.width, bounds.height);
                try {
                    myLabel.paint(g2);
                } finally {
                    g2.dispose();
                }
            }
        };
        tree.getEmptyText().clear();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    }
}