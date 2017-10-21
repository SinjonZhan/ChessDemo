package com.sinjon.chessdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.sinjon.chessdemo.R;


public class ChooseLevelActivity extends Activity {
    Boolean firstTurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();

    }

    private void initView() {
        setContentView(R.layout.activity_chooselevel);
        //获得是否先手选择
        firstTurn = getIntent().getExtras().getBoolean("firstTurn");
        Log.d("firstT", firstTurn + "");
    }

    /**
     * 选择中级难度
     *
     * @param view
     */
    public void onClickNormalLevel(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        //传递是否先手信息
        intent.putExtra("firstTurn", firstTurn);
        intent.putExtra("level", 1);

        //开始游戏界面跳转
        startActivity(intent);
        finish();
    }

    /**
     * 选择初级难度
     *
     * @param view
     */
    public void onClickEasyLevel(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        //传递是否先手信息
        intent.putExtra("firstTurn", firstTurn);
        intent.putExtra("level", 0);

        //开始游戏界面跳转
        startActivity(intent);
        finish();

    }

    /**
     * 选择高级难度
     *
     * @param view
     */
    public void onClickHardLevel(View view) {
        Intent intent = new Intent(this, GameActivity.class);
        //传递是否先手信息
        intent.putExtra("firstTurn", firstTurn);
        intent.putExtra("level", 2);
        //开始游戏界面跳转
        startActivity(intent);
        finish();

    }
}
