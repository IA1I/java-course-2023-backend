package edu.java.scrapper.migrations;

import edu.java.scrapper.IntegrationTest;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

public class MigrationTest extends IntegrationTest {
    @Test
    void should2plus2Equals4(){
        int expected = 4;
        int actual = 2+2;

        Assertions.assertThat(actual).isEqualTo(expected);
    }
}
