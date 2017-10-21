package com.sinjon.chessdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;

import com.sinjon.chessdemo.R;

/**
 * 主界面
 *
 * @作者 xinrong
 * @创建日期 2017/10/10 18:53
 */
public class MainActivity extends Activity {


    private CheckBox cb_isFirstTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        setContentView(R.layout.activity_main);

        //是否先手
        cb_isFirstTurn = (CheckBox) findViewById(R.id.cb_isfirstturn);

    }


    /**
     * 开始游戏的点击事件
     *
     * @param view
     */
    public void startGame(View view) {
        Intent intent = new Intent(this, ChooseLevelActivity.class);
        //传递是否先手信息
        intent.putExtra("firstTurn", cb_isFirstTurn.isChecked());
        //游戏难度选择界面跳转
        startActivity(intent);
    }


}
