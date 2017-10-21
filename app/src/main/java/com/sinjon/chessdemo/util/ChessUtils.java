package com.sinjon.chessdemo.util;

/**
 * 一子棋的工具类
 */
public class ChessUtils {

    /**
     * 判断所下棋子输赢
     *
     * @param tempChessDatas 棋盘信息
     * @param obj            判断的对象
     * @return 下棋位置
     */
    public static int willWinOrfail(int[] tempChessDatas, int obj) {

        int[] tempDatas = tempChessDatas.clone();
        for (int i = 0; i < tempDatas.length; i++) {

            if (tempDatas[i] == 0) {
                tempDatas[i] = obj;

                if (judgeHorizental(obj, i, tempDatas) || judgeVertical(obj, i, tempDatas)
                        || judgeSlash(obj, i, tempDatas) || judgeBacklash(obj, i, tempDatas)) {
                    return i;

                }

                tempDatas[i] = 0;

            }

        }
        return -1;
    }

    /**
     * 进行估值
     *
     * @return 返回估值结果
     */
    public static int getChessValue(int[] tempChessDatas, int obj) {
        int[] tempDatas = tempChessDatas.clone();

        //判断当前模拟下棋棋盘是否有结果
        if (judgeHorizental(obj, 0, tempDatas) || judgeHorizental(obj, 3, tempDatas) || judgeHorizental(obj, 6, tempDatas) //横向
                || judgeVertical(obj, 0, tempDatas) || judgeVertical(obj, 1, tempDatas) || judgeVertical(obj, 2, tempDatas)//竖向
                || judgeSlash(obj, 4, tempDatas) //正斜向
                || judgeBacklash(obj, 4, tempDatas)) //反斜向
        {

            return Integer.MAX_VALUE;

        }

        //将除机器下的棋子（黑棋）之外的全部置为白棋
        for (int j = 0; j < tempDatas.length; j++) {
            if (tempDatas[j] == 0) {
                tempDatas[j] = obj;
            }
        }

        //进行估值
        return computeValue(tempDatas, obj);

    }


    /**
     * 计算估值
     *
     * @param tempChessDatas 临时棋盘信息
     * @param value          要比较的值 1 / 2
     * @return 返回黑白棋的估值
     */
    public static int computeValue(int[] tempChessDatas, int value) {
        int maxValue = 0;
        if (tempChessDatas[0] == value && tempChessDatas[1] == value && tempChessDatas[2] == value) {
            maxValue++;
        }
        if (tempChessDatas[3] == value && tempChessDatas[4] == value && tempChessDatas[5] == value) {
            maxValue++;
        }
        if (tempChessDatas[6] == value && tempChessDatas[7] == value && tempChessDatas[8] == value) {
            maxValue++;
        }
        if (tempChessDatas[0] == value && tempChessDatas[3] == value && tempChessDatas[6] == value) {
            maxValue++;
        }
        if (tempChessDatas[1] == value && tempChessDatas[4] == value && tempChessDatas[7] == value) {
            maxValue++;
        }
        if (tempChessDatas[2] == value && tempChessDatas[5] == value && tempChessDatas[8] == value) {
            maxValue++;
        }
        if (tempChessDatas[0] == value && tempChessDatas[4] == value && tempChessDatas[8] == value) {
            maxValue++;
        }
        if (tempChessDatas[2] == value && tempChessDatas[4] == value && tempChessDatas[6] == value) {
            maxValue++;
        }
        return maxValue;
    }

    /**
     * 判断反斜向
     *
     * @param nowJudge 当前判断的对象
     * @param position 当前下棋的位置
     * @return 是否胜利
     */
    public static boolean judgeBacklash(int nowJudge, int position, int[] chessDatas) {
        int proPosition;
        int nextPosition;
        if (position == 0) {
            proPosition = (position + 2 + 6) % 9;
            nextPosition = position + 4;
        } else if (position == 8) {
            proPosition = position - 4;
            nextPosition = (position - 2 + 12) % 9;
        } else if (position == 4) {
            proPosition = position - 4;
            nextPosition = position + 4;
        } else {
            return false;
        }

        if (chessDatas[proPosition] == nowJudge && chessDatas[position] == nowJudge && chessDatas[nextPosition] == nowJudge) {
            //一字棋连线成功
            //游戏结束
            return true;
        }
        return false;
    }

    /**
     * 判断正斜向
     *
     * @param nowJudge 当前判断的对象
     * @param position 当前下棋的位置
     * @return 是否胜利
     */
    public static boolean judgeSlash(int nowJudge, int position, int[] chessDatas) {
        int proPosition;
        int nextPosition;
        if (position == 2) {
            proPosition = (position - 2 + 6) % 9;
            nextPosition = position + 2;
        } else if (position == 6) {
            proPosition = position - 2;
            nextPosition = (position + 2 + 12) % 9;
        } else if (position == 4) {
            proPosition = position - 2;
            nextPosition = position + 2;
        } else {
            return false;
        }

        if (chessDatas[proPosition] == nowJudge && chessDatas[position] == nowJudge && chessDatas[nextPosition] == nowJudge) {
            //一字棋连线成功
            //游戏结束
            return true;
        }
        return false;
    }

    /**
     * 判断竖向
     *
     * @param nowJudge 当前判断的对象
     * @param position 当前下棋的位置
     * @return 是否胜利
     */
    public static boolean judgeVertical(int nowJudge, int position, int[] chessDatas) {

        int proPosition;
        int nextPosition;
        if (position == 0 || position == 1 || position == 2) {
            proPosition = (position + 6) % 9;
            nextPosition = position + 3;
        } else if (position == 6 || position == 7 || position == 8) {
            proPosition = position - 3;
            nextPosition = (position + 12) % 9;
        } else {
            proPosition = position - 3;
            nextPosition = position + 3;
        }

        if (chessDatas[proPosition] == nowJudge && chessDatas[position] == nowJudge && chessDatas[nextPosition] == nowJudge) {
            //一字棋连线成功
            //游戏结束
            return true;
        }
        return false;
    }

    /**
     * 判断横向
     *
     * @param nowJudge 当前判断的对象
     * @param position 当前下棋的位置
     * @return 是否胜利
     */
    public static boolean judgeHorizental(int nowJudge, int position, int[] chessDatas) {
        int proPosition;
        int nextPosition;
        if (position == 0 || position == 3 || position == 6) {
            proPosition = (position - 1 + 12) % 9;
            nextPosition = position + 1;
        } else if (position == 2 || position == 5 || position == 8) {
            proPosition = position - 1;
            nextPosition = (position + 1 + 6) % 9;
        } else {
            proPosition = position - 1;
            nextPosition = position + 1;
        }

        if (chessDatas[proPosition] == nowJudge && chessDatas[position] == nowJudge && chessDatas[nextPosition] == nowJudge) {
            //一字棋连线成功
            //游戏结束
            return true;
        }
        return false;
    }

}
