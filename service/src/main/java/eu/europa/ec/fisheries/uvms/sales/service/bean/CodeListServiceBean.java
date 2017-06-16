package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.uvms.sales.service.CodeListService;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ReferenceDataCache;
import eu.europa.ec.fisheries.uvms.sales.service.dto.CodeListsDto;
import eu.europa.ec.fisheries.uvms.sales.service.mapper.DTO;
import ma.glasnost.orika.MapperFacade;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

@Stateless
public class CodeListServiceBean implements CodeListService {

    @Inject @DTO
    private MapperFacade mapper;

    @EJB
    private ReferenceDataCache referenceDataCache;

    @Override
    public CodeListsDto getCodeLists() {
        CodeListsDto codeListsDto = mapper.map(referenceDataCache, CodeListsDto.class);
        return codeListsDto;
    }
}
