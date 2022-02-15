package cn.tim.xchat.common.widget.loading;

import android.view.View;
import android.view.ViewGroup;

public class LoadingComponent {
    private View loadView;

    public void start(ViewGroup rootView) {
        // loadView
        rootView.addView(loadView);
    }

    public void stop() {

    }
}
