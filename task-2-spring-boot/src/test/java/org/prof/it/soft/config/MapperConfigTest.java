package org.prof.it.soft.config;

import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.prof.it.soft.dto.request.RequestVacancyDto;
import org.prof.it.soft.entity.Vacancy;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class MapperConfigTest {

    private MapperConfig mapperConfig = new MapperConfig();

    @Test
    void mapRequestVacancyDtoToVacancy() {
        ModelMapper modelMapper = mapperConfig.getModelMapper();

        RequestVacancyDto requestVacancyDto = new RequestVacancyDto(
                "Java Developer",
                1000F,
                List.of("Java", "Spring", "Hibernate"),
                100L
        );

        Vacancy vacancy = modelMapper.map(requestVacancyDto, Vacancy.class);

        assertThat(vacancy.getId()).isNull();
        assertThat(vacancy.getPosition()).isEqualTo("Java Developer");
        assertThat(vacancy.getSalary()).isEqualTo(1000F);
        assertThat(vacancy.getTechnologyStack()).containsExactlyInAnyOrder("Java", "Spring", "Hibernate");
        assertThat(vacancy.getRecruiter().getId()).isEqualTo(100L);
    }



}