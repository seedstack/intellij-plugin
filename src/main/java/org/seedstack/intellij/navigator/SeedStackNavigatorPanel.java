/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator;

import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.ActionToolbar;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataProvider;
import com.intellij.openapi.actionSystem.DefaultActionGroup;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.VfsUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.ui.ScrollPaneFactory;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

class SeedStackNavigatorPanel extends SimpleToolWindowPanel implements DataProvider {
    private final Project project;
    private final SimpleTree tree;

    SeedStackNavigatorPanel(Project project, SimpleTree tree) {
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

    @Nullable
    @Override
    public Object getData(@NonNls String dataId) {
        if (CommonDataKeys.PROJECT.is(dataId)) return project;

        if (CommonDataKeys.VIRTUAL_FILE.is(dataId)) return extractVirtualFile();
        if (CommonDataKeys.VIRTUAL_FILE_ARRAY.is(dataId)) return extractVirtualFiles();

        if (CommonDataKeys.NAVIGATABLE_ARRAY.is(dataId)) return extractNavigatables();

        return super.getData(dataId);
    }

    private VirtualFile extractVirtualFile() {
        for (SeedStackSimpleNode each : getSelectedNodes(SeedStackSimpleNode.class)) {
            VirtualFile file = each.getVirtualFile();
            if (file != null && file.isValid()) return file;
        }
        return null;
    }

    private Object extractVirtualFiles() {
        final List<VirtualFile> files = new ArrayList<>();
        for (SeedStackSimpleNode each : getSelectedNodes(SeedStackSimpleNode.class)) {
            VirtualFile file = each.getVirtualFile();
            if (file != null && file.isValid()) files.add(file);
        }
        return files.isEmpty() ? null : VfsUtil.toVirtualFileArray(files);
    }

    private Object extractNavigatables() {
        final List<Navigatable> navigatables = new ArrayList<>();
        for (SeedStackSimpleNode each : getSelectedNodes(SeedStackSimpleNode.class)) {
            Navigatable navigatable = each.getNavigatable();
            if (navigatable != null) navigatables.add(navigatable);
        }
        return navigatables.isEmpty() ? null : navigatables.toArray(new Navigatable[navigatables.size()]);
    }

    private <T extends SeedStackSimpleNode> List<T> getSelectedNodes(Class<T> aClass) {
        return SeedStackStructure.getSelectedNodes(tree, aClass);
    }
}
