package com.example.pdf.manualtest;

import static org.junit.Assert.assertNotNull;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.junit4.SpringRunner;

import com.itextpdf.forms.PdfAcroForm;
import com.itextpdf.forms.fields.PdfFormField;
import com.itextpdf.io.font.FontConstants;
import com.itextpdf.kernel.font.PdfFont;
import com.itextpdf.kernel.font.PdfFontFactory;
import com.itextpdf.kernel.geom.Rectangle;
import com.itextpdf.kernel.pdf.PdfDocument;
import com.itextpdf.kernel.pdf.PdfName;
import com.itextpdf.kernel.pdf.PdfReader;
import com.itextpdf.kernel.pdf.PdfString;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.layout.Document;
import com.itextpdf.layout.element.AreaBreak;
import com.itextpdf.layout.element.Paragraph;
import com.itextpdf.layout.element.Text;
import com.itextpdf.layout.property.AreaBreakType;
import com.itextpdf.licensekey.LicenseKey;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.NONE)
public class ItextTestPrepopulateAcroFormWithAddendum {

	private static final String TEST_FILES_LOCATION = "src/main/resources/test/itext/acroform";
	private static final String ACRO_PDF_INPUT_LOCATION = TEST_FILES_LOCATION + "/input/AUQLDREPMT12.pdf";
	private static final String ACRO_PDF_OUTPUT_LOCATION = TEST_FILES_LOCATION
			+ "/output/with-content-AUQLDREPMT12.pdf";

	private static final String LOREM = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Morbi feugiat viverra lorem sit amet ullamcorper. Nulla vehicula et eros et placerat. Nulla facilisi. Nullam sed metus at tellus gravida euismod at vitae elit. Phasellus vel condimentum est. Nam id porttitor ex. Pellentesque habitant morbi tristique senectus et netus et malesuada fames ac turpis egestas. Morbi elementum quam ac nisi euismod porttitor. Vestibulum tempus, turpis eu convallis pellentesque, diam eros mattis nunc, ac condimentum velit purus sed quam. Nullam placerat nunc sit amet nunc luctus maximus. Duis at bibendum tortor, dignissim ornare sapien. Etiam porta diam lorem, sed dictum est vulputate non";

	@Before
	public void initialize() {
		LicenseKey.loadLicenseFile("src/main/resources/test/itext-key-for-test-only.xml");
	}

	@Test
	public void testRandomStringGenerator() {
		assertNotNull(generateRandomString());
	}

	@Test
	public void addToAddendumIfTextExceed() throws IOException {
		FileInputStream pdfInputStream = new FileInputStream(new File(ACRO_PDF_INPUT_LOCATION));
		FileOutputStream pdfOutputStream = new FileOutputStream(new File(ACRO_PDF_OUTPUT_LOCATION));

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
			String text = generateRandomString();
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
					
					// auto size field
					textField.setFontSizeAutoScale();
					
					// change text
					text = "addendum # " + addendumId;
				}
			}

			textField.setValue(text);
			textField.setReadOnly(true);

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
				// if empty, means its auto
				if (array[1].isEmpty()) {
					// currently, set default to 12
					fontSize = 12;
					// update font size for form field
					field.setFontSize(fontSize);
				} else {
					fontSize = Float.parseFloat(array[1]);
				}
			}
		}
		return fontSize;
	}

	/**
	 * 
	 * @param field
	 * @return
	 */
	private Rectangle getRectangle(PdfFormField field) {
		return field.getWidgets().stream().findFirst().map(annotation -> annotation.getRectangle().toRectangle())
				.orElse(null);
	}

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

	private int generateRandomIntWithRange(int min, int max) {
		return ThreadLocalRandom.current().nextInt(min, max + 1);
	}

	/**
	 * 
	 * @return
	 */
	private String generateRandomString() {
		// int leftLimit = 97; // letter 'a'
		// int rightLimit = 122; // letter 'z'
		//
		// // from 6 to 100
		// int targetStringLength = generateRandomIntWithRange(6, 150);
		// Random random = new Random();
		// StringBuilder buffer = new StringBuilder(targetStringLength);
		// for (int i = 0; i < targetStringLength; i++) {
		// int randomLimitedInt = leftLimit + (int) (random.nextFloat() * (rightLimit -
		// leftLimit + 1));
		// buffer.append((char) randomLimitedInt);
		// }
		// return buffer.toString();
		return LOREM.substring(0, generateRandomIntWithRange(6, 150));
	}
}
