package com.readify.example.service;

import com.readify.example.dto.ResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface PdfService {
    ResponseDTO savePdf(String name, String description, MultipartFile file, MultipartFile image, String author) throws IOException;

    ResponseDTO getListPdf(Integer page, Integer pageSize, String search);

    ResponseDTO editPdf(Long id, String name, String description, MultipartFile file, MultipartFile image, String author) throws IOException;

    ResponseDTO deletePdf(Long id);

    ResponseDTO getDetailsPdf(Long id);
}
