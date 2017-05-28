package org.seedstack.intellij.navigator.rest;

import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationOwner;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiReference;
import org.jetbrains.annotations.NotNull;
import org.seedstack.intellij.navigator.SeedStackSimpleNode;
import org.seedstack.intellij.navigator.common.ClassNode;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Stream;

class ResourceNode extends ClassNode {
    private final PsiAnnotation pathAnnotation;
    private final String path;

    ResourceNode(SeedStackSimpleNode parent, PsiClass pathAnnotationClass, PsiClass psiClass) {
        super(parent, psiClass);
        this.pathAnnotation = getPathAnnotation(pathAnnotationClass, psiClass);
        this.path = getPathValue();
    }

    @Override
    public String getName() {
        return path == null ? super.getName() : path;
    }

    private PsiAnnotation getPathAnnotation(PsiClass pathAnnotationClass, @NotNull PsiModifierListOwner psiModifierListOwner) {
        return Optional.ofNullable(psiModifierListOwner.getModifierList())
                .map(PsiAnnotationOwner::getAnnotations)
                .map(Arrays::stream)
                .map(stream -> stream.filter(annotation -> Optional.ofNullable(annotation.getNameReferenceElement())
                        .map(PsiReference::resolve)
                        .map(psiElement -> psiElement == pathAnnotationClass)
                        .orElse(false)
                ))
                .flatMap(Stream::findFirst)
                .orElse(null);
    }

    private String getPathValue() {
        return Optional.ofNullable(pathAnnotation.getParameterList().getAttributes()[0].getValue())
                .map(PsiElement::getText)
                .map(text -> text.substring(1, text.length() - 1))
                .orElse(null);
    }
}
