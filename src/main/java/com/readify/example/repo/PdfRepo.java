package com.readify.example.repo;

import com.readify.example.entity.Pdf;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PdfRepo extends JpaRepository<Pdf, Long> {
    Page<Pdf> findAllByIsActiveTrue(Pageable pageable);
}
