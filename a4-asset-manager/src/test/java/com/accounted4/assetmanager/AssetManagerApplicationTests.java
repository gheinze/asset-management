package com.accounted4.assetmanager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.autoconfigure.jdbc.EmbeddedDataSourceConfiguration;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = {Application.class, EmbeddedDataSourceConfiguration.class})
@WebAppConfiguration
@ActiveProfiles("test")
public class AssetManagerApplicationTests {

	@Test
	public void contextLoads() {
	}

}
