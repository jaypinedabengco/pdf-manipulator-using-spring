package com.example.pdf.manualtest;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.forms.fields.PdfTextFormField;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.licensekey.LicenseKey;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ItextAddFormField {

	private static final String TEST_FILES_LOCATION = "src/main/resources/test/itext/acroform";
	private static final String ACRO_PDF_INPUT_LOCATION = TEST_FILES_LOCATION + "/input/AUQLDREPMT12NoneAcro.pdf";
	private static final String ACRO_PDF_OUTPUT_LOCATION = TEST_FILES_LOCATION + "/output/AUQLDREPMT12NoneAcro.pdf";
	private static final String ACRO_PDF_OUTPUT_LOCATION_WITH_CONTENT = TEST_FILES_LOCATION + "/output/with-content-AUQLDREPMT12NoneAcro.pdf";

	@Before
	public void initialize() {
		LicenseKey.loadLicenseFile("src/main/resources/test/itext-key-for-test-only.xml");
	}

	@Test
	public void addFieldToExistingPDF() throws IOException {
		FileInputStream pdfInputStream = new FileInputStream(new File(ACRO_PDF_INPUT_LOCATION));
		FileOutputStream pdfOutputStream = new FileOutputStream(new File(ACRO_PDF_OUTPUT_LOCATION));

		// stream based input & output
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfInputStream), new PdfWriter(pdfOutputStream));
		Document document = new Document(pdfDoc);
		
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
		
		// add text
		Paragraph p1 = new Paragraph("Hello worldxxxxxxxxxxxxxxxxxxxx");
		p1.setFixedPosition(41, 51, 100);
		document.add(p1);
		
		// add input text
		PdfTextFormField sampleText1 = PdfFormField.createText(pdfDoc, new Rectangle(41, 51, 100, 18), "sampleText1", "");
		
		form.addField(sampleText1);

		document.close();
		pdfDoc.close();
	}

	@Test
	public void getAcroPdfInformationForNewlyAddedField() throws IOException {
		FileInputStream pdfStream = new FileInputStream(new File(ACRO_PDF_OUTPUT_LOCATION));

		// stream based, so that it will be flexible
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfStream));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

		Map<String, PdfFormField> fields = form.getFormFields();

		fields.forEach((name, field) -> {
			System.out.println("=======");
			System.out.println("name : " + name);
			System.out.println("field Type : " + field.getFormType());
			System.out.println("kids count: " + field.getKids());
		});
	}

	@Test
	public void fillTextAcroPdfInformationForNewlyAddedField() throws IOException {
		FileInputStream pdfInputStream = new FileInputStream(new File(ACRO_PDF_OUTPUT_LOCATION));
		FileOutputStream pdfOutputStream = new FileOutputStream(
				new File(ACRO_PDF_OUTPUT_LOCATION_WITH_CONTENT));

		// stream based input & output
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfInputStream), new PdfWriter(pdfOutputStream));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
		form.setGenerateAppearance(true);

		Map<String, PdfFormField> textFields = form.getFormFields().entrySet().stream()
				.filter(entry -> PdfName.Tx.equals(entry.getValue().getFormType())
						|| PdfName.Text.equals(entry.getValue().getFormType()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		textFields.forEach((name, f) -> {
			System.out.println(name);
		});

		textFields.forEach((key, textField) -> {
			System.out.println("KEY : " + key);
			textField.setValue("are you eato long long long lon glon goonglonl g  o n");
			System.out.println("OK");
		});

		pdfDoc.close();

	}

}
