package cn.tim.xchat;

import android.os.Bundle;
import android.view.Gravity;
import android.view.View;


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
//    // Used to load the 'native-lib' library on application startup.
//    static {
//        System.loadLibrary("native-lib");
//    }

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

        ViewPager2 viewPager2 = findViewById(R.id.viewpager);
        bottomNavView.setOnItemSelectedListener(item -> {
            switch (item.getItemId()) {
                case R.id.tab_menu_chat:
                    viewPager2.setCurrentItem(0);
                    break;
                case R.id.tab_menu_contact:
                    viewPager2.setCurrentItem(1);
                    break;
                case R.id.tab_menu_personal:
                    viewPager2.setCurrentItem(2);

                    break;
                default:
                    break;
            }
            return true;
        });
    }


    private Fragment[] setupViewPager(ViewPager2 viewPager) {
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
        return fragments;
    }
}