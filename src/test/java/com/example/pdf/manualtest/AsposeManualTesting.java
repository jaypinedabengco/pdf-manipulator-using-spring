package com.example.pdf.manualtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.aspose.pdf.Document;
import com.aspose.pdf.HeaderFooter;
import com.aspose.pdf.HtmlFragment;
import com.aspose.pdf.HtmlLoadOptions;
import com.aspose.pdf.MarginInfo;
import com.aspose.pdf.PageSize;
import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class AsposeManualTesting {
	private static final String TEST_FILES_LOCATION = "src/main/resources/test/aspose";

	@Test
	public void convertHTMLToPDFUsingAspose() throws FileNotFoundException {
		String htmlFileLocation = TEST_FILES_LOCATION + "/test.html";
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
	public void prepopulateHTMLToPDFViaJSONUsingAspose() throws IOException {

		String inputLocation = TEST_FILES_LOCATION + "/prepopulate/test.html";
		String outputLocation = TEST_FILES_LOCATION + "/output/prepopulate-test.pdf";

		// ADD to beans
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache m = mf.compile(inputLocation);
		Map<String, Object> context = new HashMap<>();

		context.put("name", "Red");
		context.put("lastname", "Dead");
		context.put("listOfItems", Arrays.asList("h1", "h2", "r1", "r2"));

		// mustache prepopulate
		File tmpFile = new File("tmp_file_" + System.currentTimeMillis() + "_" + Math.random() * 10000);
		FileWriter writer = new FileWriter(tmpFile);
		m.execute(writer, context).flush();

		long start = System.currentTimeMillis();

		// set options
		HtmlLoadOptions htmloptions = new HtmlLoadOptions();
		htmloptions.getPageInfo().setLandscape(false);
		htmloptions.getPageInfo().setHeight(PageSize.getA4().getHeight());
		htmloptions.getPageInfo().setWidth(PageSize.getA4().getWidth());
		htmloptions.getPageInfo().setMargin(new MarginInfo(10, 10, 10, 10));

		Document doc = new Document(new FileInputStream(tmpFile), htmloptions);

		// save
		doc.save(outputLocation);

		System.out.println("Build by " + (System.currentTimeMillis() - start) + "ms");

		// delete temporary file
		tmpFile.delete();
	}

	@Test
	public void convertHTMLToPDFWithHeaderAndFooterUsingAspose() throws IOException {
		String inputLocation = TEST_FILES_LOCATION + "/prepopulate/forMultipleText.html";
		String outputLocation = TEST_FILES_LOCATION + "/output/header-footer-test.pdf";
		String headerLocation = TEST_FILES_LOCATION + "/template/header.html";
		String footerLocation = TEST_FILES_LOCATION + "/template/footer.html";

		String headerHTMLString = new String(Files.readAllBytes(Paths.get(headerLocation)));
		String footerHTMLString = new String(Files.readAllBytes(Paths.get(footerLocation)));

		long start = System.currentTimeMillis();
		// ADD to beans
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache m = mf.compile(inputLocation);
		Map<String, Object> context = new HashMap<>();
		
		List<String> listOfItems = new ArrayList<>();
		
		for ( int i = 0; i <= 1000; i++ ) {
			listOfItems.add((Math.random() + 10000) + "-Random-Item");
		}

		context.put("name", "Red");
		context.put("lastname", "Dead");
		context.put("listOfItems", listOfItems);

		// mustache prepopulate
		File tmpFile = new File("tmp_file_" + System.currentTimeMillis() + "_" + Math.random() * 10000);
		FileWriter writer = new FileWriter(tmpFile);
		m.execute(writer, context).flush();

		System.out.println("[Mustache] :" + (System.currentTimeMillis() - start) + "ms");

		// restart count
		start = System.currentTimeMillis();

		// set options
		HtmlLoadOptions htmloptions = new HtmlLoadOptions();
		htmloptions.getPageInfo().setLandscape(true);
		htmloptions.getPageInfo().setHeight(PageSize.getA4().getHeight());
		htmloptions.getPageInfo().setWidth(PageSize.getA4().getWidth());
		htmloptions.getPageInfo().setMargin(new MarginInfo(10, 10, 10, 10));

		Document doc = new Document(new FileInputStream(tmpFile), htmloptions);

		/*
		 * ** add header && footer
		 */
		// initialize header
		HeaderFooter header = new HeaderFooter();
		header.getMargin().setLeft(10);
		header.getMargin().setTop(10);
		header.getMargin().setRight(10);
		header.getMargin().setBottom(10);
		header.getParagraphs().add(new HtmlFragment(headerHTMLString));

		// initialize footer
		HeaderFooter footer = new HeaderFooter();
		header.getParagraphs().add(new HtmlFragment(footerHTMLString));

		doc.getPages().forEach(page -> {
			page.setHeader(header);
			page.setFooter(footer);
		});

		// save
		doc.save(outputLocation);

		System.out.println("[Aspose] HTML to PDF : " + (System.currentTimeMillis() - start) + "ms");

		// delete temporary file
		tmpFile.delete();
	}

}
