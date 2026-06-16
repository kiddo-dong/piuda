package project.piuda.global.infrastructure;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.document.Document;
import org.springframework.ai.reader.JsonReader;
import org.springframework.ai.reader.pdf.PagePdfDocumentReader;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class KnowledgeLoader implements ApplicationRunner {

    private final VectorStore vectorStore;

    @Qualifier("pgVectorJdbcTemplate")
    private final JdbcTemplate pgVectorJdbcTemplate;

    @Value("${knowledge.reload-on-startup:false}")
    private boolean reloadOnStartup;

    @Value("${knowledge.pdf-start-page:5}")
    private int pdfStartPage;

    @Override
    public void run(ApplicationArguments args) {
        if (!reloadOnStartup && !isVectorStoreEmpty()) {
            log.info("[RAG] 지식 베이스가 이미 로드되어 있습니다. 재인덱싱을 건너뜁니다. (재로드하려면 knowledge.reload-on-startup=true)");
            return;
        }
        loadKnowledgeBase();
    }

    private boolean isVectorStoreEmpty() {
        try {
            Integer count = pgVectorJdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM vector_store", Integer.class);
            return count == null || count == 0;
        } catch (Exception e) {
            // 테이블이 아직 없으면 비어있는 것으로 간주
            return true;
        }
    }

    private void loadKnowledgeBase() {
        PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        List<Document> allDocuments = new ArrayList<>();

        loadPdfDocuments(resolver, allDocuments);
        loadJsonDocuments(resolver, allDocuments);

        if (allDocuments.isEmpty()) {
            log.warn("[RAG] src/main/resources/knowledge/ 에 PDF/JSON 파일이 없습니다. RAG 없이 동작합니다.");
            return;
        }

        if (reloadOnStartup) {
            pgVectorJdbcTemplate.execute("TRUNCATE vector_store");
            log.info("[RAG] 기존 벡터 스토어 초기화 완료");
        }

        // chunkSize=800, overlap=200 → 청크 경계 부분이 양쪽에 포함되어 검색 누락 방지
        TokenTextSplitter splitter = new TokenTextSplitter(800, 200, 5, 10000, true);
        List<Document> splitDocs = splitter.apply(allDocuments);

        vectorStore.add(splitDocs);
        log.info("[RAG] 지식 베이스 인덱싱 완료: {}개 문서 → {}개 청크", allDocuments.size(), splitDocs.size());
    }

    private void loadPdfDocuments(PathMatchingResourcePatternResolver resolver, List<Document> target) {
        try {
            for (Resource resource : resolver.getResources("classpath:knowledge/*.pdf")) {
                log.info("[RAG] PDF 로딩 중: {}", resource.getFilename());
                try {
                    List<Document> allDocs = new PagePdfDocumentReader(resource).get();
                    int totalPages = allDocs.stream()
                            .mapToInt(doc -> {
                                Object p = doc.getMetadata().get("page_number");
                                return p != null ? Integer.parseInt(p.toString()) : 1;
                            })
                            .max().orElse(0);

                    // 총 페이지가 pdfStartPage보다 적으면 표지/목차 없는 짧은 문서 → 전체 읽기
                    List<Document> docs = (totalPages >= pdfStartPage)
                            ? allDocs.stream()
                                .filter(doc -> {
                                    Object p = doc.getMetadata().get("page_number");
                                    return p == null || Integer.parseInt(p.toString()) >= pdfStartPage;
                                }).toList()
                            : allDocs;

                    docs.forEach(doc -> doc.getMetadata().put("source", resource.getFilename()));
                    log.info("[RAG]   └ {} → 전체 {}p 중 {}개 문서 추출", resource.getFilename(), totalPages, docs.size());
                    target.addAll(docs);
                } catch (Exception e) {
                    log.warn("[RAG] PDF 읽기 실패 - {}: {}", resource.getFilename(), e.getMessage());
                }
            }
        } catch (IOException e) {
            log.warn("[RAG] PDF 탐색 실패: {}", e.getMessage());
        }
    }

    private void loadJsonDocuments(PathMatchingResourcePatternResolver resolver, List<Document> target) {
        try {
            for (Resource resource : resolver.getResources("classpath:knowledge/*.json")) {
                log.info("[RAG] JSON 로딩 중: {}", resource.getFilename());
                try {
                    // JSON 배열에서 "content" 필드를 문서 본문으로, "title"을 메타데이터로 사용
                    // 형식: [{"title": "...", "content": "..."}]
                    List<Document> docs = new JsonReader(resource, "content").get();
                    docs.forEach(doc -> doc.getMetadata().put("source", resource.getFilename()));
                    target.addAll(docs);
                } catch (Exception e) {
                    log.warn("[RAG] JSON 읽기 실패 - {}: {}", resource.getFilename(), e.getMessage());
                }
            }
        } catch (IOException e) {
            log.warn("[RAG] JSON 탐색 실패: {}", e.getMessage());
        }
    }
}
