package project.piuda.global.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.Message;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Component;
import project.piuda.domain.careadvice.domain.CareAdviceMessage;
import project.piuda.domain.careadvice.domain.MessageRole;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class RagChatClient {

    private static final String SYSTEM_PROMPT_BASE =
            "당신은 치매 환자 돌봄 전문 AI 상담사입니다. " +
            "보호자 또는 가족과 간병인이 환자의 돌발행동이나 돌봄 방법에 대해 질문하면, " +
            "즉시 실행 가능한 구체적인 방법을 한국어로 친절하고 간결하게 안내해주세요. " +
            "의학적 진단은 내리지 말고, 실제 돌봄 행동 지침에 집중해주세요. " +
            "답변은 3~5개의 핵심 방법으로 구성해주세요.";

    private static final int TOP_K = 3;
    private static final double SIMILARITY_THRESHOLD = 0.6;

    private final ChatModel chatModel;
    private final VectorStore vectorStore;

    public String sendMessage(List<CareAdviceMessage> history, String newUserMessage, String patientContext) {
        List<Document> relevantDocs = retrieveRelevantDocuments(newUserMessage);

        String systemPrompt = buildSystemPrompt(patientContext, relevantDocs);
        List<Message> messages = buildMessageList(systemPrompt, history, newUserMessage);

        return chatModel.call(new Prompt(messages))
                .getResult()
                .getOutput()
                .getText();
    }

    private List<Document> retrieveRelevantDocuments(String query) {
        try {
            List<Document> docs = vectorStore.similaritySearch(
                    SearchRequest.builder()
                            .query(query)
                            .topK(TOP_K)
                            .similarityThreshold(SIMILARITY_THRESHOLD)
                            .build()
            );
            log.debug("[RAG] 검색된 관련 문서: {}개", docs.size());
            return docs;
        } catch (Exception e) {
            log.warn("[RAG] 벡터 검색 실패, RAG 없이 응답합니다: {}", e.getMessage());
            return List.of();
        }
    }

    private String buildSystemPrompt(String patientContext, List<Document> relevantDocs) {
        StringBuilder sb = new StringBuilder(SYSTEM_PROMPT_BASE);

        if (!relevantDocs.isEmpty()) {
            sb.append("\n\n[관련 전문 지식 - 치매 케어 가이드라인]:\n");
            for (Document doc : relevantDocs) {
                sb.append(doc.getText()).append("\n---\n");
            }
        }

        if (patientContext != null && !patientContext.isBlank()) {
            sb.append("\n\n").append(patientContext);
        }

        return sb.toString();
    }

    private List<Message> buildMessageList(String systemPrompt, List<CareAdviceMessage> history, String newUserMessage) {
        List<Message> messages = new ArrayList<>();
        messages.add(new SystemMessage(systemPrompt));

        for (CareAdviceMessage msg : history) {
            if (msg.getRole() == MessageRole.USER) {
                messages.add(new UserMessage(msg.getContent()));
            } else {
                messages.add(new AssistantMessage(msg.getContent()));
            }
        }
        messages.add(new UserMessage(newUserMessage));

        return messages;
    }
}
