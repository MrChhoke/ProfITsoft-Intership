package org.prof.it.soft.controller;

import lombok.RequiredArgsConstructor;
import org.prof.it.soft.dto.filter.VacancyFilterDto;
import org.prof.it.soft.dto.request.RequestVacancyDto;
import org.prof.it.soft.dto.response.ResponseUploadingResultDto;
import org.prof.it.soft.dto.response.ResponseVacancyDto;
import org.prof.it.soft.service.VacancyService;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;

@RestController
@RequestMapping("/api/v1/vacancy")
@RequiredArgsConstructor
public class VacancyController {

    /**
     * Service for handling operations related to vacancies.
     */
    protected final VacancyService vacancyService;

    /**
     * Get a vacancy by id.
     *
     * @param id The id of the vacancy.
     * @return The vacancy with the given id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseVacancyDto> getVacancyById(@PathVariable Long id) {
        return ResponseEntity.ok(vacancyService.getResponseVacancyDtoById(id));
    }

    /**
     * Save a new vacancy.
     *
     * @param responseVacancyDto The data of the vacancy to be saved.
     * @return The saved vacancy.
     */
    @PostMapping
    public ResponseEntity<ResponseVacancyDto> saveVacancy(@Validated(RequestVacancyDto.Save.class) @RequestBody RequestVacancyDto responseVacancyDto) {
        return ResponseEntity.ok(vacancyService.saveVacancy(responseVacancyDto));
    }

    /**
     * Update a vacancy.
     *
     * @param responseVacancyDto The new data of the vacancy.
     * @param id                 The id of the vacancy to be updated.
     * @return A message indicating that the vacancy was updated successfully.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateVacancy(@Validated(RequestVacancyDto.Update.class) @RequestBody RequestVacancyDto responseVacancyDto,
                                                @PathVariable("id") Long id) {
        vacancyService.updateVacancy(id, responseVacancyDto);
        return ResponseEntity.ok("Vacancy updated successfully");
    }

    /**
     * Delete a vacancy.
     *
     * @param id The id of the vacancy to be deleted.
     * @return A message indicating that the vacancy was deleted successfully.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteVacancy(@PathVariable Long id) {
        vacancyService.deleteVacancy(id);
        return ResponseEntity.ok("Vacancy deleted successfully");
    }

    /**
     * Get filtered vacancies.
     *
     * @param vacancyFilterDto The filter criteria.
     * @return The vacancies that match the filter criteria.
     */
    @PostMapping(value = "_list", consumes = "application/json")
    public ResponseEntity<Page<ResponseVacancyDto>> getFilteredVacancies(@Validated(VacancyFilterDto.JsonResponse.class) @RequestBody VacancyFilterDto vacancyFilterDto) {
        return ResponseEntity.ok(vacancyService.getFilteredVacancies(vacancyFilterDto));
    }

    /**
     * Generate a report in Excel format.
     *
     * @param vacancyFilterDto The filter criteria.
     * @return The report in Excel format.
     * @throws IOException If an I/O error occurs.
     */
    @PostMapping(value = "_report", consumes = "application/json")
    public ResponseEntity<Resource> generateReportExcel(@Validated @RequestBody(required = false) VacancyFilterDto vacancyFilterDto) throws IOException {
        HttpHeaders headers = new HttpHeaders();
        headers.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=vacancies_" + LocalDateTime.now() + ".xlsx");

        Resource resource = vacancyService.generateReportExcel(vacancyFilterDto);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(resource.contentLength())
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .body(resource);
    }

    /**
     * Upload vacancies from a JSON file.
     *
     * @param file The JSON file containing the vacancies.
     * @return The result of the uploading process.
     */
    @PostMapping(value = "/upload")
    public ResponseEntity<ResponseUploadingResultDto> uploadVacanciesJsonFile(@RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(vacancyService.uploadVacanciesFromJsonFileToDatabase(file));
    }
}