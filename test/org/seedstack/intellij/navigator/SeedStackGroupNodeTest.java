package org.seedstack.intellij.navigator;

import com.google.common.collect.Lists;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import static org.mockito.Mockito.when;

public class SeedStackGroupNodeTest {
    private SeedStackGroupNode underTest;
    private MultiMap<PsiFile, TestNode> someChildren = new MultiMap<>();
    private PsiFile psiFile1 = Mockito.mock(PsiFile.class);
    private TestNode test1;
    private TestNode test2;
    private TestNode test3;
    private TestNode test4;
    private TestNode test5;

    @Before
    public void setUp() throws Exception {
        SeedStackStructure mock = Mockito.mock(SeedStackStructure.class);
        when(mock.getProject()).thenReturn(Mockito.mock(Project.class));
        underTest = new TestGroupNode(mock, someChildren);

        test1 = new TestNode(underTest, "test1");
        test2 = new TestNode(underTest, "test2");
        test3 = new TestNode(underTest, "test3");
        test4 = new TestNode(underTest, "test4");
        test5 = new TestNode(underTest, "test5");

        someChildren.putValue(null, test2);
        someChildren.putValue(null, test1);
        someChildren.putValue(null, test3);
        someChildren.putValue(psiFile1, test4);
        someChildren.putValue(psiFile1, test5);
    }

    @Test
    public void initialChildren() throws Exception {
        underTest.refresh(null);
        Assert.assertEquals(Lists.newArrayList(test1, test2, test3, test4, test5), underTest.doGetChildren());
    }

    @Test
    public void psiFileRemoved() throws Exception {
        underTest.refresh(null);
        someChildren.remove(psiFile1);
        underTest.refresh(null);
        Assert.assertEquals(Lists.newArrayList(test1, test2, test3), underTest.doGetChildren());
    }

    @Test
    public void psiFileAdded() throws Exception {
        underTest.refresh(null);
        PsiFile psiFile2 = Mockito.mock(PsiFile.class);
        TestNode test0 = new TestNode(underTest, "test0");
        someChildren.putValue(psiFile2, test0);
        underTest.refresh(null);
        Assert.assertEquals(Lists.newArrayList(test0, test1, test2, test3, test4, test5), underTest.doGetChildren());
    }

    @Test
    public void existingPsiFileChildRemoved() throws Exception {
        underTest.refresh(null);
        someChildren.remove(psiFile1, test4);
        underTest.refresh(null);
        Assert.assertEquals(Lists.newArrayList(test1, test2, test3, test5), underTest.doGetChildren());
    }

    @Test
    public void existingPsiFileChildAdded() throws Exception {
        underTest.refresh(null);
        TestNode test0 = new TestNode(underTest, "test0");
        someChildren.putValue(psiFile1, test0);
        underTest.refresh(null);
        Assert.assertEquals(Lists.newArrayList(test0, test1, test2, test3, test4, test5), underTest.doGetChildren());
    }

    @Test
    public void existingPsiChildChanged() throws Exception {
        underTest.refresh(null);
        TestNode toBeModified = someChildren.get(psiFile1).iterator().next();
        toBeModified.setName("test0");
        underTest.refresh(null);
        Assert.assertEquals(Lists.newArrayList(toBeModified, test1, test2, test3, test5), underTest.doGetChildren());
    }

    private static class TestGroupNode extends SeedStackGroupNode<TestNode> {
        private final MultiMap<PsiFile, TestNode> someChildren;

        TestGroupNode(SeedStackStructure structure, MultiMap<PsiFile, TestNode> someChildren) {
            super(structure);
            this.someChildren = someChildren;
        }

        @Override
        protected MultiMap<PsiFile, TestNode> computeChildren(@Nullable PsiFile psiFile) {
            return new MultiMap<>(someChildren);
        }
    }

    private static class TestNode extends SeedStackSimpleNode {
        private String name;

        public TestNode(SeedStackSimpleNode parent, String name) {
            super(parent);
            this.name = name;
        }

        @Override
        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        @NotNull
        @Override
        public Object[] getEqualityObjects() {
            return new Object[]{name};
        }
    }
}