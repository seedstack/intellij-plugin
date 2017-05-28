package org.seedstack.intellij.navigator;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.Disposer;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.psi.PsiFile;
import com.intellij.ui.treeStructure.SimpleNode;
import com.intellij.ui.treeStructure.SimpleTree;
import com.intellij.ui.treeStructure.SimpleTreeBuilder;
import com.intellij.ui.treeStructure.SimpleTreeStructure;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;

import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.ServiceLoader;

public class SeedStackStructure extends SimpleTreeStructure {
    private static final URL ERROR_ICON_URL = SeedStackStructure.class.getResource("/general/error.png");
    static final Comparator<SeedStackSimpleNode> NODE_COMPARATOR = (o1, o2) -> StringUtil.compare(o1.getName(), o2.getName(), true);
    private final Project project;
    private final RootNode rootNode;
    private final SimpleTreeBuilder treeBuilder;

    public SeedStackStructure(Project project, SimpleTree tree) {
        this.project = project;

        configureTree(tree);

        rootNode = new RootNode();
        treeBuilder = new SimpleTreeBuilder(tree, (DefaultTreeModel) tree.getModel(), this, null);
        Disposer.register(this.project, treeBuilder);

        treeBuilder.initRoot();
    }

    private void configureTree(final SimpleTree tree) {
        tree.setRootVisible(true);
        tree.setShowsRootHandles(true);
    }

    @Override
    public RootNode getRootElement() {
        return rootNode;
    }

    void refresh(@Nullable PsiFile psiFile) {
        rootNode.refresh(psiFile);
    }

    public void updateFrom(SimpleNode node) {
        treeBuilder.addSubtreeToUpdateByElement(node);
    }

    public void updateUpTo(SimpleNode node) {
        SimpleNode each = node;
        while (each != null) {
            updateFrom(each);
            each = each.getParent();
        }
    }

    public static <T extends SimpleNode> List<T> getSelectedNodes(SimpleTree tree, Class<T> nodeClass) {
        final List<T> filtered = new ArrayList<>();
        for (SimpleNode node : getSelectedNodes(tree)) {
            if ((nodeClass != null) && (!nodeClass.isInstance(node))) {
                filtered.clear();
                break;
            }
            //noinspection unchecked
            filtered.add((T) node);
        }
        return filtered;
    }

    private static List<SimpleNode> getSelectedNodes(SimpleTree tree) {
        List<SimpleNode> nodes = new ArrayList<>();
        TreePath[] treePaths = tree.getSelectionPaths();
        if (treePaths != null) {
            for (TreePath treePath : treePaths) {
                nodes.add(tree.getNodeFor(treePath));
            }
        }
        return nodes;
    }

    protected Class<? extends SeedStackSimpleNode>[] getVisibleNodesClasses() {
        return null;
    }

    protected boolean showDescriptions() {
        return true;
    }

    public Project getProject() {
        return project;
    }

    public enum ErrorLevel {
        NONE, ERROR
    }

    public enum DisplayKind {
        ALWAYS, NEVER, NORMAL
    }

    private class RootNode extends SeedStackGroupNode<SeedStackSimpleNode> {
        boolean initialized = false;

        private RootNode() {
            super(SeedStackStructure.this);
            setIcon(SeedStackIcons.LOGO);
        }

        @Override
        public MultiMap<PsiFile, SeedStackSimpleNode> computeChildren(PsiFile psiFile) {
            if (!initialized) {
                MultiMap<PsiFile, SeedStackSimpleNode> children = new MultiMap<>();
                for (NavigatorSectionProvider sectionProvider : ServiceLoader.load(NavigatorSectionProvider.class, SeedStackStructure.class.getClassLoader())) {
                    children.putValue(null, sectionProvider.getSectionNode(this));
                }
                initialized = true;
                return children;
            } else {
                return null;
            }
        }

        @Override
        public String getName() {
            return project.getName();
        }
    }
}
