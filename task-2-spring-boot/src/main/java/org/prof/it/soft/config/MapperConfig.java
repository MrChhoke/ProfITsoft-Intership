package org.prof.it.soft.config;

import org.modelmapper.Converter;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.modelmapper.spi.MappingContext;
import org.prof.it.soft.dto.request.RequestVacancyDto;
import org.prof.it.soft.entity.Recruiter;
import org.prof.it.soft.entity.Vacancy;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.ArrayList;
import java.util.List;

/**
 * Configuration class for ModelMapper.
 * ModelMapper is an intelligent object mapping library that automatically maps objects to each other.
 * It uses a convention based approach while providing a simple refactoring safe API for handling specific use cases.
 */
@Configuration
public class MapperConfig {

    /**
     * Bean for ModelMapper.
     * Configures the ModelMapper to skip null values and use loose matching strategy.
     * Adds custom converters.
     *
     * @return ModelMapper object
     */
    @Bean
    public ModelMapper getModelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setSkipNullEnabled(true)
                .setMatchingStrategy(MatchingStrategies.LOOSE);

        converters().forEach(modelMapper::addConverter);

        return modelMapper;
    }

    /**
     * List of custom converters.
     * Currently includes a converter from RequestVacancyDto to Vacancy.
     *
     * @return List of Converter objects
     */
    public List<Converter<?,?>> converters () {
        return List.of(new Converter<RequestVacancyDto, Vacancy>() {
            /**
             * Converts a RequestVacancyDto object to a Vacancy object.
             * Sets the recruiter id from the source to the destination.
             * If the technology stack in the source is null, sets it to a new ArrayList in the destination.
             *
             * @param context MappingContext object containing the source and destination objects
             * @return Vacancy object
             */
            @Override
            public Vacancy convert(MappingContext<RequestVacancyDto, Vacancy> context) {
                RequestVacancyDto source = context.getSource();
                Vacancy destination = context.getDestination();
                if (destination == null) {
                    destination = new Vacancy();
                }
                destination.setId(null);
                destination.setPosition(source.getPosition());
                destination.setSalary(source.getSalary());
                destination.setTechnologyStack(source.getTechnologyStack() == null ? new ArrayList<>() : source.getTechnologyStack());
                destination.setRecruiter(new Recruiter());
                destination.getRecruiter().setId(source.getRecruiterId());
                return destination;
            }
        });
    }
}