package com.semdatex.catalogs.repository;

import com.semdatex.catalogs.domain.OpsCodes;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the OpsCodes entity.
 */
@SuppressWarnings("unused")
@Repository
public interface OpsCodesRepository extends JpaRepository<OpsCodes, Long>, JpaSpecificationExecutor<OpsCodes> {}
