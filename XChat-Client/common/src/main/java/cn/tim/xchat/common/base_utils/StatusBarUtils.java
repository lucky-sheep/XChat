package cn.tim.xchat.common.base_utils;


import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;

import androidx.annotation.RequiresApi;

import java.util.UUID;

public class StatusBarUtils {

    private static final String TAG = StatusBarUtils.class.getSimpleName();

    /**
     * 全透状态栏
     */
    public static void setStatusBarFullTransparent(Activity activity) {
        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            Window window = activity.getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 半透明状态栏
     */
    public static void setHalfTransparent(Activity activity) {

        if (Build.VERSION.SDK_INT >= 21) {//21表示5.0
            View decorView = activity.getWindow().getDecorView();
            int option = View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE;
            decorView.setSystemUiVisibility(option);
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        } else if (Build.VERSION.SDK_INT >= 19) {//19表示4.4
            activity.getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            //虚拟键盘也透明
            // getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
        }
    }

    /**
     * 获取状态栏高度
     *
     * @param context context
     * @return 状态栏高度
     */
    public static int getStatusBarHeight(Context context) {
        Resources resources = context.getResources();
        // 获得状态栏高度
        int resourceId = resources.getIdentifier(
                "status_bar_height", "dimen", "android");
        return resources.getDimensionPixelSize(resourceId);
    }

    /**
     * 获取导航栏高度
     *
     * @param context context
     * @return 导航栏高度
     */
    public static int getNavigationBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier(
                "navigation_bar_height", "dimen", "android");
        return context.getResources().getDimensionPixelSize(resourceId);
    }

    /**
     * 和5.0+设置Activity移动到顶端的方式不同
     * @param window
     * @param activity
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setStatusBarTransparentKITKAT(
            Window window, Activity activity) {
        // 透明状态栏（有阴影）
        // 设置半透明状态栏,这样才能让 ContentView 向上移动到状态栏下面去
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
    }

    /**
     * 和4.4+设置Activity移动到顶端的方式不同
     * 以前说到去除状态栏和标题栏总会用到动态代码的方式实现：
     *
     * getWindow().setFlags(WindowManager.LayoutParams. FLAG_FULLSCREEN , WindowManager.LayoutParams. FLAG_FULLSCREEN);
     * requestWindowFeature(Window.FEATURE_NO_TITLE);
     * 但是有一个更好用的API，setSystemUiVisibility();
     * 基本上可以定义为状态栏和Activity之间的位置关系。 其动态隐藏和显示状态栏的方式如下：
     * View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN ：Activity全屏显示，但是状态栏不会被覆盖掉，而是正常显示，只是Activity顶端布局会被覆盖住(即拓展布局到状态栏后面)
     * View.SYSTEM_UI_FLAG_VISIBLE ：状态栏和Activity共存，Activity不全屏显示。也就是应用平常的显示画面
     * View.SYSTEM_UI_FLAG_FULLSCREEN ：Activity全屏显示，且状态栏被覆盖掉
     * View.INVISIBLE ： Activity全屏显示，隐藏状态栏
     * View.SYSTEM_UI_FLAG_LOW_PROFILE     弱化状态栏和导航栏的图标
     * View.SYSTEM_UI_FLAG_HIDE_NAVIGATION     隐藏导航栏，用户点击屏幕会显示导航栏
     * View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION     拓展布局到导航栏后面
     * View.SYSTEM_UI_FLAG_LAYOUT_STABLE     稳定的布局，不会随系统栏的隐藏、显示而变化
     * View.SYSTEM_UI_FLAG_IMMERSIVE     沉浸模式，用户可以交互的界面
     * View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY     沉浸模式，用户可以交互的界面。同时，用户上下拉系统栏时，会自动隐藏系统栏
     * @param window
     * @param activity
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarTransparentLOLLIPOP(
            Window window, Activity activity) {
        // 去除半透明状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 设置屏幕全屏显示：activity置顶，且和状态栏共存
        window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        // 需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // 设置状态栏颜色
        window.setStatusBarColor(Color.TRANSPARENT);
    }

    /**
     * 先使用 FLAG_TRANSLUCENT_STATUS 设置状态栏透明。然后用一个期望的背景色view填充statusBar
     * @param window
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static void setStatusBarColorKITKAT(
            Window window, Activity activity, int color) {
        // 设置状态栏透明
        window.addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);

        // 一个期望的背景色view填充statusBar
        ViewGroup decorViewGroup = (ViewGroup) window.getDecorView();
        View statusBarView = new View(window.getContext());

        //获取statusBar高度
        int statusBarHeight = getStatusBarHeight(window.getContext());

        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, statusBarHeight);
        params.gravity = Gravity.TOP;
        statusBarView.setLayoutParams(params);
        statusBarView.setBackgroundColor(color);
        decorViewGroup.addView(statusBarView);

        //设置标题栏下移
        ViewGroup rootView = (ViewGroup) ((ViewGroup) activity.findViewById(Window.ID_ANDROID_CONTENT)).
                getChildAt(0);
        if (null != rootView) {
            rootView.setFitsSystemWindows(true);
            rootView.setClipToPadding(true);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public static void setStatusBarColorLOLLIPOP(
            Window window, Activity activity,  int color) {
        // 取消设置透明状态栏,使 ContentView 内容不再覆盖状态栏
        window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        // 需要设置这个 flag 才能调用 setStatusBarColor 来设置状态栏颜色
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        // 设置状态栏颜色
        window.setStatusBarColor(color);
    }

    /**
     * 修改状态栏颜色。
     * @param window
     * @param activity
     * @param color
     */
    public static void setStatusBarColor(
            Window window, Activity activity, int color) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarColorLOLLIPOP(window, activity, color);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarColorKITKAT(window, activity, color);
        }
    }

    /**
     * 设置状态栏为透明的。contentView上移到状态栏下面去
     * @param window
     * @param activity
     */
    public static void setStatusBarTransparent(
            Window window, Activity activity) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setStatusBarTransparentLOLLIPOP(window, activity);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            setStatusBarTransparentKITKAT(window, activity);
        }
    }

    /**
     * 设置状态栏图标为黑色或者白色
     * <p>
     * 参数true表示StatusBar风格为Light，字体颜色为黑色
     * 参数false表示StatusBar风格不是Light，字体颜色为白色
     * <p>
     * <item name="android:windowLightStatusBar">true</item>
     * 在theme或style中使用这个属性改变StatusBar的字体颜色，这种形式相对不灵活
     * @param window
     * @param isDark true表示状态栏文字颜色为黑色  false表示白色
     */
    public static void setDarkStatusIcon(Window window, boolean isDark) {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
            View decorView = window.getDecorView();
            if(decorView != null){
                int vis = decorView.getSystemUiVisibility();
                if(isDark){
                    vis |= View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                } else{
                    vis &= ~View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR;
                }
                decorView.setSystemUiVisibility(vis);
            }
        }
    }

    public static void setStatusBarTransparent(Window window, Activity activity, boolean isDark) {
        setStatusBarTransparent(window, activity);
        setDarkStatusIcon(window, isDark);
    }

    private static int STATUS_BAR_HEIGHT;

    public static int getStatusHeight(Context context) {
        if (STATUS_BAR_HEIGHT > 0)
            return STATUS_BAR_HEIGHT;
        int statusBarHeight1 = -1;
        //获取status_bar_height资源的ID
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            //根据资源ID获取响应的尺寸值
            statusBarHeight1 = context.getResources().getDimensionPixelSize(resourceId);
        }
        return STATUS_BAR_HEIGHT = statusBarHeight1;
    }
    /**
     * 计算View Id
     *
     * @return
     */
    public static int generateViewId() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            return View.generateViewId();
        } else {
            return UUID.randomUUID().hashCode();
        }
    }

}

