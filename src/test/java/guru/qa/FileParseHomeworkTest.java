package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import model.Active;
import org.assertj.core.api.AssertionsForClassTypes;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import static org.assertj.core.api.Assertions.assertThat;

public class FileParseHomeworkTest {
    ClassLoader cl = FileParseHomeworkTest.class.getClassLoader();

    @DisplayName("parse pdf from zip")
    @Test
    void zipPdfTest() throws Exception {
        ZipFile zipfile = new ZipFile(new File("src/test/resources/homework/TestFiles.zip"));
        try (InputStream is = cl.getResourceAsStream("homework/TestFiles.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains(".pdf")) {
                    try (InputStream inputStream = zipfile.getInputStream(entry)) {
                        PDF pdf = new PDF(inputStream);
                        assertThat(pdf.producer).contains("Adobe PDF Library 15.0");
                        assertThat(pdf.numberOfPages).isEqualTo(2);
                    }
                }

            }
        }
    }

    @DisplayName("parse xls from zip")
    @Test
    void zipXlsTest() throws Exception {
        ZipFile zipfile = new ZipFile(new File("src/test/resources/homework/TestFiles.zip"));
        try (InputStream is = cl.getResourceAsStream("homework/TestFiles.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains(".xls")) {
                    try (InputStream inputStream = zipfile.getInputStream(entry)) {
                        XLS xls = new XLS(inputStream);
                        AssertionsForClassTypes.assertThat(
                                xls.excel.getSheetAt(0)
                                        .getRow(56)
                                        .getCell(0).getStringCellValue()
                        ).isEqualTo("Гранат");
                    }
                }
            }
        }
    }

    @DisplayName("parse csv from zip")
    @Test
    void zipCsvTest() throws Exception {
        ZipFile zipfile = new ZipFile(new File("src/test/resources/homework/TestFiles.zip"));
        try (InputStream is = cl.getResourceAsStream("homework/TestFiles.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (entry.getName().contains(".csv")) {
                    try (InputStream inputStream = zipfile.getInputStream(entry);
                         CSVReader reader = new CSVReader(new InputStreamReader(inputStream))) {
                        List<String[]> content = reader.readAll();
                        String[] row = content.get(3);
                        assertThat(row[0]).isEqualTo("Ramon Hernandez");
                        assertThat(row[1]).isEqualTo("BAL");
                    }
                }
            }
        }
    }

    @DisplayName("parse json file with model (jackson)")
    @Test
    void jsonJackson() throws Exception {
        File file = new File("src/test/resources/homework/active.json");
        ObjectMapper objectMapper = new ObjectMapper();
        Active active = objectMapper.readValue(file, Active.class);
        assertThat(active.id).isEqualTo(100);
        assertThat(active.name).isEqualTo("server");
        assertThat(active.dnsName).isEqualTo("google");
        assertThat(active.isActive).isTrue();
        assertThat(active.applicationSoftware.get(0)).isEqualTo("Windows Server 2000");
        assertThat(active.networkConfigurations.address).isEqualTo("192.168.1.1");
    }
}
