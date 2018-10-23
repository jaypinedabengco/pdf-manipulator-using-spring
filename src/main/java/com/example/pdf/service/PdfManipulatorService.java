package com.example.pdf.service;

import java.io.IOException;
import java.io.InputStream;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.interactive.form.PDAcroForm;
import org.springframework.stereotype.Service;

@Service
public class PdfManipulatorService {

	public void test(InputStream fileInputStream) throws IOException {
		try( PDDocument pdfDocument = PDDocument.load(fileInputStream) ) {
			
			PDAcroForm acroform = pdfDocument.getDocumentCatalog().getAcroForm();
			// may not have acroform
			if ( acroform != null ) {
				// read through all acroforms
				acroform.getFields().forEach(field -> {
					System.out.println( field.getFieldType() );
					System.out.print( field.getFullyQualifiedName() );
					System.out.print( " : ");
					System.out.println( field.getValueAsString());
					System.out.println("========");
				});
			} else {
				System.out.println("Not Acroform!");
			}
			
		} catch ( IOException e) {
			throw e;
		}
		
	}
	
}
