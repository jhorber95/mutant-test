package com.software.magneto.controller;

import com.software.magneto.MagnetoApplication;
import com.software.magneto.domain.Mutant;
import com.software.magneto.domain.Statistics;
import com.software.magneto.repository.MutantRepository;
import com.software.magneto.repository.StatisticsRepository;
import com.software.magneto.service.dto.DNAVerificationRequestDTO;
import com.software.magneto.service.dto.StatisticDTO;
import com.software.magneto.util.TestUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = MagnetoApplication.class)
@AutoConfigureMockMvc
public class MutantControllerTest {

    @Autowired
    private MutantRepository mutantRepository;

    @MockBean
    private StatisticsRepository statisticsRepository;

    @Autowired
    private MockMvc restMockMvc;

    private DNAVerificationRequestDTO request;
    private StatisticDTO statisticDTO;

    private Mutant mutant;

    private static final String DEFAULT_DNA = "AAAAA";
    private static final Boolean DEFAULT_IS_MUTANT = Boolean.TRUE;

    static DNAVerificationRequestDTO createRequest() {
        return DNAVerificationRequestDTO.builder()
                .dna(Arrays.asList("ATGCGAG", "CAGTGCG", "TTATGTC", "AGAAGGA", "CCCCTAA", "TCACTGT"))
                .build();
    }

    static Mutant createMutant() {
        return Mutant.builder()
                .dna(DEFAULT_DNA)
                .isMutant(DEFAULT_IS_MUTANT)
                .build();
    }

    static StatisticDTO createStatistic() {
        return StatisticDTO.builder()
                .countMutantDna(1L)
                .countHumanDna(1L)
                .ratio(1.0)
                .build();
    }


    @BeforeEach
    void init() {
        mutant = createMutant();
        request = createRequest();
        statisticDTO = createStatistic();
    }


    @Test
    void sendShouldExecuteDna() throws Exception {
        int databaseSizeBeforeCreate = mutantRepository.findAll().size();

        restMockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().isOk());

        List<Mutant> list = mutantRepository.findAll();
        assertThat(list).hasSize(databaseSizeBeforeCreate +1);
    }

    @Test
    void checkDnaShouldFail() throws Exception {
        int databaseSizeBeforeCreate = mutantRepository.findAll().size();

        restMockMvc.perform(post("/mutant")
                .contentType(MediaType.APPLICATION_JSON)
                .content(TestUtil.convertObjectToJsonBytes(request)))
                .andExpect(status().is4xxClientError());

        List<Mutant> list = mutantRepository.findAll();
        assertThat(list).hasSize(databaseSizeBeforeCreate);
    }

    @Test
    void testStatistic() throws Exception {

        mutantRepository.saveAndFlush(mutant);
        Statistics mutant = Statistics.builder()
                .isMutant(true)
                .total(1L)
                .build();
        Statistics human = Statistics.builder()
                .isMutant(false)
                .total(1L)
                .build();

        when(statisticsRepository.findAll()).thenReturn(Arrays.asList(mutant, human));

        restMockMvc.perform(get("/stats"))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("$.ratio").value(statisticDTO.getRatio()))
                .andExpect(jsonPath("$.count_mutant_dna").value(statisticDTO.getCountMutantDna().intValue()))
                .andExpect(jsonPath("$.count_human_dna").value(statisticDTO.getCountHumanDna().intValue()));
    }
}
