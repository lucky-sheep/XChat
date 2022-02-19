package cn.tim.xchat.common.utils.ext;
import com.tencent.mmkv.MMKV;
import cn.tim.xchat.common.constans.StorageKey;
import cn.tim.xchat.common.module.UserInfo;

public class UserUtil {
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
}
