package demo.xm.com.xmfunsdkdemo.ui.device.config.interdevlinkage.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.xm.base.code.ErrorCodeManager;
import com.xm.ui.dialog.XMPromptDlg;

import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DemoBaseActivity;
import demo.xm.com.xmfunsdkdemo.ui.adapter.ItemSetAdapter;
import demo.xm.com.xmfunsdkdemo.ui.device.config.interdevlinkage.listener.InterDevLinkageContract;
import demo.xm.com.xmfunsdkdemo.ui.device.config.interdevlinkage.presenter.InterDevLinkagePresenter;

/**
 * 设备之间联动操作（比如带屏摇头机和门锁之间的联动）
 */
public class InterDevLinkageActivity extends DemoBaseActivity<InterDevLinkagePresenter>
        implements ItemSetAdapter.OnItemSetClickListener, InterDevLinkageContract.IInterDevLinkageView {
    private RecyclerView rvNeedDevLink;
    private ItemSetAdapter itemSetAdapter;
    private List<String> supportLinkDevList;
    private Button btnDisassociate;//解除关联 disassociate

    @Override
    public InterDevLinkagePresenter getPresenter() {
        return new InterDevLinkagePresenter(this);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inter_dev_linkage);
        rvNeedDevLink = findViewById(R.id.rv_need_link_dev_list);
        rvNeedDevLink.setLayoutManager(new LinearLayoutManager(this));

        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.inter_device_linkage_link));
        titleBar.setLeftClick(this);
        titleBar.setBottomTip(getClass().getName());

        btnDisassociate = findViewById(R.id.btn_disassociate);
        btnDisassociate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.unlinkDev();
            }
        });
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra("data");
        int bindAttr = bundle.getInt("bindAttr", 0);
        boolean isBind = bundle.getBoolean("isBind", false);
        String pin = bundle.getString("pin");
        String bindSn = bundle.getString("bindSn");
        presenter.setBindAttr(bindAttr);
        presenter.setBind(isBind);
        presenter.setPin(pin);
        presenter.setBindSn(bindSn);
        itemSetAdapter = new ItemSetAdapter(rvNeedDevLink, this);
        rvNeedDevLink.setAdapter(itemSetAdapter);
    }

    @Override
    public void onItem(int position) {
        XMPromptDlg.onShow(this, getString(R.string.is_sure_link_dev), new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showWaitDialog();
                presenter.linkDev(supportLinkDevList.get(position));
            }
        }, null);
    }

    @Override
    public void onCheckDevSupportLinkResult(List<String> supportLinkDevList, boolean isBind) {
        this.supportLinkDevList = supportLinkDevList;
        itemSetAdapter.setData(supportLinkDevList);
        if (isBind) {
            btnDisassociate.setVisibility(View.VISIBLE);
        }else {
            btnDisassociate.setVisibility(View.GONE);
        }
    }

    @Override
    public void onInterDevLinkResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.inter_dev_linked_s), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.inter_dev_linked_f) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId), Toast.LENGTH_LONG);
        }
    }

    @Override
    public void onInterDevUnlinkResult(boolean isSuccess, int errorId) {
        hideWaitDialog();
        if (isSuccess) {
            showToast(getString(R.string.inter_dev_unlinked_s), Toast.LENGTH_LONG);
        } else {
            showToast(getString(R.string.inter_dev_unlinked_f) + ":" + ErrorCodeManager.getSDKStrErrorByNO(errorId), Toast.LENGTH_LONG);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.release();
    }
}
