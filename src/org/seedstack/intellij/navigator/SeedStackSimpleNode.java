package org.seedstack.intellij.navigator;

import com.intellij.ide.util.treeView.NodeDescriptor;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.psi.PsiFile;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import com.intellij.ui.treeStructure.CachingSimpleNode;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import org.jetbrains.annotations.NonNls;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.navigator.util.NavigatorUtil;

import java.awt.*;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public abstract class SeedStackSimpleNode extends CachingSimpleNode {
    private final SeedStackStructure structure;
    private SeedStackSimpleNode parent;
    private SeedStackStructure.ErrorLevel errorLevel = SeedStackStructure.ErrorLevel.NONE;
    private SeedStackStructure.ErrorLevel totalErrorLevel = null;
    private List<SeedStackSimpleNode> previous = new ArrayList<>();

    SeedStackSimpleNode(SeedStackStructure structure) {
        super(structure.getProject(), null);
        this.structure = structure;
    }

    public SeedStackSimpleNode(SeedStackSimpleNode parent) {
        this(parent.getStructure());
        setParent(parent);
    }

    public void setParent(SeedStackSimpleNode parent) {
        this.parent = parent;
    }

    @Override
    public NodeDescriptor getParentDescriptor() {
        return parent;
    }

    public <T extends SeedStackSimpleNode> T findParent(Class<T> parentClass) {
        SeedStackSimpleNode node = this;
        while (true) {
            node = node.parent;
            if (node == null || parentClass.isInstance(node)) {
                //noinspection unchecked
                return (T) node;
            }
        }
    }

    public boolean isVisible() {
        return getDisplayKind() != SeedStackStructure.DisplayKind.NEVER;
    }

    public SeedStackStructure.DisplayKind getDisplayKind() {
        Class[] visibles = structure.getVisibleNodesClasses();
        if (visibles == null) return SeedStackStructure.DisplayKind.NORMAL;

        for (Class each : visibles) {
            if (each.isInstance(this)) return SeedStackStructure.DisplayKind.ALWAYS;
        }
        return SeedStackStructure.DisplayKind.NEVER;
    }


    @Override
    protected SimpleNode[] buildChildren() {
        List<? extends SeedStackSimpleNode> children = doGetChildren();
        if (children.isEmpty()) return NO_CHILDREN;

        List<SeedStackSimpleNode> result = new ArrayList<>();
        for (SeedStackSimpleNode each : children) {
            if (each.isVisible()) result.add(each);
        }
        return result.toArray(new SeedStackSimpleNode[result.size()]);
    }

    protected List<? extends SeedStackSimpleNode> doGetChildren() {
        return Collections.emptyList();
    }

    public void refresh(@Nullable PsiFile psiFile) {

    }

    @Override
    public void cleanUpCache() {
        super.cleanUpCache();
        totalErrorLevel = null;
    }

    protected void childrenChanged() {
        SeedStackSimpleNode each = this;
        while (each != null) {
            each.cleanUpCache();
            each = (SeedStackSimpleNode) each.getParent();
        }
        structure.updateUpTo(this);
    }

    public SeedStackStructure.ErrorLevel getTotalErrorLevel() {
        if (totalErrorLevel == null) {
            totalErrorLevel = calcTotalErrorLevel();
        }
        return totalErrorLevel;
    }

    private SeedStackStructure.ErrorLevel calcTotalErrorLevel() {
        SeedStackStructure.ErrorLevel childrenErrorLevel = getChildrenErrorLevel();
        return childrenErrorLevel.compareTo(errorLevel) > 0 ? childrenErrorLevel : errorLevel;
    }

    public SeedStackStructure.ErrorLevel getChildrenErrorLevel() {
        SeedStackStructure.ErrorLevel result = SeedStackStructure.ErrorLevel.NONE;
        for (SimpleNode each : getChildren()) {
            SeedStackStructure.ErrorLevel eachLevel = ((SeedStackSimpleNode) each).getTotalErrorLevel();
            if (eachLevel.compareTo(result) > 0) result = eachLevel;
        }
        return result;
    }

    public void setErrorLevel(SeedStackStructure.ErrorLevel level) {
        if (errorLevel == level) return;
        errorLevel = level;
        structure.updateUpTo(this);
    }

    @Override
    protected void doUpdate() {
        setNameAndTooltip(getName(), null);
    }

    protected void setNameAndTooltip(String name, @Nullable String tooltip) {
        setNameAndTooltip(name, tooltip, (String) null);
    }

    protected void setNameAndTooltip(String name, @Nullable String tooltip, @Nullable String hint) {
        setNameAndTooltip(name, tooltip, getPlainAttributes());
        if (structure.showDescriptions() && !StringUtil.isEmptyOrSpaces(hint)) {
            addColoredFragment(" (" + hint + ")", SimpleTextAttributes.GRAY_ATTRIBUTES);
        }
    }

    protected void setNameAndTooltip(String name, @Nullable String tooltip, SimpleTextAttributes attributes) {
        clearColoredText();
        addColoredFragment(name, prepareAttributes(attributes));
        getTemplatePresentation().setTooltip(tooltip);
    }

    private SimpleTextAttributes prepareAttributes(SimpleTextAttributes from) {
        SeedStackStructure.ErrorLevel level = getTotalErrorLevel();
        Color waveColor = level == SeedStackStructure.ErrorLevel.NONE ? null : JBColor.RED;
        int style = from.getStyle();
        if (waveColor != null) style |= SimpleTextAttributes.STYLE_WAVED;
        return new SimpleTextAttributes(from.getBgColor(), from.getFgColor(), waveColor, style);
    }

    @Nullable
    @NonNls
    protected String getActionId() {
        return null;
    }

    @Nullable
    @NonNls
    protected String getMenuId() {
        return null;
    }

    @Nullable
    public VirtualFile getVirtualFile() {
        return null;
    }

    @Nullable
    public Navigatable getNavigatable() {
        return NavigatorUtil.createNavigatableForFile(getProject(), getVirtualFile());
    }

    @Override
    public void handleDoubleClickOrEnter(SimpleTree tree, InputEvent inputEvent) {
        String actionId = getActionId();
        if (actionId != null) {
            NavigatorUtil.executeAction(actionId, inputEvent);
        } else if (!(this instanceof SeedStackGroupNode)) {
            Optional.ofNullable(getNavigatable()).ifPresent(navigatable -> navigatable.navigate(true));
        }
    }

    protected SeedStackStructure getStructure() {
        return structure;
    }
}
