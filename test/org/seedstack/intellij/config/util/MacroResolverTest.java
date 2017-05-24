package org.seedstack.intellij.config.util;

import com.google.common.collect.Lists;
import org.junit.Assert;
import org.junit.Test;

public class MacroResolverTest {
    @Test
    public void resolveSimple() throws Exception {
        MacroResolver macroResolver = new MacroResolver();
        Assert.assertEquals(
                Lists.newArrayList(
                        new MacroResolver.Match("${simple.macro}", 2, 14, false, false)
                ),
                macroResolver.resolve("${simple.macro}"));
    }

    @Test
    public void resolveMultiple() throws Exception {
        MacroResolver macroResolver = new MacroResolver();
        Assert.assertEquals(
                Lists.newArrayList(
                        new MacroResolver.Match("${first.macro}-${second.macro}", 2, 13, false, false),
                        new MacroResolver.Match("${first.macro}-${second.macro}", 17, 29, false, false)
                ),
                macroResolver.resolve("${first.macro}-${second.macro}"));
    }

    @Test
    public void resolveNested() throws Exception {
        MacroResolver macroResolver = new MacroResolver();
        Assert.assertEquals(
                Lists.newArrayList(
                        new MacroResolver.Match("${${inner.macro}.macro}", 4, 15, false, false),
                        new MacroResolver.Match("${${inner.macro}.macro}", 2, 22, false, false)
                ),
                macroResolver.resolve("${${inner.macro}.macro}"));
    }

    @Test
    public void resolveIncompleteNested() throws Exception {
        MacroResolver macroResolver = new MacroResolver();
        Assert.assertEquals(
                Lists.newArrayList(
                        new MacroResolver.Match("${${inner.macro}.macro", 4, 15, false, false),
                        new MacroResolver.Match("${${inner.macro}.macro", 2, 22, false, true)
                ),
                macroResolver.resolve("${${inner.macro}.macro"));
    }
}