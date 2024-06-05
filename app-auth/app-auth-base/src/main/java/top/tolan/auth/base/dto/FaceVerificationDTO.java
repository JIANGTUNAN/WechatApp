package top.tolan.auth.base.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.multipart.MultipartFile;

/**
 * 验证人脸DTO
 *
 * @author tooooolan
 * @version 2024年6月6日
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
public class FaceVerificationDTO {

    // 人脸照片
    @NotBlank(message = "人脸照片不能为空")
    private MultipartFile file;

}
