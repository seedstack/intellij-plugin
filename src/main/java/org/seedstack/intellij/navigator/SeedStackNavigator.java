/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator;

import com.intellij.ide.util.treeView.TreeState;
import com.intellij.openapi.Disposable;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.AbstractProjectComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.ServiceManager;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.components.StoragePathMacros;
import com.intellij.openapi.project.DumbAwareRunnable;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.util.WriteExternalException;
import com.intellij.openapi.wm.ToolWindowAnchor;
import com.intellij.openapi.wm.ex.ToolWindowEx;
import com.intellij.openapi.wm.ex.ToolWindowManagerAdapter;
import com.intellij.openapi.wm.ex.ToolWindowManagerEx;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;
import com.intellij.ui.content.ContentManager;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jdom.Element;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.SeedStackLog;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import javax.swing.*;
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;

@State(name = "SeedstackNavigator", storages = {@Storage(StoragePathMacros.WORKSPACE_FILE)})
public class SeedStackNavigator extends AbstractProjectComponent implements
        Disposable,
        ProjectComponent,
        PersistentStateComponent<SeedStackNavigatorState> {
    private static final String TOOL_WINDOW_ID = "SeedStack";
    private SeedStackNavigatorState state = new SeedStackNavigatorState();
    private SimpleTree tree;
    private ToolWindowEx toolWindow;
    private SeedStackStructure structure;

    public static SeedStackNavigator getInstance(Project project) {
        return project.getComponent(SeedStackNavigator.class);
    }

    public SeedStackNavigator(Project project) {
        super(project);
    }

    @Nullable
    @Override
    public SeedStackNavigatorState getState() {
        ApplicationManager.getApplication().assertIsDispatchThread();
        if (structure != null) {
            try {
                state.treeState = new Element("root");
                TreeState.createOn(tree).writeExternal(state.treeState);
            } catch (WriteExternalException e) {
                SeedStackLog.LOG.warn("Cannot write SeedStack structure state", e);
            }
        }
        return state;
    }

    @Override
    public void loadState(SeedStackNavigatorState seedStackNavigatorState) {
        state = seedStackNavigatorState;
        scheduleStructureUpdate(null);
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
                if (!visible) {
                    return;
                }
                scheduleStructureUpdate(null);
            }
        };
        manager.addToolWindowManagerListener(listener, myProject);

        ActionManager actionManager = ActionManager.getInstance();
        DefaultActionGroup group = new DefaultActionGroup();
        toolWindow.setAdditionalGearActions(group);
    }

    private void initTree() {
        tree = new SimpleTree() {
            private final JLabel myLabel = new JLabel("This is not a SeedStack project.");

            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                if (!isSeedStackProject()) {
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
            }
        };
        tree.getEmptyText().clear();
        tree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);
    }

    private void scheduleStructureRequest(final Runnable r) {
        if (toolWindow == null) return;
        NavigatorUtil.runDumbAware(myProject, () -> {
            if (!toolWindow.isVisible()) return;

            boolean shouldCreate = structure == null;
            if (shouldCreate) {
                initStructure();
            }

            r.run();

            if (shouldCreate && this.state.treeState != null) {
                TreeState treeState = TreeState.createOn(this.tree);
                try {
                    treeState.readExternal(this.state.treeState);
                    treeState.applyTo(this.tree);
                } catch (InvalidDataException e) {
                    SeedStackLog.LOG.info(e);
                }
            }
        });
    }

    private void initStructure() {
        structure = new SeedStackStructure(myProject, tree);
        PsiManager.getInstance(myProject).addPsiTreeChangeListener(new PsiTreeAnyChangeAbstractAdapter() {
            @Override
            protected void onChange(@Nullable PsiFile psiFile) {
                scheduleStructureUpdate(psiFile);
            }
        });
    }

    private void scheduleStructureUpdate(@Nullable PsiFile psiFile) {
        scheduleStructureRequest(() -> structure.refresh(psiFile));
    }

    private boolean isSeedStackProject() {
        return true;
    }
}