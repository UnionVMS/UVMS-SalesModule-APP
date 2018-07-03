package eu.europa.ec.fisheries.uvms.sales.domain.mapper;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.converter.*;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.*;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.SavedSearchGroup;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.MappingDirection;
import org.apache.commons.collections.CollectionUtils;

import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;
import java.util.ArrayList;
import java.util.List;

@Singleton
@SuppressWarnings("squid:S1192")
public class DomainMapperProducer {

    @Produces
    @FLUX
    public MapperFacade getMapper() {
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .mapNulls(false)
                .build();

        configureConverters(factory);

        configureReportType(factory);
        configureStructuredAddressType(factory);
        configureContactPartyType(factory);
        configureSalesDocument(factory);
        configureFishingActivity(factory);
        configurePartyDocument(factory);
        configureAAPProductType(factory);
        configureFLUXLocationType(factory);
        configureVesselTransportMeans(factory);
        configureAuctionSale(factory);
        configureSavedSearchGroup(factory);
        configureQuery(factory);
        configureQueryParameterType(factory);
        configureResponse(factory);
        configureReportSummary(factory);

        return factory.getMapperFacade();
    }

    private void configureConverters(MapperFactory factory) {
        ConverterFactory converterFactory = factory.getConverterFactory();

        //general converters
        converterFactory.registerConverter(new PurposeConverter());
        converterFactory.registerConverter(new FluxReportItemTypeConverter());
        //https://groups.google.com/forum/#!topic/orika-discuss/m59Ytf5ekcE
        converterFactory.registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));

        //field converters
        converterFactory.registerConverter("presentation", new PresentationConverter());
        converterFactory.registerConverter("preservation", new PreservationConverter());
        converterFactory.registerConverter("freshness", new FreshnessConverter());
        converterFactory.registerConverter("freshnessBToA", new FreshnessBToAConverter());
        converterFactory.registerConverter("presentationBToA", new PresentationBToAConverter());
        converterFactory.registerConverter("preservationBToA", new PreservationBToAConverter());
        converterFactory.registerConverter("buyerPartyDocumentConverter", new BuyerPartyDocumentConverter());
        converterFactory.registerConverter("providerPartyDocumentConverter", new ProviderPartyDocumentConverter());
        converterFactory.registerConverter("recipientPartyDocumentConverter", new RecipientPartyDocumentConverter());
    }

    private void configureResponse(MapperFactory factory) {
        factory.classMap(FLUXResponseDocumentType.class, Response.class)
                .field("IDS[0].value", "extId")
                .field("referencedID.value", "referencedId")
                .field("creationDateTime.dateTime", "creationDateTime")
                .field("responseCode.value", "responseCode")
                .field("remarks.value", "remarks")
                .field("rejectionReason.value", "rejectionReason")
                .field("typeCode.value", "typeCode")
                .field("respondentFLUXParty.IDS[0].value", "respondentFLUXParty")
                .register();
    }

    private void configureQuery(MapperFactory factory) {
        factory.classMap(SalesQueryType.class, Query.class)
                .field("ID.value", "extId")
                .field("submittedDateTime.dateTime", "submittedDate")
                .field("typeCode.value", "queryType")
                .field("specifiedDelimitedPeriod.startDateTime.dateTime", "startDate")
                .field("specifiedDelimitedPeriod.endDateTime.dateTime", "endDate")
                .field("submitterFLUXParty.IDS[0].value", "submitterFLUXPartyId")
                .field("submitterFLUXParty.names[0].value", "submitterFLUXPartyName")
                .field("simpleSalesQueryParameters", "parameters")
                .register();
    }

    private void configureQueryParameterType(MapperFactory factory) {
        factory.classMap(SalesQueryParameterType.class, QueryParameterType.class)
                .field("typeCode.value", "typeCode")
                .field("valueCode.value", "valueCode")
                .field("valueDateTime.dateTime", "valueDateTime")
                .field("valueID.value", "valueID")
                .register();
    }

    private void configureReportType(MapperFactory factory) {
        factory.classMap(Report.class, FluxReport.class)
                .field("FLUXSalesReportMessage.FLUXReportDocument.IDS[0].value", "extId")
                .field("FLUXSalesReportMessage.FLUXReportDocument.creationDateTime.dateTime", "creation")
                .field("FLUXSalesReportMessage.FLUXReportDocument.purpose.value", "purposeText")
                .field("FLUXSalesReportMessage.FLUXReportDocument.purposeCode", "purpose")
                .field("FLUXSalesReportMessage.FLUXReportDocument.ownerFLUXParty.IDS[0].value", "fluxReportParty")
                .field("FLUXSalesReportMessage.FLUXReportDocument.referencedID.value", "previousFluxReportExtId")
                .field("FLUXSalesReportMessage.salesReports[0].itemTypeCode", "itemType")
                .field("FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0]", "document")
                .field("auctionSale", "auctionSale")
                .field("deletion", "deletion")
                .register();
    }

    private void configureSavedSearchGroup(MapperFactory factory) {
        factory.classMap(SavedSearchGroup.class, eu.europa.ec.fisheries.schema.sales.SavedSearchGroup.class)
                .field("fields{key}", "fields{key}")
                .field("fields{value}", "fields{value}")
                .byDefault()
                .register();
    }


    private void configureAuctionSale(final MapperFactory factory) {
        factory.classMap(AuctionSaleType.class, AuctionSale.class)
                .field("countryCode", "countryCode")
                .field("salesCategory", "category")
                .register();
    }

    private void configureAAPProductType(MapperFactory factory) {
        factory.classMap(AAPProductType.class, Product.class)
                .field("speciesCode.value", "species")
                .field("unitQuantity.value", "quantity")
                .field("weightMeasure.value", "weight")
                .field("usageCode.value", "usage")
                .field("specifiedSizeDistribution.categoryCode.value", "distributionCategory")
                .field("specifiedSizeDistribution.classCodes[0].value", "distributionClass")
                .field("originFLUXLocations", "origins")
                .fieldMap("appliedAAPProcesses[0].typeCodes", "presentation").converter("presentation").direction(MappingDirection.A_TO_B).add()
                .fieldMap("appliedAAPProcesses[0].typeCodes", "preservation").converter("preservation").direction(MappingDirection.A_TO_B).add()
                .fieldMap("appliedAAPProcesses[0].typeCodes", "freshness").converter("freshness").direction(MappingDirection.A_TO_B).add()
                .fieldMap("appliedAAPProcesses[0].typeCodes[0]", "preservation").converter("preservationBToA").direction(MappingDirection.B_TO_A).add()
                .fieldMap("appliedAAPProcesses[0].typeCodes[1]", "presentation").converter("presentationBToA").direction(MappingDirection.B_TO_A).add()
                //Freshness is optional (for a TOD), so we can't put that as the first element in the list of typeCodes because Orika creates a new list when adding an element with index 0
                //If preservation/presentation ever become optional, a completely new converter should be created that can deal with those optionals.
                .fieldMap("appliedAAPProcesses[0].typeCodes[2]", "freshness").converter("freshnessBToA").direction(MappingDirection.B_TO_A).add()
                .field("appliedAAPProcesses[0].conversionFactorNumeric.value", "factor")
                .field("totalSalesPrice.chargeAmounts[0].value", "price")
                .register();
    }

    private void configureFishingActivity(MapperFactory factory) {
        factory.classMap(FishingActivityType.class, FishingActivity.class)
                .field("IDS[0].value", "extId")
                .field("typeCode.value", "type")
                .field("specifiedDelimitedPeriods[0].startDateTime.dateTime", "startDate")
                .field("specifiedDelimitedPeriods[0].endDateTime.dateTime", "endDate")
                .field("specifiedFishingTrip.IDS[0].value", "fishingTripId")
                .field("relatedFLUXLocations[0]", "location")
                .field("relatedVesselTransportMeans[0]", "vessel")
                .register();
    }

    private void configureVesselTransportMeans(MapperFactory factory) {
        factory.classMap(VesselTransportMeansType.class, Vessel.class)
                .field("IDS[0].value", "extId")
                .field("names[0].value", "name")
                .field("registrationVesselCountry.ID.value", "countryCode")
                .field("specifiedContactParties", "vesselContacts")
                .register();
    }


    private void configureContactPartyType(MapperFactory factory) {
        factory.classMap(ContactPartyType.class, VesselContact.class)
                .field("roleCodes[0].value", "role")
                .field("specifiedContactPersons[0].title.value", "contact.title")
                .field("specifiedContactPersons[0].givenName.value", "contact.givenName")
                .field("specifiedContactPersons[0].middleName.value", "contact.middleName")
                .field("specifiedContactPersons[0].familyName.value", "contact.familyName")
                .field("specifiedContactPersons[0].nameSuffix.value", "contact.nameSuffix")
                .field("specifiedContactPersons[0].genderCode.value", "contact.gender")
                .field("specifiedContactPersons[0].alias.value", "contact.alias")
                .register();
    }

    private void configureSalesDocument(final MapperFactory factory) {
        factory.classMap(SalesDocumentType.class, Document.class)
                .field("IDS[0].value", "extId")
                .field("specifiedSalesEvents[0].occurrenceDateTime.dateTime", "occurrence")
                .field("currencyCode.value", "currency")
                .field("totalSalesPrice.chargeAmounts[0].value", "totalPrice")
                .field("specifiedFishingActivities[0]", "fishingActivity")
                .field("specifiedSalesParties", "partyDocuments")
                .field("specifiedFLUXLocations[0]", "fluxLocation")
                .customize(new CustomMapper<SalesDocumentType, Document>() {
                    //the entity contains a bidirectional relationship. In the XSD, this is unidirectional. We need to
                    //set the other direction ourselves
                    @Override
                    public void mapAtoB(SalesDocumentType salesDocumentType, Document document, MappingContext context) {

                        if (CollectionUtils.isNotEmpty(salesDocumentType.getSpecifiedSalesBatches())) {
                            SalesBatchType salesBatchType = salesDocumentType.getSpecifiedSalesBatches().get(0);
                            List<AAPProductType> productTypes = salesBatchType.getSpecifiedAAPProducts();
                            if (CollectionUtils.isNotEmpty(productTypes)) {
                                List<Product> products = new ArrayList<>();
                                factory.getMapperFacade().mapAsCollection(productTypes, products, Product.class);

                                for (Product product : products) {
                                    product.setDocument(document);
                                }
                                document.setProducts(products);
                            }
                        }

                    }

                    @Override
                    public void mapBtoA(Document document, SalesDocumentType salesDocumentType, MappingContext context) {
                        if (CollectionUtils.isNotEmpty(document.getProducts())) {
                            List<AAPProductType> productTypes = factory.getMapperFacade().mapAsList(document.getProducts(), AAPProductType.class);
                            salesDocumentType.withSpecifiedSalesBatches(new SalesBatchType().withSpecifiedAAPProducts(productTypes));
                        }
                    }
                })
                .register();
    }

    private void configurePartyDocument(MapperFactory factory) {
        factory.classMap(SalesPartyType.class, PartyDocument.class)
                .field("ID.value", "party.extId")
                .field("name.value", "party.name")
                .field("countryID.value", "country")
                .field("roleCodes[0].value", "role")
                .field("specifiedStructuredAddresses[0]", "party.address")
                .field("specifiedFLUXOrganization.postalStructuredAddresses[0]", "party.fluxOrganizationAddress")
                .field("specifiedFLUXOrganization.name.value", "party.fluxOrganizationName")
                .register();
    }


    private void configureStructuredAddressType(MapperFactory factory) {
        factory.classMap(StructuredAddressType.class, Address.class)
                .field("blockName.value", "block")
                .field("buildingName.value", "building")
                .field("cityName.value", "city")
                .field("citySubDivisionName.value", "citySubDivision")
                .field("countryID.value", "countryCode")
                .field("countryName.value", "countryName")
                .field("countrySubDivisionName.value", "countrySubDivision")
                .field("ID.value", "extId")
                .field("plotIdentification.value", "plotId")
                .field("postOfficeBox.value", "postOfficeBox")
                .field("streetName.value", "street")
                .register();
    }

    private void configureFLUXLocationType(MapperFactory factory) {
        factory.classMap(FLUXLocationType.class, FluxLocation.class)
                .field("ID.value", "extId")
                .field("typeCode.value", "type")
                .field("countryID.value", "countryCode")
                .field("specifiedPhysicalFLUXGeographicalCoordinate.latitudeMeasure.value", "latitude")
                .field("specifiedPhysicalFLUXGeographicalCoordinate.longitudeMeasure.value", "longitude")
                .field("physicalStructuredAddress", "address")
                .register();
    }


    private void configureReportSummary(MapperFactory factory) {
        factory.classMap(ReportSummary.class, FluxReport.class)
                .field("creation", "creation")
                .field("deletion", "deletion")
                .field("category", "auctionSale.category")
                .field("extId", "extId")
                .field("occurrence", "document.occurrence")
                .field("vesselName", "document.fishingActivity.vessel.name")
                .field("vesselExtId", "document.fishingActivity.vessel.extId")
                .field("flagState", "document.fishingActivity.vessel.countryCode")
                .field("landingDate", "document.fishingActivity.startDate")
                .field("landingPort", "document.fishingActivity.location.extId")
                .field("location", "document.fluxLocation.extId")
                .field("referencedId", "previousFluxReportExtId")
                .field("purpose", "purpose")
                .fieldMap("buyer", "document.partyDocuments").converter("buyerPartyDocumentConverter").direction(MappingDirection.B_TO_A).add()
                .fieldMap("provider", "document.partyDocuments").converter("providerPartyDocumentConverter").direction(MappingDirection.B_TO_A).add()
                .fieldMap("recipient", "document.partyDocuments").converter("recipientPartyDocumentConverter").direction(MappingDirection.B_TO_A).add()
                .customize(new CustomMapper<ReportSummary, FluxReport>() {
                    @Override
                    public void mapBtoA(FluxReport report, ReportSummary reportSummary, MappingContext context) {
                        super.mapBtoA(report, reportSummary, context);

                        //if no auction sale exists, the sales category is FIRST_SALE.
                        if (reportSummary.getCategory() == null) {
                            reportSummary.setCategory(SalesCategoryType.FIRST_SALE);
                        }
                    }
                })
                .register();
    }

}