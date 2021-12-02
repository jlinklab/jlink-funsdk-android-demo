package demo.xm.com.xmfunsdkdemo.ui.device.config.shadow.view;

import android.content.Intent;
import android.os.Bundle;

import com.lib.sdk.bean.ConfigJsonNameLink;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.ShadowConfigNameLink;
import com.manager.device.config.shadow.ShadowConfigEnum;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.searchbar.adapter.SearchBarAdapter;
import com.xm.ui.widget.searchbar.adapter.SearchResultAdapter;
import com.xm.ui.widget.searchbar.view.SearchBar;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.utils.PinyinUtils;

/**
 * 影子服务配置名称选择
 * Configuration name selection
 */
public class ShadowConfigNameSelActivity extends BaseConfigActivity {
    private List<Bundle> configList = new ArrayList<>();
    private HashMap<String, Bundle> configMaps = new HashMap<>();
    private SearchBar searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_name_sel);
        initView();
        initData();
    }

    private void initData() {
        parseData(ShadowConfigEnum.SysEnum.class.getFields());
        parseData(ShadowConfigEnum.StateEnum.class.getFields());
        parseData(ShadowConfigEnum.AbilityEnum.class.getFields());
        parseData(ShadowConfigEnum.FunEnum.class.getFields());
        //根据配置名称拼音首字母进行排序
        Collections.sort(configList, new Comparator<Bundle>() {
            @Override
            public int compare(Bundle t1, Bundle t2) {
                String title1 = t1.getString("title");
                String title2 = t2.getString("title");
                assert title1 != null;
                assert title2 != null;
                return title1.compareToIgnoreCase(title2);
            }
        });

        searchBar.setData((ArrayList<Bundle>) configList);
    }

    private void parseData(Field[] fields) {
        /**
         * 通过反射方式从JsonConfig中获取JsonName等信息
         * Get JsonName and other information from JsonConfig by reflection
         */
        for (Field field : fields) {
            ShadowConfigNameLink shadowConfigNameLink = field.getAnnotation(ShadowConfigNameLink.class);
            if (shadowConfigNameLink != null) {
                Bundle bundle = new Bundle();
                String firstPinYin = PinyinUtils.getFirstPinYin(shadowConfigNameLink.comment());
                bundle.putString("title", firstPinYin);
                bundle.putString("name",shadowConfigNameLink.fieldName());
                bundle.putString("content",shadowConfigNameLink.comment());
                bundle.putBoolean("isOnlyRead",shadowConfigNameLink.isOnlyRead());
                configList.add(bundle);
                configMaps.put(shadowConfigNameLink.comment(), bundle);
            }
        }
    }

    private void initView() {
        titleBar = findViewById(R.id.layoutTop);
        titleBar.setTitleText(getString(R.string.config_name_sel));
        titleBar.setLeftClick(new XTitleBar.OnLeftClickListener() {
            @Override
            public void onLeftclick() {
                finish();
            }
        });

        searchBar = findViewById(R.id.sb_config_name_sel);
        searchBar.setOnSearchBarClickListener(new SearchBarAdapter.OnSearchBarClickListener() {
            @Override
            public void onClick(String itemContent) {
                if (configMaps.containsKey(itemContent)) {
                    Bundle bundle = configMaps.get(itemContent);
                    Intent intent = getIntent();
                    intent.putExtra("fieldName", bundle.getString("name"));
                    intent.putExtra("isOnlyRead",bundle.getBoolean("isOnlyRead"));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
        searchBar.setOnSearchResultClickListener(new SearchResultAdapter.OnSearchResultClickListener() {
            @Override
            public void onResultClick(String result) {
                if (configMaps.containsKey(result)) {
                    Bundle bundle = configMaps.get(result);
                    Intent intent = getIntent();
                    intent.putExtra("fieldName", bundle.getString("name"));
                    intent.putExtra("isOnlyRead",bundle.getBoolean("isOnlyRead"));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        });
    }
}
