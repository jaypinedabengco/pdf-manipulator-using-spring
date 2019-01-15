package com.example.pdf.aspose.acroform;

import java.awt.Color;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.aspose.pdf.CheckboxField;
import com.aspose.pdf.Document;
import com.aspose.pdf.Field;
import com.aspose.pdf.Font;
import com.aspose.pdf.FontAbsorber;
import com.aspose.pdf.FontRepository;
import com.aspose.pdf.Page;
import com.aspose.pdf.PageCollection;
import com.aspose.pdf.Position;
import com.aspose.pdf.Rectangle;
import com.aspose.pdf.TextBoxField;
import com.aspose.pdf.TextBuilder;
import com.aspose.pdf.TextFragment;
import com.aspose.pdf.facades.EncodingType;
import com.aspose.pdf.facades.FontStyle;
import com.aspose.pdf.facades.FormattedText;
import com.aspose.pdf.facades.PdfFileMend;
import com.aspose.pdf.facades.WordWrapMode;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class CreateSquareFromTextField {
	private static final String TEST_FILES_LOCATION = "src/main/resources/test/aspose/acroform";
	private static final String ACRO_PDF_INPUT_LOCATION = TEST_FILES_LOCATION + "/input/SampleAcroForm.pdf";
	private static final String ACRO_PDF_OUTPUT_LOCATION = TEST_FILES_LOCATION + "/output/SampleAcroForm-text-to-square.pdf";
	private static final String LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi feugiat viverra lorem sit amet ullamcorper. Nulla vehicula et eros et placerat. Nulla facilisi. Nullam sed metus at tellus gravida euismod at vitae elit. Phasellus vel condimentum est. Nam id porttitor ex. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Morbi elementum quam ac nisi euismod porttitor. Vestibulum tempus, turpis eu convallis pellentesque, diam eros mattis nunc, ac condimentum velit purus sed quam. Nullam placerat nunc sit amet nunc luctus maximus. Duis at bibendum tortor, dignissim ornare sapien. Etiam porta diam lorem, sed dictum est vulputate non";
	
	@Test
	public void replaceInputToTextViaTextFragment() throws IOException {

		// wrap to try to autoclose
		FileInputStream pdfInputStream = new FileInputStream(ACRO_PDF_INPUT_LOCATION);
		FileOutputStream pdfOutputStream = new FileOutputStream(ACRO_PDF_OUTPUT_LOCATION);
		Document pdfDocument = new Document(pdfInputStream);
		PageCollection pages = pdfDocument.getPages();
		
		Field[] fields = pdfDocument.getForm().getFields();
		
		System.out.println("TOTAL: " + fields.length);
		Arrays.asList(fields).forEach(field -> {
			System.out.println("====UPDATING====");
			System.out.println("Name: " + field.getFullName());
			System.out.println("Value: " + field.getValue());
			System.out.println("Partial Name: " + field.getPartialName());
			
			// check field type
			if (field instanceof TextBoxField) {
				TextBoxField textBoxField = ((TextBoxField) field);
				
				
				// get page
				Page page = pages.get_Item(field.getPageIndex());
				
				// get textbox field position
				Rectangle rectangle = textBoxField.getRectangle(false);

				System.out.println("field WIDTH: " + field.getWidth());
				System.out.println("rectangle WIDTH: " + rectangle.getWidth());
				
				// setup font
				Font font = FontRepository.findFont("Arial");
				float fontSize = 12;
				String str = "sdgfdsfdfdasfsd fdf dasfds fds fdsf fdd asdfadsfdf fd fds ffsd";
				
				// check if will exceed
				double size = font.measureString(str, fontSize);
				System.out.println("Measure String:" + size);
				
				TextFragment textFragment = new TextFragment(str);
				textFragment.setPosition(new Position(rectangle.getLLX(), rectangle.getLLY()));
				
				textFragment.getTextState().setFontSize(fontSize);
				textFragment.getTextState().setFont(font);
				
				TextBuilder textBuilder = new TextBuilder(page);
				textBuilder.appendText(textFragment);
				
			} else if (field instanceof CheckboxField) {
				((CheckboxField) field).setChecked(true);
			}
		});

		pdfDocument.save(pdfOutputStream);

	}	
	
	@Test
	public void replaceInputToTextViaFormattedText() throws IOException {

		// wrap to try to autoclose
		FileInputStream pdfInputStream = new FileInputStream(ACRO_PDF_INPUT_LOCATION);
		FileOutputStream pdfOutputStream = new FileOutputStream(ACRO_PDF_OUTPUT_LOCATION);
		Document pdfDocument = new Document(pdfInputStream);
		PdfFileMend mender = new PdfFileMend();

		Field[] fields = pdfDocument.getForm().getFields();
		
		System.out.println("TOTAL: " + fields.length);
		Arrays.asList(fields).forEach(field -> {
			System.out.println("====UPDATING====");
			System.out.println("Name: " + field.getFullName());
			System.out.println("Value: " + field.getValue());
			System.out.println("Partial Name: " + field.getPartialName());
			
			// check field type
			if (field instanceof TextBoxField) {
				TextBoxField textBoxField = ((TextBoxField) field);
				
				// get textbox field position
				Rectangle rectangle = textBoxField.getRectangle(false);

				System.out.println("WIDTH: " + rectangle.getWidth());
				mender.bindPdf(pdfDocument);
				String str = "sdgfdsfdfdasfsd fdf dasfds fds fdsf fdd";
				FormattedText text = new FormattedText(str, Color.BLACK, Color.WHITE);
				
				
				mender.setWordWrap(true);
				mender.setWrapMode(WordWrapMode.Default);

				
				Font font = FontRepository.findFont("Arial");
				double size = font.measureString(str, 12);
				
				mender.addText(text, 1, (float) rectangle.getLLX(), (float) rectangle.getLLY(), (float) rectangle.getURX(), (float) rectangle.getURY());
				
			} else if (field instanceof CheckboxField) {
				((CheckboxField) field).setChecked(true);
			}
		});

		mender.save(pdfOutputStream);

	}
	
	@Test
	public void testTextByFont() {
		Font font = FontRepository.findFont("Arial");
		String str = "HELLO WORLD";
		double size = font.measureString(str, 12);
		System.out.println(size);
	}
}
