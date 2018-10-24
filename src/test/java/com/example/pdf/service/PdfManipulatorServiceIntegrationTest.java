package com.example.pdf.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class PdfManipulatorServiceIntegrationTest {

	private static final String TEST_FILES_LOCATION = "src/main/resources/test";

	@Autowired
	private PdfManipulatorService pdfManipulatorService;

	@Test
	public void testReadPdf() throws FileNotFoundException, IOException {
		File file = new File(TEST_FILES_LOCATION + "/fillable_example.pdf");
		pdfManipulatorService.test(new FileInputStream(file));
	}


}
