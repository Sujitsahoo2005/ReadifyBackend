package com.readify.example.service.serviceImpl;

import com.readify.example.config.CommonApplicationProperties;
import com.readify.example.dto.PdfDTO;
import com.readify.example.dto.ResponseDTO;
import com.readify.example.entity.Pdf;
import com.readify.example.enumaration.ErrorCode;
import com.readify.example.exception.BackendException;
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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Slf4j
@Service
public class PdfServiceImpl implements PdfService {

    @Autowired
    private CommonApplicationProperties commonApplicationProperties;
    @Autowired
    private PdfRepo pdfRepo;

    @Override
    public ResponseDTO savePdf(String name, String description, MultipartFile file, MultipartFile image, String author) throws IOException, BackendException {
        ResponseDTO responseDTO = new ResponseDTO();

        // Validate file is PDF and not empty
        if (file == null || file.isEmpty()) {
            throw new BackendException(ErrorCode.REQUEST_ERROR, "File is required");
        }

        if (!isPdfFile(file)) {
            throw new BackendException(ErrorCode.REQUEST_ERROR, "Only PDF files are allowed");
        }

        Pdf pdf = savePdfFile(name, description, file, image, author);

        responseDTO.setStatus("Success");
        responseDTO.setMessage("PDF saved successfully");
        return responseDTO;
    }


    @Override
    public ResponseDTO getListPdf(Integer page, Integer pageSize, String search) {
        ResponseDTO responseDTO = null;
        responseDTO = new ResponseDTO();
        Pageable pageable = PageRequest.of(page, pageSize);
        List<PdfDTO> pdfDTOList = new ArrayList<>();
        Page<Pdf> pdfList;
        if(search == null) {
            pdfList = pdfRepo.findAllByIsActiveTrue(pageable);
        } else{
            pdfList = pdfRepo.findByIsActiveTrueAndNameContainingIgnoreCase(search, pageable);
        }

        pdfList.forEach(pdf -> {
            PdfDTO pdfDTO = new PdfDTO();
            pdfDTO.setId(pdf.getId());
            pdfDTO.setName(pdf.getName());
            pdfDTO.setAuthor(pdf.getAuthor());
            String path = commonApplicationProperties.getBaseUrl() + pdf.getId();
            pdfDTO.setImage(path + "/" + pdf.getImage());
            pdfDTOList.add(pdfDTO);
        });

        responseDTO.setStatus("Success");
        responseDTO.setMessage("PDF list fetched successfully");
        responseDTO.setList(pdfDTOList);
        responseDTO.setPage(pdfList.getNumber());
        responseDTO.setPageSize(pdfList.getSize());
        responseDTO.setTotalPages(pdfList.getTotalPages());
        responseDTO.setTotalElements(pdfList.getTotalElements());
        return responseDTO;
    }

    @Override
    public ResponseDTO editPdf(Long id, String name, String description, MultipartFile file, MultipartFile image, String author) throws IOException, BackendException {
        ResponseDTO responseDTO = new ResponseDTO();
        Pdf pdf = pdfRepo.findById(id).orElseThrow(() -> new BackendException(ErrorCode.ENTITY_NOT_FOUND, "Id not found"));
        if (!pdf.getIsActive())
            throw new BackendException(ErrorCode.REQUEST_ERROR, "PDF already deleted");

        // Validate name and description
        if (name != null && !name.trim().isEmpty())
            pdf.setName(name);

        if (description != null && !description.trim().isEmpty())
            pdf.setDescription(description);

        if (author != null && !author.trim().isEmpty())
            pdf.setAuthor(author);

        // If file is provided, process it
        if (file != null && !file.isEmpty()) {
            // Validate file is PDF
            if (!isPdfFile(file)) {
                throw new BackendException(ErrorCode.REQUEST_ERROR, "Only PDF files are allowed");
            }

            String renamedFile = handleFileSave(file);
            pdf.setPdf(renamedFile);

            // Construct the path to save the file
            String path = commonApplicationProperties.getAssetPath() + pdf.getId();
            log.info("Saving file to path: " + path);

            // Save the file using the ImageUpload utility
            ImageUpload.saveFile(file, path, renamedFile);
        }
        if (image != null && !image.isEmpty()) {
            String renamedImage = handleFileSave(image);
            pdf.setImage(renamedImage);
            String imagePath = commonApplicationProperties.getAssetPath() + pdf.getId();
            ImageUpload.saveFile(image, imagePath, renamedImage);
        }

        pdfRepo.save(pdf);
        responseDTO.setStatus("Success");
        responseDTO.setMessage("PDF updated successfully");
        return responseDTO;
    }

    @Override
    public ResponseDTO deletePdf(Long id) throws BackendException {
        ResponseDTO responseDTO = new ResponseDTO();
        Pdf pdf = pdfRepo.findById(id).orElseThrow(() -> new BackendException(ErrorCode.ENTITY_NOT_FOUND, "Id not found"));
        pdf.setIsActive(false);
        pdfRepo.save(pdf);
        responseDTO.setStatus("Success");
        responseDTO.setMessage("PDF deleted successfully");
        return responseDTO;
    }

    @Override
    public ResponseDTO getDetailsPdf(Long id) {
        ResponseDTO responseDTO = new ResponseDTO();
        Optional<Pdf> pdf = pdfRepo.findByIdAndIsActiveTrue(id);
        PdfDTO pdfDTO = new PdfDTO();
        if (pdf.isPresent()) {
            Pdf pdf1 = pdf.get();
            pdfDTO.setName(pdf1.getName());
            pdfDTO.setDescription(pdf1.getDescription());
            pdfDTO.setAuthor(pdf1.getAuthor());
            String path = commonApplicationProperties.getBaseUrl() + pdf1.getId();
            pdfDTO.setImage(path + "/" + pdf1.getImage());
            pdfDTO.setPdf(path + "/" + pdf1.getPdf());
            responseDTO.setData(Optional.of(pdfDTO));
        }

        responseDTO.setStatus("Success");
        responseDTO.setMessage("PDF details fetched");
        return responseDTO;
    }

    private Pdf savePdfFile(String name, String description, MultipartFile file, MultipartFile image, String author) throws IOException {
        Pdf pdf = new Pdf();
        pdf.setName(name);
        pdf.setDescription(description);
        pdf.setAuthor(author);
        String renamedImage = handleFileSave(image);
        String renamedFile = handleFileSave(file);
        pdf.setImage(renamedImage);
        pdf.setPdf(renamedFile);

        // Save PDF data to DB
        pdf = pdfRepo.save(pdf);

        // Save file to disk
        String path = commonApplicationProperties.getAssetPath() + pdf.getId();
        log.info("Saving PDF to: " + path);
        ImageUpload.saveFile(file, path, renamedFile);
        ImageUpload.saveFile(image, path, renamedImage);
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
