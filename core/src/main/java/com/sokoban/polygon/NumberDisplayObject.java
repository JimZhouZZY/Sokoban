package com.sokoban.polygon;

import com.sokoban.Main;
import com.sokoban.assets.SpineAssets;
import com.sokoban.core.game.Logger;

/**
 * 动画显示数字类
 * @author Life_Checkpoint
 */
public class NumberDisplayObject extends SpineObject {
    private int value;

    public NumberDisplayObject(Main gameMain, int initialValue) {
        super(gameMain, SpineAssets.Numbers);
        setSize(0.2f, 3.6f);
        setValue(initialValue);
    }

    public void setValue(int value) {
        if (value < 0 || value > 9) {
            Logger.error("NumbersDisplayObject", "Can not set a number not in 0~9");
            return;
        }
        this.value = value;
        setAnimation(0, new StringBuilder().append(value).toString(), false);
    }

    public int getValue() {
        return value;
    }
}
