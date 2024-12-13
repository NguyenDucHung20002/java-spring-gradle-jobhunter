package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import jakarta.validation.Valid;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Company;
import vn.hoidanit.jobhunter.domain.dto.ResultPaginationDTO;
import vn.hoidanit.jobhunter.service.CompanyService;

@RestController
@RequestMapping("api/v1")
public class CompanyController {
  private final CompanyService companyService;

  public CompanyController(CompanyService companyService) {
    this.companyService = companyService;
  }

  @PostMapping("/companies")
  public ResponseEntity<?> createCompany(@Valid @RequestBody Company reqCompany) {

    return ResponseEntity.status(HttpStatus.CREATED).body(this.companyService.handleCreateCompany(reqCompany));
  }

  @GetMapping("/companies")
  @RequestMapping("api/v1")
  public ResponseEntity<ResultPaginationDTO> getCompany(
          @Filter Specification<Company> spec, Pageable pageable) {

    return ResponseEntity.ok(this.companyService.handleGetCompany(spec, pageable));
  }

  @PutMapping("/companies")
  public ResponseEntity<Company> updateCompany(@Valid @RequestBody Company reqCompany) {
    Company updatedCompany = this.companyService.handleUpdateCompany(reqCompany);
    return ResponseEntity.ok(updatedCompany);
  }

  @DeleteMapping("/companies/{id}")
  public ResponseEntity<Void> deleteCompany(@PathVariable("id") long id) {
    this.companyService.handleDeleteCompany(id);
    return ResponseEntity.ok(null);
  }
}
