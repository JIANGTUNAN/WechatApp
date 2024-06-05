package top.tolan.auth.face.service;

import com.alibaba.fastjson2.JSON;
import com.aliyun.facebody20191230.Client;
import com.aliyun.facebody20191230.models.CompareFaceRequest;
import com.aliyun.facebody20191230.models.CompareFaceResponse;
import com.aliyun.facebody20191230.models.CompareFaceResponseBody;
import com.aliyun.tea.TeaException;
import com.aliyun.teautil.models.RuntimeOptions;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import jakarta.annotation.Resource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import top.tolan.auth.base.utils.SecurityUtils;
import top.tolan.auth.face.config.FaceWithConfig;
import top.tolan.common.entity.po.SysUser;
import top.tolan.common.exception.ServiceException;
import top.tolan.system.service.ISysUserService;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Base64;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 人脸验证服务
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Service
public class FaceVerificationService {

    private static final Logger log = LoggerFactory.getLogger(FaceVerificationService.class);
    @Resource
    private ISysUserService userService;

    public CompareFaceResponseBody.CompareFaceResponseBodyData verification(MultipartFile file) {
        // 获取当前登录用户id
        Integer userId = SecurityUtils.getUserId();
        // 查询当前登录用户是否注册人脸
        LambdaQueryWrapper<SysUser> wrapper = Wrappers.lambdaQuery(SysUser.class)
                .eq(SysUser::getUserId, userId)
                .isNotNull(SysUser::getFaceImg);
        SysUser sysUser = userService.getOneOpt(wrapper)
                .orElseThrow(() -> new ServiceException("用户未注册人脸"));
        // 初始化客户端
        Client client = FaceWithConfig.client;
        // 初始化请求参数配置
        CompareFaceRequest compareFaceRequest = new CompareFaceRequest()
                .setImageDataA(fileToBase64(new File(sysUser.getFaceImg())))
                .setImageDataB(fileToBase64(file))
                .setQualityScoreThreshold(98.5F);
        RuntimeOptions runtime = new RuntimeOptions();
        try {
            // 发起请求并获取返回值
            CompareFaceResponse compareFaceResponse = client.compareFaceWithOptions(compareFaceRequest, runtime);
            // 获取响应体
            CompareFaceResponseBody body = compareFaceResponse.getBody();
            CompareFaceResponseBody.CompareFaceResponseBodyData data = body.getData();
            log.info("响应成功，可信度概率为:{} ", data.confidence);
            return data;
        } catch (TeaException error) {
            log.error("响应失败，错误信息:{}, 诊断地址为：{}",
                    JSON.toJSONString(error.getData()), error.getData().get("Recommend"));
        } catch (Exception _error) {
            TeaException error = new TeaException(_error.getMessage(), _error);
            log.error("响应失败，错误信息:{}", JSON.toJSONString(error.getData()));
        }
        return null;
    }

    /**
     * 将上传的文件转换为Base64编码的字符串。
     *
     * @param file 上传的文件
     * @return 文件内容的Base64编码字符串。如果文件为空、过大或转换过程中发生错误，则返回null。
     */
    public static String fileToBase64(MultipartFile file) {
        // 验证文件是否为空
        if (file == null || file.isEmpty())
            throw new ServiceException("文件为空");
        // 验证文件大小
        if (file.getSize() > 10485760)
            throw new ServiceException("上传的文件过大");
        // 尝试将文件转换为Base64编码
        try {
            // 使用流处理大文件，以减少内存消耗
            Path tempFile = Files.createTempFile("upload-", ".tmp");
            file.transferTo(tempFile.toFile());
            byte[] bytes = Files.readAllBytes(tempFile);
            String base64Encoded = Base64.getEncoder().encodeToString(bytes);
            // 删除临时文件
            Files.deleteIfExists(tempFile);
            return base64Encoded;
        } catch (IOException e) {
            throw new ServiceException("文件转换为Base64编码时发生错误，消息：" + e.getMessage());
        }
    }

    /**
     * 将文件转换为Base64字符串。
     *
     * @param file 需要转换的文件
     * @return 文件的Base64编码字符串
     * @throws ServiceException 如果转换过程中发生错误
     */
    public static String fileToBase64(File file) throws ServiceException {
        try (FileInputStream fileInputStream = new FileInputStream(file)) {
            AtomicInteger totalBytesRead = new AtomicInteger();
            StringBuilder base64Builder = new StringBuilder();
            byte[] buffer = new byte[1024 * 1024];
            int bytesRead;
            while ((bytesRead = fileInputStream.read(buffer)) != -1) {
                byte[] chunk = Arrays.copyOf(buffer, bytesRead);
                String chunkBase64 = Base64.getEncoder().encodeToString(chunk);
                base64Builder.append(chunkBase64);
                totalBytesRead.addAndGet(bytesRead);
            }
            return base64Builder.toString();
        } catch (IOException e) {
            throw new ServiceException("Failed to convert file to Base64");
        }
    }

}
