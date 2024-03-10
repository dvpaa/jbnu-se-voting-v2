package jbnu.se.api.util;

import jbnu.se.api.domain.ElectoralRoll;
import jbnu.se.api.domain.Member;
import jbnu.se.api.exception.CSVException;
import jbnu.se.api.exception.InvalidCsvFormatException;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.DuplicateHeaderMode;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.List;
import java.util.Map;

import static java.nio.charset.StandardCharsets.UTF_8;

@Component
public class ElectoralRollFileHandler {

    private static final String STUDENT_COL = "학번";
    private static final String NAME_COL = "이름";

    public List<ElectoralRoll> processFile(MultipartFile file) {
        try (
                Reader reader = new InputStreamReader(file.getInputStream(), UTF_8);

                CSVParser csvParser = new CSVParser(reader, CSVFormat.newFormat(',')
                        .builder()
                        .setQuote('"')
                        .setHeader(STUDENT_COL, NAME_COL)
                        .setSkipHeaderRecord(true)
                        .setRecordSeparator("\r\n")
                        .setIgnoreEmptyLines(true)
                        .setAllowMissingColumnNames(false)
                        .setDuplicateHeaderMode(DuplicateHeaderMode.ALLOW_ALL)
                        .build())
        ) {
            Map<String, Integer> headers = csvParser.getHeaderMap();
            if (!headers.containsKey(STUDENT_COL) || !headers.containsKey(NAME_COL)) {
                throw new InvalidCsvFormatException();
            }

            return csvParser.stream()
                    .map(row -> {
                        ElectoralRoll electoralRoll = new ElectoralRoll();
                        electoralRoll.setMember(new Member(row.get(STUDENT_COL), row.get(NAME_COL)));
                        return electoralRoll;
                    })
                    .toList();

        } catch (IOException e) {
            throw new CSVException();
        }
    }
}
