package eu.europa.ec.fisheries.uvms.sales.service.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.Report;
import eu.europa.ec.fisheries.schema.sales.SalesDocumentType;
import eu.europa.ec.fisheries.uvms.sales.domain.DocumentDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.QueryDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.ResponseDomainModel;
import eu.europa.ec.fisheries.uvms.sales.service.UniqueIdService;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import java.util.List;


@Stateless
public class UniqueIdServiceBean implements UniqueIdService {

    @EJB
    private DocumentDomainModel documentDomainModel;

    @EJB
    private ReportDomainModel reportDomainModel;

    @EJB
    private QueryDomainModel queryDomainModel;

    @EJB
    private ResponseDomainModel responseDomainModel;

    @Override
    public boolean doesAnySalesDocumentExistWithAnyOfTheseIds(List<String> extIds) {
        for (String extId : extIds) {
            Optional<SalesDocumentType> salesDocumentTypeOptional = documentDomainModel.findByExtId(extId);
            if (salesDocumentTypeOptional.isPresent()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean doesAnyTakeOverDocumentExistWithAnyOfTheseIds(List<String> extIds) {
        for (String extId : extIds) {
            Optional<Report> takeOverDocumentByExtId = reportDomainModel.findTakeOverDocumentByExtId(extId);
            if (takeOverDocumentByExtId.isPresent()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isQueryIdUnique(String extId) {
        return !queryDomainModel.findByExtId(extId).isPresent();
    }

    @Override
    public boolean isResponseIdUnique(String extId) {
        return !responseDomainModel.findByExtId(extId).isPresent();
    }

    @Override
    public boolean doesReferencedReportInResponseExist(String referencedId) {
        // We have to check if the referencedId exists either as a report or as a query

        boolean idExistsAsReport = Optional.fromNullable(reportDomainModel.findByExtIdOrNull(referencedId)).isPresent();
        if (idExistsAsReport) {
            return true;
        }

        boolean idExistsAsQuery = queryDomainModel.findByExtId(referencedId).isPresent();
        if (idExistsAsQuery) {
            return true;
        }

        return false;
    }


}
