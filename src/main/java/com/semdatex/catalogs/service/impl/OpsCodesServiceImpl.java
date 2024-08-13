package com.semdatex.catalogs.service.impl;

import com.semdatex.catalogs.domain.OpsCodes;
import com.semdatex.catalogs.repository.OpsCodesRepository;
import com.semdatex.catalogs.service.OpsCodesService;
import com.semdatex.catalogs.service.dto.OpsCodesDTO;
import com.semdatex.catalogs.service.mapper.OpsCodesMapper;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link com.semdatex.catalogs.domain.OpsCodes}.
 */
@Service
@Transactional
public class OpsCodesServiceImpl implements OpsCodesService {

    private static final Logger log = LoggerFactory.getLogger(OpsCodesServiceImpl.class);

    private final OpsCodesRepository opsCodesRepository;

    private final OpsCodesMapper opsCodesMapper;

    public OpsCodesServiceImpl(OpsCodesRepository opsCodesRepository, OpsCodesMapper opsCodesMapper) {
        this.opsCodesRepository = opsCodesRepository;
        this.opsCodesMapper = opsCodesMapper;
    }

    @Override
    public OpsCodesDTO save(OpsCodesDTO opsCodesDTO) {
        log.debug("Request to save OpsCodes : {}", opsCodesDTO);
        OpsCodes opsCodes = opsCodesMapper.toEntity(opsCodesDTO);
        opsCodes = opsCodesRepository.save(opsCodes);
        return opsCodesMapper.toDto(opsCodes);
    }

    @Override
    public OpsCodesDTO update(OpsCodesDTO opsCodesDTO) {
        log.debug("Request to update OpsCodes : {}", opsCodesDTO);
        OpsCodes opsCodes = opsCodesMapper.toEntity(opsCodesDTO);
        opsCodes = opsCodesRepository.save(opsCodes);
        return opsCodesMapper.toDto(opsCodes);
    }

    @Override
    public Optional<OpsCodesDTO> partialUpdate(OpsCodesDTO opsCodesDTO) {
        log.debug("Request to partially update OpsCodes : {}", opsCodesDTO);

        return opsCodesRepository
            .findById(opsCodesDTO.getId())
            .map(existingOpsCodes -> {
                opsCodesMapper.partialUpdate(existingOpsCodes, opsCodesDTO);

                return existingOpsCodes;
            })
            .map(opsCodesRepository::save)
            .map(opsCodesMapper::toDto);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<OpsCodesDTO> findOne(Long id) {
        log.debug("Request to get OpsCodes : {}", id);
        return opsCodesRepository.findById(id).map(opsCodesMapper::toDto);
    }

    @Override
    public void delete(Long id) {
        log.debug("Request to delete OpsCodes : {}", id);
        opsCodesRepository.deleteById(id);
    }
}
