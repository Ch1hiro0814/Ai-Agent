package com.chihiro.imagesearchmcpserver.tools;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class ImageSearchToolTest {

    @Resource
    private ImageSearchTool imageSearchTool;

    @Test
     public void testSearchImage() {
        String query = "computer";
        String result = imageSearchTool.searchImage(query);
        Assertions.assertNotNull(result);
    }
}
