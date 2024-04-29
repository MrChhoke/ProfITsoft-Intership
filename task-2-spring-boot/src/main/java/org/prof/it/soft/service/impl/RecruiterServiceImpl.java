package org.prof.it.soft.service.impl;

import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.prof.it.soft.dto.request.RequestRecruiterDto;
import org.prof.it.soft.dto.response.ResponseRecruiterDto;
import org.prof.it.soft.entity.Recruiter;
import org.prof.it.soft.exception.NotFoundException;
import org.prof.it.soft.repo.RecruiterRepository;
import org.prof.it.soft.service.RecruiterService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Set;

@RequiredArgsConstructor
@Transactional
@Service
public class RecruiterServiceImpl implements RecruiterService {

    /**
     * The repository for the Recruiter entity.
     */
    protected final RecruiterRepository recruiterRepository;

    /**
     * The mapper for converting between DTOs and entities.
     */
    protected final ModelMapper modelMapper;

    /**
     * Saves a new recruiter.
     *
     * @param recruiterDto the DTO of the recruiter to save
     * @return the DTO of the saved recruiter
     */
    public ResponseRecruiterDto saveRecruiter(RequestRecruiterDto recruiterDto) {
        Recruiter recruiter = modelMapper.map(recruiterDto, Recruiter.class);
        Recruiter savedRecruiter = recruiterRepository.saveAndFlush(recruiter);
        return modelMapper.map(savedRecruiter, ResponseRecruiterDto.class);
    }

    /**
     * Updates an existing recruiter.
     *
     * @param recruiterId  the id of the recruiter to update
     * @param recruiterDto the DTO of the recruiter with the updated data
     * @throws NotFoundException if the recruiter with the given id is not found
     */
    @Override
    public void updateRecruiter(Long recruiterId, RequestRecruiterDto recruiterDto) {
        Recruiter recruiterFromDto = modelMapper.map(recruiterDto, Recruiter.class);
        Recruiter recruiter = recruiterRepository.findById(recruiterId).orElseThrow(
                () -> new NotFoundException(String.format("Recruiter with id %d not found", recruiterId))
        );

        recruiter.setCompanyName(recruiterFromDto.getCompanyName());

        recruiterRepository.saveAndFlush(recruiter);
    }

    /**
     * Deletes a recruiter.
     *
     * @param id the id of the recruiter to delete
     * @throws NotFoundException if the recruiter with the given id is not found
     */
    @Override
    public void deleteRecruiter(Long id) {
        getRecruiterById(id); // check if recruiter exists (throws NotFoundException if not found

        recruiterRepository.deleteById(id);
    }

    /**
     * Gets a recruiter by id as a DTO.
     *
     * @param recruiterId the id of the recruiter to get
     * @return the DTO of the recruiter
     * @throws NotFoundException if the recruiter with the given id is not found
     */
    @Override
    public ResponseRecruiterDto getResponseRecruiterDtoById(Long recruiterId) {
        return recruiterRepository.findById(recruiterId)
                .map(recruiter -> modelMapper.map(recruiter, ResponseRecruiterDto.class))
                .orElseThrow(() -> new NotFoundException(String.format("Recruiter with id %d not found", recruiterId)));
    }

    /**
     * Gets a recruiter by id.
     *
     * @param recruiterId the id of the recruiter to get
     * @return the recruiter
     * @throws NotFoundException if the recruiter with the given id is not found
     */
    @Override
    public Recruiter getRecruiterById(Long recruiterId) {
        return recruiterRepository.findById(recruiterId)
                .orElseThrow(() -> new NotFoundException(String.format("Recruiter with id %d not found", recruiterId)));
    }

    /**
     * Gets all recruiter ids.
     *
     * @return a set of all recruiter ids
     */
    @Override
    public Set<Long> getRecruiterIds() {
        return recruiterRepository.findAllRecruiterIds();
    }
}