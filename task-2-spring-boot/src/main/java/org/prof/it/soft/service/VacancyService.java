package org.prof.it.soft.service;

import org.prof.it.soft.dto.filter.VacancyFilterDto;
import org.prof.it.soft.dto.request.RequestVacancyDto;
import org.prof.it.soft.dto.response.ResponseUploadingResultDto;
import org.prof.it.soft.dto.response.ResponseVacancyDto;
import org.prof.it.soft.entity.Vacancy;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * Service for working with vacancies
 *
 * @see org.prof.it.soft.entity.Vacancy
 * @see org.prof.it.soft.dto.request.RequestVacancyDto
 * @see org.prof.it.soft.dto.response.ResponseVacancyDto
 * @see org.prof.it.soft.dto.filter.VacancyFilterDto
 */
public interface VacancyService {

    /**
     * Saves a new vacancy.
     *
     * @param vacancyDto the DTO of the vacancy to save
     * @return the DTO of the saved vacancy
     */
    ResponseVacancyDto saveVacancy(RequestVacancyDto vacancyDto);

    /**
     * Updates an existing vacancy.
     *
     * @param vacancyId  the id of the vacancy to update
     * @param vacancyDto the DTO of the vacancy with the updated data
     * @throws org.prof.it.soft.exception.NotFoundException if the vacancy with the given id is not found
     */
    void updateVacancy(Long vacancyId, RequestVacancyDto vacancyDto);

    /**
     * Deletes a vacancy by id.
     *
     * @param id the id of the vacancy to delete
     * @throws org.prof.it.soft.exception.NotFoundException if the vacancy with the given id is not found
     */
    void deleteVacancy(Long id);

    /**
     * Gets a vacancy by id.
     *
     * @param vacancyId the id of the vacancy to get
     * @return the DTO of the vacancy with the given id
     * @throws org.prof.it.soft.exception.NotFoundException if the vacancy with the given id is not found
     */
    ResponseVacancyDto getResponseVacancyDtoById(Long vacancyId);

    /**
     * Gets a vacancy by id.
     *
     * @param id the id of the vacancy to get
     * @return the vacancy with the given id
     * @throws org.prof.it.soft.exception.NotFoundException if the vacancy with the given id is not found
     */
    Vacancy getVacancyById(Long id);

    /**
     * Gets the ids of all vacancies.
     *
     * @return a set of the ids of all vacancies
     */
    Page<ResponseVacancyDto> getFilteredVacancies(VacancyFilterDto vacancyFilterDto);

    /**
     * Generates a report in Excel format based on the specified filter.
     *
     * @param vacancyFilterDto the filter for generating the report
     * @return the report in Excel format
     */
    Resource generateReportExcel(VacancyFilterDto vacancyFilterDto);

    /**
     * Uploads vacancies from a JSON file to the database.
     *
     * @param file the JSON file with vacancies
     * @return the result of uploading vacancies
     * @throws org.prof.it.soft.exception.UploadingFileException if an error occurs while uploading the file
     */
    ResponseUploadingResultDto uploadVacanciesFromJsonFileToDatabase(MultipartFile file);
}
