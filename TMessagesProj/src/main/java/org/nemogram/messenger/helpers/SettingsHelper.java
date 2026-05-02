package org.nemogram.messenger.helpers;

import android.net.Uri;
import android.text.TextUtils;

import org.telegram.messenger.AndroidUtilities;
import org.telegram.messenger.browser.Browser;
import org.telegram.ui.ActionBar.BaseFragment;
import org.telegram.ui.LaunchActivity;

import java.util.Locale;
import java.util.function.Consumer;

import org.nemogram.messenger.settings.BaseNekoSettingsActivity;
import org.nemogram.messenger.settings.NekoAppearanceSettingsActivity;
import org.nemogram.messenger.settings.NekoChatSettingsActivity;
import org.nemogram.messenger.settings.NekoEmojiSettingsActivity;
import org.nemogram.messenger.settings.NekoExperimentalSettingsActivity;
import org.nemogram.messenger.settings.NekoGeneralSettingsActivity;
import org.nemogram.messenger.settings.NekoPasscodeSettingsActivity;
import org.nemogram.messenger.settings.NemoKeywordFilterActivity;

public class SettingsHelper {

    public static void processDeepLink(Uri uri, Consumer<BaseFragment> callback, Runnable unknown, Browser.Progress progress) {
        if (uri == null) {
            unknown.run();
            return;
        }
        var segments = uri.getPathSegments();
        if (segments.isEmpty() || segments.size() > 2) {
            unknown.run();
            return;
        }
        BaseNekoSettingsActivity fragment;
        var segment = segments.get(1);
        if (PasscodeHelper.getSettingsKey().equals(segment)) {
            fragment = new NekoPasscodeSettingsActivity();
        } else {
            switch (segment.toLowerCase(Locale.US)) {
                case "appearance":
                case "a":
                    fragment = new NekoAppearanceSettingsActivity();
                    break;
                case "chat":
                case "chats":
                case "c":
                    fragment = new NekoChatSettingsActivity();
                    break;
                case "experimental":
                case "e":
                    fragment = new NekoExperimentalSettingsActivity();
                    break;
                case "emoji":
                    fragment = new NekoEmojiSettingsActivity();
                    break;
                case "general":
                case "g":
                    fragment = new NekoGeneralSettingsActivity();
                    break;
                case "keywordfilter":
                case "kf":
                    fragment = new NemoKeywordFilterActivity();
                    break;
                case "update":
                    LaunchActivity.instance.checkAppUpdate(true, progress);
                    return;
                default:
                    unknown.run();
                    return;
            }
        }
        callback.accept(fragment);
        var row = uri.getQueryParameter("r");
        if (TextUtils.isEmpty(row)) {
            row = uri.getQueryParameter("row");
        }
        if (!TextUtils.isEmpty(row)) {
            var rowFinal = row;
            AndroidUtilities.runOnUIThread(() -> fragment.scrollToRow(rowFinal, unknown));
        }
    }
}
