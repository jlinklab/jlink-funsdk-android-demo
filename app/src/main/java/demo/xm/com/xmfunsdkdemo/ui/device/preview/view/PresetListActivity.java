package demo.xm.com.xmfunsdkdemo.ui.device.preview.view;

import android.os.Bundle;
import android.view.View;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.lib.sdk.bean.preset.ConfigGetPreset;
import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;
import com.xm.ui.dialog.XMPromptDlg;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.dialog.EditDialog;

import java.util.ArrayList;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.PresetListAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.listener.PresetListContract;
import demo.xm.com.xmfunsdkdemo.ui.device.preview.presenter.PresetListPresenter;

/**
 * 预置点列表
 */
public class PresetListActivity extends DemoBaseActivity<PresetListPresenter> implements PresetListContract.IPresetListView {
    private RecyclerView rvChannelList;
    private PresetListAdapter presetListAdapter;

    @Override
    public PresetListPresenter getPresenter() {
        return new PresetListPresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_preset_list);
        initView();
        initData();
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.turn_to_preset_list));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });
        rvChannelList = findViewById(R.id.rv_preset_list);
        rvChannelList.setLayoutManager(new LinearLayoutManager(this));
    }

    private void initData() {
        presetListAdapter = new PresetListAdapter(new PresetListAdapter.OnItemPresetClickListener() {
            @Override
            public void onItemClick(int position, int presetId, String presetName) {
                XMPromptDlg.onShowEditDialog(PresetListActivity.this, getString(R.string.modify_preset_name), presetName, new EditDialog.OnEditContentListener() {
                    @Override
                    public void onResult(String content) {
                        showWaitDialog();
                        //修改预置点名称
                        presenter.modifyPresetName(presetId, content);
                    }
                });
            }

            @Override
            public boolean onLongItemClick(int position, int presetId) {
                XMPromptDlg.onShow(PresetListActivity.this, getString(R.string.is_sure_to_delete_preset), new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showWaitDialog();
                        //删除预置点
                        presenter.deletePreset(presetId);
                    }
                }, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                    }
                });
                return false;
            }
        });
        rvChannelList.setAdapter(presetListAdapter);
        showWaitDialog();
        presenter.updatePresetList();
    }

    @Override
    public void onGetPresetListResult(List<ConfigGetPreset> presetList) {
        hideWaitDialog();
        presetListAdapter.setData(presetList);
    }

    @Override
    public void onDeletePresetResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        showToast(getString(R.string.delete_s), Toast.LENGTH_LONG);
        presenter.updatePresetList();
    }

    @Override
    public void onModifyPresetNameResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        showToast(getString(R.string.modify_s), Toast.LENGTH_LONG);
        presenter.updatePresetList();
    }
}
