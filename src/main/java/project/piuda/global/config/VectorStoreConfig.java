package project.piuda.global.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.ai.vectorstore.pgvector.PgVectorStore;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;

@Configuration
public class VectorStoreConfig implements DisposableBean {

    // DataSource 빈으로 노출하지 않음 → MySQL JPA 자동 구성과 충돌 방지
    // DisposableBean.destroy()로 앱 종료 시 연결 풀 정상 해제
    private HikariDataSource pgVectorDataSource;

    @Bean(name = "pgVectorJdbcTemplate")
    public JdbcTemplate pgVectorJdbcTemplate(
            @Value("${pgvector.datasource.url}") String url,
            @Value("${pgvector.datasource.username}") String username,
            @Value("${pgvector.datasource.password}") String password) {
        pgVectorDataSource = new HikariDataSource();
        pgVectorDataSource.setJdbcUrl(url);
        pgVectorDataSource.setUsername(username);
        pgVectorDataSource.setPassword(password);
        pgVectorDataSource.setDriverClassName("org.postgresql.Driver");
        return new JdbcTemplate(pgVectorDataSource);
    }

    @Bean
    public VectorStore vectorStore(
            @Qualifier("pgVectorJdbcTemplate") JdbcTemplate jdbcTemplate,
            EmbeddingModel embeddingModel) {
        return PgVectorStore.builder(jdbcTemplate, embeddingModel)
                .dimensions(1536)
                .initializeSchema(true)
                .build();
    }

    @Override
    public void destroy() {
        if (pgVectorDataSource != null && !pgVectorDataSource.isClosed()) {
            pgVectorDataSource.close();
        }
    }
}
