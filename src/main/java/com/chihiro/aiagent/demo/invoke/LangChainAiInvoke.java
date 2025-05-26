package com.chihiro.aiagent.demo.invoke;

import dev.langchain4j.community.model.dashscope.QwenChatModel;

public class LangChainAiInvoke {
    public static void main(String[] args) {
        QwenChatModel qwenChatModel = QwenChatModel.builder()
                .apiKey(TestApiKey.API_KEY)
                .modelName("qwen-plus")
                .build();
        String answer = qwenChatModel.chat("我是文泽凯，我在学习java的大模型应用开发");
        System.out.println(answer);
    }
}
