package com.chihiro.aiagent.app;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class LoveAppTest {
    @Resource
    private LoveApp loveApp;


    @Test
    void testDoChat() {
        String charId = UUID.randomUUID().toString();
        String message = "我叫文泽凯，我是电子科技大学格拉斯哥学院的一名大三本科生";
        String response = loveApp.doChat(message, charId);
        message = "专业是通信工程，平均分为84，GPA为3.56";
        response = loveApp.doChat(message, charId);
        message = "想去新加坡或者美国留学，请给我一些建议";
        response = loveApp.doChat(message, charId);
    }

    @Test
    void doChatWithReportS() {
        String charId = UUID.randomUUID().toString();
        String message = "我叫文泽凯，我是电子科技大学格拉斯哥学院的一名大三本科生，专业是通信工程，平均分为84，GPA为3.56，想去新加坡或者美国留学，请给我一些建议";
        LoveApp.SchoolReport schoolReport = loveApp.doChatWithReportS(message, charId);
    }

    @Test
    void doChatWithRAG() {
        String charId = UUID.randomUUID().toString();
        String message = "我叫文泽凯，我是电子科技大学格拉斯哥学院的一名大三本科生，专业是通信工程，平均分为84，GPA为3.56，想去美国留学，请给我介绍一些美国的学校，并给出一些提升背景的方法，除此之外，给出留学准备事项以及如何在美国找工作";
//        String message = "请给我介绍美国前十名高校有哪些及特色？";
        String response = loveApp.doChatWithRAG(message, charId);
    }
}