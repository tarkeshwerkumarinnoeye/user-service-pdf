package com.tarkesh.learn.service.impl;

import com.lowagie.text.DocumentException;
import com.tarkesh.learn.config.CwmImSearchMsConfig;
import com.tarkesh.learn.model.SearchCSVResponseDTO;
import com.tarkesh.learn.model.User;
import com.tarkesh.learn.service.PdfService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.xhtmlrenderer.pdf.ITextRenderer;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private TemplateEngine templateEngine;

    @Autowired
    CwmImSearchMsConfig cwmImSearchMsConfig;

    // @Autowired
    // public PdfServiceImpl(SpringTemplateEngine templateEngine) {
    // this.templateEngine = templateEngine;
    // }

    public File generatePdf(String templateName, Context context, String fileName)
            throws IOException, DocumentException {
        String html = loadAndFillTemplate(context, templateName);
        return renderPdf(html, templateName);
    }

    public <T> List<File> getPDFFiles(String templateName, String fileName, List<T> invoiceDTO)
            throws IOException, DocumentException {
        int numFiles = (int) Math.ceil((double) invoiceDTO.size() /
                (double) cwmImSearchMsConfig.getCsvSearchConfig().getMaxNumRecordsPerFile());
        List<File> pdfFiles = new ArrayList<>();
        for (int i = 0; i < numFiles; i++) {
            int startIndex = i * cwmImSearchMsConfig.getCsvSearchConfig().getMaxNumRecordsPerFile();
            int endIndex = Math.min(startIndex + cwmImSearchMsConfig.getCsvSearchConfig().getMaxNumRecordsPerFile(),
                    invoiceDTO.size());
            List<T> subList = invoiceDTO.subList(startIndex, endIndex);
            Context context = getContext("exportExternalInvoiceCSVDTOS", subList);
            File pdfFile = generatePdf(templateName, context, fileName + "-" + i);
            pdfFiles.add(pdfFile);
        }
        return pdfFiles;
    }

    @Override
    public byte[] exportExternalUserInvoiceExportAsPDF(List<User> exportExternalInvoiceCSVDTOS,
            SearchCSVResponseDTO retSearchCSVResponseDTO) throws IOException, DocumentException {
        String templateName = "external-user-invoice";
        String fileName = "external-user-invoice-pdf";
        if (exportExternalInvoiceCSVDTOS.size() <= cwmImSearchMsConfig.getCsvSearchConfig().getMaxNumRecordsPerFile()) {
            Context context = getContext("exportExternalInvoiceCSVDTOS", exportExternalInvoiceCSVDTOS);
            File pdfFile = generatePdf(templateName, context, fileName);
            retSearchCSVResponseDTO.setFileType("application/pdf");
            return Files.readAllBytes(pdfFile.toPath());
        } else {
            List<File> pdfFiles = getPDFFiles(templateName, fileName, exportExternalInvoiceCSVDTOS);
            retSearchCSVResponseDTO.setFileType("application/octet-stream");
            return createZipFile(pdfFiles);
        }
    }

    public File renderPdf(String html, String fileName) throws IOException, DocumentException {
        File file = File.createTempFile(fileName, ".pdf");

        try (OutputStream outputStream = new FileOutputStream(file)) {
            ITextRenderer renderer = new ITextRenderer(120f, 10);
            renderer.setDocumentFromString(html);

            renderer.layout();
            renderer.createPDF(outputStream);
            file.deleteOnExit();
            return file;
        }
    }

    public <T> Context getContext(String variableName, List<T> values) {
        Context context = new Context();
        context.setVariable(variableName, values);
        return context;
    }

    public String loadAndFillTemplate(Context context, String templateName) {
        return templateEngine.process(templateName, context);
    }

    /**
     * Method to create the Zip FIle from the List of PDF Files
     *
     * @param files - List of Files to Zip
     * @return - Zip file
     */
    public byte[] createZipFile(List<File> files) {
        byte[] retZip = null;
        try (FileOutputStream fileOutputStream = new FileOutputStream(
                cwmImSearchMsConfig.getCsvSearchConfig().getZipFile())) {
            ZipOutputStream zipOutputStream = new ZipOutputStream(fileOutputStream);

            for (File file : files) {
                File srcFile = new File(file.getPath());
                try (FileInputStream fileInputStream = new FileInputStream(srcFile)) {

                    // begin writing a new ZIP entry, positions the stream to the start of the entry
                    // data
                    zipOutputStream.putNextEntry(new ZipEntry(srcFile.getName()));

                    // write input file to zipfile
                    zipOutputStream.write(fileInputStream.readAllBytes());

                    zipOutputStream.closeEntry();
                }
            }
            // close the ZipOutputStream
            zipOutputStream.close();

            retZip = Files.readAllBytes(Paths.get(cwmImSearchMsConfig.getCsvSearchConfig().getZipFile()));

        } catch (Exception e) {
            throw new RuntimeException(e.getMessage());
        }
        return retZip;
    }

}