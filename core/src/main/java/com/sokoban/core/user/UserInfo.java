package com.sokoban.core.user;

import java.util.ArrayList;
import java.util.List;

import com.sokoban.core.game.Logger;
import com.sokoban.core.map.gamedefault.SokobanMaps;

/**
 * 用户信息类
 * @author Life_Checkpoint
 * @author ChatGPT
 */
public class UserInfo {
    private String userID;
    private String userPasswordHash;
    private Boolean rememberPassword;
    private Boolean guest;
    private Boolean tutorial;
    private List<SaveArchiveInfo> saveArchives;

    public UserInfo() {
        this.saveArchives = new ArrayList<>();
        this.saveArchives.add(new SaveArchiveInfo());
        this.saveArchives.add(new SaveArchiveInfo());
        this.saveArchives.add(new SaveArchiveInfo());
        this.guest = true;
    }

    public UserInfo(String userID, String userPasswordHash, boolean rememberPassword) {
        this.userID = userID.toLowerCase();
        this.userPasswordHash = userPasswordHash;
        this.rememberPassword = rememberPassword;
        this.guest = false;
        this.tutorial = false;
        this.saveArchives = new ArrayList<>();

        SaveArchiveInfo newArchive = new SaveArchiveInfo();
        updateArchiveMapStatue(newArchive);
        this.saveArchives.add(newArchive.deepCopy());
        this.saveArchives.add(newArchive.deepCopy());
        this.saveArchives.add(newArchive.deepCopy());
    }

    /**
     * 更新存档所有地图状态
     * @return 更新状态存档
     */
    private void updateArchiveMapStatue(SaveArchiveInfo saveArchive) {
        // 临时禁用所有 info 输出
        Logger.enableLog = false;

        // 检测并调整每个地图状态
        for (SokobanMaps map : SokobanMaps.values()) {
            if (map == SokobanMaps.None) continue;
            if (saveArchive.getMapStatue(map) == null || saveArchive.getMapStatue(map) == SaveArchiveInfo.MapStatue.Unknown) {
                saveArchive.updateMapStaute(map, SaveArchiveInfo.MapStatue.Unreached);
            }
        }

        // 启用所有 info 输出
        Logger.enableLog = true;
    }

    public String getUserID() {
        return userID.toLowerCase();
    }
    public void setUserID(String userID) {
        this.userID = userID.toLowerCase();
    }
    public String getUserPasswordHash() {
        return userPasswordHash;
    }
    public void setUserPasswordHash(String userPasswordHash) {
        this.userPasswordHash = userPasswordHash;
    }
    public Boolean isRememberPassword() {
        return rememberPassword;
    }
    public void setRememberPassword(Boolean rememberPassword) {
        this.rememberPassword = rememberPassword;
    }
    public List<SaveArchiveInfo> getSaveArchives() {
        return saveArchives;
    }
    public void setSaveArchives(List<SaveArchiveInfo> saveArhives) {
        this.saveArchives = saveArhives;
    }
    public Boolean isGuest() {
        return guest;
    }
    public void setGuest(Boolean guest) {
        this.guest = guest;
    }
    public Boolean isTutorial() {
        return tutorial;
    }
    public void setTutorial(Boolean tutorial) {
        this.tutorial = tutorial;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UserInfo)) return false;
        UserInfo otherUserInfo = (UserInfo) obj;

        if (!userID.equals(otherUserInfo.userID)) return false;
        if (!userPasswordHash.equals(otherUserInfo.getUserPasswordHash())) return false;
        if (!rememberPassword.equals(otherUserInfo.rememberPassword)) return false;
        if (!saveArchives.equals(otherUserInfo.getSaveArchives())) return false;
        if (!tutorial.equals(otherUserInfo.isTutorial())) return false;
        if (!guest.equals(otherUserInfo.isGuest())) return false;

        return true;
    }

}
