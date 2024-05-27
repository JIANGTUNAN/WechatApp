package top.tolan.sms.service.Impl;

import com.alibaba.fastjson2.JSON;
import com.aliyun.facebody20191230.Client;
import com.aliyun.facebody20191230.models.CompareFaceRequest;
import com.aliyun.facebody20191230.models.CompareFaceResponse;
import com.aliyun.facebody20191230.models.CompareFaceResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.Common;
import com.aliyun.teautil.models.RuntimeOptions;
import org.springframework.stereotype.Service;
import top.tolan.sms.config.FaceWithConfig;
import top.tolan.sms.config.SmsConfig;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Base64;

@Service
public class FaceWithImpl {


    public CompareFaceResponseBody.CompareFaceResponseBodyData test() {

        Client client = FaceWithConfig.client;

        File file1 = new File("D:\\FTP_Path\\face\\0001.png");
        File file2 = new File("D:\\FTP_Path\\face\\0003.png");

        CompareFaceRequest compareFaceRequest = new CompareFaceRequest()
                .setImageDataA(fileToBase64(file1))
                .setImageDataB(fileToBase64(file2))
                .setQualityScoreThreshold(98.5F);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 复制代码运行请自行打印 API 的返回值
            CompareFaceResponse compareFaceResponse = client.compareFaceWithOptions(compareFaceRequest, runtime);
            CompareFaceResponseBody body = compareFaceResponse.getBody();
            CompareFaceResponseBody.CompareFaceResponseBodyData data = body.getData();
            String jsonString = JSON.toJSONString(data);
            System.out.println(jsonString);
            return data;
        } catch (TeaException error) {
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            Common.assertAsString(error.message);
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            // 此处仅做打印展示，请谨慎对待异常处理，在工程项目中切勿直接忽略异常。
            // 错误 message
            System.out.println(error.getMessage());
            // 诊断地址
            System.out.println(error.getData().get("Recommend"));
            Common.assertAsString(error.message);
        }
        return null;
    }

    public static String fileToBase64(File file) {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            byte[] bytes = new byte[(int) file.length()];
            fileInputStream.read(bytes);
            return Base64.getEncoder().encodeToString(bytes);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

}
