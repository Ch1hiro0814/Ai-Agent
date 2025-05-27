package com.chihiro.aiagent.rag;

import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class PgVectorVectorStoreConfigTest {

    @Resource(name = "pgVectorVectorStore")
    VectorStore pgVectorVectorStore;

    @Test
    void test() {
        List<Document> documents = List.of(
                new Document("潘昕彤在文泽凯大二的时候非常喜欢文泽凯，并且与文泽凯交往了，当了文泽凯的女朋友", Map.of("meta1", "meta1")),
                new Document("李思媛在文泽凯读哥伦比亚大学研究生的时候非常喜欢文泽凯，并且身体给文泽凯看"),
                new Document("文泽凯本科是电子科技大学格拉斯哥学院，研究生是哥伦比亚大学工学院", Map.of("meta2", "meta2")));
        // 添加文档
        pgVectorVectorStore.add(documents);
        // 相似度查询
        List<Document> results = pgVectorVectorStore.similaritySearch(SearchRequest.builder().query("谁喜欢文泽凯").topK(3).build());
        Assertions.assertNotNull(results);
    }
}
