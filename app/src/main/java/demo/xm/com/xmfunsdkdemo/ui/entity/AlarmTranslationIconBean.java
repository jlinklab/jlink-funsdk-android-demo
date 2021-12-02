package demo.xm.com.xmfunsdkdemo.ui.entity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AlarmTranslationIconBean {
    private HashMap<String, HashMap<String, AlarmLanIconInfo>> languageInfo = new HashMap<>();

    public HashMap<String, HashMap<String, AlarmLanIconInfo>> getLanguageInfo() {
        return languageInfo;
    }

    public void setLanguageInfo(HashMap<String, HashMap<String, AlarmLanIconInfo>> languageInfo) {
        this.languageInfo = languageInfo;
    }

    public static class AlarmLanIconInfo {
        private String tl; // 翻译内容
        private String url; // 图标下载地址
        private String et; // 报警事件Event

        public String getTl() {
            return tl;
        }

        public void setTl(String tl) {
            this.tl = tl;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public String getEt() {
            return et;
        }

        public void setEt(String et) {
            this.et = et;
        }
    }
}
