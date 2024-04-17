# Application for calculating statistics by a specified attribute

This application is created to read data from a file containing information about vacancies in JSON format and output
information about them in an XML file.

## Implementation Details

This application is designed to read data from a file containing information about vacancies in JSON format and output
information about them in an XML file. Here are some key implementation details:

1. **Data Reading**: The application reads data from JSON files. Each file contains an array of `VacancyDto` objects.
   The required fields for each `VacancyDto` are `position` and `recruiter_first_name`. **If JSON file doesn't contain
   any of these fields, the JSON record will be skipped and parsing will continue.**

2. **Data Processing**: **The application will calculate statistics for JSON files without creating a Java object for
   each vacancy. The approach can be useful when the JSON files are large and the application needs to process them
   quickly.** The application calculates statistics based on a specified attribute. This attribute is passed
   as a command-line argument. The possible attributes are `position`, `salary`, `recruiter`, and `technology_stack`.

3. **Data Writing**: The application writes the calculated statistics to an XML file. The name of the output file
   is `statistics_by_{attribute}.xml`, where `{attribute}` is the attribute passed as a command-line argument.

4. **Parallel Processing**: The `VacancyStatisticService` can process data in parallel. The number of threads used for
   parallel processing. The `VacancyStatsServiceParallelTest` class tests the performance of parallel processing by
   measuring the execution time for different numbers of threads.

5. **Testing**: The application includes unit tests for the `VacancyStatsService` and performance tests for parallel
   processing. The tests use the JUnit 5 framework. The performance tests generate test JSON files in
   the `src/test/resources/json/vacancy/temp` directory.

6. **Error Handling**: The application handles errors such as invalid input files and invalid command-line arguments. In
   case of an error, the application displays an appropriate error message and terminates.

7. **Dependencies**: The application uses the Jackson library for JSON and XML processing, the JUnit 5 framework for
   testing, and the Faker library for generating test data.

## How to run the application

To run the application, follow these steps:

1. Download the project from the repository.
2. Open the project in an IDE.
3. Add Program arguments in the following format: "input-folder statistic-attribute", where:
    - input-folder is the path to the folder containing vacancy files.
    - statistic-attribute is the attribute by which statistics will be conducted (e.g., "position").
4. Run the application.
5. Check the result in the statistics_by_{attribute}.xml file. The file will be created in the project root directory.

## Example of Input and Output Files

- Input files: JSON files, each containing an array of VacancyDto objects. Each VacancyDto can contain the following
  fields:
  position, salary, technology_stack, recruiter_first_name, recruiter_last_name, recruiter_company_name.
  **But required fields are position and recruiter_first_name**. For example:

```json
[
  {
    "position": "Full-Stack developer",
    "salary": 1000.0,
    "technology_stack": "Java, Spring, React",
    "recruiter_first_name": "Vladyslav",
    "recruiter_company_name": "SoftServe"
  },
  {
    "position": "Python Developer",
    "salary": 1200.0,
    "technology_stack": "Python, Machine Learning",
    "recruiter_first_name": "Vladyslav"
  },
  {
    "position": "Junior QA Engineer",
    "recruiter_first_name": "Іван",
    "recruiter_last_name": "Франко"
  },
  {
    "position": "Full-Stack developer",
    "technology_stack": "Java, Spring Boot, React",
    "recruiter_first_name": "Іван",
    "recruiter_last_name": "Петров",
    "recruiter_company_name": "SoftServe"
  }
]
```

- Output files: XML files that contain statistics for the specified attribute. For example:

```xml
<?xml version="1.0" encoding="UTF-8" ?>
<statistic>
    <vacancy-count-by-position-statistic>
        <item>
            <key>Full-Stack developer</key>
            <count>2</count>
        </item>
        <item>
            <key>Python Developer</key>
            <count>1</count>
        </item>
        <item>
            <key>Junior QA Engineer</key>
            <count>1</count>
        </item>
    </vacancy-count-by-position-statistic>
</statistic>
```

## Verifying the Parallel Execution of VacancyStatisticService

To validate the parallel execution of `VacancyStatisticService`, adhere to the following procedure:

1. Clone the project repository to your local machine.
2. Launch your preferred IDE and open the cloned project.
3. Navigate to the `VacancyStatisticServiceParallelTest` class, which is located
   at `src/test/java/org/prof/it/soft/service/VacancyStatsServiceParallel.java`, and execute it.
4. This test is designed to evaluate the performance of `VacancyStatisticService` in a parallel execution environment.
   It does not generate an output file named `statistics_by_{attribute}.xml`. Instead, it measures and displays the
   execution time.

   The test automatically generates test JSON files in the `src/test/resources/json/vacancy/temp` directory. If the
   directory does not exist or is empty, the test will create it and populate it with test JSON files. If the directory
   already contains files, the test will overwrite them.

   The test executes the service in parallel mode using 1, 2, 4, and 8 threads, and records the execution time for each
   run. The execution time, in milliseconds, is displayed in the console.

   You have the option to modify the number of files and the number of vacancies per file in the test class to suit your
   testing needs.

## Statistics Vacancy Parsing

1. Single thread parsing: 157 files with 27673 vacancies per file - total size 1,35 GB
    - Position stats: 5277 ms
    - Salary stats: 6901 ms
    - Recruiter stats: 11622 ms
    - Technology stack stats: 13092 ms
2. Two threads parsing: 157 files with 27673 vacancies per file - total size 1,35 GB
    - Position stats: 3570 ms
    - Salary stats: 3547 ms
    - Recruiter stats: 9075 ms
    - Technology stack stats: 7020 ms
3. Four threads parsing: 157 files with 27673 vacancies per file - total size 1,35 GB
    - Position stats: 1444 ms
    - Salary stats: 2109 ms
    - Recruiter stats: 6398 ms
    - Technology stack stats: 3587 ms
4. Eight threads parsing: 157 files with 27673 vacancies per file - total size 1,35 GB
    - Position stats: 744 ms
    - Salary stats: 1284 ms
    - Recruiter stats: 6033 ms
    - Technology stack 2540: 2683 ms