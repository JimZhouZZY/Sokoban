package com.sokoban.scenes;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.scenes.scene2d.InputEvent;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.scenes.scene2d.ui.ImageButton;
import com.badlogic.gdx.scenes.scene2d.utils.ClickListener;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.sokoban.Main;
import com.sokoban.manager.BackgroundGrayParticleManager;
import com.sokoban.manager.MouseMovingTraceManager;
import com.sokoban.polygon.ImageButtonContainer;
import com.sokoban.polygon.ImageLabelContainer;

public class AboutScene extends ApplicationAdapter implements Screen {
    private Main gameMain;
    private FitViewport viewport;
    private Stage stage;
    private boolean initFlag = false;

    // 画面相机跟踪
    private MouseMovingTraceManager moveTrace;

    // Background 粒子
    private BackgroundGrayParticleManager bgParticle;

    // UI
    private ImageButtonContainer buttonContainer;
    private ImageLabelContainer labelContainer;
    private ImageButton returnButton;
    private Image infoLabel;

    private int clickLabelCount = 0;

    public AboutScene(Main gameMain) {
        this.gameMain = gameMain;
    }

    public Main getGameMain() {
        return gameMain;
    }

    @Override
    public void show() {
        if (!initFlag) init();
        Gdx.input.setInputProcessor(stage);
    }

    public void init() {
        viewport = new FitViewport(16, 9);
        moveTrace = new MouseMovingTraceManager(viewport);

        // UI Stage
        stage = new Stage(viewport);
        buttonContainer = new ImageButtonContainer(0.3f);
        labelContainer = new ImageLabelContainer(0.3f);

        // 初始化按钮
        returnButton = buttonContainer.createButton("img/left_arrow.png");
        returnButton.setPosition(0.5f, 8f);

        // 信息 label
        infoLabel = labelContainer.createLabel("img/about_info.png", 3f);
        infoLabel.setPosition(6f, 4.5f - infoLabel.getHeight() / 2);

        // 返回按钮监听
        returnButton.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                System.out.println("Return!");
                gameMain.getScreenManager().returnPreviousScreen();
            }
        });

        // label 点击彩蛋
        infoLabel.addListener(new ClickListener() {
            @Override
            public void clicked(InputEvent event, float x, float y) {
                clickLabelCount += 1;
                if (clickLabelCount >= 10) {
                    System.out.println("Colorful eggs");
                    labelContainer.resetLabel(infoLabel, "img/about_info2.png", 3f);
                }
            }
        });

        bgParticle = new BackgroundGrayParticleManager(stage);
        bgParticle.startCreateParticles();

        // 添加 UI
        stage.addActor(returnButton);
        stage.addActor(infoLabel);

        initFlag = true;
    }

    // 输入事件处理
    private void input() {}

    // 重绘逻辑
    private void draw() {
        // 相机跟踪
        moveTrace.setPositionWithUpdate();

        // stage 更新
        stage.act(Gdx.graphics.getDeltaTime());
        stage.draw();
    }

    // 主渲染帧
    @Override
    public void render(float delta) {
        input();
        draw();
    }

    @Override
    public void hide() {}

    // 资源释放
    @Override
    public void dispose() {
        // 释放 stage
        if (stage != null) stage.dispose();
    }
}