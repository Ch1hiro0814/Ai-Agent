package com.chihiro.aiagent.app;

import com.chihiro.aiagent.advisor.MyLoggerAdvisor;
import com.chihiro.aiagent.advisor.ReReadingAdvisor;
import com.chihiro.aiagent.chatmemory.FileBasedChatMemory;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.QuestionAnswerAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.InMemoryChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;

import java.util.List;

import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_CONVERSATION_ID_KEY;
import static org.springframework.ai.chat.client.advisor.AbstractChatMemoryAdvisor.CHAT_MEMORY_RETRIEVE_SIZE_KEY;

@Component
@Slf4j
public class LoveApp {

    private final ChatClient chatClient;

    private static final String SYSTEM_PROMPT = "你是一位资深留学选校定位专家，具备丰富的海外院校匹配经验；" +
            "在对话中，你要通过开放式且分层次的引导性问题，从学术背景、语言成绩、预算、地域与文化偏好、专业兴趣、职业规划和生活方式等多维度深入了解用户需求，" +
            "并模拟真实留学生选校过程中的痛点，不断提出后续追问；在充分收集信息后，基于最新院校排名、专业强项、奖学金政策、申请难度和就业前景，" +
            "为用户提供 3–5 所最匹配的学校及专业，详细分析每所院校的优势与劣势；同时，针对申请流程节点、材料准备、签证面签、成本预算及生活环境差异等常见疑虑，给出切实可行的建议与资源；" +
            "在每轮对话中，你要根据用户新增信息动态调整推荐，并始终以清晰易懂的语言，帮助用户梳理思路、规划时间表，指导其下一步行动，如如何提高语言成绩、获取校友支持或参加招生宣讲会等。";

    public LoveApp(ChatModel dashscopeChatModel){
//        ChatMemory chatMemory = new InMemoryChatMemory();
        String fileDir = System.getProperty("user.dir") + "/temp/chat-memory";
        ChatMemory chatMemory = new FileBasedChatMemory(fileDir);
        chatClient = ChatClient.builder(dashscopeChatModel)
                .defaultSystem(SYSTEM_PROMPT)
                .defaultAdvisors(
                        new MessageChatMemoryAdvisor(chatMemory),
                        new MyLoggerAdvisor()
//                        , new ReReadingAdvisor()
                ).
                build();
    }

    public String doChat(String message, String chatId){
        ChatResponse chatResponse = chatClient.prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", text);
        return text;
    }

    record SchoolReport(String title, List<String> schoolName, List<String> schoolMajor, List<String> schoolAdmissionRate){

    }

    public SchoolReport doChatWithReportS(String message, String chatId){
        SchoolReport report = chatClient
                .prompt()
                .system(SYSTEM_PROMPT + "每次对话后都要生成选校报告，标题为{用户名}的选校报告，内容为选校列表以及申请专业和录取率")
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .call()
                .entity(SchoolReport.class);
        log.info("schoolReport: {}", report);
        return report;
    }

    @Resource
    private VectorStore loveAppVectorStore;

    @Resource
    private Advisor loveAppragCloudAdvisor;

    @Resource
    private VectorStore pgVectorVectorStore;

    public String doChatWithRAG(String message, String chatId){
        ChatResponse chatResponse = chatClient
                .prompt()
                .user(message)
                .advisors(spec -> spec.param(CHAT_MEMORY_CONVERSATION_ID_KEY, chatId)
                        .param(CHAT_MEMORY_RETRIEVE_SIZE_KEY, 10))
                .advisors(new MyLoggerAdvisor())
                .advisors(new QuestionAnswerAdvisor(loveAppVectorStore))
//                .advisors(loveAppragCloudAdvisor)
//                .advisors(new QuestionAnswerAdvisor(pgVectorVectorStore))
                .call()
                .chatResponse();
        String text = chatResponse.getResult().getOutput().getText();
        log.info("content: {}", text);
        return text;
    }
}