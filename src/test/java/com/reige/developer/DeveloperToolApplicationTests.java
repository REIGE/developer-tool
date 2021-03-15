package com.reige.developer;

import com.reige.developer.common.mybatis.Page;
import com.reige.developer.module.base.mapper.TestMapper;
import org.apache.ibatis.io.Resources;
import org.apache.ibatis.jdbc.ScriptRunner;
import org.junit.Before;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import javax.annotation.Resource;
import javax.sql.DataSource;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = DeveloperToolApplication.class, webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class DeveloperToolApplicationTests {

	@Resource
	private DataSource dataSource;

	@Resource
	private TestMapper testMapper;

	@Before
	public void start() throws SQLException, IOException {
		ScriptRunner runner = new ScriptRunner(dataSource.getConnection());
		runner.setSendFullScript(true);
		runner.runScript(Resources.getResourceAsReader("database/init.sql"));
	}

	@Test
	public void contextLoads() throws IOException, SQLException {
		start();
		Page page = new Page();
		page.setCurrent(1);
		page.setSize(10);
		List<Map<String, Object>> list = testMapper.page(page);
		System.out.println(page);
	}
}
