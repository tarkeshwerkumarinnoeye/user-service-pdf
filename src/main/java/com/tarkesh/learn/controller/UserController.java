package com.tarkesh.learn.controller;

import com.tarkesh.learn.model.SearchCSVResponseDTO;
import com.tarkesh.learn.model.User;
import com.tarkesh.learn.service.PdfService;
import com.tarkesh.learn.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.lowagie.text.DocumentException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/user/v1")
public class UserController {

    private final UserService userService;
    private final PdfService pdfService; // Added PdfService

    @Autowired
    public UserController(UserService userService, PdfService pdfService) { // Injected PdfService
        this.userService = userService;
        this.pdfService = pdfService; // Assigned PdfService
    }

    @GetMapping("/users")
    public List<User> getUsers(@RequestParam(value = "count", defaultValue = "10") int count) {
        return userService.getUsers(count);
    }

    // New PDF endpoint
    @GetMapping("/pdf")
    public ResponseEntity<byte[]> getUsersAsPdf(@RequestParam(value = "count", defaultValue = "10") int count) throws DocumentException, IOException {
        List<User> users = userService.getUsers(count);
        SearchCSVResponseDTO responseDTO = new SearchCSVResponseDTO(); // Create a dummy DTO for now
        byte[] pdfBytes = pdfService.exportExternalUserInvoiceExportAsPDF(users, responseDTO);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Suggest a filename for the browser
        String filename = "users.pdf";
        if ("application/octet-stream".equals(responseDTO.getFileType())) {
            filename = "users.zip";
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
        }
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }
}
