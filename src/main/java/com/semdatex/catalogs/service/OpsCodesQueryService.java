package com.semdatex.catalogs.service;

import com.semdatex.catalogs.domain.*; // for static metamodels
import com.semdatex.catalogs.domain.OpsCodes;
import com.semdatex.catalogs.repository.OpsCodesRepository;
import com.semdatex.catalogs.service.criteria.OpsCodesCriteria;
import com.semdatex.catalogs.service.dto.OpsCodesDTO;
import com.semdatex.catalogs.service.mapper.OpsCodesMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tech.jhipster.service.QueryService;

/**
 * Service for executing complex queries for {@link OpsCodes} entities in the database.
 * The main input is a {@link OpsCodesCriteria} which gets converted to {@link Specification},
 * in a way that all the filters must apply.
 * It returns a {@link Page} of {@link OpsCodesDTO} which fulfills the criteria.
 */
@Service
@Transactional(readOnly = true)
public class OpsCodesQueryService extends QueryService<OpsCodes> {

    private static final Logger log = LoggerFactory.getLogger(OpsCodesQueryService.class);

    private final OpsCodesRepository opsCodesRepository;

    private final OpsCodesMapper opsCodesMapper;

    public OpsCodesQueryService(OpsCodesRepository opsCodesRepository, OpsCodesMapper opsCodesMapper) {
        this.opsCodesRepository = opsCodesRepository;
        this.opsCodesMapper = opsCodesMapper;
    }

    /**
     * Return a {@link Page} of {@link OpsCodesDTO} which matches the criteria from the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @param page The page, which should be returned.
     * @return the matching entities.
     */
    @Transactional(readOnly = true)
    public Page<OpsCodesDTO> findByCriteria(OpsCodesCriteria criteria, Pageable page) {
        log.debug("find by criteria : {}, page: {}", criteria, page);
        final Specification<OpsCodes> specification = createSpecification(criteria);
        return opsCodesRepository.findAll(specification, page).map(opsCodesMapper::toDto);
    }

    /**
     * Return the number of matching entities in the database.
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the number of matching entities.
     */
    @Transactional(readOnly = true)
    public long countByCriteria(OpsCodesCriteria criteria) {
        log.debug("count by criteria : {}", criteria);
        final Specification<OpsCodes> specification = createSpecification(criteria);
        return opsCodesRepository.count(specification);
    }

    /**
     * Function to convert {@link OpsCodesCriteria} to a {@link Specification}
     * @param criteria The object which holds all the filters, which the entities should match.
     * @return the matching {@link Specification} of the entity.
     */
    protected Specification<OpsCodes> createSpecification(OpsCodesCriteria criteria) {
        Specification<OpsCodes> specification = Specification.where(null);
        if (criteria != null) {
            // This has to be called first, because the distinct method returns null
            if (criteria.getDistinct() != null) {
                specification = specification.and(distinct(criteria.getDistinct()));
            }
            if (criteria.getId() != null) {
                specification = specification.and(buildRangeSpecification(criteria.getId(), OpsCodes_.id));
            }
            if (criteria.getCode() != null) {
                specification = specification.and(buildStringSpecification(criteria.getCode(), OpsCodes_.code));
            }
            if (criteria.getDescription() != null) {
                specification = specification.and(buildStringSpecification(criteria.getDescription(), OpsCodes_.description));
            }
        }
        return specification;
    }
}
