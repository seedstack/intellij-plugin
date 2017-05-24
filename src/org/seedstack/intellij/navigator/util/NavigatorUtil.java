package org.seedstack.intellij.navigator.util;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.application.impl.LaterInvocator;
import com.intellij.openapi.project.DumbService;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupManager;
import com.intellij.util.DisposeAwareRunnable;

public final class NavigatorUtil {
    private NavigatorUtil() {
        // no instantiation allowed
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

    public static void runDumbAware(final Project project, final Runnable r) {
        if (DumbService.isDumbAware(r)) {
            r.run();
        } else {
            DumbService.getInstance(project).runWhenSmart(DisposeAwareRunnable.create(r, project));
        }
    }
}
