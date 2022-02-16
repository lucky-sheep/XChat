package cn.tim.xchat.login.adapter;

import android.text.Editable;
import android.text.TextWatcher;

public abstract class MyEditTextWatcher implements TextWatcher {
    @Override
    public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // do nothing
    }
    @Override
    public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        // do nothing
    }

    @Override
    public void afterTextChanged(Editable editable) {
        //do nothing
    }
}
