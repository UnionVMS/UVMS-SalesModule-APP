package eu.europa.ec.fisheries.uvms.sales.service.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.*;
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

    @EJB
    private UnsavedMessageDomainModel unsavedMessageDomainModel;

    @Override
    public boolean doesAnySalesDocumentExistWithAnyOfTheseIds(List<String> extIds) {
        for (String extId : extIds) {
            if (documentDomainModel.findByExtId(extId).isPresent()) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean doesAnySalesReportExistWithAnyOfTheseIds(List<String> extIds) {
        for (String extId : extIds) {
            if (reportDomainModel.findByExtId(extId).isPresent() || unsavedMessageDomainModel.exists(extId)) {
                return true;
            }
        }

        return false;
    }

    @Override
    public boolean isQueryIdUnique(String extId) {
        return !(queryDomainModel.findByExtId(extId).isPresent()
                || unsavedMessageDomainModel.exists(extId));
    }

    @Override
    public boolean isResponseIdUnique(String extId) {
        return !responseDomainModel.findByExtId(extId).isPresent();
    }

    @Override
    public boolean doesReferencedReportInResponseExist(String referencedId) {
        return reportDomainModel.findByExtId(referencedId).isPresent()
                || queryDomainModel.findByExtId(referencedId).isPresent()
                || unsavedMessageDomainModel.exists(referencedId);
    }


}
