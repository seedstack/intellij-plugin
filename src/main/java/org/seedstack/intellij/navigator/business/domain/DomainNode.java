/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.business.domain;

import com.google.common.collect.Lists;
import com.intellij.psi.PsiFile;
import com.intellij.util.containers.MultiMap;
import org.jetbrains.annotations.Nullable;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackGroupNode;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public class DomainNode extends SeedStackGroupNode {
    private static final String NAME = "Domain";
    private final AggregatesNode aggregatesNode;
    private final DomainServicesNode domainServicesNode;
    private final PoliciesNode policiesNode;

    public DomainNode(SeedStackSimpleNode parent) {
        super(parent);
        this.aggregatesNode = new AggregatesNode(this);
        this.domainServicesNode = new DomainServicesNode(this);
        this.policiesNode = new PoliciesNode(this);
        setIcon(SeedStackIcons.FOLDER);
    }

    @Override
    public String getName() {
        return NAME;
    }

    @Override
    protected MultiMap<PsiFile, SeedStackGroupNode> computeChildren(@Nullable PsiFile psiFile) {
        MultiMap<PsiFile, SeedStackGroupNode> children = new MultiMap<>();
        children.put(null, Lists.newArrayList(aggregatesNode, domainServicesNode, policiesNode));
        return children;
    }
}
