package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.sales.service.dto.CodeListsDto;

import javax.ejb.Local;

@Local
public interface CodeListService {

    CodeListsDto getCodeLists();

}