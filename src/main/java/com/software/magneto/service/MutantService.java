package com.software.magneto.service;

import com.software.magneto.domain.Mutant;
import com.software.magneto.domain.Statistics;
import com.software.magneto.repository.MutantRepository;
import com.software.magneto.repository.StatisticsRepository;
import com.software.magneto.service.dto.DNAVerificationRequestDTO;
import com.software.magneto.service.dto.StatisticDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class MutantService {

    private final MutantRepository repository;
    private final StatisticsRepository statisticsRepository;
//    private final MutantMapper mapper;

    public boolean isMutant(DNAVerificationRequestDTO data) {

        String dna = String.join(",", data.getDna());

        Optional<Mutant> optional = repository.findByDna(dna);

        if (optional.isPresent()) {
            return false;
        }

        String[][] arrays = {};

        var se = data.getDna().stream()
                .map(s -> s.split(""))
                .collect(Collectors.toList());

        String[][] matrix = se.toArray(arrays);

        int totalMatchers = printLeftToRight(matrix) + printRightToLeft(matrix) + printColumns(matrix) + printRows(data.getDna());

        if (totalMatchers >= 1) {
            log.debug(" :: is mutant. Total matchers {}", totalMatchers);
        }
        Mutant entity = Mutant.builder()
                .dna(dna)
                .isMutant(totalMatchers >= 1)
                .build();

        repository.save(entity);

        return !data.getDna().isEmpty();
    }

    public StatisticDTO getStatistic() {
        List<Statistics> data = statisticsRepository.findAll();

        StatisticDTO response = new StatisticDTO();
        for (Statistics s : data) {
            if (s.getIsMutant()) {
                response.setCountMutantDna(s.getTotal());
            } else {
                response.setCountHumanDna(s.getTotal());
            }
        }

        double ratio = response.getCountMutantDna() / response.getCountHumanDna();
        response.setRatio(ratio);

        return response;

    }

    private int checkString(String s) {
        int cont = 0;

        if (s.length() < 4) {
            return 0;
        }
        Pattern[] patterns = {Pattern.compile("TTTT"), Pattern.compile("AAAA"), Pattern.compile("CCCC"), Pattern.compile("GGGG")};

        for (Pattern p : patterns) {
            Matcher m = p.matcher(s);

            while (m.find()) {
                cont++;
            }
        }

        return cont;
    }

    public int printRows(List<String> data) {
        int contMatchers = 0;

        for (String s : data) {
            contMatchers += checkString(s);
        }

        log.debug(" :: total match in rows: {}", contMatchers);
        return contMatchers;
    }

    public int printColumns(String[][] matrix) {
        int contMatchers = 0;

        for (int j  = 0; j< matrix[0].length; j++) {
            StringBuilder s = new StringBuilder();

            for (String[] ints : matrix) {
                s.append(ints[j]);
            }
            contMatchers += checkString(s.toString());
        }

        log.debug(" :: total match in columns: {}", contMatchers);
        return contMatchers;
    }

    private int printRightToLeft(String[][] matrix) {
        int contMatchers = 0;

        int rows = matrix.length;
        int cols = matrix[0].length;


        int j1 = cols;
        while (j1 >= 0) {
            int k = 0;
            StringBuilder s = new StringBuilder();
            while (k < rows) {
                if ((j1 + k) < cols) {
                    s.append(matrix[k][j1 + k]);
                } else {
                    break;
                }
                k++;
            }
//            log.debug(s.toString());
            contMatchers += checkString(s.toString());

            j1--;
        }

        int i1 = 1;
        while (i1 < rows) {
            int j = i1, k = 0;
            StringBuilder s = new StringBuilder();

            while (j < rows && k < cols) {
                s.append(matrix[j][k]);
                j++;
                k++;
            }
            contMatchers += checkString(s.toString());
            i1++;
        }

        log.debug(" :: total match in right diagonal: {}", contMatchers);

        return contMatchers;
    }

    private int printLeftToRight(String[][] matrix) {
        int contMatchers = 0;


        //print first half
        int row = 0;
        int col;

        while (row < matrix.length) {
            col = 0;
            int rowTemp = row;
            StringBuilder s = new StringBuilder();
            while (rowTemp >= 0) {
                s.append(matrix[rowTemp][col]);
                rowTemp--;
                col++;
            }
            contMatchers += checkString(s.toString());

            row++;
        }

        //print second half
        col = 1;

        while (col < matrix.length) {
            int colTemp = col;
            row = matrix.length - 1;
            StringBuilder s = new StringBuilder();
            while (colTemp <= matrix.length - 1) {
                s.append(matrix[row][colTemp]);
                row--;
                colTemp++;
            }
            contMatchers += checkString(s.toString());

            col++;
        }

        log.debug(" :: total match in left diagonal: {}", contMatchers);

        return contMatchers;

    }
}
