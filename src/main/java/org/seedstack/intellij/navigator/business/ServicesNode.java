/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.business;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;

public abstract class ServicesNode extends AnnotatedInterfaceNode<ServiceNode> {
    private static final String SERVICE_ANNOTATION = "org.seedstack.business.Service";

    public ServicesNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.SERVICE);
    }

    @Override
    protected ServiceNode createChild(PsiClass psiClass) {
        return new ServiceNode(this, psiClass);
    }

    @Override
    protected String getAnnotationQName() {
        return SERVICE_ANNOTATION;
    }

    protected abstract boolean isSatisfying(PsiClass psiClass);
}
