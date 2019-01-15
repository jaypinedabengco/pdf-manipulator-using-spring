package com.example.pdf.aspose.acroform;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.Arrays;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.aspose.pdf.CheckboxField;
import com.aspose.pdf.Document;
import com.aspose.pdf.Field;
import com.aspose.pdf.TextBoxField;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class GetAllFormFieldInformation {
	private static final String TEST_FILES_LOCATION = "src/main/resources/test/aspose/acroform";
	private static final String ACRO_PDF_INPUT_LOCATION = TEST_FILES_LOCATION + "/input/SampleAcroForm.pdf";
	
	@Test
	public void getAcroPdfInformation() {
		Document pdfDocument = new Document(ACRO_PDF_INPUT_LOCATION);
		
		Field[] fields = pdfDocument.getForm().getFields();
		
		checkFieldInformation(fields);
	}
	
	@Test
	public void getAcroPdfInformationFromStream() throws FileNotFoundException {
		FileInputStream pdfInputStream = new FileInputStream(ACRO_PDF_INPUT_LOCATION);
		Document pdfDocument = new Document(pdfInputStream);
		
		Field[] fields = pdfDocument.getForm().getFields();
		
		checkFieldInformation(fields);
	}
	
	private void checkFieldInformation(Field[] fields ) {
		Arrays.asList(fields).forEach(field -> {
			System.out.println("========");
			System.out.println("Name: " + field.getFullName());
			System.out.println("Value: " + field.getValue());
			System.out.println("Partial Name: " + field.getPartialName());
			System.out.println("Height: " + field.getHeight());
			System.out.println("Width: " + field.getWidth());
			System.out.println("Vertical Alignment: " + field.getVerticalAlignment());
			System.out.println("Max Font Size: " + field.getMaxFontSize());
			
			// check field type
			String formType = "I do not know what i am";
			if (field instanceof TextBoxField) {
				formType = "TextBoxField";
			} else if (field instanceof CheckboxField) {
				formType = "CheckboxField";
			}
			System.out.println("Field Type: " + formType);
		});
	}
}
