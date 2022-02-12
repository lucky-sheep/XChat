package cn.tim.xchat;

import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
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
import androidx.lifecycle.Lifecycle;
import androidx.lifecycle.LifecycleEventObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.util.List;

import cn.tim.xchat.chat.MessageListFragment;
import cn.tim.xchat.common.base_utils.DensityUtil;
import cn.tim.xchat.common.base_widget.BaseTitleBar;
import cn.tim.xchat.common.base_widget.TitleBarType;
import cn.tim.xchat.contacts.ContactsFragment;
import cn.tim.xchat.personal.PersonalFragment;
import q.rorbin.badgeview.QBadgeView;

public class MainActivityLogic implements LifecycleEventObserver {
    private static final String TAG = XChatBaseActivity.TAG;
    protected QBadgeView chatQBadgeView;
    protected ActivityProvider activityProvider;

    public MainActivityLogic(MainActivity activity, Bundle savedInstanceState) {
        activityProvider = activity;
    }

    @Override
    public void onStateChanged(@NonNull LifecycleOwner source, @NonNull Lifecycle.Event event) {
        if(event.equals(Lifecycle.Event.ON_CREATE)){
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
                    Log.i(TAG, "onPageSelected: position = " + position);
                    int itemId = -1;
                    switch (position){
                        case 0:
                            itemId = R.id.tab_menu_chat;
                            baseTitleBar.autoChangeByType(TitleBarType.MESSAGE_MAIN_PAGER);
                            break;
                        case 1:
                            itemId = R.id.tab_menu_contact;
                            baseTitleBar.autoChangeByType(TitleBarType.CONTACTS_MAIN_PAGER);
                            break;
                        case 2:
                            itemId = R.id.tab_menu_personal;
                            baseTitleBar.autoChangeByType(TitleBarType.PERSONAL_MAIN_PAGER);
                            break;
                    }
                    bottomNavView.setSelectedItemId(itemId);
                }
            });
        }
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
