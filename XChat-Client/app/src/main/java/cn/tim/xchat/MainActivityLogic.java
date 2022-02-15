package cn.tim.xchat;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.DisplayCutout;
import android.view.Gravity;
import android.view.View;
import android.view.WindowInsets;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tencent.mmkv.MMKV;

import java.util.List;

import cn.tim.xchat.chat.MessageListFragment;
import cn.tim.xchat.common.utils.DensityUtil;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.titlebar.TitleBarType;
import cn.tim.xchat.common.widget.toast.XChatToast;
import cn.tim.xchat.contacts.ContactsFragment;
import cn.tim.xchat.personal.PersonalFragment;
import q.rorbin.badgeview.QBadgeView;

public class MainActivityLogic implements DefaultLifecycleObserver {
    private static final String TAG = ActivityProvider.TAG;
    protected QBadgeView chatQBadgeView;
    protected ActivityProvider activityProvider;
    protected Bundle savedStateBundle;
    protected String currentTab;

    public MainActivityLogic(MainActivity activity,
                             Bundle savedInstanceState,
                             String tab) {
        activityProvider = activity;
        savedStateBundle = savedInstanceState;
        currentTab = tab;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            getNotchParams();
        }

        BottomNavigationView bottomNavView = activityProvider.findViewById(R.id.app_main_bottom_navigation);
        BottomNavigationItemView chat = bottomNavView.findViewById(R.id.tab_menu_chat);

        if(chatQBadgeView == null) {
            chatQBadgeView = new QBadgeView(activityProvider.getActivity());
        }

        chatQBadgeView.bindTarget(chat)
                .setBadgeGravity(Gravity.END|Gravity.TOP)
                .setGravityOffset(20, 0, true)
                .setBadgeNumber(20)
                .setBadgeTextSize(9, true)
                .setOnDragStateChangedListener((dragState, badge, targetView) -> {

                });

        ViewPager2 viewPager = activityProvider.findViewById(R.id.app_main_viewpager);
        setupViewPager(viewPager);

        bottomNavView.setOnItemSelectedListener(item -> {
            int itemId = item.getItemId();
            if(itemId == R.id.tab_menu_chat){
                viewPager.setCurrentItem(0);
            }else if(itemId == R.id.tab_menu_contact){
                viewPager.setCurrentItem(1);
            }else if(itemId == R.id.tab_menu_personal){
                viewPager.setCurrentItem(2);
            }
            return true;
        });

        BaseTitleBar baseTitleBar = activityProvider.findViewById(R.id.app_main_titlebar);
        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                switch (position){
                    case 0:
                        currentTab = "chat";
                        selectTabByName(viewPager, bottomNavView, baseTitleBar);
                        break;
                    case 1:
                        currentTab = "contacts";
                        selectTabByName(viewPager, bottomNavView, baseTitleBar);
                        break;
                    case 2:
                        currentTab = "personal";
                        selectTabByName(viewPager, bottomNavView, baseTitleBar);
                        break;
                }
            }
        });

        selectTabByName(viewPager, bottomNavView, baseTitleBar);
    }

    private void selectTabByName(ViewPager2 viewPager,
                                 BottomNavigationView bottomNavView,
                                 BaseTitleBar baseTitleBar) {
        int itemIndex = 0;
        int navItemId = R.id.tab_menu_chat;
        TitleBarType type = TitleBarType.MESSAGE_MAIN_PAGER;
        if(!TextUtils.isEmpty(currentTab)) {
            if (currentTab.equals("contacts")) {
                itemIndex = 1;
                navItemId = R.id.tab_menu_contact;
                type = TitleBarType.CONTACTS_MAIN_PAGER;
            } else if (currentTab.equals("personal")) {
                itemIndex = 2;
                navItemId = R.id.tab_menu_personal;
                type = TitleBarType.PERSONAL_MAIN_PAGER;
            }
        }
        if(bottomNavView != null) bottomNavView.setSelectedItemId(navItemId);
        if(viewPager != null) viewPager.setCurrentItem(itemIndex);
        if(baseTitleBar != null) baseTitleBar.autoChangeByType(type);
    }


    private void setupViewPager(ViewPager2 viewPager) {
        Fragment[] fragments = new Fragment[]{
                new MessageListFragment(),
                new ContactsFragment(),
                new PersonalFragment()
        };
        FragmentStateAdapter stateAdapter = new FragmentStateAdapter
                ((FragmentActivity) activityProvider.getActivity()) {
            @Override
            public int getItemCount() {
                return fragments.length;
            }

            @NonNull
            @Override
            public Fragment createFragment(int position) {
                return fragments[position];
            }
        };
        viewPager.setAdapter(stateAdapter);
    }

    @RequiresApi(api = Build.VERSION_CODES.P)
    public void getNotchParams() {
        final View decorView = activityProvider.getWindow().getDecorView();
        RelativeLayout topSafeLayout = activityProvider.findViewById(R.id.app_main_top_safe);
        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) topSafeLayout.getLayoutParams();

        decorView.post(() -> {
            WindowInsets rootWindowInsets = decorView.getRootWindowInsets();
            DisplayCutout displayCutout = rootWindowInsets.getDisplayCutout();
            if(displayCutout != null) {
                Log.v(TAG, "安全区域距离屏幕左边的距离 SafeInsetLeft:" + displayCutout.getSafeInsetLeft());
                Log.v(TAG, "安全区域距离屏幕右部的距离 SafeInsetRight:" + displayCutout.getSafeInsetRight());
                Log.v(TAG, "安全区域距离屏幕顶部的距离 SafeInsetTop:" + displayCutout.getSafeInsetTop());
                Log.v(TAG, "安全区域距离屏幕底部的距离 SafeInsetBottom:" + displayCutout.getSafeInsetBottom());
                List<Rect> rectList = displayCutout.getBoundingRects();
                if (rectList == null || rectList.size() == 0) {
                    Log.v(TAG, "不是刘海屏");
                } else {
                    Log.v(TAG, "刘海屏数量:" + rectList.size());
                    for (Rect rect : rectList) {
                        Log.v(TAG, "刘海屏区域：" + rect);
                    }
                }

                layoutParams.height = displayCutout.getSafeInsetTop();
                Log.i(TAG, "getNotchParams: layoutParams.height = " + layoutParams.height);
            }else {
                layoutParams.height = DensityUtil.dp2px(50);
            }
            topSafeLayout.setLayoutParams(layoutParams);
        });
    }
}
