package eu.europa.ec.fisheries.uvms.sales.domain.helper;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import org.joda.time.DateTime;

import javax.ejb.Stateless;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Helper class to provide convenience methods to retrieve information from a report.
 * A more ideal solution would be: have these methods in the domain classes. But since we work with a generated
 * contract, this is not an option.
 */
@Stateless
public class ReportHelper {

    public boolean isReportCorrected(Report report) {
        String purposeAsString = getFluxReportDocument(report).getPurposeCode().getValue();
        Purpose purpose = Purpose.forNumericCode(Integer.parseInt(purposeAsString));
        return purpose.equals(Purpose.CORRECTION);
    }

    public boolean isReportDeleted(Report report) {
        String purposeAsString = getFluxReportDocument(report).getPurposeCode().getValue();
        Purpose purpose = Purpose.forNumericCode(Integer.parseInt(purposeAsString));
        return purpose.equals(Purpose.DELETE);
    }

    public String getSalesLocationCountry(Report report) {
        return getSalesDocument(report)
                .getSpecifiedFLUXLocations().get(0)
                .getCountryID().getValue();
    }

    public String getLandingCountry(Report report) {
        FishingActivityType fishingActivity = getFishingActivity(report);
        return fishingActivity.getRelatedFLUXLocations().get(0).getCountryID().getValue();
    }

    public String getVesselFlagState(Report report) {
        VesselTransportMeansType vessel = getVessel(report);
        return vessel.getRegistrationVesselCountry().getID().getValue();
    }

    public String getVesselExtId(Report report) {
        return getVessel(report)
                .getIDS().get(0).getValue();
    }

    public String getDocumentCurrency(Report report) {
        return getSalesDocument(report)
                .getCurrencyCode()
                .getValue();
    }

    public DateTime getDocumentDate(Report report) {
        return getSalesDocument(report)
                .getSpecifiedSalesEvents().get(0)
                .getOccurrenceDateTime()
                .getDateTime();
    }

    public List<AAPProductType> getProductsOfReport(Report report) {
        return getSalesDocument(report)
                .getSpecifiedSalesBatches().get(0)
                .getSpecifiedAAPProducts();
    }

    public FLUXPartyType getFLUXReportDocumentOwner(Report report) {
        return getFluxReportDocument(report)
                .getOwnerFLUXParty();
    }

    public String getFLUXReportDocumentId(Report report) {
        return getFluxReportDocument(report).getIDS().get(0).getValue();
    }

    private SalesDocumentType getSalesDocument(Report report) {
        return report
                .getFLUXSalesReportMessage()
                .getSalesReports().get(0)
                .getIncludedSalesDocuments().get(0);
    }

    private SalesDocumentType getSalesDocumentOrNull(Report report) {
        try {
            return getSalesDocument(report);
        } catch (NullPointerException | IndexOutOfBoundsException e) {
            return null;
        }
    }

    private VesselTransportMeansType getVessel(Report report) {
        FishingActivityType fishingActivity = getFishingActivity(report);
        return fishingActivity.getRelatedVesselTransportMeans().get(0);
    }

    private FishingActivityType getFishingActivity(Report report) {
        return getSalesDocument(report)
                .getSpecifiedFishingActivities().get(0);
    }

    private FLUXReportDocumentType getFluxReportDocument(Report report) {
        return report.getFLUXSalesReportMessage()
                .getFLUXReportDocument();
    }

    public String getFLUXReportDocumentOwnerId(Report report) {
        return getFLUXReportDocumentOwner(report).getIDS().get(0).getValue();
    }

    public String getFLUXReportDocumentReferencedId(Report report) {
        return getFluxReportDocument(report).getReferencedID().getValue();
    }

    public String getFLUXReportDocumentReferencedIdOrNull(Report report) {
        try {
            return getFLUXReportDocumentReferencedId(report);
        } catch (NullPointerException e) {
            return null;
        }
    }

    public String getId(Report report) {
        return getId(report.getFLUXSalesReportMessage());
    }

    public String getId(FLUXSalesReportMessage reportMessage) {
        return reportMessage.getFLUXReportDocument().getIDS().get(0).getValue();
    }

    public DateTime getCreationDate(Report report) {
        return getFluxReportDocument(report)
                    .getCreationDateTime()
                    .getDateTime();
    }

    public boolean hasReferencesToTakeOverDocuments(Report report) {
        SalesDocumentType salesDocument = getSalesDocumentOrNull(report);
        if (salesDocument != null) {
            return !salesDocument
                    .getTakeoverDocumentIDs()
                    .isEmpty();
        } else {
            return false;
        }
    }

    public List<String> getReferenceIdsToTakeOverDocuments(Report report) {
        List<String> ids = new ArrayList<>();
        SalesDocumentType salesDocument = getSalesDocumentOrNull(report);
        if (salesDocument != null) {
            List<IDType> takeoverDocumentIDs = salesDocument.getTakeoverDocumentIDs();
            for (IDType id : takeoverDocumentIDs) {
                ids.add(id.getValue());
            }
        }
        return ids;
    }

    public boolean isFirstSaleOrNegotiatedSale(Report report) {
        List<SalesCategoryType> salesCategoryTypes = Arrays.asList(SalesCategoryType.FIRST_SALE, SalesCategoryType.NEGOTIATED_SALE);
        return report.getAuctionSale() != null
                && salesCategoryTypes.contains(report.getAuctionSale().getSalesCategory());
    }
}

