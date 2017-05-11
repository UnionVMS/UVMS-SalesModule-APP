package eu.europa.ec.fisheries.uvms.sales.service.mapper;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ReferenceDataCache;
import eu.europa.ec.fisheries.uvms.sales.service.converter.*;
import eu.europa.ec.fisheries.uvms.sales.service.dto.*;
import ma.glasnost.orika.MapperFacade;
import ma.glasnost.orika.MapperFactory;
import ma.glasnost.orika.converter.ConverterFactory;
import ma.glasnost.orika.converter.builtin.PassThroughConverter;
import ma.glasnost.orika.impl.DefaultMapperFactory;
import ma.glasnost.orika.metadata.MappingDirection;

import javax.ejb.Singleton;
import javax.enterprise.inject.Produces;

@Singleton
public class MapperProducer {

    @Produces
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

        return factory.getMapperFacade();
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


    private void configureConverters(MapperFactory factory) {
        ConverterFactory converterFactory = factory.getConverterFactory();

        converterFactory.registerConverter(new PassThroughConverter(org.joda.time.DateTime.class));

        //general converters
        converterFactory.registerConverter(new ListFLUXLocationTypeConverter());

        //field converters
        converterFactory.registerConverter("presentation", new PresentationConverter());
        converterFactory.registerConverter("preservation", new PreservationConverter());
        converterFactory.registerConverter("freshness", new FreshnessConverter());
        converterFactory.registerConverter("freshnessBToA", new FreshnessBToAConverter());
        converterFactory.registerConverter("presentationBToA", new PresentationBToAConverter());
        converterFactory.registerConverter("preservationBToA", new PreservationBToAConverter());
        converterFactory.registerConverter("buyerSalesPartyTypeListConverter", new BuyerSalesPartyTypeListConverter());
        converterFactory.registerConverter("sellerSalesPartyTypeListConverter", new SellerSalesPartyTypeListConverter());
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
                .field("salesNote.parties", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesParties")
                .field("salesNote.fluxReport.extId", "FLUXSalesReportMessage.FLUXReportDocument.IDS[0].value")
                .field("salesNote.fluxReport.creation", "FLUXSalesReportMessage.FLUXReportDocument.creationDateTime.dateTime")
                .field("salesNote.fluxReport.purposeCode", "FLUXSalesReportMessage.FLUXReportDocument.purposeCode.value")
                .field("salesNote.fluxReport.purposeText", "FLUXSalesReportMessage.FLUXReportDocument.purpose.value")
                .field("salesNote.fluxReport.fluxReportParty", "FLUXSalesReportMessage.FLUXReportDocument.ownerFLUXParty.IDS[0].value")
                .field("salesNote.location", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFLUXLocations[0]")
                .field("salesNote.products", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesBatches[0].specifiedAAPProducts")
                .field("salesNote.fluxReport.fluxReportParty", "FLUXSalesReportMessage.FLUXReportDocument.ownerFLUXParty.IDS[0].value")
                .field("salesNote.document", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0]")
                .field("salesNote.location", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFLUXLocations[0]")
                .field("salesNote.products", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesBatches[0].specifiedAAPProducts")
                .field("salesNote.category", "auctionSale.salesCategory")
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
                .field("category", "auctionSale.salesCategory")
                .field("extId", "FLUXSalesReportMessage.FLUXReportDocument.IDS[0].value")
                .field("occurrence", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesEvents[0].occurrenceDateTime.dateTime")
                .field("vesselName", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].relatedVesselTransportMeans[0].names[0].value")
                .field("vesselExtId", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].relatedVesselTransportMeans[0].IDS[0].value")
                .field("flagState", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].relatedVesselTransportMeans[0].specifiedRegistrationEvents[0].relatedRegistrationLocation.countryID.value")
                .field("landingDate", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].specifiedDelimitedPeriods[0].startDateTime.dateTime")
                .field("landingPort", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFishingActivities[0].relatedFLUXLocations[0].ID.value")
                .field("location", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedFLUXLocations[0].ID.value")
                .fieldMap("buyer", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesParties").converter("buyerSalesPartyTypeListConverter").direction(MappingDirection.B_TO_A).add()
                .fieldMap("seller", "FLUXSalesReportMessage.salesReports[0].includedSalesDocuments[0].specifiedSalesParties").converter("sellerSalesPartyTypeListConverter").direction(MappingDirection.B_TO_A).add()
                .register();
    }

    private void configureFLUXSalesQueryMessage(MapperFactory factory) {
        factory.classMap(FLUXSalesQueryMessage.class, ReportQuery.class)
                .field("salesQuery.specifiedDelimitedPeriod.startDateTime.dateTime", "filters.salesStartDate")
                .field("salesQuery.specifiedDelimitedPeriod.endDateTime.dateTime", "filters.salesEndDate")
                .customize(new SalesQueryParameterTypeCustomMapper())
                .register();
    }
}