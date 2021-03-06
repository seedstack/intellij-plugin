/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.rest;

import org.seedstack.intellij.navigator.NavigatorSectionProvider;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public class RestSectionProvider implements NavigatorSectionProvider {
    @Override
    public SeedStackSimpleNode getSectionNode(SeedStackGroupNode rootNode) {
        return new ResourcesNode(rootNode);
    }
}
