package demo.xm.com.xmfunsdkdemo.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.core.content.FileProvider;

import com.blankj.utilcode.util.PathUtils;
import com.blankj.utilcode.util.ZipUtils;
import com.lib.SDKCONST;
import com.utils.XUtils;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.ListSelectItem;

import java.io.File;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.app.SDKDemoApplication;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.utils.SPUtil;

/**
 * 关于Demo的介绍,包括logo,版本,授权
 * Introduction to Demo, including logo, version, authorization
 * Created by jiangping on 2018-10-23.
 */
public class AboutActivity extends DemoBaseActivity<AboutPresenter> implements AboutContract.IAboutView {
    private TextView tvVersion;
    private ListSelectItem lsiFeedback;//反馈
    private ListSelectItem lsiSaveMediaData;//保存实时预览媒体数据
    private ListSelectItem lsiSaveTalkData;//保存对讲数据
    private ListSelectItem lsiShareMediaData;//分享实时预览媒体数据
    private ListSelectItem lsiShareTalkData;//分享对讲数据

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_about);
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());
        tvVersion = findViewById(R.id.txtVersion);
        tvVersion.setText(XUtils.getVersion(this));
        lsiFeedback = findViewById(R.id.lsi_feedback);

        lsiFeedback.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                XMPromptDlg.onShow(AboutActivity.this, getString(R.string.is_sure_feedback), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        presenter.sendFeedBack(AboutActivity.this);
                    }
                }, null);
            }
        });

        lsiSaveMediaData = findViewById(R.id.lsi_save_media_data);
        lsiSaveTalkData = findViewById(R.id.lsi_save_talk_data);

        lsiSaveMediaData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSaveMediaData(lsiSaveMediaData.getSwitchState() == SDKCONST.Switch.Close);
            }
        });

        lsiSaveTalkData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeSaveTalkData(lsiSaveTalkData.getSwitchState() == SDKCONST.Switch.Close);
            }
        });

        lsiShareMediaData = findViewById(R.id.lsi_share_media_data);
        lsiShareTalkData = findViewById(R.id.lsi_share_talk_data);
        lsiShareMediaData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipPath = PathUtils.getExternalAppDataPath() + File.separator + "original_media_data.zip";
                try {
                    ZipUtils.zipFile(SDKDemoApplication.PATH_ORIGINAL_MEDIA_DATA, zipPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                try {
                    File file = new File(zipPath);
                    if (file.exists()) {
                        Uri uri = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(AboutActivity.this, XUtils.getPackageName(AboutActivity.this) + ".fileprovider", file);
                        } else {
                            uri = Uri.fromFile(file);
                        }
                        intent.setType("application/zip");
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.putExtra(Intent.EXTRA_SUBJECT, "share_soft");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // 自定义选择框的标题
                startActivity(Intent.createChooser(intent, "选择分享类型"));
            }
        });

        lsiShareTalkData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String zipPath = PathUtils.getExternalAppDataPath() + File.separator + "talk_data.zip";
                try {
                    ZipUtils.zipFile(SDKDemoApplication.PATH_ORIGINAL_TALK_DATA, zipPath);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Intent intent = new Intent(Intent.ACTION_SEND);
                try {
                    File file = new File(zipPath);
                    if (file.exists()) {
                        Uri uri = null;
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                            uri = FileProvider.getUriForFile(AboutActivity.this, XUtils.getPackageName(AboutActivity.this) + ".fileprovider", file);
                        } else {
                            uri = Uri.fromFile(file);
                        }
                        intent.setType("application/zip");
                        intent.putExtra(Intent.EXTRA_STREAM, uri);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                intent.putExtra(Intent.EXTRA_SUBJECT, "share_soft");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                // 自定义选择框的标题
                startActivity(Intent.createChooser(intent, "选择分享类型"));
            }
        });

        initData();
    }

    private void initData() {
        boolean isSaveOriginalMediaData = SPUtil.getInstance(this).getSettingParam("IS_SAVE_ORIGINAL_MEDIA_DATA", false);
        boolean isSaveTalkData = SPUtil.getInstance(this).getSettingParam("IS_SAVE_TALK_DATA", false);

        lsiSaveMediaData.setRightImage(isSaveOriginalMediaData ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
        lsiSaveTalkData.setRightImage(isSaveTalkData ? SDKCONST.Switch.Open : SDKCONST.Switch.Close);
    }


    /**
     * 是否打开保存原始媒体数据
     *
     * @param isOpen
     */
    private void changeSaveMediaData(boolean isOpen) {
        if (isOpen) {
            lsiSaveMediaData.setRightImage(SDKCONST.Switch.Open);
            SPUtil.getInstance(this).setSettingParam("IS_SAVE_ORIGINAL_MEDIA_DATA", true);
        } else {
            lsiSaveMediaData.setRightImage(SDKCONST.Switch.Close);
            SPUtil.getInstance(this).setSettingParam("IS_SAVE_ORIGINAL_MEDIA_DATA", false);
        }
    }

    /**
     * 是否打开保存对讲数据
     *
     * @param isOpen
     */
    private void changeSaveTalkData(boolean isOpen) {
        if (isOpen) {
            lsiSaveTalkData.setRightImage(SDKCONST.Switch.Open);
            SPUtil.getInstance(this).setSettingParam("IS_SAVE_TALK_DATA", true);
        } else {
            lsiSaveTalkData.setRightImage(SDKCONST.Switch.Close);
            SPUtil.getInstance(this).setSettingParam("IS_SAVE_TALK_DATA", true);
        }
    }

    @Override
    public AboutPresenter getPresenter() {
        return new AboutPresenter(this);
    }


    @Override
    public void onUpdateView() {

    }
}
