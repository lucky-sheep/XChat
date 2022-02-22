package cn.tim.xchat;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.tencent.mmkv.MMKV;

import cn.tim.xchat.chat.MessageListFragment;
import cn.tim.xchat.common.XChatBaseActivity;
import cn.tim.xchat.common.mvvm.MainViewModel;
import cn.tim.xchat.common.widget.titlebar.BaseTitleBar;
import cn.tim.xchat.common.widget.titlebar.TitleBarType;
import cn.tim.xchat.contacts.ContactsFragment;
import cn.tim.xchat.personal.PersonalFragment;
import q.rorbin.badgeview.QBadgeView;

public class MainActivityLogic implements DefaultLifecycleObserver {
    protected QBadgeView chatQBadgeView;
    protected QBadgeView contactQBadgeView;
//    protected QBadgeView newFriendQBadgeView;

    protected ActivityProvider provider;
    protected Bundle savedStateBundle;
    protected String currentTab;
    private BaseTitleBar baseTitleBar;

    private static final String TAG = "MainActivityLogic";
    private final MMKV mmkv;
    private MainViewModel mainViewModel;
    private ContactsFragment contactsFragment;
    private BottomNavigationItemView chat;
    private BottomNavigationItemView contact;
    private XChatBaseActivity activity;

    public MainActivityLogic(MainActivity activity,
                             Bundle savedInstanceState,
                             String tab) {
        provider = activity;
        savedStateBundle = savedInstanceState;
        currentTab = (tab == null ? "chat":tab);
        mmkv = MMKV.defaultMMKV();
        baseTitleBar = provider.findViewById(R.id.app_main_titlebar);
        this.activity = activity;
    }

    @Override
    public void onCreate(@NonNull LifecycleOwner owner) {
        BottomNavigationView bottomNavView = provider.findViewById(R.id.app_main_bottom_navigation);
        chat = bottomNavView.findViewById(R.id.tab_menu_chat);
        contact = bottomNavView.findViewById(R.id.tab_menu_contact);

        mainViewModel = new ViewModelProvider(activity).get(MainViewModel.class);

        ViewPager2 viewPager = provider.findViewById(R.id.app_main_viewpager);
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

//        mainViewModel = new ViewModelProvider(activity).get(MainViewModel.class);
        mainViewModel.status.observe(activity, type -> {
            if(currentTab.equals("chat")) {
                baseTitleBar.setDescText(type.getName());
            }
        });
        
        mainViewModel.newFriendReqNum.observe(activity, num -> {
            contactQBadgeView.setBadgeNumber(num);
        });

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

    @Override
    public void onStart(@NonNull LifecycleOwner owner) {
        DefaultLifecycleObserver.super.onStart(owner);
        initQBadgeView(chat, contact);
    }

    private void initQBadgeView(BottomNavigationItemView chat,
                                BottomNavigationItemView contact) {

        chatQBadgeView = new QBadgeView(activity);
        chatQBadgeView.bindTarget(chat)
                .setBadgeGravity(Gravity.END|Gravity.TOP)
                .setGravityOffset(20, 0, true)
                .setBadgeNumber(20)
                .setShowShadow(false)
                .setExactMode(false)
                .setBadgeTextSize(9, true)
                .setOnDragStateChangedListener((dragState, badge, targetView) -> {

                });

        contactQBadgeView = new QBadgeView(activity);
        contactQBadgeView.bindTarget(contact)
                .setBadgeGravity(Gravity.END|Gravity.TOP)
                .setGravityOffset(25, 0, true)
                .setBadgeNumber(0)
                .setShowShadow(false)
                .setExactMode(false)
                .setBadgeTextSize(9, true)
                .setOnDragStateChangedListener((dragState, badge, targetView) -> {

                });
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

        if(navItemId == R.id.tab_menu_chat){
            if(mainViewModel != null) {
                baseTitleBar.setDescText(mainViewModel.status.getValue().getName());
            }else {
                baseTitleBar.setDescText("");
            }
        }

        if(bottomNavView != null) bottomNavView.setSelectedItemId(navItemId);
        if(viewPager != null) viewPager.setCurrentItem(itemIndex);
        if(baseTitleBar != null) baseTitleBar.autoChangeByType(type);
    }

    private void setupViewPager(ViewPager2 viewPager) {
        MessageListFragment messageListFragment = new MessageListFragment();
        contactsFragment = new ContactsFragment(baseTitleBar, mainViewModel);
        PersonalFragment personalFragment = new PersonalFragment();

        Fragment[] fragments = new Fragment[]{
                messageListFragment,
                contactsFragment,
                personalFragment
        };
        FragmentStateAdapter stateAdapter = new FragmentStateAdapter
                ((FragmentActivity) activity) {
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

    @Override
    public void onDestroy(@NonNull LifecycleOwner owner) {
    }
}
