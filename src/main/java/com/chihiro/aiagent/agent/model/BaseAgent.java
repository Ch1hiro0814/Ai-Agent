package com.chihiro.aiagent.agent.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;

import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public abstract class BaseAgent {
    private String name;

    private String systemPrompt;
    private String nextStepPrompt;

    private AgentState state =  AgentState.IDLE;

    private int currentStep = 0;
    private int maxStep = 10;

    private ChatClient chatClient;

    private List<Message> messageList = new ArrayList<>();

    public String run(String userPrompt){
        if(this.state != AgentState.IDLE){
            throw new RuntimeException("Can not run agent from state: " + this.state);
        }
        if(StrUtil.isBlank(userPrompt)){
            throw new RuntimeException("User prompt can not be empty");
        }
        this.state = AgentState.RUNNING;
        messageList.add(new UserMessage(userPrompt));
        List<String> results = new ArrayList<>();
        try {
            for(int i = 0; i < maxStep && state != AgentState.FINISHED; i++){
                int stepNumber = i + 1;
                currentStep = stepNumber;
                log.info("Executing step {}/{}", stepNumber, maxStep);
                String stepResult = step();
                String result = "Step " +  stepNumber + ": " + stepResult;
                results.add(result);
            }
            if(currentStep >= maxStep){
                this.state = AgentState.FINISHED;
                results.add("Terminated: Reached the max steps (" + maxStep + ")");
            }
            return String.join("\n", results);
        } catch (Exception e) {
            state = AgentState.ERROR;
            log.error( "Error running agent", e);
            return "Error running agent: " + e.getMessage();
        } finally {
            this.cleanup();
        }
    }

    public abstract String step();

    protected void cleanup(){

    }
}
