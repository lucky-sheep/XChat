package cn.tim.xchat.common.widget.toast;

import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import cn.tim.xchat.common.R;
import cn.tim.xchat.common.utils.DensityUtil;

public enum XChatToast {
    INSTANCE;
    private Toast mToast;
    private TextView mTvToast;

    public void showToast(Context ctx, String content) {
        showToast(ctx, content, Gravity.BOTTOM, 120);
    }

    public void showToast(Context ctx, String content, int gravity, int yOffset) {
//        if(mToast != null) mToast.cancel();
        if (mToast == null) {
            mToast = new Toast(ctx);
            mToast.setGravity(gravity, 0, DensityUtil.dp2px(yOffset));
            mToast.setDuration(Toast.LENGTH_SHORT);
            View _root = LayoutInflater.from(ctx).inflate(R.layout.layout_xchat_toast, null);
            mTvToast = _root.findViewById(R.id.xchat_toast_tv);
            mToast.setView(_root);
        }
        mTvToast.setText(content);
        mToast.show();
    }


    public void cancelToast() {
        if (mToast != null) {
            mToast.cancel();
            mToast = null;
        }
    }
}
