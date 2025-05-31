package com.chihiro.aiagent.tools;

import cn.hutool.core.io.FileUtil;
import com.chihiro.aiagent.constant.FileConstant;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

public class FileOperationTool {

    private final String FILE_DIR = FileConstant.FILE_SAVE_DIR +  "/file";

    @Tool(description = "Read content from a file")
    public String readFile(@ToolParam(description = "The name of the file to read") String FileName){
        String filePath = FILE_DIR + "/" + FileName;
        try{
            return FileUtil.readUtf8String(filePath);
        } catch (Exception e){
            return "Fail Read file: " + e.getMessage();
        }
    }

    @Tool(description = "Write content to a file")
    public String writeFile(@ToolParam(description = "The name of the file to write") String FileName,
                            @ToolParam(description = "The content to write to the file") String content){
        String filePath = FILE_DIR + "/" + FileName;
        try {
            FileUtil.mkdir(FILE_DIR);
            FileUtil .writeUtf8String(content, filePath);
            return "Success Write file to " + filePath;
        } catch (Exception e) {
            return  "Fail Write file: " + e.getMessage();
        }
    }
}
