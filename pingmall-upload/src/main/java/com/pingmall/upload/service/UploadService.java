package com.pingmall.upload.service;

import com.github.tobato.fastdfs.domain.StorePath;
import com.github.tobato.fastdfs.service.FastFileStorageClient;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

@Service
public class UploadService {

    //合法文件类型集合
    private static final List<String> CONTENT_TYPES = Arrays.asList
            ("application/x-img", "image/jpeg", "application/x-jpg", "image/png",
             "application/x-png",
             "image/gif");

    private static final Logger LOGGER = LoggerFactory.getLogger(UploadService.class);

    //注入FastDFS客户端对象
    @Autowired
    private FastFileStorageClient fastFileStorageClient;

    /**
     * 上传图片文件，返回路径。
     *
     * @param file
     * @return
     */
    public String uploadIMG(MultipartFile file) {

        //获取原始文件名称
        String originalFilename = file.getOriginalFilename();
        //获取文件类型
        String contentType = file.getContentType();
        //校验文件类型
        if (!CONTENT_TYPES.contains(contentType)) {
            LOGGER.info("文件类型不合法：{}", originalFilename);
            return null;
        }
        //校验文件内容
        BufferedImage bufferedImage = null;
        try {
            bufferedImage = ImageIO.read(file.getInputStream());
        } catch (IOException e) {
            LOGGER.info("ImageIO.read()服务器内部错误：{}", originalFilename);
            e.printStackTrace();
            return null;
        }
        if (bufferedImage == null) {
            LOGGER.info("文件内容不合法：{}", originalFilename);
            return null;
        }
        //保存到服务器
        //file.transferTo(new File("D:\\UploadFiles\\Images\\" + originalFilename));
        //获取文件的扩展名
        String ext = StringUtils.substringAfterLast(originalFilename, ".");
        StorePath storePath = null;
        try {
            //保存到Storage（第一个参数：文件流，第二个参数：文件大小，第三个参数：文件扩展名），返回图片在Storage的路径
            storePath = fastFileStorageClient.uploadFile(file.getInputStream(), file.getSize(), ext, null);
        } catch (IOException e) {
            LOGGER.info("fastFileStorageClient.uploadFile()服务器内部错误：{}", originalFilename);
            e.printStackTrace();
        }
        //返回图片路径，进行回显。
        //return "http://image.pingmall.com/" + originalFilename;
        //返回图片在Storage的路径，进行回显
        return "http://image.pingmall.com/" + storePath.getFullPath();

    }

}
