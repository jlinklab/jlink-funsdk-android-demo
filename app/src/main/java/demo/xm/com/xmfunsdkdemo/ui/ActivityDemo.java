package demo.xm.com.xmfunsdkdemo.ui;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.FragmentActivity;

import com.xm.ui.widget.SpinnerSelectItem;

import java.util.Arrays;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.base.DialogWaitting;
import demo.xm.com.xmfunsdkdemo.base.UIFactory;

public class ActivityDemo extends FragmentActivity {
	private DialogWaitting mWaitDialog = null;
	private Toast mToast = null;
	
	private View mNavRightView = null;
	
	public void showWaitDialog() {
		if ( null == mWaitDialog ) {
			mWaitDialog = new DialogWaitting(this);
		}
		mWaitDialog.show();
	}
	
	public void showWaitDialog(int resid) {
		if ( null == mWaitDialog ) {
			mWaitDialog = new DialogWaitting(this);
		}
		mWaitDialog.show(resid);
	}
	
	public void showWaitDialog(String text) {
		if ( null == mWaitDialog ) {
			mWaitDialog = new DialogWaitting(this);
		}
		mWaitDialog.show(text);
	}
	
	public void hideWaitDialog() {
		if ( null != mWaitDialog ) {
			mWaitDialog.dismiss();
		}
	}
	
	public void showToast(String text){
		if ( null != text ) {
			if ( null != mToast ) {
				mToast.cancel();
			}
			mToast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}
	
	public void showToast(int resid){
		if ( resid > 0 ) {
			if ( null != mToast ) {
				mToast.cancel();
			}
			mToast = Toast.makeText(this, resid, Toast.LENGTH_SHORT);
			mToast.show();
		}
	}
	
	/**
	  *  判断某个字符串是否存在于数组中
	  *  用来判断该配置是否通道相关
	  *  @param stringArray 原数组
	  *  @param source 查找的字符串
	  *  @return 是否找到
	  */
	 public static boolean contains(String[] stringArray, String source) {
	  // 转换为list
	  List<String> tempList = Arrays.asList(stringArray);
	  
	  // 利用list的包含方法,进行判断
		 return tempList.contains(source);
	 }
	
	// 只有布局中有指定的标题栏的Activity才允许设置
	protected View setNavagateRightButton(int resource) {
		return setNavagateRightButton(resource, 48, LayoutParams.MATCH_PARENT);
	}
	
	protected View setNavagateRightButton(int resource, int witdhOfDP, int heightOfDP) {
		if ( null != findViewById(R.id.layoutTop) ) {
			RelativeLayout navLayout = (RelativeLayout)findViewById(R.id.layoutTop);
			if ( null != mNavRightView ) {
				navLayout.removeView(mNavRightView);
			}
			
			mNavRightView = LayoutInflater.from(this).inflate(resource, null);
			RelativeLayout.LayoutParams lp = null;
			
			int lpWidth = 0;
			int lpHeight = 0;
			if ( witdhOfDP > 0 ) {
				lpWidth = UIFactory.dip2px(this, witdhOfDP);
			} else {
				lpWidth = witdhOfDP;
			}
			
			if ( heightOfDP > 0 ) {
				lpHeight = UIFactory.dip2px(this, heightOfDP);
			} else {
				lpHeight = heightOfDP;
			}
			
			lp = new RelativeLayout.LayoutParams(
					lpWidth, lpHeight);
			
			lp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
			lp.addRule(RelativeLayout.CENTER_VERTICAL);
			
			lp.rightMargin = UIFactory.dip2px(this, 5);
			
			navLayout.addView(mNavRightView, lp);
			
			return mNavRightView;
		}
		return null;
	}

	protected int getIntValue(View layout,int id) {
		if (layout == null) {
			return 0;
		}
		View v = layout.findViewById(id);
		return getIntValue(v);
	}

	protected int getIntValue(View v) {
		if (v == null) {
			return 0;
		}
		if (v instanceof EditText) {
			EditText v0 = (EditText) v;
			return Integer.valueOf(v0.getText().toString());
		} else if (v instanceof CheckBox) {
			CheckBox v0 = (CheckBox) v;
			return v0.isChecked() ? 1 : 0;
		} else if (v instanceof SeekBar) {
			SeekBar v0 = (SeekBar) v;
			return v0.getProgress();
		} else if (v instanceof Spinner) {
			Spinner sp = (Spinner) v;
			Object iv = v.getTag();
			if (iv != null && iv instanceof int[]) {
				int[] values = (int[]) iv;
				int i = sp.getSelectedItemPosition();
				if (i >= 0 && i < values.length) {
					return values[i];
				}
				return 0;
			}
		} else {
//            System.err.println("GetIntValue:" + id);
		}
		return 0;
	}

	protected int initSpinnerText(Spinner sp, String[] texts, int[] values) {
		ArrayAdapter<CharSequence> adapter = new ArrayAdapter<CharSequence>(this, android.R.layout.simple_spinner_item,
				texts);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		sp.setAdapter(adapter);
		if (values == null) {
			values = new int[texts.length];
			for (int i = 0; i < texts.length; ++i) {
				values[i] = i;
			}
		}
		sp.setTag(values);
		return 0;
	}

	public int setValue(View v, int value) {
		if (v instanceof SpinnerSelectItem) {
			v = ((SpinnerSelectItem) v).getSpinner();
		}
		if (v instanceof EditText) {
			EditText v0 = (EditText) v;
			v0.setText(String.valueOf(value));
		} else if (v instanceof CheckBox) {
			CheckBox v0 = (CheckBox) v;
			v0.setChecked(value != 1);
		} else if (v instanceof SeekBar) {
			SeekBar v0 = (SeekBar) v;
			v0.setProgress(value);
		} else if (v instanceof Spinner) {
			Spinner sp = (Spinner) v;
			Object iv = v.getTag();
			if (iv != null && iv instanceof int[]) {
				int values[] = (int[]) iv;
				for (int i = 0; i < values.length; ++i) {
					if (value == values[i]) {
						sp.setSelection(i);
						break;
					}
				}
			}
		}
		return 0;
	}
}
