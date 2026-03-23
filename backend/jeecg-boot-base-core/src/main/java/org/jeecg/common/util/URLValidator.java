package org.jeecg.common.util;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author lf
 * @version 1.0.0
 * @ClassName URLValidator.java
 * @Description TODO
 * @createTime 2024年05月30日 15:20:00
 */
public class URLValidator {
    // Regular expression for validating HTTP and HTTPS URLs
    private static final String URL_REGEX =
            "^(https?://)([^\\s/$.?#].[^\\s]*)$";

    private static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);

    // Static method to check if a URL is valid
    public static boolean checkVideoUrl(String videoUrl) {
        if (videoUrl == null || videoUrl.isEmpty()) {
            return false;
        }

        Matcher matcher = URL_PATTERN.matcher(videoUrl);
        if (matcher.matches()) {
            // Extract the domain part and check if it contains "tiktok.com"
            String domain = matcher.group(2);
            return domain != null && domain.contains("tiktok.com");
        }

        return false;
    }

    public static void main(String[] args) {
        // Test URLs
        String[] testUrls = {
                "https://www.example.com/video.mp4",
                "http://www.example.com/video.avi",
                "ftp://www.example.com/video.mov",
                "invalid_url",
                "www.example.com"
        };

        for (String url : testUrls) {
            System.out.println("URL: " + url + " is valid: " + URLValidator.checkVideoUrl(url));
        }
    }
}
