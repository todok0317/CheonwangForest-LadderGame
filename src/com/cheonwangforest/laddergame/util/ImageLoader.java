package com.cheonwangforest.laddergame.util;

import javax.imageio.ImageIO;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * JAR 파일에서도 작동하는 이미지 로더 유틸리티 클래스
 */
public class ImageLoader {

    /**
     * 클래스패스에서 이미지를 로드합니다.
     * JAR 파일과 일반 파일 시스템 모두에서 작동합니다.
     *
     * @param imagePath 클래스패스 상대 경로 (예: "/com/cheonwangforest/images/홈 버튼2.png")
     * @return 로드된 Image 객체, 실패시 null
     */
    public static Image loadImage(String imagePath) {
        try {
            // 클래스패스에서 리소스 URL 가져오기
            URL imageUrl = ImageLoader.class.getResource(imagePath);
            if (imageUrl != null) {
                return ImageIO.read(imageUrl);
            }

            // URL이 null인 경우 InputStream으로 시도
            InputStream imageStream = ImageLoader.class.getResourceAsStream(imagePath);
            if (imageStream != null) {
                Image image = ImageIO.read(imageStream);
                imageStream.close();
                return image;
            }

            System.err.println("이미지를 찾을 수 없습니다: " + imagePath);
            return null;

        } catch (IOException e) {
            System.err.println("이미지 로드 중 오류 발생: " + imagePath + " - " + e.getMessage());
            return null;
        }
    }

    /**
     * 이미지 경로를 파일 시스템 경로에서 클래스패스 경로로 변환합니다.
     *
     * @param filePath 파일 시스템 경로 (예: "src/com/cheonwangforest/images/홈 버튼2.png")
     * @return 클래스패스 경로 (예: "/com/cheonwangforest/images/홈 버튼2.png")
     */
    public static String convertToResourcePath(String filePath) {
        if (filePath.startsWith("src/")) {
            return "/" + filePath.substring(4); // "src/" 제거하고 앞에 "/" 추가
        }
        if (!filePath.startsWith("/")) {
            return "/" + filePath;
        }
        return filePath;
    }
}