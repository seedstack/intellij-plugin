/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.intellij.navigator.util;

import com.google.common.base.CaseFormat;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionManager;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.DataContext;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.ModalityState;
import com.intellij.openapi.application.impl.LaterInvocator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.pom.Navigatable;
import com.intellij.pom.NavigatableAdapter;
import com.intellij.psi.PsiClass;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiManager;
import com.intellij.psi.PsiModifierList;
import com.intellij.util.DisposeAwareRunnable;
import com.intellij.util.concurrency.Semaphore;
import org.jetbrains.annotations.Nullable;

import java.awt.event.InputEvent;

public final class NavigatorUtil {
    private NavigatorUtil() {
        // no instantiation allowed
    }

    public static boolean hasProject(DataContext context) {
        return CommonDataKeys.PROJECT.getData(context) != null;
    }

    @Nullable
    public static Project getProject(DataContext context) {
        return CommonDataKeys.PROJECT.getData(context);
    }

    @Nullable
    public static Navigatable createNavigatableForFile(final Project project, final VirtualFile file) {
        if (file != null && file.isValid()) {
            final PsiFile result = PsiManager.getInstance(project).findFile(file);
            return result == null ? null : new NavigatableAdapter() {
                public void navigate(boolean requestFocus) {
                    navigate(project, file, 0, requestFocus);
                }
            };
        } else {
            return null;
        }
    }

    public static void executeAction(String actionId, InputEvent e) {
        ActionManager actionManager = ActionManager.getInstance();
        AnAction action = actionManager.getAction(actionId);
        if (action != null) {
            Presentation presentation = new Presentation();
            AnActionEvent event = new AnActionEvent(e, DataManager.getInstance().getDataContext(e.getComponent()), "", presentation, actionManager, 0);
            action.update(event);
            if (presentation.isEnabled()) {
                action.actionPerformed(event);
            }
        }
    }

    public static void runWhenInitialized(final Project project, final Runnable r) {
        if (project.isDisposed()) return;

        if (isNoBackgroundMode()) {
            r.run();
            return;
        }

        if (!project.isInitialized()) {
            StartupManager.getInstance(project).registerPostStartupActivity(DisposeAwareRunnable.create(r, project));
            return;
        }

        runDumbAware(project, r);
    }

    public static boolean isNoBackgroundMode() {
        return (ApplicationManager.getApplication().isUnitTestMode()
                || ApplicationManager.getApplication().isHeadlessEnvironment());
    }

    public static boolean isInModalContext() {
        if (isNoBackgroundMode()) return false;
        return LaterInvocator.isInModalContext();
    }

    public static void invokeLater(Project p, Runnable r) {
        invokeLater(p, ModalityState.defaultModalityState(), r);
    }

    public static void invokeLater(final Project p, final ModalityState state, final Runnable r) {
        if (isNoBackgroundMode()) {
            r.run();
        } else {
            ApplicationManager.getApplication().invokeLater(DisposeAwareRunnable.create(r, p), state);
        }
    }

    public static void invokeAndWait(Project p, Runnable r) {
        invokeAndWait(p, ModalityState.defaultModalityState(), r);
    }

    public static void invokeAndWait(final Project p, final ModalityState state, final Runnable r) {
        if (isNoBackgroundMode()) {
            r.run();
        } else {
            ApplicationManager.getApplication().invokeAndWait(DisposeAwareRunnable.create(r, p), state);
        }
    }

    public static void smartInvokeAndWait(final Project p, final ModalityState state, final Runnable r) {
        if (isNoBackgroundMode() || ApplicationManager.getApplication().isDispatchThread()) {
            r.run();
        } else {
            final Semaphore semaphore = new Semaphore();
            semaphore.down();
            DumbService.getInstance(p).smartInvokeLater(() -> {
                try {
                    r.run();
                } finally {
                    semaphore.up();
                }
            }, state);
            semaphore.waitFor();
        }
    }

    public static void invokeAndWaitWriteAction(Project p, final Runnable r) {
        invokeAndWait(p, () -> ApplicationManager.getApplication().runWriteAction(r));
    }

    public static void runDumbAware(final Project project, final Runnable r) {
        if (DumbService.isDumbAware(r)) {
            r.run();
        } else {
            DumbService.getInstance(project).runWhenSmart(DisposeAwareRunnable.create(r, project));
        }
    }

    public static String humanizeString(String name, String suffixToRemove) {
        if (suffixToRemove != null && name.endsWith(suffixToRemove)) {
            name = name.substring(0, name.length() - suffixToRemove.length());
        }
        name = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, name).replace("_", " ");
        return name.substring(0, 1).toUpperCase() + name.substring(1).toLowerCase();
    }

    public static boolean isAbstract(PsiClass psiClass) {
        PsiModifierList modifierList = psiClass.getModifierList();
        return modifierList != null && modifierList.hasModifierProperty("abstract");
    }
}
