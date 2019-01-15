package com.example.pdf.aspose.acroform;

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
import com.aspose.pdf.TextBoxField;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class UpdateFormFieldInformation {
	private static final String TEST_FILES_LOCATION = "src/main/resources/test/aspose/acroform";
	private static final String ACRO_PDF_INPUT_LOCATION = TEST_FILES_LOCATION + "/input/SampleAcroForm.pdf";
	private static final String ACRO_PDF_OUTPUT_LOCATION = TEST_FILES_LOCATION + "/output/SampleAcroForm.pdf";
	private static final String LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi feugiat viverra lorem sit amet ullamcorper. Nulla vehicula et eros et placerat. Nulla facilisi. Nullam sed metus at tellus gravida euismod at vitae elit. Phasellus vel condimentum est. Nam id porttitor ex. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Morbi elementum quam ac nisi euismod porttitor. Vestibulum tempus, turpis eu convallis pellentesque, diam eros mattis nunc, ac condimentum velit purus sed quam. Nullam placerat nunc sit amet nunc luctus maximus. Duis at bibendum tortor, dignissim ornare sapien. Etiam porta diam lorem, sed dictum est vulputate non";

	@Test
	public void updateAcroPdfInformationAndCheckOutputContent() throws IOException {

		// wrap to try to autoclose
		FileInputStream pdfInputStream = new FileInputStream(ACRO_PDF_INPUT_LOCATION);
		FileOutputStream pdfOutputStream = new FileOutputStream(ACRO_PDF_OUTPUT_LOCATION);
		Document pdfDocument = new Document(pdfInputStream);

		Field[] fields = pdfDocument.getForm().getFields();

		System.out.println("TOTAL: " + fields.length);
		Arrays.asList(fields).forEach(field -> {
			System.out.println("====UPDATING====");
			System.out.println("Name: " + field.getFullName());
			System.out.println("Value: " + field.getValue());
			System.out.println("Partial Name: " + field.getPartialName());

			// check field type
			if (field instanceof TextBoxField) {
				((TextBoxField) field).setValue("Hello World");
			} else if (field instanceof CheckboxField) {
				((CheckboxField) field).setChecked(true);
			}

		});

		pdfDocument.save(pdfOutputStream);

		// check saved pdf content
		Document outputDocument = new Document(ACRO_PDF_OUTPUT_LOCATION);

		Arrays.asList(outputDocument.getForm().getFields()).forEach(field -> {
			System.out.println("====RECHECK====");
			System.out.println("Name: " + field.getFullName());
			System.out.println("Value: " + field.getValue());
			System.out.println("Partial Name: " + field.getPartialName());

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
	
	@Test
	public void updateAcroPdfInformationAndCheckOutputContentWithLongText() throws IOException {

		// wrap to try to autoclose
		FileInputStream pdfInputStream = new FileInputStream(ACRO_PDF_INPUT_LOCATION);
		FileOutputStream pdfOutputStream = new FileOutputStream(ACRO_PDF_OUTPUT_LOCATION);
		Document pdfDocument = new Document(pdfInputStream);

		Field[] fields = pdfDocument.getForm().getFields();

		System.out.println("SIZE : " + fields.length);
		System.out.println("=============== BEFORE UPDATE ================");
		checkFieldInformation(fields);
		
		Arrays.asList(fields).forEach(field -> {
			// check field type
			if (field instanceof TextBoxField) {
				TextBoxField textBoxField = ((TextBoxField) field);
				textBoxField.setMultiline(true);
				textBoxField.setValue(LOREM);
			} else if (field instanceof CheckboxField) {
				((CheckboxField) field).setChecked(true);
			}

		});

		pdfDocument.save(pdfOutputStream);

		System.out.println();
		System.out.println();
		System.out.println("=============== AFTER UPDATE ================");
		// check saved pdf content
		Document outputDocument = new Document(ACRO_PDF_OUTPUT_LOCATION);

		checkFieldInformation(outputDocument.getForm().getFields());

	}	

	@Test
	public void updateAcroPdfInformationAndCheckIfExceedsContainer() throws IOException {

		// wrap to try to autoclose
		FileInputStream pdfInputStream = new FileInputStream(ACRO_PDF_INPUT_LOCATION);
		Document pdfDocument = new Document(pdfInputStream);

		Field[] fields = pdfDocument.getForm().getFields();
		
		System.out.println("=============== BEFORE UPDATE ================");
		checkFieldInformation(fields);
		
		// UPDATE
		Arrays.asList(fields).forEach(field -> {
			// check field type
			if (field instanceof TextBoxField) {
				TextBoxField textBoxField = ((TextBoxField) field);
				textBoxField.setMultiline(true);
				textBoxField.setValue("Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)Hello, I'm new here :)");
			} else if (field instanceof CheckboxField) {
				((CheckboxField) field).setValue("What will happen if i do this??");
			}
		});
		
		System.out.println();
		System.out.println();
		System.out.println("=============== AFTER UPDATE ================");
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
			System.out.println("Rectangle: " + field.getRectangle(false).getHeight());
			
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
