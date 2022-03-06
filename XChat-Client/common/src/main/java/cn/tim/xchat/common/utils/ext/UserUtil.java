package cn.tim.xchat.common.utils.ext;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.mmkv.MMKV;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.module.FriendInfo;
import cn.tim.xchat.common.module.UserInfo;

public class UserUtil {
    private static final String TAG = "UserUtil";
    static MMKV mmkv = MMKV.defaultMMKV();
    public static UserInfo get(){
        UserInfo userInfo = new UserInfo();
        userInfo.setUsername(mmkv.getString(StorageKey.USERNAME_KEY, ""));
        userInfo.setId(mmkv.getString(StorageKey.USERID_KEY, ""));
        userInfo.setEmail(mmkv.getString(StorageKey.EMAIL_KEY, ""));
        userInfo.setPassword(mmkv.getString(StorageKey.PASSWORD_KEY, ""));
        userInfo.setClientId(mmkv.getString(StorageKey.DEVICE_ID_KEY, ""));
        return userInfo;
    }


    public static void clear(){
        mmkv.remove(StorageKey.USERNAME_KEY);
        mmkv.remove(StorageKey.USERID_KEY);
        mmkv.remove(StorageKey.EMAIL_KEY);
        mmkv.remove(StorageKey.PASSWORD_KEY);
//        mmkv.remove(StorageKey.DEVICE_ID_KEY);
        mmkv.remove(StorageKey.TOKEN_KEY);
    }


    public static String getUserShowName(FriendInfo friendInfo) {
        String retName = "";

        String notes = friendInfo.getNotes();
        String nickname = friendInfo.getNickname();
        String username = friendInfo.getUsername();

        retName = TextUtils.isEmpty(notes) ? ( TextUtils.isEmpty(nickname) ?
                username: nickname) : notes;
        return retName;
    }

    public static String getUserShowNameFirstChar(FriendInfo friendInfo) {
        String name = getUserShowName(friendInfo);
        if(TextUtils.isEmpty(name)) {
            Log.e(TAG, "getUserShowNameFirstChar: null");
            return "X";
        }else {
            return name.substring(0, 1);
        }
    }
}
