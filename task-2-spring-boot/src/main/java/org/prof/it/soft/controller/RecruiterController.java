package org.prof.it.soft.controller;

import lombok.RequiredArgsConstructor;
import org.prof.it.soft.dto.request.RequestPersonDto;
import org.prof.it.soft.dto.request.RequestRecruiterDto;
import org.prof.it.soft.dto.response.ResponseRecruiterDto;
import org.prof.it.soft.service.RecruiterService;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/api/v1/recruiter")
@RequiredArgsConstructor
public class RecruiterController {

    /**
     * Service for handling operations related to recruiters.
     */
    protected final RecruiterService recruiterService;

    /**
     * Get a recruiter by id.
     * @param id The id of the recruiter.
     * @return The recruiter with the given id.
     */
    @GetMapping("/{id}")
    public ResponseEntity<ResponseRecruiterDto> getRecruiterById(@PathVariable Long id) {
        return ResponseEntity.ok(recruiterService.getResponseRecruiterDtoById(id));
    }

    /**
     * Save a new recruiter.
     * @param requestRecruiterDto The data of the recruiter to be saved.
     * @return The saved recruiter.
     */
    @PostMapping
    public ResponseEntity<ResponseRecruiterDto> saveRecruiter(@Validated({RequestRecruiterDto.Save.class, RequestPersonDto.Save.class})
                                                                  @RequestBody RequestRecruiterDto requestRecruiterDto) {
        ResponseRecruiterDto responseRecruiterDto = recruiterService.saveRecruiter(requestRecruiterDto);
        return ResponseEntity.ok(responseRecruiterDto);
    }

    /**
     * Update a recruiter.
     * @param requestRecruiterDto The new data of the recruiter.
     * @param id The id of the recruiter to be updated.
     * @return A message indicating that the recruiter was updated successfully.
     */
    @PutMapping("/{id}")
    public ResponseEntity<String> updateRecruiter(@Validated(RequestRecruiterDto.Update.class) @RequestBody RequestRecruiterDto requestRecruiterDto,
                                                  @PathVariable Long id) {
        recruiterService.updateRecruiter(id, requestRecruiterDto);
        return ResponseEntity.ok("Recruiter updated successfully");
    }

    /**
     * Delete a recruiter.
     * @param id The id of the recruiter to be deleted.
     * @return A message indicating that the recruiter was deleted successfully.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteRecruiter(@PathVariable Long id) {
        recruiterService.deleteRecruiter(id);
        return ResponseEntity.ok("Recruiter deleted successfully");
    }
}