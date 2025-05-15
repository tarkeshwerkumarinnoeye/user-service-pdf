package com.tarkesh.learn.service;

import java.io.IOException;
import java.util.List;

import com.lowagie.text.DocumentException;
import com.tarkesh.learn.model.SearchCSVResponseDTO;
import com.tarkesh.learn.model.User;

public interface PdfService {
    public byte[] exportExternalUserInvoiceExportAsPDF(List<User> users, SearchCSVResponseDTO retSearchCSVResponseDTO) throws IOException, DocumentException;
}
