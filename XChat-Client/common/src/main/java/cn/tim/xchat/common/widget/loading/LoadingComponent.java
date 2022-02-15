package cn.tim.xchat.common.widget.loading;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import com.airbnb.lottie.LottieAnimationView;

import cn.tim.xchat.common.R;

public class LoadingComponent {
    private View loadView;
    private ViewGroup rootView;
    private Context context;
    private final LottieAnimationView loadLottieView;

    public LoadingComponent(Context context, ViewGroup rootView){
        this.context = context;
        loadView = View.inflate(context, R.layout.layout_loading_ae, null);
        loadLottieView = loadView.findViewById(R.id.common_loading_ea_lottie_view);
        this.rootView = rootView;
    }

    public void start() {
        FrameLayout.LayoutParams layoutParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        rootView.addView(loadView, layoutParams);
        if(loadLottieView != null) {
            loadLottieView.playAnimation();
        }
    }

    public void stop() {
        if(loadLottieView != null) {
            loadLottieView.cancelAnimation();
        }

        if(loadView.getParent() != null) {
            rootView.removeView(loadView);
        }
    }
}
