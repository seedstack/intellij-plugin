/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.util;

import org.junit.Test;

import static org.assertj.core.api.Assertions.assertThat;


public class MacroResolverTest {
    @Test
    public void resolveSimple() throws Exception {
        MacroResolver macroResolver = new MacroResolver();
        assertThat(macroResolver.resolve("${simple.macro}")).containsExactly(
                new MacroResolver.Match("${simple.macro}", 2, 14, false, false)
        );
    }

    @Test
    public void resolveMultiple() throws Exception {
        MacroResolver macroResolver = new MacroResolver();
        assertThat(macroResolver.resolve("${first.macro}-${second.macro}")).containsExactly(
                new MacroResolver.Match("${first.macro}-${second.macro}", 2, 13, false, false),
                new MacroResolver.Match("${first.macro}-${second.macro}", 17, 29, false, false)
        );
    }

    @Test
    public void resolveNested() throws Exception {
        MacroResolver macroResolver = new MacroResolver();
        assertThat(macroResolver.resolve("${${inner.macro}.macro}")).containsExactly(
                new MacroResolver.Match("${${inner.macro}.macro}", 4, 15, false, false),
                new MacroResolver.Match("${${inner.macro}.macro}", 2, 22, false, false)
        );
    }

    @Test
    public void resolveIncompleteNested() throws Exception {
        MacroResolver macroResolver = new MacroResolver();
        assertThat(macroResolver.resolve("${${inner.macro}.macro")).containsExactly(
                new MacroResolver.Match("${${inner.macro}.macro", 4, 15, false, false),
                new MacroResolver.Match("${${inner.macro}.macro", 2, 22, false, true)
        );
    }
}