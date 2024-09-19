package dev.dishoo.triptask;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@EnableAutoConfiguration(exclude = { DataSourceAutoConfiguration.class })
class TriptaskApplicationTests {

	@Test
	void contextLoads() {
	}

}
