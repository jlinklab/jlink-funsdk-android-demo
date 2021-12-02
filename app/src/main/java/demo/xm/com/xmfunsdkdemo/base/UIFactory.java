package demo.xm.com.xmfunsdkdemo.base;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.text.TextUtils;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;

public class UIFactory {
	
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}
	
	public static void setTopDrawable(Context context, TextView v, int resId, int wInDip, int hInDip) {
		Drawable drawable= context.getResources().getDrawable(resId);
		int width = UIFactory.dip2px(context, wInDip);
		int height = UIFactory.dip2px(context, hInDip);
		drawable.setBounds(0, 0, width, height);
		v.setCompoundDrawables(null, drawable, null, null);
	}
	
	public static void setLeftDrawable(Context context, TextView v, int resId, int wInDip, int hInDip) {
		Drawable drawable= context.getResources().getDrawable(resId);
		int width = UIFactory.dip2px(context, wInDip);
		int height = UIFactory.dip2px(context, hInDip);
		drawable.setBounds(0, 0, width, height);
		v.setCompoundDrawables(drawable, null, null, null);
	}
	
	public static void setRightDrawable(Context context, TextView v, int resId, int wInDip, int hInDip) {
		Drawable drawable= context.getResources().getDrawable(resId);
		int width = UIFactory.dip2px(context, wInDip);
		int height = UIFactory.dip2px(context, hInDip);
		drawable.setBounds(0, 0, width, height);
		v.setCompoundDrawables(null, null, drawable, null);
	}
	
	/**
	 * Create QR code
	 * 创建二维码
	 * @param text
	 * @param format
	 * @param qrSize
	 * @param color
	 * @return
	 */
    public static Bitmap createCode(String text, int qrSize, int color) {
    	try {
    		BitMatrix matrix = new MultiFormatWriter().encode(
    				text,BarcodeFormat.QR_CODE, qrSize, qrSize);  
            int width = matrix.getWidth();  
            int height = matrix.getHeight();  
            int[] pixels = new int[width * height];  
            for (int y = 0; y < height; y++) {  
                for (int x = 0; x < width; x++) {  
                    if(matrix.get(x, y)){  
                        pixels[y * width + x] = color;  
                    }  
                      
                }  
            }  
            
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);  
            bitmap.setPixels(pixels, 0, width, 0, 0, width, height);  
            return bitmap;  
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
	}
    
    public static boolean isForeground(Context context, String className) {
        if (context == null || TextUtils.isEmpty(className)) {
            return false;  
        }
   
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);  
        List<RunningTaskInfo> list = am.getRunningTasks(1);  
        if (list != null && list.size() > 0) {
            ComponentName cpn = list.get(0).topActivity;  
            if (className.equals(cpn.getClassName())) {  
                return true;
            }
        }
        
        return false;  
    }  
}