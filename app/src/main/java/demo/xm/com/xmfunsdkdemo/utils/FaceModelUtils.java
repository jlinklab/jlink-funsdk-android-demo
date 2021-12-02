package demo.xm.com.xmfunsdkdemo.utils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Linke on 2018-01-03.
 */

public class FaceModelUtils {
    /**
     * 人脸模型文件名
     */
    public static String[] FACE_MODEL = {
            "faceDetectNet12.bin",
            "faceDetectNet12.param",
            "faceDetectNet24.bin",
            "faceDetectNet24.param",
            "faceDetectNet48.bin",
            "faceDetectNet48.param",
            "facePoint.bin",
            "facePoint.param",
            "facePtn.bin",
            "facePtn.param",
            "faceRec.bin",
            "faceRec.param"};

    /**
     * 将assert 文件夹下面的 人脸模型移动到本地公有文件夹
      * @param context
     * @param sdcardPath
     * @return
     */
    public static String moveAssertFaceModelToSDCard(Context context, String sdcardPath) {
        if (TextUtils.isEmpty(sdcardPath))
            return "";
        boolean success = true;
        String modelPath = sdcardPath + File.separator + "facemodel" + File.separator;
        try {
            File file = new File(modelPath);
            if (!file.exists())
                file.mkdirs();
            for (String fileName : FACE_MODEL) {
                success &= copyAssetsResToSDCard(context, fileName, modelPath + fileName);
            }
        } catch (IOException e) {
            Toast.makeText(context, "load_face_model_error", Toast.LENGTH_LONG).show();
            Log.e("moveAssert exception", e.getMessage());
            return "";
        }
        return success ? modelPath : "";
    }

    public static boolean copyAssetsResToSDCard(Context context, String resName, String outFileName) throws IOException {
        InputStream fileInputStream = context.getAssets().open(resName);
        if ( null == fileInputStream ) {
            // asset资源不存在
            return false;
        }

        boolean needCopy = false;
        boolean retVal = false;
        File file = new File(outFileName);
        if (file.exists()) {
            // 如果文件存在，以文件大小来判断是否有变化，简化处理
            long fileSize = file.length();
            long newFileSize = fileInputStream.available();
            if ( fileSize != newFileSize ) {
                needCopy = true;
            } else {
                needCopy = false;
            }
        } else {
            if ( !file.createNewFile() ) {
                fileInputStream.close();
                return false;
            }

            needCopy = true;
        }

        // 已经存在并且文件未改变,不需要覆盖
        /*It already exists and the file has not changed so that no overwrite is needed*/

        if ( needCopy ) {
            FileOutputStream fileOutputStream = new FileOutputStream(outFileName);
            byte[] buffer = new byte[1024];
            int length = fileInputStream.read(buffer);
            while (length > 0) {
                fileOutputStream.write(buffer, 0, length);
                length = fileInputStream.read(buffer);
            }
            fileOutputStream.flush();
            fileOutputStream.close();
        }

        fileInputStream.close();

        return true;
    }

    public static boolean saveFaceFeatureToLocal(String featurePath, float[] faceFeature) {
        if (faceFeature == null || TextUtils.isEmpty(featurePath)) {
            return false;
        }
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(new File(featurePath));
            DataOutputStream dataOutputStream = new DataOutputStream(fileOutputStream);
            for (float v : faceFeature) {
                dataOutputStream.writeFloat(v);
            }
            dataOutputStream.flush();
            dataOutputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
