package com.sokoban.polygon;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.Batch;
import com.badlogic.gdx.graphics.g2d.PolygonSpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.utils.Disposable;
import com.esotericsoftware.spine.*;
import com.esotericsoftware.spine.AnimationState.TrackEntry;
import com.sokoban.Main;
import com.sokoban.manager.APManager;

/**
 * Spine 对象类，支持移动缩放与动画切换、错误处理、性能优化、功能扩展
 * <br><br>
 * 这真得谢谢 Claude 了
 * @author Claude
 */
public class SpineObject extends Actor implements Disposable {
    // 资源相关
    protected TextureAtlas atlas;
    protected SkeletonData skeletonData;
    protected AnimationStateData animationData;
    protected PolygonSpriteBatch batch;
    protected SkeletonRenderer skeletonRenderer;
    
    // 状态相关
    protected Skeleton skeleton;
    protected AnimationState animationState;
    protected boolean isPaused = false;
    protected boolean isHide = false;
    
    // 原始尺寸
    protected float originalWidth;
    protected float originalHeight;
    
    // 变换相关
    protected float scaleX = 1f;
    protected float scaleY = 1f;
    protected boolean flipX = false;
    protected boolean flipY = false;
    
    // 动画混合时间（秒）
    protected float defaultMixTime = 0.2f;
    
    // 调试模式
    protected boolean debugBones = false;

    // 仅在子类可见的无参构造
    protected SpineObject() {}

    /**
     * 构造函数
     * @throws IllegalArgumentException 如果资源加载失败
     */
    public SpineObject(Main gameMain, APManager.SpineAtlasAssets atlasEnum) {
        // 加载纹理图集
        atlas = gameMain.getAssetsPathManager().get(atlasEnum);
        if (atlas == null) {
            throw new IllegalArgumentException("Failed to load atlas: " + atlasEnum.getAliasAtlas());
        }
        
        // 创建Skeleton数据
        SkeletonJson json = new SkeletonJson(atlas);
        json.setScale(1f);
        
        skeletonData = json.readSkeletonData(gameMain.getAssetsPathManager().fileObj(atlasEnum.getAliasJson()));
        if (skeletonData == null) {
            throw new IllegalArgumentException("Failed to load skeleton data: " + atlasEnum.getAliasJson());
        }
        
        // 初始化动画状态数据
        animationData = new AnimationStateData(skeletonData);
        animationData.setDefaultMix(defaultMixTime);
        
        // 创建渲染器
        batch = new PolygonSpriteBatch();
        skeletonRenderer = new SkeletonRenderer();
        skeletonRenderer.setPremultipliedAlpha(true);
        
        // 创建骨骼和动画状态
        skeleton = new Skeleton(skeletonData);
        animationState = new AnimationState(animationData);
        
        // 缓存原始尺寸
        originalWidth = skeletonData.getWidth();
        originalHeight = skeletonData.getHeight();
        
        // 设置初始大小和位置
        setSize(originalWidth, originalHeight);
        setPosition(0f, 0f);
    }

    /**
     * 设置位置
     * 重写以同步骨骼位置
     */
    @Override
    public void setPosition(float x, float y) {
        super.setPosition(x, y);
        if (skeleton != null) {
            skeleton.setPosition(x, y);
            skeleton.updateWorldTransform();
        }
    }

    /**
     * 设置缩放
     * 重写以同步骨骼缩放
     */
    @Override
    public void setScale(float scaleX, float scaleY) {
        this.scaleX = scaleX;
        this.scaleY = scaleY;
        if (skeleton != null) {
            skeleton.getRootBone().setScale(
                flipX ? -scaleX : scaleX,
                flipY ? -scaleY : scaleY
            );
            skeleton.updateWorldTransform();
        }
    }

    /**
     * 设置尺寸
     * 自动计算并应用缩放
     */
    @Override
    public void setSize(float width, float height) {
        super.setSize(width, height);
        if (originalWidth != 0 && originalHeight != 0) {
            setScale(width / originalWidth, height / originalHeight);
        }
    }

    /**
     * 设置水平翻转
     */
    public void setFlipX(boolean flip) {
        if (this.flipX != flip) {
            this.flipX = flip;
            setScale(scaleX, scaleY); // 重新应用缩放以更新翻转状态
        }
    }

    /**
     * 设置垂直翻转
     */
    public void setFlipY(boolean flip) {
        if (this.flipY != flip) {
            this.flipY = flip;
            setScale(scaleX, scaleY);
        }
    }

    /**
     * 设置动画混合时间
     */
    public void setDefaultMixTime(float mixTime) {
        this.defaultMixTime = mixTime;
        if (animationData != null) {
            animationData.setDefaultMix(mixTime);
        }
    }

    /**
     * 设置特定动画之间的混合时间
     */
    public void setMixTime(String fromAnimation, String toAnimation, float mixTime) {
        if (animationData != null) {
            animationData.setMix(fromAnimation, toAnimation, mixTime);
        }
    }

    /**
     * 设置动画
     * @param trackIndex 动画轨道
     * @param animationName 动画名称
     * @param loop 是否循环
     * @return 返回设置的动画，如果失败返回null
     */
    public TrackEntry setAnimation(int trackIndex, String animationName, boolean loop) {
        try {
            return animationState.setAnimation(trackIndex, animationName, loop);
        } catch (Exception e) {
            Gdx.app.error("SpineObject", "Failed to set animation: " + animationName + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 将动画保持停留在首帧
     * @param animationName 动画名称
     */
    public void stayAnimationAtFirst(String animationName) {
        animationState.setAnimation(0, animationName, false); // 设置非循环动画
        animationState.update(0); // 设置时间为第一帧
        animationState.apply(skeleton); // 应用到骨骼上
    }

    /**
     * 将动画保持停留在尾帧
     * @param animationName 动画名称
     */
    public void stayAnimationAtLast(String animationName) {
        animationState.setAnimation(0, animationName, false); // 设置非循环动画
        animationState.update(animationState.getCurrent(0).getAnimation().getDuration()); // 设置时间为最后一帧
        animationState.apply(skeleton); // 应用到骨骼上
    }

    /**
     * 将动画保持停留在指定位置
     * <br><br>
     * 调用 releaseAnimationPause 取消动画暂停
     * @param animationName 动画名称
     * @param time 介于 0~1 的时间刻系数
     */
    public void stayAnimationAtTime(String animationName, float time) {
        isPaused = true;
        TrackEntry entry = animationState.setAnimation(0, animationName, false);
        float animationDuration = entry.getAnimation().getDuration();
        float targetTime = animationDuration * time;
        entry.setTrackTime(targetTime);
        animationState.apply(skeleton);
    }

    /**
     * 释放动画暂停状态
     */
    public void releaseAnimationPause() {
        isPaused = false;
    }

    /**
     * 隐藏 Spine
     */
    public void hide() {
        isHide = true;
    }

    /**
     * 显示 Spine
     */
    public void show() {
        isHide = false;
    }

    /**
     * 添加动画到队列
     * @param trackIndex 动画轨道
     * @param animationName 动画名
     * @param loop 是否循环
     * @param delay 延迟时间
     */
    public TrackEntry addAnimation(int trackIndex, String animationName, boolean loop, float delay) {
        try {
            return animationState.addAnimation(trackIndex, animationName, loop, delay);
        } catch (Exception e) {
            Gdx.app.error("SpineObject", "Failed to add animation: " + animationName + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 设置调试绘制模式
     */
    public void setDebugMode(boolean bones) {
        this.debugBones = bones;
    }

    /**
     * 更新动画状态
     */
    @Override
    public void act(float delta) {
        super.act(delta);
        if (animationState != null && !isPaused) {
            animationState.update(delta);
            animationState.apply(skeleton);
            skeleton.updateWorldTransform();
        }
    }

    /**
     * 渲染骨骼动画
     */
    @Override
    public void draw(Batch parentBatch, float parentAlpha) {
        if (skeleton == null || batch == null) return;
        if (isHide) return;
        
        float worldX = getX();
        float worldY = getY();
        
        // 暂停父级batch的绘制
        parentBatch.end();
        
        try {
            // 使用PolygonSpriteBatch绘制
            batch.begin();
            batch.setProjectionMatrix(parentBatch.getProjectionMatrix());
            batch.setTransformMatrix(parentBatch.getTransformMatrix());
            
            // 更新骨骼变换
            skeleton.setPosition(worldX, worldY);
            skeleton.getRootBone().setScale(
                flipX ? -scaleX : scaleX,
                flipY ? -scaleY : scaleY
            );
            skeleton.updateWorldTransform();
            
            // 正常绘制骨骼
            skeleton.getColor().a = parentAlpha * getColor().a;  // 设置骨骼的透明度
            skeletonRenderer.draw(batch, skeleton);
            
            // 调试绘制（未测试）
            if (debugBones) {
                if (debugBones) {
                    // 如果是4.0以下版本使用 debugRenderer.draw(batch, skeleton);
                    skeletonRenderer.setVertexEffect(null);
                    // 绘制骨骼线条
                    ShapeRenderer shapes = new ShapeRenderer();
                    shapes.setProjectionMatrix(batch.getProjectionMatrix());
                    shapes.begin(ShapeRenderer.ShapeType.Line);
                    for (Bone bone : skeleton.getBones()) {
                        float length = bone.getData().getLength();
                        float x = bone.getWorldX();
                        float y = bone.getWorldY();
                        float rotation = bone.getWorldRotationX();
                        float endX = x + length * MathUtils.cosDeg(rotation);
                        float endY = y + length * MathUtils.sinDeg(rotation);
                        shapes.setColor(Color.RED);
                        shapes.line(x, y, endX, endY);
                    }
                    shapes.end();
                }   
            }
            
        } catch (Exception e) {
            Gdx.app.error("SpineObject", "Failed to draw skeleton: " + e.getMessage());
        } finally {
            batch.end();
            // 恢复父级batch的绘制
            parentBatch.begin();
        }
    }

    /**
     * 获取当前动画状态
     */
    public TrackEntry getCurrentAnimation(int trackIndex) {
        return animationState.getCurrent(trackIndex);
    }

    /**
     * 清理资源
     */
    @Override
    public void dispose() {
        if (batch != null) {
            batch.dispose();
        }
        if (atlas != null) {
            atlas.dispose();
        }
    }
}