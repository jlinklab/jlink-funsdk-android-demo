package demo.xm.com.xmfunsdkdemo.ui.device.config.intelligentvigilance.alert.model;

/**
 * Created by zhangyongyong on 2017-05-08-14:41.
 */

public class FunctionViewItemElement {
    private int normalResourceId;
    private int selectedResourceId;
    private String name;
    private boolean isSelected;
    private String label;
    private int itemType;

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public FunctionViewItemElement(int normalResourceId, int selectResourceId, String name,int itemType) {
        this.normalResourceId = normalResourceId;
        this.selectedResourceId = selectResourceId;
        this.name = name;
        this.itemType = itemType;
    }

    public int getNormalResourceId() {
        return normalResourceId;
    }

    public void setNormalResourceId(int normalResourceId) {
        this.normalResourceId = normalResourceId;
    }

    public int getSelectedResourceId() {
        return selectedResourceId;
    }

    public void setSelectedResourceId(int selectResourceId) {
        this.selectedResourceId = selectResourceId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }
}
