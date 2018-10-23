package com.example.pdf.service;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.aspose.pdf.Document;
import com.aspose.pdf.HtmlLoadOptions;
import com.aspose.pdf.HtmlSaveOptions;
import com.aspose.pdf.MarginInfo;
import com.aspose.pdf.PageSize;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

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

	@Test
	public void convertHTMLToPDFUsingAspose() throws FileNotFoundException {
		String htmlFileLocation = TEST_FILES_LOCATION + "/aspose/test.html";
		FileInputStream file = new FileInputStream(new File(htmlFileLocation));

		HtmlLoadOptions htmloptions = new HtmlLoadOptions();
		htmloptions.getPageInfo().setLandscape(false);
		htmloptions.getPageInfo().setHeight(PageSize.getA4().getHeight());
		htmloptions.getPageInfo().setWidth(PageSize.getA4().getWidth());
		htmloptions.getPageInfo().setMargin(new MarginInfo(10, 10, 10, 10));

		// Load HTML file
		Document doc = new Document(file, htmloptions);
		// Save HTML file
		doc.save(TEST_FILES_LOCATION + "/aspose/output/with_header_and_footer.pdf");
	}
	
	@Test
	public void convertHTMLToPDFWithHeaderAndFooterUsingAspose() throws FileNotFoundException {
		String bodyLocation = TEST_FILES_LOCATION + "/aspose/test.html";
		String headerLocation = TEST_FILES_LOCATION + "/aspose/template/header.html";
		String footerLocation = TEST_FILES_LOCATION + "/aspose/template/footer.html";
		
		FileInputStream fileBody = new FileInputStream(new File(bodyLocation));

		HtmlLoadOptions htmloptions = new HtmlLoadOptions();
		htmloptions.getPageInfo().setLandscape(false);
		htmloptions.getPageInfo().setHeight(PageSize.getA4().getHeight());
		htmloptions.getPageInfo().setWidth(PageSize.getA4().getWidth());
		htmloptions.getPageInfo().setMargin(new MarginInfo(10, 10, 10, 10));

		// Load HTML file
		Document doc = new Document(fileBody, htmloptions);
		// Save HTML file

//		doc.getPages().forEach(page -> {
//			page.
//		});
		
		doc.save(TEST_FILES_LOCATION + "/aspose/output/with_header_and_footer.pdf");
	}
	
	@Test
	public void prepopulateHTMLToPDFViaJSONUsingAspose() throws IOException {
		String htmlFileLocation = TEST_FILES_LOCATION + "/aspose/prepopulate/test.html";

		// ADD to beans
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache m = mf.compile(htmlFileLocation);
		Map<String, Object> context = new HashMap<>();
		context.put("name", "Helloxxx");
		context.put("lastname", "Doewee");
		
		File tmpFile = new File("tmp_file_" + System.currentTimeMillis() + "_" + Math.random() * 10000);
		FileWriter writer = new FileWriter(tmpFile);
		m.execute(writer, context).flush();
		
		HtmlLoadOptions htmloptions = new HtmlLoadOptions();
		htmloptions.getPageInfo().setLandscape(false);
		htmloptions.getPageInfo().setHeight(PageSize.getA4().getHeight());
		htmloptions.getPageInfo().setWidth(PageSize.getA4().getWidth());
		htmloptions.getPageInfo().setMargin(new MarginInfo(10, 10, 10, 10));
		
		Document doc = new Document(new FileInputStream(tmpFile), htmloptions);
		
		doc.save(TEST_FILES_LOCATION + "/aspose/prepopulate/test.pdf");
		
		tmpFile.delete();
	}

	@Test
	public void convertPDFToHTMLUsingAspose() throws FileNotFoundException {
		String inputFileLocation = TEST_FILES_LOCATION + "/aspose/sample_with_input.pdf";
		String outputFileLocation = TEST_FILES_LOCATION + "/aspose/output/sample_with_input.html";
		FileInputStream file = new FileInputStream(new File(inputFileLocation));

		Document pdfDocument = new Document(file);
		HtmlSaveOptions saveOptions = new HtmlSaveOptions();
		
		pdfDocument.save(outputFileLocation, saveOptions);
	}

}
