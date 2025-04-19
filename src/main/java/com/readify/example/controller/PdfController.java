package com.readify.example.controller;

import com.readify.example.dto.ResponseDTO;
import com.readify.example.service.PdfService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("api/v1")
public class PdfController {

    @Autowired
    private PdfService service;

    @PostMapping("save-pdf")
    public ResponseEntity<ResponseDTO> savePdf(@RequestParam("name") String name,
                                               @RequestParam("description") String description,
                                               @RequestParam("file") MultipartFile file) throws IOException {
        ResponseDTO responseDTO = service.savePdf(name, description, file);
        return ResponseEntity.ok(responseDTO);
    }

    @GetMapping("get-list-pdf")
    public ResponseEntity<ResponseDTO> getListPdf(@RequestParam(value = "page", defaultValue = "0") Integer page,
                                                  @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize) throws IOException {
        ResponseDTO responseDTO = service.getListPdf(page, pageSize);
        return ResponseEntity.ok(responseDTO);
    }

    @PutMapping("edit-pdf")
    public ResponseEntity<ResponseDTO> editPdf(@RequestParam(value = "id") Long id,
                                               @RequestParam(value = "name", required = false) String name,
                                               @RequestParam(value = "description", required = false) String description,
                                               @RequestParam(value = "file", required = false) MultipartFile file) throws IOException {
        ResponseDTO responseDTO = service.editPdf(id, name, description, file);
        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("delete-pdf")
    public ResponseEntity<ResponseDTO> editPdf(@RequestParam(value = "id") Long id) throws IOException {
        ResponseDTO responseDTO = service.deletePdf(id);
        return ResponseEntity.ok(responseDTO);
    }
}