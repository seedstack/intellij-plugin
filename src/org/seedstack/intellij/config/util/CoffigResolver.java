package org.seedstack.intellij.config.util;

import com.intellij.openapi.project.Project;
import com.intellij.psi.JavaPsiFacade;
import com.intellij.psi.PsiAnnotation;
import com.intellij.psi.PsiAnnotationOwner;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiClassType;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiField;
import com.intellij.psi.PsiModifierListOwner;
import com.intellij.psi.PsiReference;
import com.intellij.psi.PsiVariable;
import com.intellij.psi.search.GlobalSearchScope;
import com.intellij.psi.search.searches.AnnotatedElementsSearch;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Predicate;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public class CoffigResolver {
    private static final String COFFIG_ANNOTATION_QNAME = "org.seedstack.coffig.Config";

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
            if (limit >= -1 && !path.isEmpty()) {
                String[] split = path.split("\\.");
                return classStream()
                        .filter(psiClass -> classMatch(psiClass, split[0]))
                        .map(candidate -> findSub(split, candidate, limit))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .findFirst();
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

        private Optional<Match> findSub(@NotNull String[] path, @Nullable PsiClass startClass, int limit) {
            PsiClass result = startClass;
            int i;
            for (i = 1; i < path.length && (limit == -1 || i < limit + 1); i++) {
                String part = path[i];
                Optional<PsiClass> found = CoffigResolver.from(context.getProject())
                        .onlyInside(startClass)
                        .classStream()
                        .filter(psiClass -> classMatch(psiClass, part))
                        .findFirst();
                if (found.isPresent()) {
                    result = found.get();
                } else {
                    break;
                }
            }
            final int lastIndex = i;
            return Optional.ofNullable(result).map(matchClass -> buildMatch(path, lastIndex, matchClass));
        }

        @NotNull
        private Match buildMatch(@NotNull String[] path, int lastIndex, PsiClass matchClass) {
            return new Match(
                    context,
                    matchClass,
                    String.join(".", Arrays.copyOfRange(path, 0, lastIndex)),
                    String.join(".", Arrays.copyOfRange(path, lastIndex, path.length)));
        }

        private boolean classMatch(PsiClass psiClass, String pathPart) {
            return pathPart.equals(context.getConfigAnnotation(psiClass).flatMap(context::getConfigValue).orElse(null));
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
        private final String matchedPath;
        private final String unmatchedPath;
        private final String fullPath;
        private final String name;
        private final Map<PsiField, String> fieldCache = new ConcurrentHashMap<>();

        Match(Context context, PsiClass configClass, String matchedPath, String unmatchedPath) {
            this.context = context;
            this.configClass = configClass;
            this.matchedPath = matchedPath;
            this.unmatchedPath = unmatchedPath;
            this.fullPath = matchedPath + (!matchedPath.isEmpty() && !unmatchedPath.isEmpty() ? "." : "") + unmatchedPath;
            this.name = Optional.of(configClass)
                    .flatMap(context::getConfigAnnotation)
                    .flatMap(context::getConfigValue)
                    .orElseThrow(() -> new IllegalStateException("No @Config annotation on class " + configClass));
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
                if (propertyName.equals(resolveFieldName(psiField))) {
                    return Optional.of(psiField);
                }
            }
            return Optional.empty();
        }

        public Stream<String> allProperties() {
            return Arrays.stream(configClass.getAllFields())
                    .filter(psiField -> !psiField.hasModifierProperty(STATIC))
                    .map(this::resolveFieldName);
        }

        private String resolveFieldName(PsiField psiField) {
            String name = fieldCache.get(psiField);
            if (name == null) {
                fieldCache.putIfAbsent(psiField, name = context.getConfigAnnotation(psiField)
                        .flatMap(context::getConfigValue)
                        .orElseGet(() -> Optional.of(psiField)
                                .map(PsiVariable::getType)
                                .filter(psiType -> psiType instanceof PsiClassType)
                                .map(psiType -> ((PsiClassType) psiType).resolve())
                                .flatMap(context::getConfigAnnotation)
                                .flatMap(context::getConfigValue)
                                .orElse(psiField.getName())
                        ));
            }
            return name;
        }
    }

    private static class Context {
        private final Project project;
        private final PsiClass configAnnotationClass;
        private Predicate<PsiClass> filter;

        private Context(Project project) {
            this.project = project;
            this.configAnnotationClass = JavaPsiFacade.getInstance(project).findClass(COFFIG_ANNOTATION_QNAME, GlobalSearchScope.allScope(project));
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
    }
}

