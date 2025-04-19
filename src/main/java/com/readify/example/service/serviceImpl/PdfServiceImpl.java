package com.readify.example.service.serviceImpl;

import com.readify.example.config.CommonApplicationProperties;
import com.readify.example.dto.PdfDTO;
import com.readify.example.dto.ResponseDTO;
import com.readify.example.entity.Pdf;
import com.readify.example.repo.PdfRepo;
import com.readify.example.service.PdfService;
import com.readify.example.utils.ImageUpload;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private CommonApplicationProperties commonApplicationProperties;
    @Autowired
    private PdfRepo pdfRepo;

    @Override
    public ResponseDTO savePdf(String name, String description, MultipartFile file) throws IOException {
        ResponseDTO responseDTO = new ResponseDTO();

        // Validate file is PDF and not empty
        if (file == null || file.isEmpty()) {
            responseDTO.setStatus("Failed");
            responseDTO.setMessage("File is required");
            return responseDTO;
        }

        if (!isPdfFile(file)) {
            responseDTO.setStatus("Failed");
            responseDTO.setMessage("Only PDF files are allowed");
            return responseDTO;
        }

        // Validate name and description
        if (name == null || name.trim().isEmpty()) {
            responseDTO.setStatus("Failed");
            responseDTO.setMessage("Name is required");
            return responseDTO;
        }

        // Save PDF and file
        Pdf pdf = savePdfFile(name, description, file);

        responseDTO.setStatus("Success");
        responseDTO.setMessage("PDF saved successfully");
        return responseDTO;
    }


    @Override
    public ResponseDTO getListPdf(Integer page, Integer pageSize) {
        ResponseDTO responseDTO = null;
        responseDTO = new ResponseDTO();
        Pageable pageable = PageRequest.of(page, pageSize);
        List<PdfDTO> pdfDTOList = new ArrayList<>();
        Page<Pdf> pdfList = pdfRepo.findAllByIsActiveTrue(pageable);
        pdfList.forEach(pdf -> {
            PdfDTO pdfDTO = new PdfDTO();
            pdfDTO.setId(pdf.getId());
            pdfDTO.setName(pdf.getName());
            pdfDTO.setDescription(pdf.getDescription());
            String path = commonApplicationProperties.getBaseUrl() + pdf.getId();
            pdfDTO.setPdf(path +"/"+ pdf.getPdf());
            pdfDTOList.add(pdfDTO);
        });

        responseDTO.setStatus("Success");
        responseDTO.setMessage("Pdf list fetched successfully");
        responseDTO.setList(pdfDTOList);
        responseDTO.setPage(pdfList.getNumber());
        responseDTO.setPageSize(pdfList.getSize());
        responseDTO.setTotalPages(pdfList.getTotalPages());
        responseDTO.setTotalElements(pdfList.getTotalElements());
        return responseDTO;
    }

    @Override
    public ResponseDTO editPdf(Long id, String name, String description, MultipartFile file) throws IOException {
        ResponseDTO responseDTO = new ResponseDTO();
        Pdf pdf = pdfRepo.findById(id).orElseThrow(() -> new RuntimeException("Id not found"));
        if(!pdf.getIsActive())
            throw new RuntimeException("PDF already deleted");

        // Validate name and description
        if (name != null && !name.trim().isEmpty()) {
            pdf.setName(name);
        }

        if (description != null && !description.trim().isEmpty()) {
            pdf.setDescription(description);
        }

        // If file is provided, process it
        if (file != null && !file.isEmpty()) {
            // Validate file is PDF
            if (!isPdfFile(file)) {
                responseDTO.setStatus("Failed");
                responseDTO.setMessage("Only PDF files are allowed");
                return responseDTO;
            }

            String renamedFile = handleFileSave(file);
            pdf.setPdf(renamedFile);

            // Construct the path to save the file
            String path = commonApplicationProperties.getAssetPath() + pdf.getId();
            log.info("Saving file to path: " + path);

            // Save the file using the ImageUpload utility
            ImageUpload.saveFile(file, path, renamedFile);
        }
        pdfRepo.save(pdf);
        responseDTO.setStatus("Success");
        responseDTO.setMessage("PDF updated successfully");
        return responseDTO;
    }

    @Override
    public ResponseDTO deletePdf(Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        Pdf pdf = pdfRepo.findById(id).orElseThrow(() -> new RuntimeException("Id not found"));
        pdf.setIsActive(false);
        pdfRepo.save(pdf);
        responseDTO.setStatus("Success");
        responseDTO.setMessage("Pdf deleted successfully");
        return responseDTO;
    }

    private Pdf savePdfFile(String name, String description, MultipartFile file) throws IOException {
        Pdf pdf = new Pdf();
        pdf.setName(name);
        pdf.setDescription(description);

        String renamedFile = handleFileSave(file);
        pdf.setPdf(renamedFile);

        // Save PDF data to DB
        pdf = pdfRepo.save(pdf);

        // Save file to disk
        String path = commonApplicationProperties.getAssetPath() + pdf.getId();
        log.info("Saving PDF to: " + path);
        ImageUpload.saveFile(file, path, renamedFile);

        return pdf;
    }

    private String handleFileSave(MultipartFile file) throws IOException {
        String originalFileName = file.getOriginalFilename();
        String extension = "";
        int i = originalFileName.lastIndexOf('.');
        if (i >= 0) {
            extension = originalFileName.substring(i); // includes the dot, e.g., ".pdf"
        }

        return System.currentTimeMillis() + extension;
    }

    private boolean isPdfFile(MultipartFile file) {
        String contentType = file.getContentType();
        String filename = file.getOriginalFilename();

        // Check MIME type and file extension
        return contentType != null && contentType.equalsIgnoreCase("application/pdf")
                && filename != null && filename.toLowerCase().endsWith(".pdf");
    }


}
