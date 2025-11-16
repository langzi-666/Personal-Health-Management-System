package com.health.utils;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

/**
 * 文件工具类
 */
@Slf4j
public class FileUtil {

    /**
     * 上传文件保存目录
     */
    private static final String UPLOAD_DIR = "uploads";

    /**
     * 头像保存子目录
     */
    private static final String AVATAR_DIR = "avatars";

    /**
     * 允许的头像文件类型
     */
    private static final String[] ALLOWED_AVATAR_TYPES = {"image/jpeg", "image/jpg", "image/png", "image/gif"};

    /**
     * 最大文件大小（5MB）
     */
    private static final long MAX_FILE_SIZE = 5 * 1024 * 1024;

    /**
     * 上传头像文件
     *
     * @param file 上传的文件
     * @param userId 用户ID
     * @return 文件访问URL
     * @throws IOException 文件操作异常
     */
    public static String uploadAvatar(MultipartFile file, Long userId) throws IOException {
        // 验证文件
        validateAvatarFile(file);

        // 创建上传目录
        String uploadPath = UPLOAD_DIR + File.separator + AVATAR_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        // 生成唯一文件名
        String originalFilename = file.getOriginalFilename();
        String extension = "";
        if (originalFilename != null && originalFilename.contains(".")) {
            extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        }
        String fileName = userId + "_" + UUID.randomUUID().toString() + extension;

        // 保存文件
        Path filePath = Paths.get(uploadPath, fileName);
        Files.write(filePath, file.getBytes());

        log.info("头像上传成功: userId={}, fileName={}", userId, fileName);

        // 返回文件访问URL
        return "/uploads/avatars/" + fileName;
    }

    /**
     * 验证头像文件
     *
     * @param file 上传的文件
     */
    private static void validateAvatarFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new RuntimeException("文件不能为空");
        }

        // 检查文件大小
        if (file.getSize() > MAX_FILE_SIZE) {
            throw new RuntimeException("文件大小不能超过5MB");
        }

        // 检查文件类型
        String contentType = file.getContentType();
        boolean allowed = false;
        if (contentType != null) {
            for (String allowedType : ALLOWED_AVATAR_TYPES) {
                if (contentType.equals(allowedType)) {
                    allowed = true;
                    break;
                }
            }
        }
        if (!allowed) {
            throw new RuntimeException("不支持的文件类型，仅支持 JPG、PNG、GIF 格式");
        }
    }

    /**
     * 删除文件
     *
     * @param fileUrl 文件URL
     */
    public static void deleteFile(String fileUrl) {
        if (fileUrl == null || fileUrl.isEmpty()) {
            return;
        }

        try {
            // 从URL中提取文件路径
            String filePath = fileUrl.replace("/uploads/", UPLOAD_DIR + File.separator);
            Path path = Paths.get(filePath);
            if (Files.exists(path)) {
                Files.delete(path);
                log.info("文件删除成功: {}", filePath);
            }
        } catch (IOException e) {
            log.error("删除文件失败: {}", fileUrl, e);
        }
    }
}

