package com.example.pdf.manualtest;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.PageSize;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfPage;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.canvas.PdfCanvas;
import com.itextpdf.layout.Canvas;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.licensekey.LicenseKey;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ItextCheckIfTextWillExceedForm {

	private static final String TEST_FILES_LOCATION = "src/main/resources/test/itext/acroform";
	private static final String ACRO_PDF_INPUT_LOCATION = TEST_FILES_LOCATION + "/input/AUQLDREPMT12NoneAcro.pdf";
	private static final String ACRO_PDF_OUTPUT_LOCATION = TEST_FILES_LOCATION + "/output/AUQLDREPMT12NoneAcro.pdf";
	private static final String ACRO_PDF_OUTPUT_LOCATION_WITH_CONTENT = TEST_FILES_LOCATION
			+ "/output/with-content-AUQLDREPMT12NoneAcro.pdf";

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

		Rectangle mainRectangle = new Rectangle(41, 1550, 100, 18);

		// add input text
		Rectangle sampleText1Rec = new Rectangle(mainRectangle);
		sampleText1Rec.setY(mainRectangle.getBottom());
		PdfTextFormField sampleText1 = PdfFormField.createText(pdfDoc, sampleText1Rec, "sampleText1", "");
		// set font
		// sampleText1.setFont(PdfFontFactory.createFont());
		// sampleText1.setFontSize(5);

		form.addField(sampleText1);

		// add input text
		Rectangle sampleText2Rec = new Rectangle(mainRectangle);
		sampleText2Rec.setY(sampleText1Rec.getBottom() + sampleText1Rec.getHeight());
		PdfTextFormField sampleText2 = PdfFormField.createText(pdfDoc, sampleText2Rec, "sampleText2", "");

		form.addField(sampleText2);

		document.close();
		pdfDoc.close();
	}

	// @Test
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

		pdfDoc.close();
	}

	// @Test
	public void getFontInformation() throws IOException {
		FileInputStream pdfInputStream = new FileInputStream(new File(ACRO_PDF_OUTPUT_LOCATION));
		FileOutputStream pdfOutputStream = new FileOutputStream(new File(ACRO_PDF_OUTPUT_LOCATION_WITH_CONTENT));

		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfInputStream), new PdfWriter(pdfOutputStream));
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);

		Map<String, PdfFormField> textFields = form.getFormFields().entrySet().stream()
				.filter(entry -> PdfName.Tx.equals(entry.getValue().getFormType())
						|| PdfName.Text.equals(entry.getValue().getFormType()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		textFields.forEach((key, textField) -> {

			System.out.println("KEY : " + key);
			// get font
			PdfFont font = getFont(pdfDoc, textField);
			// get font size
			float fontSize = getFontSize(textField);
			// get rectangle
			Rectangle rectangle = getRectangle(textField);
			// should have font
			assertNotNull(font);
			assertNotEquals(0, fontSize);
			assertNotNull(rectangle);
		});

		pdfDoc.close();
	}

	@Test
	public void addToAddendumIfTextExceed() throws IOException {
		FileInputStream pdfInputStream = new FileInputStream(new File(ACRO_PDF_OUTPUT_LOCATION));
		FileOutputStream pdfOutputStream = new FileOutputStream(new File(ACRO_PDF_OUTPUT_LOCATION_WITH_CONTENT));

		// stream based input & output
		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfInputStream), new PdfWriter(pdfOutputStream));
		Document document = new Document(pdfDoc);
		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
		form.setGenerateAppearance(true);

		Map<String, PdfFormField> textFields = form.getFormFields().entrySet().stream()
				.filter(entry -> PdfName.Tx.equals(entry.getValue().getFormType())
						|| PdfName.Text.equals(entry.getValue().getFormType()))
				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));

		// reference_id && actual text
		Map<Integer, String> addendum = new HashMap<>();
		textFields.forEach((name, textField) -> {

			// will get this from the mapping
			String text = "I'm the long and wind asdf";

			float originalFontSize = getFontSize(textField);
			PdfFont font = getFont(pdfDoc, textField);
			Rectangle rectangle = getRectangle(textField);

			boolean willExceed = willTextExceedContainer(font, originalFontSize, rectangle, text);

			// if exceed, try to apply logic for minifying text
			if (willExceed) {
				// will check if text will fit after 3 tries
				float fontSizeThatWillFit = getFontSizeThatWillFitTextContainer(font, originalFontSize, rectangle, text,
						3, 1);

				// change font size, so that it will fit
				if (fontSizeThatWillFit > 0) {
					textField.setFontSize(fontSizeThatWillFit);
				} else {

					// add to addendum
					// addendum.add(text);
					int addendumId = addendum.size() + 1;
					addendum.put(addendumId, text);

					// change text
					text = "addendum # " + addendumId;
				}
			}

			textField.setValue(text);

		});

		// add addendum
		if (addendum.size() > 0) {
			pdfDoc.addNewPage();
			document.add(new AreaBreak(AreaBreakType.LAST_PAGE));

			// add per text
			for (Entry<Integer, String> entry : addendum.entrySet()) {
				int id = entry.getKey();
				String content = entry.getValue();

				
				PdfFont boldFont = PdfFontFactory.createFont(FontConstants.HELVETICA_BOLD);
				Text label = new Text("Addendum # " + id + " : ").setFont(boldFont);
				Text value = new Text(content);

				Paragraph p = new Paragraph(label).add(new Text("\n")).add(value);
				document.add(p);
			}
		}

		document.close();
		pdfDoc.close();

	}

//	// @Test
//	public void addToAddendumIfTextExceedUsingCanvas() throws IOException {
//		FileInputStream pdfInputStream = new FileInputStream(new File(ACRO_PDF_OUTPUT_LOCATION));
//		FileOutputStream pdfOutputStream = new FileOutputStream(new File(ACRO_PDF_OUTPUT_LOCATION_WITH_CONTENT));
//
//		// stream based input & output
//		PdfDocument pdfDoc = new PdfDocument(new PdfReader(pdfInputStream), new PdfWriter(pdfOutputStream));
//		Document document = new Document(pdfDoc);
//		PdfAcroForm form = PdfAcroForm.getAcroForm(pdfDoc, true);
//		form.setGenerateAppearance(true);
//
//		Map<String, PdfFormField> textFields = form.getFormFields().entrySet().stream()
//				.filter(entry -> PdfName.Tx.equals(entry.getValue().getFormType())
//						|| PdfName.Text.equals(entry.getValue().getFormType()))
//				.collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
//
//		// reference_id && actual text
//		Map<Integer, String> addendum = new HashMap<>();
//		textFields.forEach((name, textField) -> {
//
//			// will get this from the mapping
//			String text = "I'm the long and wind asdf";
//
//			float originalFontSize = getFontSize(textField);
//			PdfFont font = getFont(pdfDoc, textField);
//			Rectangle rectangle = getRectangle(textField);
//
//			boolean willExceed = willTextExceedContainer(font, originalFontSize, rectangle, text);
//
//			// if exceed, try to apply logic for minifying text
//			if (willExceed) {
//				// will check if text will fit after 3 tries
//				float fontSizeThatWillFit = getFontSizeThatWillFitTextContainer(font, originalFontSize, rectangle, text,
//						3, 1);
//
//				// change font size, so that it will fit
//				if (fontSizeThatWillFit > 0) {
//					textField.setFontSize(fontSizeThatWillFit);
//				} else {
//
//					// add to addendum
//					// addendum.add(text);
//					int addendumId = addendum.size() + 1;
//					addendum.put(addendumId, text);
//
//					// change text
//					text = "addendum # " + addendumId;
//				}
//			}
//
//			textField.setValue(text);
//
//		});
//
//		// add addendum
//		if (addendum.size() > 0) {
//			// document.add(new AreaBreak(AreaBreakType.NEXT_PAGE));
//			// add new page
//
//			// create canvas for last page
//			PdfPage page = pdfDoc.addNewPage();
//			PdfCanvas pdfCanvas = new PdfCanvas(page);
//
//			PageSize pageSize = pdfDoc.getDefaultPageSize();
//
//			float margin = 36;
//			Rectangle baseRectangle = new Rectangle(margin, pageSize.getHeight() - margin,
//					pageSize.getWidth() - (margin * 2), 40);
//			for (Entry<Integer, String> entry : addendum.entrySet()) {
//				int id = entry.getKey();
//				String content = entry.getValue();
//
//				Text label = new Text("Addendum # " + id + " : " + content);
//				Text value = new Text(content);
//
//				Rectangle rectangle = new Rectangle(baseRectangle);
//				rectangle.setY(baseRectangle.getBottom() - baseRectangle.getHeight());
//
//				// add rectangle to canvas
//				pdfCanvas.rectangle(rectangle);
//
//				Canvas canvas = new Canvas(pdfCanvas, pdfDoc, rectangle);
//				canvas.add(new Paragraph(label).add(new Text("\n")).add(value));
//				canvas.close();
//
//				pdfCanvas.stroke();
//
//				baseRectangle = rectangle;
//			}
//
//		}
//
//		document.close();
//		pdfDoc.close();
//
//	}

	/**
	 * 
	 * @param document
	 * @param field
	 * @return
	 */
	private PdfFont getFont(PdfDocument document, PdfFormField field) {
		PdfFont font = field.getFont();
		if (font == null) {
			// set default
			System.out.println("No Local Font, will use documents font");
			font = document.getDefaultFont();
		}
		return font;
	}

	/**
	 * 
	 * @param field
	 * @return
	 */
	private float getFontSize(PdfFormField field) {
		PdfString defaultAppearance = field.getDefaultAppearance();
		float fontSize = 0;
		if (defaultAppearance != null) {
			String[] array = defaultAppearance.toString().split(" ");
			if (array.length > 2) {
				fontSize = Float.parseFloat(array[1]);
			}
		}
		return fontSize;
	}

	private Rectangle getRectangle(PdfFormField field) {
		return field.getWidgets().stream().findFirst().map(annotation -> annotation.getRectangle().toRectangle())
				.orElse(null);
	}

	/**
	 * 
	 * @param document
	 * @param field
	 * @param text
	 * @param fontSize
	 * @return
	 */
	// private boolean willTextExceedContainer(PdfDocument document, PdfFormField
	// field, String text, float fontSize) {
	// PdfFont font = getFont(document, field);
	// Rectangle rectangle = getRectangle(field);
	// return willTextExceedContainer(font, fontSize, rectangle, text);
	// }

	/**
	 * 
	 * @param font
	 * @param fontSize
	 * @param rectangle
	 * @param text
	 * @return
	 */
	private boolean willTextExceedContainer(PdfFont font, float fontSize, Rectangle rectangle, String text) {
		double expectedWidthOnField = font.getContentWidth(new PdfString(text)) * font.getFontMatrix()[0] * fontSize;
		double containerWidth = rectangle.getWidth();
		return expectedWidthOnField > containerWidth;
	}

	/**
	 * 
	 * @param document
	 * @param field
	 * @param text
	 * @param originalFontSize
	 * @param reduction
	 * @param textSizePerReduction
	 * @return
	 */
	private float getFontSizeThatWillFitTextContainer(PdfFont font, float fontSize, Rectangle rectangle, String text,
			int reduction, int textSizePerReduction) {
		boolean ableToFit = false;
		float adjustableFontSize = fontSize;
		do {
			reduction--;
			adjustableFontSize = adjustableFontSize - textSizePerReduction;
			ableToFit = !willTextExceedContainer(font, adjustableFontSize, rectangle, text);
		} while (ableToFit == false && reduction > 0);

		return ableToFit ? adjustableFontSize : 0;

	}
}
