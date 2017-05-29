/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.business.domain;

import com.intellij.psi.PsiClass;
import org.seedstack.intellij.SeedStackIcons;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.business.AnnotatedInterfaceNode;

import java.util.Optional;

class PoliciesNode extends AnnotatedInterfaceNode<PolicyNode> {
    private static final String POLICY_ANNOTATION = "org.seedstack.business.domain.DomainPolicy";
    private static final String NAME = "Policies";

    PoliciesNode(SeedStackSimpleNode parent) {
        super(parent);
        setIcon(SeedStackIcons.POLICY);
    }

    @Override
    public String getName() {
        return NAME;
    }


    @Override
    protected PolicyNode createChild(PsiClass psiClass) {
        return new PolicyNode(this, psiClass);
    }

    @Override
    protected boolean isSatisfying(PsiClass psiClass) {
        return Optional.ofNullable(psiClass.getQualifiedName())
                .filter(qualifiedName -> qualifiedName.contains(".domain."))
                .isPresent();
    }

    @Override
    protected String getAnnotationQName() {
        return POLICY_ANNOTATION;
    }
}
