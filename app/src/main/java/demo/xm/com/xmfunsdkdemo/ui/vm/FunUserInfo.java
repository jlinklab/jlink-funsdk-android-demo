package demo.xm.com.xmfunsdkdemo.ui.vm;


/**
 * Created by Jeff on 16/4/12.
 */
public class FunUserInfo {

    /**
     * user_id : 169363
     * user_name : jijianfeng
     * email : xm@hatcloud.me
     * mobile_phone :
     * sex : 0
     * reg_time : 0
     */

    private String user_id;
    private String user_name;
    private String email;
    private String mobile_phone;
    private int sex;
    private String reg_time;

    public FunUserInfo(String user_id, String user_name, String email,
                    String mobile_phone, int sex, String reg_time) {
        this.user_id = user_id;
        this.user_name = user_name;
        this.email = email;
        this.mobile_phone = mobile_phone;
        this.sex = sex;
        this.reg_time = reg_time;
    }

    public String getUserId() {
        return user_id;
    }

    public void setUserId(String user_id) {
        this.user_id = user_id;
    }

    public String getUserName() {
        return user_name;
    }

    public void setUserName(String user_name) {
        this.user_name = user_name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getMobile_phone() {
        return mobile_phone;
    }

    public void setMobile_phone(String mobile_phone) {
        this.mobile_phone = mobile_phone;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public String getReg_time() {
        return reg_time;
    }

    public void setReg_time(String reg_time) {
        this.reg_time = reg_time;
    }
}

