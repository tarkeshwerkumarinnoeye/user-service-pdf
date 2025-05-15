package com.tarkesh.learn.service.impl;

import com.lowagie.text.DocumentException;
import com.tarkesh.learn.config.CsvSearchConfig;
import com.tarkesh.learn.config.CwmImSearchMsConfig;
import com.tarkesh.learn.model.SearchCSVResponseDTO;
import com.tarkesh.learn.model.User;
import com.tarkesh.learn.repository.UserRepository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipInputStream;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.doThrow;
import org.mockito.Spy;


@ExtendWith(MockitoExtension.class)
class PdfServiceImplTest {

    @Mock
    private TemplateEngine templateEngine;

    @Mock
    private CwmImSearchMsConfig cwmImSearchMsConfig;

    @Mock
    private CsvSearchConfig csvSearchConfig;

    @Spy
    @InjectMocks
    private PdfServiceImpl pdfService;

    private List<User> users;
    private SearchCSVResponseDTO searchCSVResponseDTO;

    private UserServiceImpl userService = new UserServiceImpl(new UserRepository());

    @BeforeEach
    void setUp() {
        users = userService.getUsers(2);
        searchCSVResponseDTO = new SearchCSVResponseDTO();

        // Mock CsvSearchConfig behavior
        when(cwmImSearchMsConfig.getCsvSearchConfig()).thenReturn(csvSearchConfig);
    }

    @Test
    void testExportPdf_SingleFile_Success() throws IOException, DocumentException {
        // Given: Max records per file is greater than or equal to user list size
        when(csvSearchConfig.getMaxNumRecordsPerFile()).thenReturn(5);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html><body>Generated PDF Content</body></html>");
        
        // When
        byte[] result = pdfService.exportExternalUserInvoiceExportAsPDF(users, searchCSVResponseDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0, "PDF byte array should not be empty");
        assertEquals("application/pdf", searchCSVResponseDTO.getFileType());
        // Basic check that it's some kind of PDF content. 
        // A more robust check would involve a PDF parsing library if feasible.
        assertTrue(new String(result).contains("PDF"), "Resulting bytes should represent a PDF, check if ITextRenderer worked");
    }

    @Test
    void testExportPdf_ZipFile_Success() throws IOException, DocumentException {
        // Given: Max records per file is less than user list size
        when(csvSearchConfig.getMaxNumRecordsPerFile()).thenReturn(1); // Each user in a separate PDF
        // It's important that this path is writable and accessible during the test.
        // Using a subdirectory within target is standard for test outputs.
        // File tempZipDir = new File("target/test-output/zip");
        File tempZipDir = new File(getClass().getClassLoader().getResource("").getFile());
        String dummyZipPath = new File(tempZipDir, "test_users.zip").getAbsolutePath();
        when(csvSearchConfig.getZipFile()).thenReturn(dummyZipPath);

        when(templateEngine.process(anyString(), any(Context.class)))
            .thenReturn("<html><body>User 1 PDF Content</body></html>")
            .thenReturn("<html><body>User 2 PDF Content</body></html>");

        // When
        byte[] result = pdfService.exportExternalUserInvoiceExportAsPDF(users, searchCSVResponseDTO);

        // Then
        assertNotNull(result);
        assertTrue(result.length > 0, "ZIP byte array should not be empty");
        assertEquals("application/octet-stream", searchCSVResponseDTO.getFileType());

        // Verify zip content (basic check for number of entries)
        int entryCount = 0;
        try (ZipInputStream zis = new ZipInputStream(new ByteArrayInputStream(result))) {
            while (zis.getNextEntry() != null) {
                entryCount++;
            }
        }
        assertEquals(users.size(), entryCount, "Zip file should contain an entry for each user.");
        
        // Clean up the dummy zip file created by the service
        new File(dummyZipPath).delete();
    }

    @Test
    void testExportPdf_TemplateProcessingIOException() throws IOException, DocumentException {
        // Given: Max records per file is greater than or equal to user list size
        when(csvSearchConfig.getMaxNumRecordsPerFile()).thenReturn(5);
        when(templateEngine.process(anyString(), any(Context.class))).thenReturn("<html><body>Generated PDF Content</body></html>");

        doThrow(new IOException("Mocked IOException during template processing")).when(pdfService).renderPdf(anyString(), anyString());
        
        // When and Then
        assertThrows(IOException.class, () -> pdfService.exportExternalUserInvoiceExportAsPDF(users, searchCSVResponseDTO));
    }
}