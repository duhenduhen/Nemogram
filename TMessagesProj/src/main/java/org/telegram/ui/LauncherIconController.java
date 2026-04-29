package org.telegram.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageManager;

import org.telegram.messenger.ApplicationLoader;
import org.telegram.messenger.R;

public class LauncherIconController {

    public static void tryFixLauncherIconIfNeeded() {
        if (!isEnabled(LauncherIcon.DEFAULT)) {
            setIcon(LauncherIcon.DEFAULT);
        }
    }

    public static boolean isEnabled(LauncherIcon icon) {
        Context ctx = ApplicationLoader.applicationContext;
        int state = ctx.getPackageManager().getComponentEnabledSetting(icon.getComponentName(ctx));
        return state == PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                || state == PackageManager.COMPONENT_ENABLED_STATE_DEFAULT;
    }

    public static void setIcon(LauncherIcon icon) {
        Context ctx = ApplicationLoader.applicationContext;
        ctx.getPackageManager().setComponentEnabledSetting(
                icon.getComponentName(ctx),
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP
        );
    }

    public enum LauncherIcon {
        DEFAULT("DefaultIcon", R.color.ic_launcher_background, R.drawable.ic_launcher_foreground, R.string.AppIconDefault);

        public final String key;
        public final int background;
        public final int foreground;
        public final int title;
        public final boolean premium = false;

        private ComponentName componentName;

        public ComponentName getComponentName(Context ctx) {
            if (componentName == null) {
                componentName = new ComponentName(ctx.getPackageName(), "org.telegram.messenger." + key);
            }
            return componentName;
        }

        LauncherIcon(String key, int background, int foreground, int title) {
            this.key = key;
            this.background = background;
            this.foreground = foreground;
            this.title = title;
        }
    }
}