package cn.tim.xchat;

import android.os.Bundle;
import android.view.Gravity;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager2.adapter.FragmentStateAdapter;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import cn.tim.xchat.chat.MessageListFragment;
import cn.tim.xchat.contacts.ContactsFragment;
import cn.tim.xchat.personal.PersonalFragment;
import q.rorbin.badgeview.QBadgeView;

public class MainActivity extends XChatBaseActivity {
    QBadgeView chatQBadgeView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        BottomNavigationView bottomNavView = findViewById(R.id.bottom_navigation);
        BottomNavigationItemView chat = bottomNavView.findViewById(R.id.tab_menu_chat);

        if(chatQBadgeView == null) {
            chatQBadgeView = new QBadgeView(this);
        }

        chatQBadgeView.bindTarget(chat)
                .setBadgeGravity(Gravity.TOP|Gravity.START)
                .setGravityOffset(75, 0, true)
                .setBadgeNumber(20);

        ViewPager2 viewPager = findViewById(R.id.viewpager);
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

        viewPager.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                super.onPageSelected(position);
                bottomNavView.setSelectedItemId(position);
            }
        });
    }


    private void setupViewPager(ViewPager2 viewPager) {
        Fragment[] fragments = new Fragment[]{
                new MessageListFragment(),
                new ContactsFragment(),
                new PersonalFragment()
        };

        FragmentStateAdapter stateAdapter = new FragmentStateAdapter(this) {
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
}