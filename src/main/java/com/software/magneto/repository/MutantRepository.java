package com.software.magneto.repository;

import com.software.magneto.domain.Mutant;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MutantRepository  extends JpaRepository<Mutant, Long> {


    Optional<Mutant> findByDna(String dna);
}
