package com.example.wellueducationservice.service;

import com.example.wellueducationservice.dto.request.CsvQuestionRow;
import com.opencsv.CSVReader;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

@Service
public class CsvParserService {

    public List<CsvQuestionRow> parse(MultipartFile file) {
        List<CsvQuestionRow> rows = new ArrayList<>();

        try (CSVReader reader = new CSVReader(new InputStreamReader(file.getInputStream()))) {

            String[] line;
            boolean isFirstRow = true;
            int rowNumber = 0;

            while ((line = reader.readNext()) != null) {
                rowNumber++;

                if (isFirstRow) {
                    isFirstRow = false;
                    continue;
                }

                validate(line, rowNumber);

                rows.add(new CsvQuestionRow(
                        line[0].trim(),
                        line[1].trim(),
                        line[2].trim(),
                        line[3].trim(),
                        line[4].trim(),
                        line[5].trim()
                ));
            }

        } catch (Exception e) {
            throw new RuntimeException("Failed to parse CSV", e);
        }

        return rows;
    }

    private void validate(String[] line, int rowNumber) {
        if (line.length != 6) {
            throw new IllegalArgumentException("Invalid column count at row " + rowNumber);
        }

        for (int i = 0; i < line.length; i++) {
            if (line[i] == null || line[i].isBlank()) {
                throw new IllegalArgumentException("Empty value at row " + rowNumber + ", column " + i);
            }
        }
    }
}
