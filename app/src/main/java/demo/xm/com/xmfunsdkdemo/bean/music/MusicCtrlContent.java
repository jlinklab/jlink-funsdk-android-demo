package demo.xm.com.xmfunsdkdemo.bean.music;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class MusicCtrlContent {
    @SerializedName("Dev.MusicInfo")
    private List<MusicInfo> musicInfo; // 这里是Dev.MusicInfo的数组
    //0：单曲循环，1：列表循环，2：随机循环
    private int Loop;
    //当前播放的是第几首歌
    private int Music;
    //播放状态  0 : 停止播放    1 : 开始播放
    private int Play;
    //播放音量 1-5
    private int Volume;
    //选中的数量
    private int SelMusicNum;
    private String Name;

    // Getters and Setters  
    public List<MusicInfo> getMusicInfo() {
        return musicInfo;
    }

    public void setMusicInfo(List<MusicInfo> musicInfo) {
        this.musicInfo = musicInfo;
    }

    public int getLoop() {
        return Loop;
    }

    public void setLoop(int loop) {
        Loop = loop;
    }

    public int getMusic() {
        return Music;
    }

    public void setMusic(int music) {
        Music = music;
    }

    public int getPlay() {
        return Play;
    }

    public void setPlay(int play) {
        Play = play;
    }

    public int getVolume() {
        return Volume;
    }

    public void setVolume(int volume) {
        Volume = volume;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public int getSelMusicNum() {
        return SelMusicNum;
    }

    public void setSelMusicNum(int selMusicNum) {
        SelMusicNum = selMusicNum;
    }
}