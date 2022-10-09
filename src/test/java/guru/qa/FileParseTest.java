package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.opencsv.CSVReader;
import guru.qa.model.Teacher;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

public class FileParseTest {

    ClassLoader cl = FileParseTest.class.getClassLoader();

    @DisplayName("parse pdf file")
    @Test
    void pdfTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File downloadedFile = $("a[href*='junit-user-guide-5.9.1.pdf']").download();
        PDF pdf = new PDF(downloadedFile);
        assertThat(pdf.author).contains("Sam Brannen");

    }

    @DisplayName("parse xls file")
    @Test
    void xlsTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("sample-xlsx-file.xlsx")) {
            XLS xls = new XLS(is);
            assertThat(
                    xls.excel.getSheetAt(0)
                            .getRow(1)
                            .getCell(1).getStringCellValue()
            ).isEqualTo("Dulce");
        }
    }

    @DisplayName("parse csv file")
    @Test
    void csvTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("qa_guru.csv");
             CSVReader reader = new CSVReader(new InputStreamReader(is))) {
            List<String[]> content = reader.readAll();
            String[] row = content.get(2);
            assertThat(row[0]).isEqualTo("Azat");
            assertThat(row[1]).isEqualTo("Ziyatdinov");
        }
    }

    @DisplayName("parse zip file")
    @Test
    void zipTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("sample-zip-file.zip");
             ZipInputStream zis = new ZipInputStream(is)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                String entryName = entry.getName();
            }
        }
    }

    @Test
    @DisplayName("parse json file (gson)")
    void jsonTest() throws Exception {
        try (InputStream is = cl.getResourceAsStream("teacher.json")) {
            Gson gson = new Gson();
            JsonObject jsonObject = gson.fromJson(new InputStreamReader(is), JsonObject.class);
            assertThat(jsonObject.get("name").getAsString()).isEqualTo("Dmitrii");
            assertThat(jsonObject.get("isGoodTeacher").getAsBoolean()).isTrue();
            assertThat(jsonObject.get("passport").getAsJsonObject().get("number").getAsInt()).isEqualTo(123456);
        }
    }

    @Test
    @DisplayName("parse json file with model (gson)")
    void jsonTestWithModel() throws Exception {
        try (InputStream is = cl.getResourceAsStream("teacher.json")) {
            Gson gson = new Gson();
            Teacher teacher = gson.fromJson(new InputStreamReader(is), Teacher.class);
            assertThat(teacher.name).isEqualTo("Dmitrii");
            assertThat(teacher.isGoodTeacher).isTrue();
            assertThat(teacher.passport.number).isEqualTo(123456);
        }
    }
}
