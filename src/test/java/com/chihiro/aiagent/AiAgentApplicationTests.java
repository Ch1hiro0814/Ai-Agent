package com.chihiro.aiagent;

import com.chihiro.aiagent.app.LoveApp;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class AiAgentApplicationTests {

    @Resource
    private LoveApp loveApp;
    @Test
    void contextLoads() {

    }

}
