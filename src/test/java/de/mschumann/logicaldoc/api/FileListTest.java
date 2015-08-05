package de.mschumann.logicaldoc.api;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "application-context.xml" })
public class FileListTest {

	@Autowired
	LogicalDocSession session;
	private long folderId = 3964952;

	@Test
	public void testGetFileNames() {
		List<String> names = session.getFileNames(folderId);
		assertNotNull(names);
		assertEquals(1, names.size());
		assertTrue(names.get(0).startsWith("Steckbrief"));
	}

	@Test
	public void upload() throws IOException, URISyntaxException {

		Path path = Paths.get(getClass().getResource("application-context.xml").toURI());
		byte[] data = Files.readAllBytes(path);

		Long result = session.upload(data, "test" + System.currentTimeMillis(),
				3964972);
		assertNotNull(result);
	}

}
