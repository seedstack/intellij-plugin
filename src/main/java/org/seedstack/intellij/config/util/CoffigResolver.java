/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.config.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationOwner;
import com.intellij.psi.PsiArrayType;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiElementFactory;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiType;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static com.google.common.base.Preconditions.checkArgument;

public class CoffigResolver {
    private static final String ORG_SEEDSTACK_COFFIG_CONFIG = "org.seedstack.coffig.Config";
    private static final String JAVA_UTIL_COLLECTION = "java.util.Collection";
    private static final String JAVA_UTIL_MAP = "java.util.Map";
    private static final Set<String> directlyMappable = new HashSet<>();

    static {
        directlyMappable.add("java.lang.Boolean");
        directlyMappable.add("java.lang.Byte");
        directlyMappable.add("java.lang.Character");
        directlyMappable.add("java.lang.Double");
        directlyMappable.add("java.lang.Float");
        directlyMappable.add("java.lang.Integer");
        directlyMappable.add("java.lang.Long");
        directlyMappable.add("java.lang.Short");
        directlyMappable.add("java.lang.String");
        directlyMappable.add("java.util.Optional");
        directlyMappable.add("java.io.File");
        directlyMappable.add("java.lang.Class");
        directlyMappable.add("java.net.URL");
        directlyMappable.add("java.net.URI");
        directlyMappable.add("java.time.Duration");
        directlyMappable.add("com.google.common.net.HostAndPort");
    }

    private CoffigResolver() {
        // no instantiation
    }

    public static FromClass from(Project project) {
        return new FromClass(new Context(project));
    }

    public static class End {
        final Context context;

        End(Context context) {
            this.context = context;
        }

        public Stream<Match> classes() {
            return classStream()
                    .map(psiClass -> {
                        String[] path = resolvePath(psiClass);
                        return buildMatch(path, path.length, psiClass);
                    });
        }

        public Optional<Match> find(@NotNull String path) {
            return find(path, -1);
        }

        public Optional<Match> find(@NotNull String path, int limit) {
            checkArgument(limit >= -1, "limit should be -1, 0 or a positive number");
            String[] split = path.split("\\.");
            int offset = split.length;
            while (!path.isEmpty()) {
                String finalPath = path;
                int finalOffset = offset;
                Optional<Match> match = classStream()
                        .filter(psiClass -> classMatch(psiClass, finalPath))
                        .map(candidate -> findSub(candidate, split, finalOffset, limit))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst();

                if (match.isPresent()) {
                    return match;
                }

                int lastDot = path.lastIndexOf(".");
                if (lastDot == -1 || lastDot >= path.length()) {
                    break;
                } else {
                    path = path.substring(0, lastDot);
                    offset--;
                }
            }

            return Optional.empty();
        }

        private String[] resolvePath(PsiClass psiClass) {
            List<String> path = new ArrayList<>();
            do {
                Optional<String> name = context.getConfigAnnotation(psiClass).flatMap(context::getConfigValue);
                if (name.isPresent()) {
                    path.add(0, name.get());
                } else {
                    return new String[0];
                }
                psiClass = psiClass.getContainingClass();
            } while (psiClass != null);
            return path.toArray(new String[path.size()]);
        }

        Stream<PsiClass> classStream() {
            if (context.isValid()) {
                Stream<PsiClass> stream = StreamSupport.stream(
                        AnnotatedElementsSearch.searchPsiClasses(
                                context.getConfigAnnotationClass(),
                                GlobalSearchScope.allScope(context.getProject())).spliterator(),
                        false);
                Predicate<PsiClass> filter = context.getFilter();
                if (filter != null) {
                    stream = stream.filter(filter);
                }
                return stream;
            } else {
                return Stream.empty();
            }
        }

        private Optional<Match> findSub(@NotNull PsiClass startClass, @NotNull String[] path, int offset, int limit) {
            PsiClass result = startClass;
            int i;
            for (i = offset; i < path.length && (limit == -1 || i < limit + offset); i++) {
                String part = path[i];
                Optional<PsiClass> found = CoffigResolver.from(context.getProject())
                        .onlyInside(result)
                        .classStream()
                        .filter(psiClass -> classMatch(psiClass, part))
                        .findFirst();
                if (found.isPresent()) {
                    // We found a config subclass, let's look into it
                    result = found.get();
                } else {
                    // No config subclass found, find special attributes
                    Optional<PsiClass> found2 = Optional.empty();
                    for (PsiField psiField : result.getAllFields()) {
                        if (part.equals(context.resolveFieldName(psiField))) {
                            found2 = context.resolveFieldConfigType(psiField);
                            if (found2.isPresent()) {
                                break;
                            }
                        }
                    }
                    if (found2.isPresent()) {
                        result = found2.get();
                    } else {
                        break;
                    }
                }
            }
            final int lastIndex = i;
            return Optional.of(result).map(matchClass -> buildMatch(path, lastIndex, matchClass));
        }

        @NotNull
        private Match buildMatch(@NotNull String[] path, int lastIndex, PsiClass matchClass) {
            return new Match(
                    context,
                    matchClass,
                    path[lastIndex - 1],
                    String.join(".", Arrays.copyOfRange(path, 0, lastIndex)),
                    String.join(".", Arrays.copyOfRange(path, lastIndex, path.length)));
        }

        private boolean classMatch(PsiClass psiClass, String path) {
            return path.equals(context.getConfigAnnotation(psiClass).flatMap(context::getConfigValue).orElse(null));
        }
    }

    public static class FromClass extends End {
        FromClass(Context context) {
            super(context);
        }

        public FromClass onlyInside(PsiClass containingClass) {
            applyFilter(psiClass -> psiClass.getContainingClass() == containingClass);
            return this;
        }

        public FromClass onlyAtTopLevel() {
            applyFilter(psiClass -> psiClass.getContainingClass() == null);
            return this;
        }

        public FromClass filteredBy(@NotNull Predicate<PsiClass> filter) {
            context.setFilter(filter);
            return this;
        }

        private void applyFilter(Predicate<PsiClass> filter) {
            Predicate<PsiClass> existingFilter = context.getFilter();
            if (existingFilter != null) {
                context.setFilter(existingFilter.and(filter));
            } else {
                context.setFilter(filter);
            }
        }
    }

    public static class Match {
        private static final String STATIC = "static";
        private final Context context;
        private final PsiClass configClass;
        private final String name;
        private final String matchedPath;
        private final String unmatchedPath;
        private final String fullPath;

        Match(Context context, PsiClass configClass, String name, String matchedPath, String unmatchedPath) {
            this.context = context;
            this.configClass = configClass;
            this.name = name;
            this.matchedPath = matchedPath;
            this.unmatchedPath = unmatchedPath;
            this.fullPath = matchedPath + (!matchedPath.isEmpty() && !unmatchedPath.isEmpty() ? "." : "") + unmatchedPath;
        }

        public PsiClass getConfigClass() {
            return configClass;
        }

        public String getFullPath() {
            return fullPath;
        }

        public String getMatchedPath() {
            return matchedPath;
        }

        public String getUnmatchedPath() {
            return unmatchedPath;
        }

        public String getName() {
            return name;
        }

        public Match fullyResolve() {
            if (!isFullyResolved()) {
                // If multiple levels are still unmatched, try to resolve the deepest class
                return CoffigResolver.from(configClass.getProject())
                        .onlyInside(configClass)
                        .find(unmatchedPath)
                        .orElse(this);
            } else {
                return this;
            }
        }

        public boolean isFullyResolved() {
            return unmatchedPath.isEmpty();
        }

        public Optional<PsiField> resolveField(String propertyName) {
            for (PsiField psiField : configClass.getAllFields()) {
                if (propertyName.equals(context.resolveFieldName(psiField))) {
                    return Optional.of(psiField);
                }
            }
            return Optional.empty();
        }

        public Stream<String> allProperties() {
            return Arrays.stream(configClass.getAllFields())
                    .filter(psiField -> !psiField.hasModifierProperty(STATIC))
                    .map(context::resolveFieldName);
        }
    }

    private static class Context {
        private final Project project;
        private final PsiClass configAnnotationClass;
        private final PsiType collectionType;
        private final PsiType mapType;
        private Predicate<PsiClass> filter;

        private Context(Project project) {
            this.project = project;
            this.configAnnotationClass = JavaPsiFacade.getInstance(project).findClass(ORG_SEEDSTACK_COFFIG_CONFIG, GlobalSearchScope.allScope(project));
            this.collectionType = Optional.ofNullable(JavaPsiFacade.getInstance(project).findClass(JAVA_UTIL_COLLECTION, GlobalSearchScope.allScope(project)))
                    .map(psiClass -> PsiElementFactory.SERVICE.getInstance(project).createType(psiClass))
                    .orElse(null);
            this.mapType = Optional.ofNullable(JavaPsiFacade.getInstance(project).findClass(JAVA_UTIL_MAP, GlobalSearchScope.allScope(project)))
                    .map(psiClass -> PsiElementFactory.SERVICE.getInstance(project).createType(psiClass))
                    .orElse(null);
        }

        Project getProject() {
            return project;
        }

        PsiClass getConfigAnnotationClass() {
            return configAnnotationClass;
        }

        boolean isValid() {
            return configAnnotationClass != null;
        }

        Predicate<PsiClass> getFilter() {
            return filter;
        }

        void setFilter(Predicate<PsiClass> filter) {
            this.filter = filter;
        }

        Optional<PsiAnnotation> getConfigAnnotation(@NotNull PsiModifierListOwner psiModifierListOwner) {
            return Optional.ofNullable(psiModifierListOwner.getModifierList())
                    .map(PsiAnnotationOwner::getAnnotations)
                    .map(Arrays::stream)
                    .map(stream -> stream.filter(annotation -> Optional.ofNullable(annotation.getNameReferenceElement())
                            .map(PsiReference::resolve)
                            .map(psiElement -> psiElement == configAnnotationClass)
                            .orElse(false)
                    ))
                    .flatMap(Stream::findFirst);
        }

        Optional<String> getConfigValue(PsiAnnotation psiAnnotation) {
            return Optional.ofNullable(psiAnnotation.getParameterList().getAttributes()[0].getValue())
                    .map(PsiElement::getText)
                    .map(text -> text.substring(1, text.length() - 1));
        }

        String resolveFieldName(PsiField psiField) {
            return getConfigAnnotation(psiField)
                    .flatMap(this::getConfigValue)
                    .orElseGet(() -> Optional.of(psiField)
                            .map(PsiVariable::getType)
                            .filter(psiType -> psiType instanceof PsiClassType)
                            .map(psiType -> ((PsiClassType) psiType).resolve())
                            .flatMap(this::getConfigAnnotation)
                            .flatMap(this::getConfigValue)
                            .orElse(psiField.getName())
                    );
        }

        Optional<PsiClass> resolveFieldConfigType(PsiField psiField) {
            PsiType fieldType = psiField.getType();
            if (fieldType instanceof PsiClassType) {
                PsiClassType fieldClassType = ((PsiClassType) fieldType);
                if (collectionType != null && collectionType.isAssignableFrom(fieldType) && fieldClassType.getParameterCount() == 1) {
                    return toPsiClass(fieldClassType.getParameters()[0]);
                } else if (mapType != null && mapType.isAssignableFrom(fieldType) && fieldClassType.getParameterCount() == 2) {
                    return toPsiClass(fieldClassType.getParameters()[1]);
                } else {
                    return toPsiClass(fieldType);
                }
            } else if (fieldType instanceof PsiArrayType) {
                return toPsiClass(((PsiArrayType) fieldType).getComponentType());
            } else {
                return Optional.empty();
            }
        }

        private Optional<PsiClass> toPsiClass(PsiType psiType) {
            if (psiType instanceof PsiClassType) {
                PsiClass psiClass = ((PsiClassType) psiType).resolve();
                if (psiClass != null && !psiClass.isEnum() && !directlyMappable.contains(psiClass.getQualifiedName())) {
                    return Optional.of(psiClass);
                }
            }
            return Optional.empty();
        }
    }
}

