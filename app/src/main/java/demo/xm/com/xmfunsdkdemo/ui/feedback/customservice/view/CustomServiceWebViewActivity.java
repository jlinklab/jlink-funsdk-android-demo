package demo.xm.com.xmfunsdkdemo.ui.feedback.customservice.view;

import android.Manifest;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import androidx.fragment.app.FragmentTransaction;

import com.xm.activity.base.XMBaseActivity;
import com.xm.activity.base.XMBasePresenter;

import demo.xm.com.xmfunsdkdemo.R;
import permissions.dispatcher.NeedsPermission;

public class CustomServiceWebViewActivity extends XMBaseActivity {

    CustomServiceWebViewFragment mCustomServiceWebViewFragment;

    @Override
    public XMBasePresenter getPresenter() {
        return null;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        boolean isHelpPage = getIntent().getBooleanExtra("isHelpPage", false);
        setContentView(R.layout.activity_custom_service);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT); // 状态栏透明
        }
        mCustomServiceWebViewFragment = CustomServiceWebViewFragment.newInstance(isHelpPage);
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.add(R.id.fragment_content, mCustomServiceWebViewFragment).setCustomAnimations(R.anim.quick_left_in, R.anim.quick_left_out);
        fragmentTransaction.commit();
    }

    @Override
    public void onBackPressed() {
        if(mCustomServiceWebViewFragment!=null){
            mCustomServiceWebViewFragment.onBackPressed();
        }

    }

    @NeedsPermission({Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.CAMERA})
    protected void initData() {

    }

}
