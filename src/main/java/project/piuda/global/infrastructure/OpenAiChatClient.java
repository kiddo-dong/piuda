package project.piuda.global.infrastructure;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.chat.completions.ChatCompletion;
import com.openai.models.chat.completions.ChatCompletionCreateParams;
import com.openai.models.chat.completions.ChatCompletionMessageParam;
import com.openai.models.chat.completions.ChatCompletionUserMessageParam;
import com.openai.models.chat.completions.ChatCompletionAssistantMessageParam;
import com.openai.models.chat.completions.ChatCompletionSystemMessageParam;
import com.openai.models.ChatModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import project.piuda.domain.careadvice.domain.CareAdviceMessage;
import project.piuda.domain.careadvice.domain.MessageRole;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
public class OpenAiChatClient {

    private static final String SYSTEM_PROMPT_BASE =
            "당신은 치매 환자 돌봄 전문 AI 상담사입니다. " +
            "보호자 또는 가족과 간병인이 환자의 돌발행동이나 돌봄 방법에 대해 질문하면, " +
            "즉시 실행 가능한 구체적인 방법을 한국어로 친절하고 간결하게 안내해주세요. " +
            "의학적 진단은 내리지 말고, 실제 돌봄 행동 지침에 집중해주세요. " +
            "답변은 3~5개의 핵심 방법으로 구성해주세요.";

    private final OpenAIClient client;

    public OpenAiChatClient(@Value("${openai.api-key}") String apiKey) {
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public String sendMessage(List<CareAdviceMessage> history, String newUserMessage, String patientContext) {
        List<ChatCompletionMessageParam> messages = new ArrayList<>();

        String systemPrompt = patientContext == null || patientContext.isBlank()
                ? SYSTEM_PROMPT_BASE
                : SYSTEM_PROMPT_BASE + "\n\n" + patientContext;

        messages.add(ChatCompletionMessageParam.ofSystem(
                ChatCompletionSystemMessageParam.builder()
                        .content(systemPrompt)
                        .build()
        ));

        for (CareAdviceMessage msg : history) {
            if (msg.getRole() == MessageRole.USER) {
                messages.add(ChatCompletionMessageParam.ofUser(
                        ChatCompletionUserMessageParam.builder()
                                .content(msg.getContent())
                                .build()
                ));
            } else {
                messages.add(ChatCompletionMessageParam.ofAssistant(
                        ChatCompletionAssistantMessageParam.builder()
                                .content(msg.getContent())
                                .build()
                ));
            }
        }

        messages.add(ChatCompletionMessageParam.ofUser(
                ChatCompletionUserMessageParam.builder()
                        .content(newUserMessage)
                        .build()
        ));

        ChatCompletion completion = client.chat().completions().create(
                ChatCompletionCreateParams.builder()
                        .model(ChatModel.GPT_4O_MINI)
                        .messages(messages)
                        .build()
        );

        return completion.choices().get(0).message().content().orElse("");
    }
}
