package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.uvms.sales.service.constants.MDRCodeListKey;
import un.unece.uncefact.data.standard.mdr.communication.ObjectRepresentation;

import java.util.List;

public interface MDRService {

    List<ObjectRepresentation> findCodeList(MDRCodeListKey acronym);

}
