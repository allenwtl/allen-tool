package com.allen.tool.pdf;

import org.apache.commons.lang3.StringUtils;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PDFTool {

    //private static final String PDF_FILE = "C:\\Users\\Administrator\\Desktop\\pdf\\HS.pdf";

    //private static final String PDF_FILE = "/Users/allen/Documents/pdf/hunsun/HS.pdf";

    //private static final String TXT_FILE = "/Users/allen/Documents/pdf/hunsun/HS.txt";

    private static final String STOCK_FILE = "/Users/allen/Documents/pdf/hunsun/stock-file.txt";

    //private static final String ABSOLUTE_FILEPATH = "/Users/allen/Documents/app/";


    private static final String PDF_FILE = "."+File.separator+"HS.pdf";
    private static final String TXT_FILE = "."+File.separator+"HS.txt";
    private static final String ABSOLUTE_FILEPATH = "."+File.separator;

    public static final String endStr = "END OF REPORT";
    public static final String pageKey = "PAGE";


    public static void main(String[] args) {

        String content = getPdfTextContent();

        writePdfToTxt(content);

        List<String> stockFlagList = parseTxtFileAndCreateStockList();

        try {
            processCcassTextV3(stockFlagList);
        } catch (Exception e){

        }

    }

    public static String getPdfTextContent() {
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
        stripper.setWordSeparator("|");
        stripper.setStartPage(0);
        stripper.setEndPage(pages);
        String content = null;
        try {
            content = stripper.getText(pdDocument);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                pdDocument.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return content;
    }


    public static void writePdfToTxt(String pdfContent){
        if (StringUtils.isBlank(pdfContent) ){
            return;
        }

        BufferedWriter bufferedWriter = null ;
        try {
            bufferedWriter = new BufferedWriter(new FileWriter(TXT_FILE));

            bufferedWriter.write(pdfContent);


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                bufferedWriter.flush();
                bufferedWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }



    public static List<String> parseTxtFileAndCreateStockList (){
        List<String>  stockFlagList = new ArrayList<>();

        File txtFile = getTxtFile();
        BufferedReader reader = null ;
        try {
            reader = new BufferedReader(new FileReader(txtFile));
            String line = null;
            while ( (line = reader.readLine() ) != null ){

                char a = line.charAt(0);
                boolean isDigit = Character.isDigit(a);
                if  (!isDigit){
                    continue;
                }

                String  [] lines = line.split("\\|");
                if ( lines.length != 4 ){
                    continue;
                }


                String str = lines[2];

                str = str.replace(",", "");

                int sInt = Integer.parseInt(str);
                if ( sInt != 0 ) {
                    continue;
                }

                String stockStr = lines[0];
                if ( !stockStr.contains("-")){
                    continue;
                }

                String [] stockStrs = stockStr.split(" - ");

                String stockCode = stockStrs[0];

                Integer stockCodeInt = Integer.parseInt(stockCode);

                String stockFlag = StringUtils.deleteWhitespace(String.valueOf(stockCodeInt).concat(stockStrs[1]));
                stockFlagList.add(stockFlag);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return stockFlagList;

    }



    /**
     * 可分行
     * @throws IOException
     */
    public static void processCcassTextV3(final List<String> stockCodeList) throws IOException {


        String filePath = ABSOLUTE_FILEPATH+"ccass.txt";

        StringBuilder sb = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new FileReader(filePath));

        String line= "";
        String currentPage = "";

        List<String> pageRelatedStockList = new ArrayList<>();

        while ( true ){

            line = bufferedReader.readLine();
            String lineTemp = line;
            if ( StringUtils.isBlank(lineTemp) ){
                continue;
            }

            if( lineTemp.contains(endStr) ){
                break;
            }


            lineTemp = StringUtils.deleteWhitespace(lineTemp);

            if ( lineTemp.contains(pageKey) ){
                if ( !pageRelatedStockList.isEmpty() ) {
                    int lastIndex = currentPage.lastIndexOf(pageKey);
                    currentPage = currentPage.substring( lastIndex );
                    sb.append(currentPage).append("\n");
                    for (String stockLine : pageRelatedStockList){
                        sb.append(stockLine).append("\n");
                    }
                    sb.append("\n");
                    pageRelatedStockList = new ArrayList<>();
                }
                currentPage = line;
            } else {
                for ( String stock: stockCodeList ){
                    if ( lineTemp.contains(stock)){
                        pageRelatedStockList.add(line);
                    }
                }
            }


        }

        bufferedReader.close();


        String writeFile = ABSOLUTE_FILEPATH+"output.txt";
        BufferedWriter bufferedWriter  = new BufferedWriter(new FileWriter(writeFile));

        bufferedWriter.write(sb.toString());
        bufferedWriter.flush();
        bufferedWriter.close();


    }




    public static File getPdfFile(){

        return new File(PDF_FILE);
    }



    public static File getTxtFile(){
        return new  File(TXT_FILE);
    }


}
