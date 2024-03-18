package edu.java.scrapper.migrations;

import edu.java.scrapper.IntegrationTest;
import java.util.List;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest
public class MigrationTest extends IntegrationTest {
    @Autowired
    private JdbcClient jdbcClient;

    @Test
    void shouldCheckThatAllTablesAreCreated() {
        List<Object> expected =
            List.of("databasechangelog", "databasechangeloglock", "chat", "tracked_link", "link", "question");
        List<Object> tableNames = jdbcClient.sql(
                """
                    SELECT table_name FROM information_schema.tables
                    WHERE table_schema='public'
                    AND table_catalog = current_database()
                    """
            )
            .query()
            .singleColumn();

        Assertions.assertThat(tableNames).containsExactlyInAnyOrderElementsOf(expected);
    }
}
