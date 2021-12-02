package demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.view;

import android.content.Intent;
import android.os.Bundle;

import com.lib.sdk.bean.ConfigJsonNameLink;
import com.lib.sdk.bean.JsonConfig;
import com.lib.sdk.bean.StringUtils;
import com.xm.activity.base.XMBaseActivity;
import com.xm.ui.widget.XTitleBar;
import com.xm.ui.widget.searchbar.adapter.SearchBarAdapter;
import com.xm.ui.widget.searchbar.adapter.SearchResultAdapter;
import com.xm.ui.widget.searchbar.view.SearchBar;

import java.lang.reflect.Field;
import java.text.Collator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import demo.xm.com.xmfunsdkdemo.R;
import demo.xm.com.xmfunsdkdemo.ui.device.config.BaseConfigActivity;
import demo.xm.com.xmfunsdkdemo.ui.device.config.simpleconfig.presenter.ConfigNameSelPresenter;
import demo.xm.com.xmfunsdkdemo.utils.PinyinUtils;

/**
 * 配置名称选择
 * Configuration name selection
 */
public class ConfigNameSelActivity extends BaseConfigActivity<ConfigNameSelPresenter> {
    private List<Bundle> configList = new ArrayList<>();
    private HashMap<String,Bundle> configMaps = new HashMap<>();
    private SearchBar searchBar;
    @Override
    public ConfigNameSelPresenter getPresenter() {
        return new ConfigNameSelPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_config_name_sel);
        initView();
        initData();
    }

    private void initData() {
        Field[] fields = JsonConfig.class.getFields();
        ArrayList<String> commentList = new ArrayList<>();
        ArrayList<String> jsonNameList = new ArrayList<>();

        /**
         * 通过反射方式从JsonConfig中获取JsonName等信息
         * Get JsonName and other information from JsonConfig by reflection
         */
        for (Field field : fields) {
            ConfigJsonNameLink configJsonNameLink = field.getAnnotation(ConfigJsonNameLink.class);
            if (configJsonNameLink != null) {
                Bundle bundle = new Bundle();
                String firstPinYin = PinyinUtils.getFirstPinYin(configJsonNameLink.comment());
                bundle.putString("title",firstPinYin);
                bundle.putString("name",configJsonNameLink.name());
                bundle.putString("content",configJsonNameLink.comment());
                bundle.putString("example",configJsonNameLink.example());
                bundle.putInt("cmdId",configJsonNameLink.cmdId());
                configList.add(bundle);
                configMaps.put(configJsonNameLink.comment(),bundle);
            }
        }

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
                    intent.putExtra("configName", itemContent);
                    intent.putExtra("jsonName", bundle.getString("name"));
                    intent.putExtra("jsonData", bundle.getString("example"));
                    intent.putExtra("cmdId", bundle.getInt("cmdId"));
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
                   intent.putExtra("configName", result);
                   intent.putExtra("jsonName",bundle.getString("name"));
                   intent.putExtra("jsonData",bundle.getString("example"));
                   intent.putExtra("cmdId",bundle.getInt("cmdId"));
                   setResult(RESULT_OK, intent);
                   finish();
               }
            }
        });
    }
}
