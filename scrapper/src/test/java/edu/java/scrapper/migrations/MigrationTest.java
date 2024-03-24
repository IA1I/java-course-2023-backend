package edu.java.scrapper.migrations;

import edu.java.scrapper.IntegrationTest;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.simple.JdbcClient;

@SpringBootTest(properties = "app.access-type=jdbc")
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
                    WHERE table_schema = 'public'
                    AND table_catalog = current_database()
                    """
            )
            .query()
            .singleColumn();

        Assertions.assertThat(tableNames).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void shouldCheckAllCreatedFieldsInTableChat() {
        Map<String, Object> field1 = new HashMap<>();
        field1.put("column_name", "id");
        field1.put("data_type", "bigint");
        Map<String, Object> field2 = new HashMap<>();
        field2.put("column_name", "tg_chat_id");
        field2.put("data_type", "bigint");
        List<Map<String, Object>> expected = List.of(
            field1,
            field2
        );
        List<Map<String, Object>> actual = jdbcClient.sql(
                """
                    SELECT column_name, data_type FROM information_schema.columns
                    WHERE table_name ='chat'
                    """
            )
            .query()
            .listOfRows();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void shouldCheckAllCreatedFieldsInTableLink() {
        Map<String, Object> field1 = new HashMap<>();
        field1.put("column_name", "link_id");
        field1.put("data_type", "bigint");
        Map<String, Object> field2 = new HashMap<>();
        field2.put("column_name", "uri");
        field2.put("data_type", "text");
        Map<String, Object> field3 = new HashMap<>();
        field3.put("column_name", "updated_at");
        field3.put("data_type", "timestamp with time zone");
        Map<String, Object> field4 = new HashMap<>();
        field4.put("column_name", "last_check");
        field4.put("data_type", "timestamp with time zone");
        List<Map<String, Object>> expected = List.of(
            field1,
            field2,
            field3,
            field4
        );
        List<Map<String, Object>> actual = jdbcClient.sql(
                """
                    SELECT column_name, data_type FROM information_schema.columns
                    WHERE table_name ='link'
                    """
            )
            .query()
            .listOfRows();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void shouldCheckAllCreatedFieldsInTableQuestion() {
        Map<String, Object> field1 = new HashMap<>();
        field1.put("column_name", "link_id");
        field1.put("data_type", "bigint");
        Map<String, Object> field2 = new HashMap<>();
        field2.put("column_name", "comments_count");
        field2.put("data_type", "integer");
        Map<String, Object> field3 = new HashMap<>();
        field3.put("column_name", "answers_count");
        field3.put("data_type", "integer");
        List<Map<String, Object>> expected = List.of(
            field1,
            field2,
            field3
        );
        List<Map<String, Object>> actual = jdbcClient.sql(
                """
                    SELECT column_name, data_type FROM information_schema.columns
                    WHERE table_name ='question'
                    """
            )
            .query()
            .listOfRows();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    void shouldCheckAllCreatedFieldsInTableTrackedLink() {
        Map<String, Object> field1 = new HashMap<>();
        field1.put("column_name", "chat_id");
        field1.put("data_type", "bigint");
        Map<String, Object> field2 = new HashMap<>();
        field2.put("column_name", "link_id");
        field2.put("data_type", "bigint");
        List<Map<String, Object>> expected = List.of(
            field1,
            field2
        );
        List<Map<String, Object>> actual = jdbcClient.sql(
                """
                    SELECT column_name, data_type FROM information_schema.columns
                    WHERE table_name ='tracked_link'
                    """
            )
            .query()
            .listOfRows();

        Assertions.assertThat(actual).containsExactlyInAnyOrderElementsOf(expected);
    }
}
