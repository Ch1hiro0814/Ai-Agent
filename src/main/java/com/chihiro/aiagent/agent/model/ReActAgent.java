package com.chihiro.aiagent.agent.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.extern.slf4j.Slf4j;
@EqualsAndHashCode(callSuper = true)
@Data
@Slf4j
public abstract class ReActAgent extends BaseAgent{

    public abstract boolean think();

    public abstract String act();

    @Override
    public String step() {
        try {
            boolean should_act = think();
            if(!should_act){
                return "思考完成，无需执行";
            }
            return act();
        } catch (Exception e) {
            e.printStackTrace();
            return "步骤执行失败: " + e.getMessage();
        }
    }
}
