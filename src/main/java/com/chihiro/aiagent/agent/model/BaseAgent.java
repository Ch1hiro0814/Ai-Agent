package com.chihiro.aiagent.agent.model;

import cn.hutool.core.util.StrUtil;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

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

    public SseEmitter runStream(String userPrompt){
        SseEmitter sseEmitter = new SseEmitter(300000L);
        CompletableFuture.runAsync(() -> {
            try{
                if(this.state != AgentState.IDLE){
                    sseEmitter.send("Can not run agent from state: " + this.state);
                    sseEmitter.complete();
                    return;
                }
                if(StrUtil.isBlank(userPrompt)){
                    sseEmitter.send("User prompt can not be empty");
                    sseEmitter.complete();
                    return;
                }
            }catch (Exception e){
                sseEmitter.completeWithError(e);
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
                    sseEmitter.send(result);
                }
                if(currentStep >= maxStep){
                    this.state = AgentState.FINISHED;
                    results.add("Terminated: Reached the max steps (" + maxStep + ")");
                    sseEmitter.send("Terminated: Reached the max steps (" + maxStep + ")");
                }
                sseEmitter.complete();
            } catch (Exception e) {
                state = AgentState.ERROR;
                log.error( "Error running agent", e);
                try {
                    sseEmitter.send("Error running agent: " + e.getMessage());
                    sseEmitter.complete();
                } catch (IOException ex) {
                    sseEmitter.completeWithError(ex);
                }
//                sseEmitter.complete();
            } finally {
                this.cleanup();
            }
        });
        sseEmitter.onTimeout(() -> {
            this.state = AgentState.ERROR;
            this.cleanup();
            log.warn("SSE Connect Timeout");
        });
        sseEmitter.onCompletion(() -> {
            if(this.state == AgentState.RUNNING){
                this.state = AgentState.FINISHED;
            }
            this.cleanup();
            log.info("SSE Completed");
        });
        return sseEmitter;
    }

    public abstract String step();

    protected void cleanup(){

    }
}
