package com.sokoban.core.state;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import com.sokoban.core.logic.Direction;
import com.sokoban.core.map.MapData;
import com.sokoban.utils.DeepClonable;

/**
 * 游戏状态帧
 * <br><br>
 * 用于记录步数与历史记录等
 */
public class GameStateFrame implements DeepClonable<GameStateFrame> {
    public MapData mapData; // 地图数据
    public int stepCount; // 当前步数
    public LocalDateTime frameTime; // 该帧时间戳
    public Direction action; // 到达当前状态进行的移动
    public boolean undo; // 是否为撤回步
    public List<String> moves; // 到达当前状态发生的地图位移信息

    public GameStateFrame() {
        this.mapData = new MapData();
        this.stepCount = 0;
        this.frameTime = LocalDateTime.now();
        this.action = Direction.None;
        this.undo = false;
        this.moves = new ArrayList<>();
    }

    public GameStateFrame deepCopy() {
        GameStateFrame newFrame = new GameStateFrame();
        newFrame.mapData = mapData.deepCopy();
        newFrame.stepCount = stepCount;
        newFrame.frameTime = frameTime; // 不可变类型直接复制
        newFrame.action = action;
        newFrame.undo = undo;
        newFrame.moves = new ArrayList<>(moves);
        return newFrame;
    }
}
