package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import com.sun.org.apache.xpath.internal.operations.Bool;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.DocumentDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.UniqueIdService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;

/**
 * Created by MATBUL on 31/08/2017.
 */
@Stateless
public class UniqueIdServiceBean implements UniqueIdService {

    @EJB
    DocumentDomainModel documentDomainModel;

    @EJB
    ReportDomainModel reportDomainModel;


    @Override
    public Boolean doesAnySalesDocumentExistWithAnyOfTheseIds(List<String> extIds) {
        for (String extId : extIds) {
            Optional<SalesDocumentType> salesDocumentTypeOptional = documentDomainModel.findByExtId(extId);
            if (salesDocumentTypeOptional.isPresent()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public Boolean doesAnyTakeOverDocumentExistWithAnyOfTheseIds(List<String> extIds) {
        for (String extId : extIds) {
            Optional<Report> takeOverDocumentByExtId = reportDomainModel.findTakeOverDocumentByExtId(extId);
            if (takeOverDocumentByExtId.isPresent()) {
                return true;
            }
        }

        return false;
    }
}
