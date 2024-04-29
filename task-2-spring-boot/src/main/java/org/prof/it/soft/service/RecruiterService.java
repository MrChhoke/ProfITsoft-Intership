package org.prof.it.soft.service;

import org.prof.it.soft.dto.request.RequestRecruiterDto;
import org.prof.it.soft.dto.response.ResponseRecruiterDto;
import org.prof.it.soft.entity.Recruiter;

import java.util.List;
import java.util.Set;

/**
 * Service for working with recruiters
 *
 * @see org.prof.it.soft.entity.Recruiter
 * @see org.prof.it.soft.dto.request.RequestRecruiterDto
 * @see org.prof.it.soft.dto.response.ResponseRecruiterDto
 */
public interface RecruiterService {

    /**
     * Saves a new recruiter.
     *
     * @param recruiterDto the DTO of the recruiter to save
     * @return the {@link ResponseRecruiterDto} of the saved recruiter
     */
    ResponseRecruiterDto saveRecruiter(RequestRecruiterDto recruiterDto);

    /**
     * Updates an existing recruiter.
     *
     * @param recruiterId  the id of the recruiter to update
     * @param recruiterDto the {@link ResponseRecruiterDto} of the recruiter with the updated data
     * @throws org.prof.it.soft.exception.NotFoundException if the recruiter with the given id is not found
     */
    void updateRecruiter(Long recruiterId, RequestRecruiterDto recruiterDto);

    /**
     * Deletes a recruiter by id.
     *
     * @param id the id of the {@link Recruiter} to delete
     * @throws org.prof.it.soft.exception.NotFoundException if the recruiter with the given id is not found
     */
    void deleteRecruiter(Long id);

    /**
     * Gets a recruiter by id.
     *
     * @param recruiterId the id of the recruiter to get
     * @return the DTO of the recruiter with the given id
     * @throws org.prof.it.soft.exception.NotFoundException if the recruiter with the given id is not found
     */
    ResponseRecruiterDto getResponseRecruiterDtoById(Long recruiterId);

    /**
     * Gets a recruiter by id.
     *
     * @param recruiterId the id of the {@link Recruiter} to get
     * @return the recruiter with the given id
     * @throws org.prof.it.soft.exception.NotFoundException if the recruiter with the given id is not found
     */
    Recruiter getRecruiterById(Long recruiterId);

    /**
     * Gets the ids of all recruiters.
     *
     * @return a set of the ids of all recruiters
     */
    Set<Long> getRecruiterIds();
}
