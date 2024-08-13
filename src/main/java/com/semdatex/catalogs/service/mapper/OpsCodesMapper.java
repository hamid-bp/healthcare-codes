package com.semdatex.catalogs.service.mapper;

import com.semdatex.catalogs.domain.OpsCodes;
import com.semdatex.catalogs.service.dto.OpsCodesDTO;
import org.mapstruct.*;

/**
 * Mapper for the entity {@link OpsCodes} and its DTO {@link OpsCodesDTO}.
 */
@Mapper(componentModel = "spring")
public interface OpsCodesMapper extends EntityMapper<OpsCodesDTO, OpsCodes> {}
