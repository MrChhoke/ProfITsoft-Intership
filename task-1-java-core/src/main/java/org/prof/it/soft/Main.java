package org.prof.it.soft;


import org.prof.it.soft.dto.AbstractDto;
import org.prof.it.soft.service.DtoSerializer;
import org.prof.it.soft.service.VacancyStatsService;

import java.nio.file.Path;

public class Main {

    private static final VacancyStatsService vacancyStatsService = new VacancyStatsService();
    private static final DtoSerializer dtoSerializer = new DtoSerializer();

    public static void main(String[] args) {
        if (args.length != 2 || args[0] == null || args[1] == null) {
            throw new IllegalArgumentException("Please provide two arguments: the first argument is" +
                    " the name of the file to read, the second argument is the name of the field" +
                    " to calculate the statistics for.");
        }

        String path = args[0];
        String statisticField = args[1];

        if (!VacancyStatsService.allowedStatisticFields.contains(statisticField)) {
            throw new IllegalArgumentException("The field " + statisticField + " is not allowed. " +
                    "Please provide one of the following fields: " + VacancyStatsService.allowedStatisticFields);
        }

        AbstractDto abstractDto;
        try {
            abstractDto = vacancyStatsService.calculateVacancyStats(Path.of(path), statisticField);
        } catch (Exception e) {
            System.err.println("An error occurred while calculating the statistics: " + e.getMessage());
            return;
        }

        try {
            dtoSerializer.objectToXmlFile(abstractDto, Path.of("statistics_by_" + statisticField + ".xml").toFile());
        } catch (Exception e) {
            System.err.println("An error occurred while writing the result to a file: " + e.getMessage());
        }
    }
}