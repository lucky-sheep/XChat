package cn.tim.xchat.common.widget.titlebar;

public interface TitleBarFun {
    /**
     * 根据类型自动处理UI
     */
    void autoChangeByType(TitleBarType type);

    /**
     * findView
     */
    void findView();
}
