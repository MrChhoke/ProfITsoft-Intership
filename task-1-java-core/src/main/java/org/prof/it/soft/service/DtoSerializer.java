package org.prof.it.soft.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.prof.it.soft.dto.AbstractDto;
import org.prof.it.soft.dto.VacancyDto;

import java.io.File;
import java.util.Collection;
import java.util.List;

/**
 * This class is responsible for serializing and deserializing DTOs.
 * It supports both JSON and XML formats.
 */
@RequiredArgsConstructor
public class DtoSerializer {

    private final ObjectMapper jsonMapper;
    private final XmlMapper xmlMapper;

    /**
     * Default constructor.
     * Initializes JSON and XML mappers with default settings.
     */
    public DtoSerializer() {
        jsonMapper = new ObjectMapper();
        xmlMapper = new XmlMapper();

        jsonMapper.enable(SerializationFeature.INDENT_OUTPUT);
        xmlMapper.enable(SerializationFeature.INDENT_OUTPUT);

        xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
    }

    /**
     * Converts a DTO to a JSON string.
     *
     * @param dto the DTO to convert
     * @return the JSON string
     * @throws Exception if an error occurs during conversion
     */
    public String objectToJson(@NonNull AbstractDto dto) throws Exception {
        return jsonMapper.writeValueAsString(dto);
    }

    /**
     * Converts a DTO to an XML file.
     *
     * @param dto the DTO to convert
     * @param output the output file
     * @throws Exception if an error occurs during conversion
     */
    public void objectToXmlFile(@NonNull AbstractDto dto, File output) throws Exception {
        xmlMapper.writeValue(output, dto);
    }

    /**
     * Converts a collection of DTOs to a JSON file.
     *
     * @param dtos the DTOs to convert
     * @param resultFile the output file
     * @throws Exception if an error occurs during conversion
     */
    public void collectionObjectToJsonFile(@NonNull Collection<? extends AbstractDto> dtos, File resultFile) throws Exception {
        jsonMapper.writeValue(resultFile, dtos);
    }

    /**
     * Converts a JSON string to a VacancyDto.
     *
     * @param json the JSON string
     * @return the VacancyDto
     * @throws Exception if an error occurs during conversion
     */
    public VacancyDto jsonToVacancyDto(@NonNull String json) throws Exception {
        return jsonMapper.readValue(json, VacancyDto.class);
    }

    /**
     * Converts a JSON string to a list of VacancyDto.
     *
     * @param json the JSON string
     * @return the list of VacancyDto
     * @throws Exception if an error occurs during conversion
     */
    public List<VacancyDto> jsonToVacancyDtoList(@NonNull String json) throws Exception {
        return jsonMapper.readValue(json, jsonMapper.getTypeFactory().constructCollectionType(List.class, VacancyDto.class));
    }

    /**
     * Converts a JSON file to a list of VacancyDto.
     *
     * @param jsonFile the JSON file
     * @return the list of VacancyDto
     * @throws Exception if an error occurs during conversion
     */
    public List<VacancyDto> jsonFileToVacancyDtoList(@NonNull File jsonFile) throws Exception {
        return jsonMapper.readValue(jsonFile, jsonMapper.getTypeFactory().constructCollectionType(List.class, VacancyDto.class));
    }
}