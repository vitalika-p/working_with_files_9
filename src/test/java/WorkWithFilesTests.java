import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import model.Person;
import org.junit.jupiter.api.Test;
import utils.FilesUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class WorkWithFilesTests {

    FilesUtils workWithFiles = new FilesUtils();

    @Test
    void zipParsingTest() throws Exception {
        try (ZipInputStream zis = new ZipInputStream(
                getClass().getResourceAsStream(workWithFiles.getZipName())
        )) {
            ZipEntry entry;
            List<String> expectedFiles = List.of("csv.csv", "pdf.pdf", "xlsx.xlsx");
            List<String> actualFiles = new ArrayList<>();

            while ((entry = zis.getNextEntry()) != null) {
                actualFiles.add(entry.getName());
            }
            assertEquals(expectedFiles, actualFiles);
        }
    }

    @Test
    void pdfParsingTest() throws Exception {
        try (InputStream pdfFile = workWithFiles.getFileFromZip("pdf.pdf")) {
            PDF pdf = new PDF(pdfFile);
            assertThat(pdf.numberOfPages).isEqualTo(1);
            assertThat(pdf.text.contains("Проверка содержимого pdf документа"));
        }
    }

    @Test
    void xlsxParsingTest() throws Exception {
        try (InputStream xlsxFile = workWithFiles.getFileFromZip("xlsx.xlsx")) {
            XLS xlsFile = new XLS(xlsxFile);
            String actualTitle = xlsFile.excel.getSheetAt(0).getRow(0).getCell(0).getStringCellValue();
            assertThat(actualTitle).isEqualTo("Tаблица 1 QA");
        }
    }

    @Test
    void csvParsingTest() throws Exception {
        try (InputStream csvFile = workWithFiles.getFileFromZip("csv.csv")) {
            CSVReader csvReader = new CSVReader(new InputStreamReader(csvFile));
            List<String[]> data = csvReader.readAll();
            assertThat(data.size()).isEqualTo(2);
        }
    }


    @Test
    void testJsonParsing() throws IOException {
        String json = "[\n" +
                "    {\n" +
                "        \"name\": \"John Doe\",\n" +
                "        \"age\": 30,\n" +
                "        \"address\": {\n" +
                "            \"street\": \"123 Elm St\",\n" +
                "            \"city\": \"Springfield\",\n" +
                "            \"zipcode\": \"12345\"\n" +
                "        }\n" +
                "    },\n" +
                "    {\n" +
                "        \"name\": \"Jane Smith\",\n" +
                "        \"age\": 25,\n" +
                "        \"address\": {\n" +
                "            \"street\": \"456 Oak St\",\n" +
                "            \"city\": \"Greenville\",\n" +
                "            \"zipcode\": \"67890\"\n" +
                "        }\n" +
                "    }\n" +
                "]";

        ObjectMapper objectMapper = new ObjectMapper();
        List<Person> people = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, Person.class));
        assertEquals(2, people.size(), "The list should contain 2 persons");
        Person firstPerson = people.get(0);
        assertEquals("John Doe", firstPerson.getName(), "First person name should be 'John Doe'");
        assertEquals(30, firstPerson.getAge(), "First person age should be 30");
}
}


