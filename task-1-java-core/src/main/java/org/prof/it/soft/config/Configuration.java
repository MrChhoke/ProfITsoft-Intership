package org.prof.it.soft.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;

/**
 * This class provides configuration for the ModelMapper.
 */
public class Configuration {

    // The ModelMapper instance used in the application
    private static ModelMapper modelMapper;

    /**
     * Returns the ModelMapper instance used in the application.
     * If the ModelMapper instance has not been created yet, it initializes it with specific configuration:
     * - Skip null values during the mapping process
     * - Use loose matching strategy which works based on the number of matching properties
     *
     * @return the ModelMapper instance
     */
    public static ModelMapper getModelMapper() {
        if (modelMapper == null) {
            modelMapper = new ModelMapper();
            modelMapper.getConfiguration()
                    .setSkipNullEnabled(true)
                    .setMatchingStrategy(MatchingStrategies.LOOSE);
        }

        return modelMapper;
    }
}