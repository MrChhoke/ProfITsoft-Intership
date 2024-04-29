package org.prof.it.soft.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.modelmapper.ModelMapper;
import org.prof.it.soft.dto.filter.VacancyFilterDto;
import org.prof.it.soft.dto.request.RequestVacancyDto;
import org.prof.it.soft.dto.response.ResponseUploadingResultDto;
import org.prof.it.soft.dto.response.ResponseVacancyDto;
import org.prof.it.soft.entity.Recruiter;
import org.prof.it.soft.entity.Vacancy;
import org.prof.it.soft.exception.NotFoundException;
import org.prof.it.soft.exception.UploadingFileException;
import org.prof.it.soft.parser.VacancyJsonParser;
import org.prof.it.soft.repo.VacancyRepository;
import org.prof.it.soft.service.RecruiterService;
import org.prof.it.soft.service.VacancyService;
import org.prof.it.soft.spec.VacancySpecification;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Set;

@Service
@Slf4j
@Transactional
@RequiredArgsConstructor
public class VacancyServiceImpl implements VacancyService {

    /**
     * The repository for the Vacancy entity.
     */
    protected final VacancyRepository vacancyRepository;

    /**
     * The service for the Recruiter entity.
     */
    protected final RecruiterService recruiterService;

    /**
     * The parser for parsing vacancies from JSON.
     */
    protected final VacancyJsonParser vacancyJsonParser;

    /**
     * The mapper for converting between DTOs and entities.
     */
    protected final ModelMapper modelMapper;

    /**
     * Saves a new vacancy.
     *
     * @param vacancyDto the DTO of the vacancy to save
     * @return the DTO of the saved vacancy
     * @throws NotFoundException if the recruiter with the given id is not found
     */
    @Override
    public ResponseVacancyDto saveVacancy(RequestVacancyDto vacancyDto) {
        Vacancy vacancyFromDto = modelMapper.map(vacancyDto, Vacancy.class);
        Recruiter recruiterById = recruiterService.getRecruiterById(vacancyDto.getRecruiterId());
        recruiterById.addVacancy(vacancyFromDto);

        Vacancy savedVacancy = vacancyRepository.saveAndFlush(vacancyFromDto);
        log.info("Vacancy[id={}, position={}, recruiterId={}] saved successfully",
                savedVacancy.getId(), savedVacancy.getPosition(), savedVacancy.getRecruiter().getId());
        return modelMapper.map(savedVacancy, ResponseVacancyDto.class);
    }

    /**
     * Updates an existing vacancy.
     *
     * @param vacancyId  the id of the vacancy to update
     * @param vacancyDto the DTO of the vacancy with the updated data
     * @throws NotFoundException if the vacancy with the given id is not found
     */
    @Override
    public void updateVacancy(Long vacancyId, RequestVacancyDto vacancyDto) {
        Vacancy vacancyFromDto = modelMapper.map(vacancyDto, Vacancy.class);
        Vacancy vacancy = getVacancyById(vacancyId);

        vacancy.setPosition(vacancyFromDto.getPosition());
        vacancy.setSalary(vacancyFromDto.getSalary());
        vacancy.setTechnologyStack(vacancyFromDto.getTechnologyStack());
        vacancy.setRecruiter(recruiterService.getRecruiterById(vacancyDto.getRecruiterId()));

        vacancyRepository.saveAndFlush(vacancy);
        log.info("Vacancy[id={}, position={}, recruiterId={}] updated successfully",
                vacancy.getId(), vacancy.getPosition(), vacancy.getRecruiter().getId());
    }

    /**
     * Deletes a vacancy by id.
     *
     * @param id the id of the vacancy to delete
     * @throws NotFoundException if the vacancy with the given id is not found
     */
    @Override
    public void deleteVacancy(Long id) {
        // check if vacancy exists (throws NotFoundException if not found)
        Vacancy vacancy = getVacancyById(id);

        vacancy.getRecruiter().removeVacancy(vacancy);

        vacancyRepository.deleteById(id);
        log.info("Vacancy[id={}] deleted successfully", id);
    }

    /**
     * Gets a vacancy by id as a DTO {@link ResponseVacancyDto}.
     *
     * @param vacancyId the id of the vacancy to get
     * @return the DTO of the vacancy with the given id
     * @throws NotFoundException if the vacancy with the given id is not found
     */
    @Override
    public ResponseVacancyDto getResponseVacancyDtoById(Long vacancyId) {
        return vacancyRepository.findById(vacancyId)
                .map(vacancy -> modelMapper.map(vacancy, ResponseVacancyDto.class))
                .orElseThrow(() -> new NotFoundException(String.format("Vacancy with id %d not found", vacancyId)));
    }

    /**
     * Gets a vacancy by id.
     *
     * @param id the id of the vacancy to get
     * @return the vacancy with the given id
     * @throws NotFoundException if the vacancy with the given id is not found
     */
    @Override
    public Vacancy getVacancyById(Long id) {
        return vacancyRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Vacancy with id %d not found", id)));
    }

    /**
     * Gets all vacancies as a page.
     *
     * @param vacancyFilterDto the filter for the number of vacancies
     *                         page number is required,
     *                         page size is required,
     *                         the other fields are optional
     * @return a page of all vacancies
     * @see org.prof.it.soft.dto.filter.VacancyFilterDto
     * @see org.springframework.data.domain.Page
     * @see org.prof.it.soft.dto.response.ResponseVacancyDto
     */
    @Override
    public Page<ResponseVacancyDto> getFilteredVacancies(VacancyFilterDto vacancyFilterDto) {
        return vacancyRepository.findAll(VacancySpecification.of(vacancyFilterDto),
                        PageRequest.of(vacancyFilterDto.getPage(), vacancyFilterDto.getSize(),
                                Sort.by(Sort.Direction.ASC, "id")))
                .map(vacancy -> modelMapper.map(vacancy, ResponseVacancyDto.class));
    }

    /**
     * Genereates an Excel report with vacancies.
     * The report contains the following columns:
     * - Vacancy ID
     * - Position
     * - Salary
     * - Technology Stack
     * - Company Name
     * - Created At
     * - Recruiter_id
     * - Recruiter First Name
     * - Recruiter Last Name
     * The report is generated based on the filter. All filter fields are optional.
     * All vacancies that match the filter are included in the report.
     *
     * @param vacancyFilterDto the filter for the vacancies
     * @return the Excel file as a Resource
     */
    @Override
    @SneakyThrows
    public Resource generateReportExcel(VacancyFilterDto vacancyFilterDto) {
        // try-with-resources to close workbook and output stream
        try (XSSFWorkbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {

            // create a sheet for vacancies
            XSSFSheet vacanciesSheet = workbook.createSheet("Vacancies");

            List<Vacancy> filterVacancies = vacancyRepository.findAll(VacancySpecification.of(vacancyFilterDto),
                    Sort.by(Sort.Direction.ASC, "id"));

            int rowNum = 0;
            int cellNum = 0;

            // create header row
            Row headerRow = vacanciesSheet.createRow(rowNum++);
            headerRow.createCell(cellNum++).setCellValue("Vacancy ID");
            headerRow.createCell(cellNum++).setCellValue("Position");
            headerRow.createCell(cellNum++).setCellValue("Salary");
            headerRow.createCell(cellNum++).setCellValue("Technology Stack");
            headerRow.createCell(cellNum++).setCellValue("Company Name");
            headerRow.createCell(cellNum++).setCellValue("Created At");
            headerRow.createCell(cellNum++).setCellValue("Recruiter_id");
            headerRow.createCell(cellNum++).setCellValue("Recruiter First Name");
            headerRow.createCell(cellNum++).setCellValue("Recruiter Last Name");

            // create data rows for all filtered vacancies
            for (Vacancy vacancy : filterVacancies) {
                cellNum = 0;
                Row row = vacanciesSheet.createRow(rowNum++);
                row.createCell(cellNum++).setCellValue(vacancy.getId());
                row.createCell(cellNum++).setCellValue(vacancy.getPosition());
                // check if salary is not null
                // if salary is null - skip cell
                if (vacancy.getSalary() != null) {
                    row.createCell(cellNum++).setCellValue(vacancy.getSalary());
                } else {
                    cellNum++;
                }
                row.createCell(cellNum++).setCellValue(String.join(", ", vacancy.getTechnologyStack()));
                row.createCell(cellNum++).setCellValue(vacancy.getRecruiter().getCompanyName());
                row.createCell(cellNum++).setCellValue(vacancy.getCreatedAt().toString());
                row.createCell(cellNum++).setCellValue(vacancy.getRecruiter().getId());
                row.createCell(cellNum++).setCellValue(vacancy.getRecruiter().getPerson().getFirstName());
                row.createCell(cellNum).setCellValue(vacancy.getRecruiter().getPerson().getLastName());
            }
            workbook.write(outputStream);
            return new ByteArrayResource(outputStream.toByteArray());
        }
    }

    /**
     * Uploads vacancies from a JSON file to the database.
     * The file is parsed and vacancies are saved.
     * The result of the uploading is returned as a {@link ResponseUploadingResultDto}.
     * <p>
     * All invalid records are skipped. Nothing will be thrown.
     *
     * @param file the JSON file with vacancies
     * @return the result of the uploading as a DTO
     * @throws UploadingFileException if an error occurs while uploading the file.
     *                                But it is not related to invalid records.
     */
    public ResponseUploadingResultDto uploadVacanciesFromJsonFileToDatabase(MultipartFile file) {
        try {
            String jsonContent = file.getResource().getContentAsString(StandardCharsets.UTF_8);
            Set<Long> recruiterIds = recruiterService.getRecruiterIds();

            // parse json content and save vacancies
            vacancyJsonParser.parseVacancies(jsonContent, recruiterIds).forEach(this::saveVacancy);

            return ResponseUploadingResultDto.builder()
                    .message(vacancyJsonParser.getSuccessParsingCounter() != 0
                            ? vacancyJsonParser.getSuccessParsingCounter() + " vacancies uploaded successfully"
                            : "No vacancies uploaded")
                    .successUploaded(vacancyJsonParser.getSuccessParsingCounter())
                    .failedUploaded(vacancyJsonParser.getFailedParsingCounter())
                    .build();
        } catch (Throwable e) {
            log.error("Error while uploading vacancies json file", e);
            throw new UploadingFileException(e);
        }
    }
}
