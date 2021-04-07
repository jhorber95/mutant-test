package com.software.magneto.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

@Data
@Table
@Entity(name = "mutant_statics")
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Statistics implements Serializable {

    @Id
    private Boolean isMutant;
    private Long total;
}
