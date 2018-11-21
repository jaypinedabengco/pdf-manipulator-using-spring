package com.example.pdf.manualtest;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotEquals;

import java.io.File;
import java.io.FileInputStream;
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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.licensekey.LicenseKey;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ItextCheckFormInformation {

	private static final String TEST_FILES_LOCATION = "src/main/resources/test/itext/acroform";
	private static final String ACRO_PDF_INPUT_LOCATION = TEST_FILES_LOCATION + "/input/AUQLDREPMT12.pdf";

	@Before
	public void initialize() {
		LicenseKey.loadLicenseFile("src/main/resources/test/itext-key-for-test-only.xml");
	}

	@Test
	public void getAcroPdfInformation() throws IOException {
		FileInputStream pdfStream = new FileInputStream(new File(ACRO_PDF_INPUT_LOCATION));

		// stream based, so that it will be flexible
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfStream));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

		Map<String, PdfFormField> fields = form.getFormFields();

		fields.forEach((name, field) -> {
			System.out.println("=======");
			System.out.println("name : " + name);
			System.out.println("field Type : " + field.getFormType());
			System.out.println("kids count: " + (field.getKids() != null ? field.getKids().size() : null));
		});
	}

	@Test
	public void getSpecificExistingFieldByName() throws IOException {
		FileInputStream pdfStream = new FileInputStream(new File(ACRO_PDF_INPUT_LOCATION));

		// stream based, so that it will be flexible
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfStream));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
		PdfFormField field = form.getField("tenants.name.address.postcode");

		assertNotEquals(null, field);
	}

	@Test
	public void getSpecificExistingTextFieldByName() throws IOException {
		FileInputStream pdfStream = new FileInputStream(new File(ACRO_PDF_INPUT_LOCATION));

		// stream based, so that it will be flexible
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfStream));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
		PdfFormField field = form.getField("tenants.name.address.postcode");

		assertEquals(PdfName.Tx, field.getFormType());
	}

	@Test
	public void getTextOnlyAcroPdfInformation() throws IOException {
		FileInputStream pdfStream = new FileInputStream(new File(ACRO_PDF_INPUT_LOCATION));

		// stream based, so that it will be flexible
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfStream));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

		Map<String, PdfFormField> fields = form.getFormFields();

		Map<String, PdfFormField> textFields = fields.entrySet().stream()
				.filter(entry -> PdfName.Tx.equals(entry.getValue().getFormType())
						|| PdfName.Text.equals(entry.getValue().getFormType()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		textFields.forEach((a, f) -> {
			System.out.println(a + ":" + f.getFormType());
		});
	}

}
