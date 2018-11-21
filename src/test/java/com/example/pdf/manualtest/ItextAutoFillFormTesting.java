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
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.licensekey.LicenseKey;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ItextAutoFillFormTesting {

	private static final String TEST_FILES_LOCATION = "src/main/resources/test/itext/acroform";
	private static final String ACRO_PDF_INPUT_LOCATION = TEST_FILES_LOCATION + "/input/AUQLDREPMT12.pdf";
	private static final String ACRO_PDF_OUTPUT_LOCATION = TEST_FILES_LOCATION + "/output/populated-AUQLDREPMT12.pdf";

	@Before
	public void initialize() {
		LicenseKey.loadLicenseFile("src/main/resources/test/itext-key-for-test-only.xml");
	}

	@Test
	public void fillTextAcroPdfInformation() throws IOException {
		String pdfname = "AUQLDREPMT12.pdf";
		FileInputStream pdfInputStream = new FileInputStream(new File(ACRO_PDF_INPUT_LOCATION + "/" + pdfname));
		FileOutputStream pdfOutputStream = new FileOutputStream(new File(ACRO_PDF_OUTPUT_LOCATION + "/" + pdfname));

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
			textField.setValue("are you eato");
			System.out.println("OK");
		});

		pdfDoc.close();

	}

}
