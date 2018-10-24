package com.example.pdf.manualtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

import org.dom4j.DocumentException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.github.mustachejava.DefaultMustacheFactory;
import com.github.mustachejava.Mustache;
import com.github.mustachejava.MustacheFactory;
import com.itextpdf.html2pdf.ConverterProperties;
import com.itextpdf.html2pdf.HtmlConverter;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ItextManualTesting {
	private static final String TEST_FILES_LOCATION = "src/main/resources/test/itext";

	@Test
	public void simpleWrite() throws FileNotFoundException {
		String outputLocation = TEST_FILES_LOCATION + "/output/simple.pdf";
		PdfWriter writer = new PdfWriter(outputLocation);
		PdfDocument pdf = new PdfDocument(writer);
		Document document = new Document(pdf);
		document.add(new Paragraph("Hello World!"));
		document.close();
	}
	
	@Test
	public void prepopulateHTMLToPDFViaJSONUsingItext() throws IOException, DocumentException {
		long start = System.currentTimeMillis();
		String inputLocation = TEST_FILES_LOCATION + "/prepopulate/test.html";
		String outputLocation = TEST_FILES_LOCATION + "/output/prepopulate-test.pdf";

		// Mustache (for prepopulate)
		// ADD to beans
		MustacheFactory mf = new DefaultMustacheFactory();
		Mustache m = mf.compile(inputLocation);
		Map<String, Object> context = new HashMap<>();
		context.put("name", "REDdxxx");
		context.put("lastname", "Dxxxs");

		StringWriter writer = new StringWriter();
		m.execute(writer, context).flush();

		// convert from html to pdf
		ConverterProperties converterProperties = new ConverterProperties();
		HtmlConverter.convertToPdf(new FileInputStream(new File(inputLocation)), new FileOutputStream(new File(outputLocation)), converterProperties);

		// end
		System.out.println("Build by " + (System.currentTimeMillis() - start) + "ms");
	}


}
