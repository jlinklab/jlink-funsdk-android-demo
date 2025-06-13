package demo.xm.com.xmfunsdkdemo.ui.feedback.customservice.manager;

import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.PUT;
import retrofit2.http.Url;


public interface LogManagerServerInteraction {

    /**
     * 上传日志文件
     *
     * @param url  上传地址
     * @param file requestBody数据
     * @return
     */
    @PUT()
    Call<ResponseBody> putBodyLogFile(@Url String url, @Body RequestBody file);
}
