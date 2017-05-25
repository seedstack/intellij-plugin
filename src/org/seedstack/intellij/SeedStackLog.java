package org.seedstack.intellij;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.diagnostic.Logger;

public final class SeedStackLog {
    public static final Logger LOG = Logger.getInstance("#org.seedstack.intellij");

    private SeedStackLog() {
    }

    public static void printInTests(Throwable e) {
        if (ApplicationManager.getApplication().isUnitTestMode()) {
            e.printStackTrace();
        }
    }
}