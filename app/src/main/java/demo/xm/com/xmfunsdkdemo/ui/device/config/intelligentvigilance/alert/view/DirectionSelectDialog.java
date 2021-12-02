package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.view;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.DialogFragment;

import com.lib.SDKCONST;
import com.xm.ui.widget.ListSelectItem;

import demo.xm.com.xmfunsdkdemo.R;
import io.reactivex.annotations.Nullable;

import static com.lib.sdk.bean.HumanDetectionBean.IA_BIDIRECTION;
import static com.lib.sdk.bean.HumanDetectionBean.IA_DIRECT_BACKWARD;
import static com.lib.sdk.bean.HumanDetectionBean.IA_DIRECT_FORWARD;


/**
 * @author hws
 * @name XMEye_Android
 * @class name：com.mobile.myeye.view.atv.view
 * @class 反向选择
 * @time 2019-05-07 14:43
 */
public class DirectionSelectDialog extends DialogFragment implements View.OnClickListener{
    private View rootLayout;
    private TextView tvOk;
    private TextView tvCancel;
    private int direction = -1;
    private ListSelectItem[] lsiDirections;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(STYLE_NO_TITLE, R.style.SimpleDialog);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        if (rootLayout == null) {
            rootLayout = inflater.inflate(R.layout.dialog_direction_pick, container, false);
            initView();
            initData();
        } else {
            //缓存的rootLayout需要判断是否已经被加过parent， 如果有parent需要从parent删除，要不然会发生这个rootLayout已经有parent的错误。
            ViewGroup parent = (ViewGroup) rootLayout.getParent();
            if (parent != null) {
                parent.removeView(rootLayout);
            }
        }
        return rootLayout;
    }

    @Override
    public void onStart() {
        super.onStart();
        DisplayMetrics dm = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
        getDialog().getWindow().setLayout( dm.widthPixels, getDialog().getWindow().getAttributes().height);
    }

    private void initView() {
        tvOk = rootLayout.findViewById(R.id.tv_sure);
        tvCancel = rootLayout.findViewById(R.id.tv_cancel);
        tvOk.setOnClickListener(this);
        tvCancel.setOnClickListener(this);
        lsiDirections = new ListSelectItem[3];
        lsiDirections[IA_DIRECT_FORWARD] = rootLayout.findViewById(R.id.lsi_direction_backward);
        lsiDirections[IA_DIRECT_BACKWARD] = rootLayout.findViewById(R.id.lsi_direction_forward);
        lsiDirections[IA_BIDIRECTION] = rootLayout.findViewById(R.id.lsi_direction_two_way);
        for (ListSelectItem listSelectItem : lsiDirections) {
            listSelectItem.setOnClickListener(this);
        }
    }

    private void initData() {
        if (direction == -1) {
            direction = IA_DIRECT_FORWARD;
        }
        directionSelect(direction);
    }

    public void setDirection(int direction) {
        directionSelect(direction);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lsi_direction_backward:
                directionSelect(IA_DIRECT_FORWARD);
                break;
            case R.id.lsi_direction_forward:
                directionSelect(IA_DIRECT_BACKWARD);
                break;
            case R.id.lsi_direction_two_way:
                directionSelect(IA_BIDIRECTION);
                break;
            case R.id.tv_sure:
                if (listener != null) {
                    listener.onDirection(direction);
                }
                dismiss();
                break;
            case R.id.tv_cancel:
                dismiss();
                break;
            default:
                break;
        }
    }

    private void directionSelect(int direction) {
        this.direction = direction;
        if (lsiDirections == null) {
            return;
        }
        for (int i = 0 ; i < lsiDirections.length;i++) {
            if (i == direction) {
                lsiDirections[i].setRightImage(SDKCONST.Switch.Open);
            } else {
                lsiDirections[i].setRightImage(SDKCONST.Switch.Close);
            }
        }
    }

    private OnDirectionSelListener listener;
    public void setOnDirectionSelListener(OnDirectionSelListener listener) {
        this.listener = listener;
    }
    public interface OnDirectionSelListener {
        void onDirection(int direction);
    }
}
