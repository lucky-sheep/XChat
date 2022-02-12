package cn.tim.xchat;

import android.app.Activity;
import android.content.res.Resources;
import android.view.View;
import android.view.Window;

import androidx.annotation.IdRes;
import androidx.annotation.StringRes;
import androidx.fragment.app.FragmentManager;

public interface ActivityProvider {
    <T extends View> T findViewById(@IdRes int id);

    Resources getResources();

    FragmentManager getSupportFragmentManager();

    String getString(@StringRes int resId);

    Window getWindow();

    Activity getActivity();

    void postRunnable(Runnable runnable);

    void postDelayRunnable(Runnable runnable, long delayMs);
}
