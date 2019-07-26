package com.allen.tool.pdf;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.File;
import java.io.IOException;

public class PDFTool {

    private static final String PDF_FILE = "C:\\Users\\Administrator\\Desktop\\pdf\\HS.pdf";


    public static void main(String[] args) {

        File localPdfFile = getPdfFile();

        PDDocument pdDocument = null;
        try {
            pdDocument = PDDocument.load(localPdfFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

        int pages = pdDocument.getNumberOfPages();


        PDFTextStripper stripper= null;
        try {
            stripper = new PDFTextStripper();
        } catch (IOException e) {
            e.printStackTrace();
        }
        stripper.setSortByPosition(true);
        stripper.setStartPage(1);
        stripper.setEndPage(pages);
        String content = null;
        try {
            content = stripper.getText(pdDocument);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println(content);


    }


    public static File getPdfFile(){

        return new File(PDF_FILE);
    }



}
