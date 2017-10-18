package cn.edu.gdmec.android.mobileguard.m2theftguard;

import android.os.Bundle;
import android.widget.RadioButton;

import cn.edu.gdmec.android.mobileguard.R;

/**
 * Created by 周国钦 on 2017/10/17.
 */

public class Setup3Activity extends BaseSetUpActivity {
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup_3);
        ((RadioButton)findViewById(R.id.rb_third)).setChecked(true);

    }
    public void showNext(){
        startActivityAndFinishSelf(Setup4Activity.class);
    }
    public void showPre(){
        startActivityAndFinishSelf(Setup2Activity.class);
    }
}
