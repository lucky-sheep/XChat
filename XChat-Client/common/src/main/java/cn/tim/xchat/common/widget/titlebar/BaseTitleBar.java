package cn.tim.xchat.common.widget.titlebar;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.CallSuper;

import cn.tim.xchat.common.R;

public class BaseTitleBar extends RelativeLayout implements TitleBarFun {

    protected ImageView backBtn;
    protected TextView descTv;
    protected ImageView findBtn;
    protected ImageView addBtn;
    protected ImageView menuBtn;
    protected RelativeLayout titleBarView;
    protected LinearLayout rightAll;

    public BaseTitleBar(Context context) {
        this(context, null);
    }

    public BaseTitleBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public BaseTitleBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        titleBarView = (RelativeLayout) View.inflate(context, R.layout.layout_base_titlebar, null);
        findView();
        addView(titleBarView);
    }

    public BaseTitleBar(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr);
    }

    @CallSuper
    @Override
    public void autoChangeByType(TitleBarType type) {
        reset();
        switch (type) {
            case MESSAGE_MAIN_PAGER:
                descTv.setText("会话列表");
                backBtn.setVisibility(GONE);
                menuBtn.setVisibility(GONE);
                addBtn.setVisibility(GONE);
                break;
            case CONTACTS_MAIN_PAGER:
                descTv.setText("通讯录");
                backBtn.setVisibility(GONE);
                break;
            case PERSONAL_MAIN_PAGER:
                titleBarView.setVisibility(INVISIBLE);
                break;
            case PERSONAL_DETAIL_PAGER:
                rightAll.setVisibility(INVISIBLE);
                break;
        }
    }

    private void reset(){
        titleBarView.setVisibility(VISIBLE);
        backBtn.setVisibility(VISIBLE);
        descTv.setVisibility(VISIBLE);
        findBtn.setVisibility(VISIBLE);
        addBtn.setVisibility(VISIBLE);
        menuBtn.setVisibility(VISIBLE);
    }

    @CallSuper
    @Override
    public void findView() {
        backBtn = titleBarView.findViewById(R.id.common_base_titlebar_back_btn_icon);
        descTv = titleBarView.findViewById(R.id.common_base_titlebar_back_btn_text);
        findBtn = titleBarView.findViewById(R.id.common_base_titlebar_find_btn);
        addBtn = titleBarView.findViewById(R.id.common_base_titlebar_add_btn);
        menuBtn = titleBarView.findViewById(R.id.common_base_titlebar_menu_btn);
        rightAll = titleBarView.findViewById(R.id.common_base_titlebar_right_all);
    }
}
