package com.sinjon.chessdemo.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.sinjon.chessdemo.R;

import static com.sinjon.chessdemo.util.ChessUtils.getChessValue;
import static com.sinjon.chessdemo.util.ChessUtils.judgeBacklash;
import static com.sinjon.chessdemo.util.ChessUtils.judgeHorizental;
import static com.sinjon.chessdemo.util.ChessUtils.judgeSlash;
import static com.sinjon.chessdemo.util.ChessUtils.judgeVertical;
import static com.sinjon.chessdemo.util.ChessUtils.willWinOrfail;

/**
 * 游戏界面
 *
 * @作者 xinrong
 * @创建日期 2017/10/10 18:53
 */
public class GameActivity extends Activity {

    private static final int UPDATE = 1;
    private static final int GAMEOVER = 2;
    private static final int GAMEDRAW = 3;//平局

    private GridView gv_chessBoard;
    private MyAdapter adapter;
    private boolean isOver = true;//游戏结束标志
    private boolean isTurn;//轮流下棋标志
    private int level;//难度等级

    private boolean normalLevel = false;
    private boolean hardLevel = false;

    private int[] chessDatas; //存放棋盘棋子信息 白1黑2 机器执黑人执白
    private int[] corner = {0, 2, 6, 8};
    private int[] middle = {1, 3, 5, 7};
    private AdapterView.OnItemClickListener listener; //监听器

    /**
     * 线程UI处理
     */
    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case UPDATE:
                    adapter.notifyDataSetChanged();

                    break;

                case GAMEOVER: {
                    //用来判断是人还是机器的标志 1人 2机器
                    int nowJudge = (int) msg.obj;
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);
                    if (nowJudge == 1) {
                        builder.setTitle("游戏结果").setMessage("               恭喜您取得胜利！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    } else {
                        builder.setTitle("游戏结果").setMessage("               胜败乃兵家常事\n               请骚年从头再来！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Intent intent = new Intent(GameActivity.this, MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        });
                    }
                    builder.show();
                    isTurn = false;
                    break;
                }
                case GAMEDRAW: {
                    AlertDialog.Builder builder = new AlertDialog.Builder(GameActivity.this);

                    builder.setTitle("游戏结果").setMessage("                          平局！").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(GameActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

                    builder.show();

                    isTurn = false;

                    break;
                }
                default:
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();//初始化视图
        initData();//初始化数据
        initEvent();//初始化事件


    }

    /**
     * 初始化数据
     */
    private void initData() {
        //存放棋盘棋子信息 白1黑2 机器执黑人执白
        chessDatas = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0};

        //适配器
        adapter = new MyAdapter();
        gv_chessBoard.setAdapter(adapter);

        //谁先手的判断
        isTurn = getIntent().getExtras().getBoolean("firstTurn");
        //玩家所选难度判断 0/1/2 低/中/高
        level = getIntent().getExtras().getInt("level");
        if (level == 1) {
            normalLevel = true;

        } else if (level == 2) {
            normalLevel = true;
            hardLevel = true;
        }

        //游戏线程
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开始游戏 isOver状态设置为false
                isOver = false;
                while (!isOver) {//游戏未结束

                    if (!isTurn) {
                        //机器的应对棋
                        robotPlayingChess();

                        SystemClock.sleep(500);
                    }

                }

            }
        }).start();

    }

    /**
     * 判断游戏结果
     *
     * @param isTurn   用来判断轮到是人还是机器下棋
     * @param position 判断
     */
    private boolean isGameOver(boolean isTurn, int position) {
        int nowJudge;//结果判断的对象

        if (isTurn) {//判断人是否胜利 人执白棋 1
            nowJudge = 1;
        } else {//判断机器是否胜利  机器执黑棋 2
            nowJudge = 2;
        }
        //position位置判断1357不需要做斜线判断
        if (position == 1 || position == 3 || position == 5 || position == 7) {
            if (judgeHorizental(nowJudge, position, chessDatas) || judgeVertical(nowJudge, position, chessDatas)) {
                Message msg = handler.obtainMessage(GAMEOVER);
                msg.obj = nowJudge;
                handler.sendMessage(msg);
                isOver = true;
                return true;
            }


        } else {
            if (judgeHorizental(nowJudge, position, chessDatas) ||
                    judgeVertical(nowJudge, position, chessDatas) ||
                    judgeSlash(nowJudge, position, chessDatas) ||
                    judgeBacklash(nowJudge, position, chessDatas)) {
                Message msg = handler.obtainMessage(GAMEOVER);
                msg.obj = nowJudge;
                handler.sendMessage(msg);
                isOver = true;
                return true;

            }
        }

        //和棋判断
        if (isPlayDraw(chessDatas)) {
            isOver = true;
            handler.obtainMessage(GAMEDRAW).sendToTarget();//通知和棋
            return true;
        } else {
            return false;
        }

    }

    /**
     * 判断是否和棋
     *
     * @return 是否和棋
     */
    private boolean isPlayDraw(int[] chessDatas) {
        //判断是否和棋
        int count = 0;
        for (int chessData : chessDatas) {
            if (chessData == 0) {
                count++;
            }
        }
        return count == 0;

    }

    /**
     * 获得下棋位置
     */
    private int getChessPlayingPosition(int[] tempChessDatas, int obj) {

        int position = -1;//下棋位置
        int result1 = Integer.MIN_VALUE;//估值的结果

        //进行赢棋判断
        if (willWinOrfail(tempChessDatas, obj) != -1) {
            position = willWinOrfail(tempChessDatas, obj);
            return position;
        }

        //进行防守判断
        if (willWinOrfail(tempChessDatas, 3 - obj) != -1) {
            position = willWinOrfail(tempChessDatas, 3 - obj);
            return position;
        }


        //搜索算法
        //模拟估值
        for (int i = 0; i < tempChessDatas.length; i++) {

            if (tempChessDatas[i] == 0) {
                tempChessDatas[i] = obj;

                for (int j = 0; j < tempChessDatas.length; j++) {

                    if (tempChessDatas[j] == 0) {

                        tempChessDatas[j] = 3 - obj;

                        //黑白棋估值
                        int blackChessValue = getChessValue(tempChessDatas, 2);
                        int whiteChessValue = getChessValue(tempChessDatas, 1);
                        int chessValue;//f(n)
                        //结果比较
                        if (obj == 1) {
                            chessValue = whiteChessValue - blackChessValue;
                        } else {
                            chessValue = blackChessValue - whiteChessValue;
                        }
                        if (result1 < chessValue) {
                            result1 = chessValue;
                            position = i;
                        }

                        tempChessDatas[j] = 0;
                    }

                }//for j end

                if (position == -1) {
                    //只剩一个空位 不用比较
                    position = i;
                }

                tempChessDatas[i] = 0;
            }

        }//for i end

        //高级难度下增加的代码
        if (hardLevel && obj == 2) {
            //只进来一次
            hardLevel = false;
            //重置
            tempChessDatas = chessDatas.clone();
            //加强攻击
            if (position == 0 || position == 2 || position == 6 || position == 8) {
                for (int tempPosition : corner) {
                    if (chessDatas[tempPosition] == 0) {


                        //根据数组多次尝试,找出最佳点
                        appointSimulateLoop(tempChessDatas, tempPosition);


                        if (simulateLoop(tempChessDatas) == 1) {
                            position = tempPosition;

                        }
                        if (simulateLoop(tempChessDatas) == 1) {
                            position = tempPosition;
                            break;
                        }
                        //重置
                        tempChessDatas = chessDatas.clone();
                    }

                }//for  end

            }
            hardLevel = true;

        }


        return position;
    }


    /**
     * 轮到机器下棋
     * 使用人工智能搜索算法
     */
    private void robotPlayingChess() {

        //人工智能极小极大搜索算法
        // 假定所走位置并用静态函数f(p)估值

        int[] tempChessDatas = chessDatas.clone();//克隆该数组
        int position = -1;
        position = getChessPlayingPosition(tempChessDatas, 2);
        //模拟下棋结果 在此可分难度等级
        //不进行模拟为 为初级难度
        //进行两次模拟增加防御 为中级难度
        //进行两次模拟增加攻击防御 为高级难度

        //重置
        tempChessDatas = chessDatas.clone();
        //中高级难度下增加的代码
        if (normalLevel) {
            //加强防御
            if (simulatePlayingChessResult(tempChessDatas) == 0) {
                //cornor[]走不通就走middle[]，反之亦然
                if (position == 0 || position == 2 || position == 6 || position == 8) {
                    for (int i : middle) {
                        if (chessDatas[i] == 0) {
                            position = i;
                            break;
                        }
                    }
                } else {
                    for (int i : corner) {
                        if (chessDatas[i] == 0) {
                            position = i;
                            break;
                        }
                    }
                }

            }

            //重置
            tempChessDatas = chessDatas.clone();
        }
        //防守
        if (willWinOrfail(tempChessDatas, 1) != -1) {
            position = willWinOrfail(tempChessDatas, 1);
        }
        //能否赢棋
        if (willWinOrfail(tempChessDatas, 2) != -1) {
            position = willWinOrfail(tempChessDatas, 2);
        }

        //下棋位置
        chessDatas[position] = 2;

        //更新界面
        handler.obtainMessage(UPDATE).sendToTarget();

        //判断机器是否胜利
        if (isGameOver(isTurn, position)) {

        } else {
            isTurn = !isTurn;
        }
    }

    /**
     * 模拟下棋结果
     */
    private int simulatePlayingChessResult(int[] tempChessDatas) {

        //机器来模拟对局 中级难度
        if (simulateLoop(tempChessDatas) == 0) {
            return 0;
        }

        if (simulateLoop(tempChessDatas) == 0) {
            return 0;
        }


        return -1;

    }

    /**
     * 指定位置的循环模拟
     *
     * @param tempChessDatas 棋盘信息
     * @return 判断是否可以在所选位置下棋
     */
    private int appointSimulateLoop(int[] tempChessDatas, int location) {
        int blackPosition = -1;
        int whitePosition = -1;
        //获得黑棋下棋位置
        blackPosition = getChessPlayingPosition(tempChessDatas, 2);
        //如果没有空位
        if (blackPosition == -1) {
            return -1;

        }
        tempChessDatas[location] = 2;

        int blackChessValue = getChessValue(tempChessDatas, 2);
        if (blackChessValue == Integer.MAX_VALUE) {
            //战胜
            return 1;
        }
        //判断是否和棋
        if (isPlayDraw(tempChessDatas)) {
            return -1;
        }

        //获得白棋下棋位置
        whitePosition = getChessPlayingPosition(tempChessDatas, 1);
        tempChessDatas[whitePosition] = 1;
        //判断模拟对局中机器是否能战胜

        return -1;
    }


    /**
     * 循环模拟
     *
     * @param tempChessDatas 棋盘信息
     * @return 判断是否可以在所选位置下棋
     */
    private int simulateLoop(int[] tempChessDatas) {
        int blackPosition = -1;
        int whitePosition = -1;
        //获得黑棋下棋位置
        blackPosition = getChessPlayingPosition(tempChessDatas, 2);
        //如果没有空位
        if (blackPosition == -1) {
            return -1;

        }
        tempChessDatas[blackPosition] = 2;
        int blackChessValue = getChessValue(tempChessDatas, 2);
        if (blackChessValue == Integer.MAX_VALUE) {
            //战胜
            return 1;
        }

        //判断是否和棋
        if (isPlayDraw(tempChessDatas)) {
            return -1;
        }

        //获得白棋下棋位置
        whitePosition = getChessPlayingPosition(tempChessDatas, 1);
        tempChessDatas[whitePosition] = 1;
        //判断模拟对局中机器是否会战败
        int whiteChessValue = getChessValue(tempChessDatas, 1);


        if (whiteChessValue == Integer.MAX_VALUE) {
            //战败
            return 0;
        }

        return -1;
    }

    /**
     * 销毁
     */
    @Override
    protected void onDestroy() {
        isOver = true;
        super.onDestroy();
    }

    /**
     * 初始化事件
     */
    private void initEvent() {
        //动态设置棋盘的点击监听器
        listener = new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ImageView iv_chess = (ImageView) view.findViewById(R.id.iv_chess);
                if (isTurn) {
                    if (chessDatas[position] == 0) {
                        iv_chess.setImageResource(R.drawable.stone_white);

                        chessDatas[position] = 1;
                        //判断人是否胜利
                        if (isGameOver(isTurn, position)) {

                        } else {
                            isTurn = !isTurn;
                        }
                    }
                }
            }
        };
        //棋盘添加监听器
        gv_chessBoard.setOnItemClickListener(listener);
    }


    /**
     * 初始化视图
     */
    private void initView() {
        setContentView(R.layout.activity_game);
        //棋盘GridView
        gv_chessBoard = (GridView) findViewById(R.id.gv_chessboard);

    }

    @Override
    public void onBackPressed() {
        isOver = true;
        super.onBackPressed();
    }

    /**
     * 棋盘的适配器
     */
    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return gv_chessBoard.getNumColumns() * gv_chessBoard.getNumColumns();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = View.inflate(getApplicationContext(), R.layout.item_gridview, null);
            }

            ImageView iv_chess = (ImageView) convertView.findViewById(R.id.iv_chess);
            iv_chess.setMinimumWidth(getWindowManager().getDefaultDisplay().getWidth() / 3);
            iv_chess.setMinimumHeight(getWindowManager().getDefaultDisplay().getWidth() / 3);
            iv_chess.setScaleType(ImageView.ScaleType.FIT_XY);


            iv_chess.setPadding(0, 0, 0, 0);


            if (chessDatas[position] == 1) {
                iv_chess.setImageResource(R.drawable.stone_white);

            } else if (chessDatas[position] == 2) {
                iv_chess.setImageResource(R.drawable.stone_black);

            }
            return convertView;
        }
    }
}
