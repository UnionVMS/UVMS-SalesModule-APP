package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.converter.*;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ReferenceDataCache;
import eu.europa.ec.fisheries.uvms.sales.service.converter.BuyerSalesPartyTypeListConverter;
import eu.europa.ec.fisheries.uvms.sales.service.converter.ListFLUXLocationTypeConverter;
import eu.europa.ec.fisheries.uvms.sales.service.converter.RecipientSalesPartyTypeListConverter;
import eu.europa.ec.fisheries.uvms.sales.service.converter.SellerSalesPartyTypeListConverter;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import ma.glasnost.orika.CustomMapper;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.MappingContext;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.MappingDirection;

import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;

@Singleton
public class MapperProducer {

    @Produces
    @DTO
    public MapperFacade getMapper() {
        MapperFactory factory = new DefaultMapperFactory.Builder()
                .mapNulls(false)
                .build();

        configureConverters(factory);

        configureFluxReportDto(factory);
        configureDocumentDto(factory);
        configureLocationDto(factory);
        configureSalesDetailsDto(factory);
        configureProductDto(factory);
        configurePartyDto(factory);
        configureSavedSearchGroup(factory);
        configureSavedSearchGroupField(factory);
        configureReportFilterDto(factory);
        configurePageCriteriaDto(factory);
        configureCodeListsDto(factory);
        configureReportListDto(factory);
        configureReportListExportDto(factory);
        configureFLUXSalesQueryMessage(factory);
        configureSalesDetailsRelation(factory);
        configureObjectRepresentationToReferenceCode(factory);
        configureObjectRepresentationToReferenceCoordinates(factory);

        return factory.getMapperFacade();
    }

    private void configureConverters(MapperFactory factory) {
        ConverterFactory converterFactory = factory.getConverterFactory();

        //general converters
        converterFactory.registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));
        converterFactory.registerConverter(new ListFLUXLocationTypeConverter());
        converterFactory.registerConverter(new FluxReportItemTypeConverter());

        //field converters
        converterFactory.registerConverter("presentation", new PresentationConverter());
        converterFactory.registerConverter("preservation", new PreservationConverter());
        converterFactory.registerConverter("freshness", new FreshnessConverter());
        converterFactory.registerConverter("freshnessBToA", new FreshnessBToAConverter());
        converterFactory.registerConverter("presentationBToA", new PresentationBToAConverter());
        converterFactory.registerConverter("preservationBToA", new PreservationBToAConverter());
        converterFactory.registerConverter("buyerSalesPartyTypeListConverter", new BuyerSalesPartyTypeListConverter());
        converterFactory.registerConverter("sellerSalesPartyTypeListConverter", new SellerSalesPartyTypeListConverter());
        converterFactory.registerConverter("recipientSalesPartyTypeListConverter", new RecipientSalesPartyTypeListConverter());
        converterFactory.registerConverter("fluxReportItemTypeConverter", new FluxReportItemTypeConverter());
        converterFactory.registerConverter("purposeConverter", new PurposeConverter());
    }

    private void configureReportListExportDto(MapperFactory factory) {
        //look out: this mapper only works properly when mapping from ReportListDto to ReportListExportDto
        //not vice versa!
        factory.classMap(ReportListDto.class, ReportListExportDto.class)
                .byDefault()
                .register();
    }

    private void configureCodeListsDto(MapperFactory factory) {
        factory.classMap(CodeListsDto.class, ReferenceDataCache.class)
                .field("landingPorts", "salesLocations")
                .field("salesLocations", "salesLocations")
                .byDefault()
                .register();
    }

    private void configureSavedSearchGroup(MapperFactory factory) {
        factory.classMap(SavedSearchGroup.class, SavedSearchGroupDto.class)
                .byDefault()
                .register();
    }

    private void configureSavedSearchGroupField(MapperFactory factory) {
        factory.classMap(SavedSearchGroupField.class, FieldDto.class)
                .byDefault()
                .register();
    }

    private void configurePartyDto(MapperFactory factory) {
        factory.classMap(PartyDto.class, SalesPartyType.class)
                .field("extId", "ID.value")
                .field("name", "name.value")
                .field("role", "roleCodes[0].value")
                .register();
    }

    private void configureSalesDetailsDto(MapperFactory factory) {
        factory.classMap(SalesDetailsDto.class, Report.class)
                .field("fishingTrip.landingDate", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].specifiedDelimitedPeriods[0].startDateTime.dateTime")
                .field("fishingTrip.landingLocation", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].relatedFLUXLocations[0].ID.value")
                .field("fishingTrip.extId", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].IDS[0].value")
                .field("salesReport.deletion", "deletion")
                .field("salesReport.parties", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesParties")
                .field("salesReport.fluxReport.extId", "FLUXSalesReportMessage.FLUXReportDocument.IDS[0].value")
                .field("salesReport.fluxReport.creation", "FLUXSalesReportMessage.FLUXReportDocument.creationDateTime.dateTime")
                .fieldMap("salesReport.fluxReport.purposeCode", "FLUXSalesReportMessage.FLUXReportDocument.purposeCode").converter("purposeConverter").add()
                .field("salesReport.fluxReport.purposeText", "FLUXSalesReportMessage.FLUXReportDocument.purpose.value")
                .field("salesReport.fluxReport.fluxReportParty", "FLUXSalesReportMessage.FLUXReportDocument.ownerFLUXParty.IDS[0].value")
                .field("salesReport.location", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFLUXLocations[0]")
                .field("salesReport.products", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesBatches[0].specifiedAAPProducts")
                .field("salesReport.fluxReport.fluxReportParty", "FLUXSalesReportMessage.FLUXReportDocument.ownerFLUXParty.IDS[0].value")
                .field("salesReport.document", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0]")
                .field("salesReport.location", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFLUXLocations[0]")
                .field("salesReport.products", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesBatches[0].specifiedAAPProducts")
                .field("salesReport.category", "auctionSale.salesCategory")
                .field("salesReport.itemType", "FLUXSalesReportMessage.salesReports[0].itemTypeCode")
                .customize(new CustomMapper<SalesDetailsDto, Report>() {
                    @Override
                    public void mapBtoA(Report report, SalesDetailsDto salesDetailsDto, MappingContext context) {
                        super.mapBtoA(report, salesDetailsDto, context);

                        //if no auction sale exists, the sales category is FIRST_SALE.
                        if (salesDetailsDto.getSalesReport().getCategory() == null) {
                            salesDetailsDto.getSalesReport().setCategory(SalesCategoryType.FIRST_SALE);
                        }
                    }
                })
                .register();
    }


    private void configureProductDto(MapperFactory factory) {
        factory.classMap(AAPProductType.class, ProductDto.class)
                .field("speciesCode.value", "species")
                .field("originFLUXLocations", "areas")
                .field("unitQuantity.value", "quantity")
                .field("weightMeasure.value", "weight")
                .field("usageCode.value", "usage")
                .field("specifiedSizeDistribution.categoryCode.value", "distributionCategory")
                .field("specifiedSizeDistribution.classCodes[0].value", "distributionClass")
                .fieldMap("appliedAAPProcesses[0].typeCodes", "presentation").converter("presentation").direction(MappingDirection.A_TO_B).add()
                .fieldMap("appliedAAPProcesses[0].typeCodes", "preservation").converter("preservation").direction(MappingDirection.A_TO_B).add()
                .fieldMap("appliedAAPProcesses[0].typeCodes", "freshness").converter("freshness").direction(MappingDirection.A_TO_B).add()
                .fieldMap("appliedAAPProcesses[0].typeCodes[0]", "freshness").converter("freshnessBToA").direction(MappingDirection.B_TO_A).add()
                .fieldMap("appliedAAPProcesses[0].typeCodes[1]", "presentation").converter("presentationBToA").direction(MappingDirection.B_TO_A).add()
                .fieldMap("appliedAAPProcesses[0].typeCodes[2]", "preservation").converter("preservationBToA").direction(MappingDirection.B_TO_A).add()
                .field("appliedAAPProcesses[0].conversionFactorNumeric.value", "factor")
                .register();
    }


    private void configureLocationDto(MapperFactory factory) {
        factory.classMap(FLUXLocationType.class, LocationDto.class)
                .field("ID.value", "extId")
                .field("countryID.value", "country")
                .field("specifiedPhysicalFLUXGeographicalCoordinate.latitudeMeasure.value", "latitude")
                .field("specifiedPhysicalFLUXGeographicalCoordinate.longitudeMeasure.value", "longitude")
                .register();
    }

    private void configureDocumentDto(MapperFactory factory) {
        factory.classMap(DocumentDto.class, SalesDocumentType.class)
                .field("extId", "IDS[0].value")
                .field("currency", "currencyCode.value")
                .field("occurrence", "specifiedSalesEvents[0].occurrenceDateTime.dateTime")
                .register();
    }

    private void configureFluxReportDto(MapperFactory factory) {
        factory.classMap(FluxReportDto.class, FLUXReportDocumentType.class)
                .field("extId", "IDS[0].value")
                .field("creation", "creationDateTime.dateTime")
                .field("purposeCode", "purposeCode.value")
                .field("purposeText", "purpose.value")
                .field("fluxReportParty", "ownerFLUXParty.names[0].value")
                .register();
    }

    private void configureReportFilterDto(MapperFactory factory) {
        factory.classMap(ReportQueryFilterDto.class, ReportQueryFilter.class)
                .byDefault()
                .register();
    }

    private void configurePageCriteriaDto(MapperFactory factory) {
        factory.classMap(PageCriteriaDto.class, ReportQuery.class)
                .field("filters", "filters")
                .field("pageIndex", "paging.page")
                .field("pageSize", "paging.itemsPerPage")
                .field("sortField", "sorting.field")
                .field("sortDirection", "sorting.direction")
                .register();
    }

    private void configureReportListDto(MapperFactory factory) {
        factory.classMap(ReportListDto.class, Report.class)
                .field("deletion", "deletion")
                .field("category", "auctionSale.salesCategory")
                .field("extId", "FLUXSalesReportMessage.FLUXReportDocument.IDS[0].value")
                .field("occurrence", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesEvents[0].occurrenceDateTime.dateTime")
                .field("vesselName", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].relatedVesselTransportMeans[0].names[0].value")
                .field("vesselExtId", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].relatedVesselTransportMeans[0].IDS[0].value")
                .field("flagState", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].relatedVesselTransportMeans[0].registrationVesselCountry.ID.value")
                .field("landingDate", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].specifiedDelimitedPeriods[0].startDateTime.dateTime")
                .field("landingPort", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].relatedFLUXLocations[0].ID.value")
                .field("location", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFLUXLocations[0].ID.value")
                .field("referencedId", "FLUXSalesReportMessage.FLUXReportDocument.referencedID.value")
                .field("purpose", "FLUXSalesReportMessage.FLUXReportDocument.purposeCode.value")
                .fieldMap("buyer", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesParties").converter("buyerSalesPartyTypeListConverter").direction(MappingDirection.B_TO_A).add()
                .fieldMap("seller", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesParties").converter("sellerSalesPartyTypeListConverter").direction(MappingDirection.B_TO_A).add()
                .fieldMap("recipient", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesParties").converter("recipientSalesPartyTypeListConverter").direction(MappingDirection.B_TO_A).add()
                .customize(new CustomMapper<ReportListDto, Report>() {
                    @Override
                    public void mapBtoA(Report report, ReportListDto reportListDto, MappingContext context) {
                        super.mapBtoA(report, reportListDto, context);

                        //if no auction sale exists, the sales category is FIRST_SALE.
                        if (reportListDto.getCategory() == null) {
                            reportListDto.setCategory(SalesCategoryType.FIRST_SALE);
                        }
                    }
                })
                .register();
    }

    private void configureFLUXSalesQueryMessage(MapperFactory factory) {
        factory.classMap(FLUXSalesQueryMessage.class, ReportQuery.class)
                .field("salesQuery.specifiedDelimitedPeriod.startDateTime.dateTime", "filters.salesStartDate")
                .field("salesQuery.specifiedDelimitedPeriod.endDateTime.dateTime", "filters.salesEndDate")
                .customize(new SalesQueryParameterTypeCustomMapper())
                .register();
    }

    private void configureSalesDetailsRelation(MapperFactory factory) {
        factory.classMap(Report.class, SalesDetailsRelation.class)
                .field("FLUXSalesReportMessage.FLUXReportDocument.IDS[0].value", "reportExtId")
                .field("FLUXSalesReportMessage.salesReports[0].itemTypeCode", "type")
                .field("FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].IDS[0].value", "documentExtId")
                .register();
    }

    private void configureObjectRepresentationToReferenceCoordinates(MapperFactory factory) {
        factory.registerMapper(new ObjectRepresentationToReferenceCoordinatesCustomMapper());
    }

    private void configureObjectRepresentationToReferenceCode(MapperFactory factory) {
        factory.registerMapper(new ObjectRepresentationToReferenceCodeCustomMapper());
    }

}