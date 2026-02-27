package demo.xm.com.xmfunsdkdemo.utils;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.graphics.Insets;
import androidx.core.view.OnApplyWindowInsetsListener;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class ViewCompatUtils {
    /**
     *  通过监听系统栏状态， 为视图加上底部padding , 距离为导航栏高度
     */
    public static void addRootViewBottomPadding(View view){
        if (view == null){
            throw new NullPointerException("view is null");
        }
        ViewCompat.setOnApplyWindowInsetsListener(view, new OnApplyWindowInsetsListener() {
            @NonNull
            @Override
            public WindowInsetsCompat onApplyWindowInsets(@NonNull View v, @NonNull WindowInsetsCompat insets) {
                Insets insets1 = insets.getInsets(WindowInsetsCompat.Type.navigationBars());
                view.setPadding(0, 0, 0, insets1.bottom);
                return insets;
            }
        });

    }
}
