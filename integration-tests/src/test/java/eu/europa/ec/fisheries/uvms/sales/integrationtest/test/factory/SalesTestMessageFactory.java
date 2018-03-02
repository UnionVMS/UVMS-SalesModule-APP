package eu.europa.ec.fisheries.uvms.sales.integrationtest.test.factory;

import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.SalesModuleRequestMapper;
import org.apache.commons.io.IOUtils;

import javax.ejb.Stateless;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Stateless
public class SalesTestMessageFactory {

    public String composeSalesReportRequestAsString(String messageGuid, String vesselFlagState, String landingCountry) throws Exception {
        String request = composeFLUXSalesReportMessageAsString(messageGuid, vesselFlagState, landingCountry);
        String messageValidationStatus = "OK";
        String pluginToSendResponseThrough = "BELGIAN_SALES";
        List<ValidationQualityAnalysisType> validationQualityAnalysisList = new ArrayList<>();
        return SalesModuleRequestMapper.createSalesReportRequest(request, messageValidationStatus, validationQualityAnalysisList, pluginToSendResponseThrough);
    }

    public String composeFLUXSalesReportMessageAsString(String messageGuid, String vesselFlagState, String landingCountry) {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns4:Report xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xmlns:ns4=\"eu.europa.ec.fisheries.schema.sales\" xmlns:ns3=\"eu.europa.ec.fisheries.schema.sales.flux\">\n" +
                "<ns4:FLUXSalesReportMessage>\n" +
                "<ns3:FLUXReportDocument>\n" +
                "<ID schemeID=\"UUID\">" + messageGuid + "</ID>\n" +
                "<ReferencedID schemeID=\"UUID\">" + messageGuid + "</ReferencedID>\n" +
                "<CreationDateTime>\n" +
                "<ns2:DateTime>2017-05-11T12:10:38Z</ns2:DateTime>\n" +
                "</CreationDateTime>\n" +
                "<PurposeCode listID=\"FLUX_GP_PURPOSE\">5</PurposeCode>\n" +
                "<Purpose>Test correction post</Purpose>\n" +
                "<OwnerFLUXParty>\n" +
                "<ID schemeID=\"FLUX_GP_PARTY\">BE1</ID>\n" +
                "</OwnerFLUXParty>\n" +
                "</ns3:FLUXReportDocument>\n" +
                "<ns3:SalesReport>\n" +
                "<ItemTypeCode listID=\"FLUX_SALES_TYPE\">SN</ItemTypeCode>\n" +
                "<IncludedSalesDocument>\n" +
                "<ID schemeID=\"EU_SALES_ID\">BEL-SN-2007-7777777</ID>\n" +
                "<CurrencyCode listID=\"TERRITORY_CURR\">DKK</CurrencyCode>\n" +
                "<SpecifiedSalesBatch>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">PLE</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">6</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">123456789</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>1.31</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">1</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">PLE</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">36</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>1.29</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">DAB</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">517</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>1.12</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">COD</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">13</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">GUT</TypeCode>\n" +
                "<ConversionFactorNumeric>20</ConversionFactorNumeric>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>2</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">3</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">FLE</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">102</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>0.82</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">LIN</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">9</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">E</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">GUT</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>3.55</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">3</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.7.A</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "</SpecifiedSalesBatch>\n" +
                "<SpecifiedSalesEvent>\n" +
                "<OccurrenceDateTime>\n" +
                "<ns2:DateTime>2017-10-16T07:05:22Z</ns2:DateTime>\n" +
                "</OccurrenceDateTime>\n" +
                "</SpecifiedSalesEvent>\n" +
                "<SpecifiedFishingActivity>\n" +
                "<TypeCode>LAN</TypeCode>\n" +
                "<RelatedFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</TypeCode>\n" +
                "<CountryID schemeID=\"TERRITORY\">" + landingCountry + "</CountryID>\n" +
                "<ID schemeID=\"LOCATION\">BEOST</ID>\n" +
                "</RelatedFLUXLocation>\n" +
                "<SpecifiedDelimitedPeriod>\n" +
                "<StartDateTime>\n" +
                "<ns2:DateTime>2017-05-10T05:32:30Z</ns2:DateTime>\n" +
                "</StartDateTime>\n" +
                "</SpecifiedDelimitedPeriod>\n" +
                "<SpecifiedFishingTrip>\n" +
                "<ID schemeID=\"EU_TRIP_ID\">BEL-TRP-20171610</ID>\n" +
                "</SpecifiedFishingTrip>\n" +
                "<RelatedVesselTransportMeans>\n" +
                "<ID schemeID=\"CFR\">BEL123456799</ID>\n" +
                "<Name>FAKE VESSEL2</Name>\n" +
                "<RegistrationVesselCountry>\n" +
                "<ID schemeID=\"TERRITORY\">" + vesselFlagState + "</ID>\n" +
                "</RegistrationVesselCountry>\n" +
                "<SpecifiedContactParty>\n" +
                "<RoleCode listID=\"FLUX_CONTACT_ROLE\">MASTER</RoleCode>\n" +
                "<SpecifiedContactPerson>\n" +
                "<GivenName>Henrick</GivenName>\n" +
                "<MiddleName>Jan</MiddleName>\n" +
                "<FamilyName>JANSEN</FamilyName>\n" +
                "</SpecifiedContactPerson>\n" +
                "</SpecifiedContactParty>\n" +
                "</RelatedVesselTransportMeans>\n" +
                "</SpecifiedFishingActivity>\n" +
                "<SpecifiedFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</TypeCode>\n" +
                "<CountryID schemeID=\"TERRITORY\">BEL</CountryID>\n" +
                "<ID schemeID=\"LOCATION\">BEOST</ID>\n" +
                "</SpecifiedFLUXLocation>\n" +
                "<SpecifiedSalesParty>\n" +
                "<ID schemeID=\"MS\">123456</ID>\n" +
                "<Name>Mr SENDER</Name>\n" +
                "<RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">SENDER</RoleCode>\n" +
                "</SpecifiedSalesParty>\n" +
                "<SpecifiedSalesParty>\n" +
                "<ID schemeID=\"VAT\">0679223791</ID>\n" +
                "<Name>Mr BUYER</Name>\n" +
                "<RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">BUYER</RoleCode>\n" +
                "</SpecifiedSalesParty>\n" +
                "<SpecifiedSalesParty>\n" +
                "<Name>Mr PROVIDER</Name>\n" +
                "<RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">PROVIDER</RoleCode>\n" +
                "</SpecifiedSalesParty>\n" +
                "</IncludedSalesDocument>\n" +
                "</ns3:SalesReport>\n" +
                "</ns4:FLUXSalesReportMessage>\n" +
                "<ns4:AuctionSale>\n" +
                "<ns4:CountryCode>BE4</ns4:CountryCode>\n" +
                "<ns4:SalesCategory>FIRST_SALE</ns4:SalesCategory>\n" +
                "</ns4:AuctionSale>\n" +
                "</ns4:Report>\n";
    }

    public String composePullSettingsRequest() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns2:PullSettingsRequest xmlns:ns2=\"urn:module.config.schema.fisheries.ec.europa.eu:v1\">\n" +
                "    <method>PULL</method>\n" +
                "    <moduleName>sales</moduleName>\n" +
                "</ns2:PullSettingsRequest>\n";
    }

    public String composeFLUXSalesReportMessageAsString_BAD() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns4:Report xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xmlns:ns4=\"eu.europa.ec.fisheries.schema.sales\" xmlns:ns3=\"eu.europa.ec.fisheries.schema.sales.flux\">\n" +
                "<ns4:FLUXSalesReportMessage>\n" +
                "<ns3:FLUXReportDocument>\n" +
                "<ID schemeID=\"UUID\">d5da24ff-42b4-5e76-967f-ad97762a0312</ID>\n" +
                "<ReferencedID schemeID=\"UUID\">d5da24ff-c3b3-4e76-9785-ac97762a0312</ReferencedID>\n" +
                "<CreationDateTime>\n" +
                "<ns2:DateTime>2017-05-11T12:10:38Z</ns2:DateTime>\n" +
                "</CreationDateTime>\n" +
                "<PurposeCode listID=\"FLUX_GP_PURPOSE\">5</PurposeCode>\n" +
                "<Purpose>Test correction post</Purpose>\n" +
                "<OwnerFLUXParty>\n" +
                "<ID schemeID=\"FLUX_GP_PARTY\">BEL</ID>\n" +
                "</OwnerFLUXParty>\n" +
                "</ns3:FLUXReportDocument>\n" +
                "<ns3:SalesReport>\n" +
                "<ItemTypeCode listID=\"FLUX_SALES_TYPE\">SN</ItemTypeCode>\n" +
                "<IncludedSalesDocument>\n" +
                "<ID schemeID=\"EU_SALES_ID\">BEL-SN-2007-7777777</ID>\n" +
                "<CurrencyCode listID=\"TERRITORY_CURR\">DKK</CurrencyCode>\n" +
                "<SpecifiedSalesBatch>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">PLE</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">6</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">123456789</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>1.31</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">1</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">PLE</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">36</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>1.29</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">DAB</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">517</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>1.12</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">COD</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">13</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">GUT</TypeCode>\n" +
                "<ConversionFactorNumeric>20</ConversionFactorNumeric>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>2</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">3</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">FLE</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">102</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>0.82</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">LIN</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">9</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">E</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">GUT</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>3.55</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">3</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.7.A</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "</SpecifiedSalesBatch>\n" +
                "<SpecifiedSalesEvent>\n" +
                "<OccurrenceDateTime>\n" +
                "<ns2:DateTime>2017-10-16T07:05:22Z</ns2:DateTime>\n" +
                "</OccurrenceDateTime>\n" +
                "</SpecifiedSalesEvent>\n" +
                "<SpecifiedFishingActivity>\n" +
                "<TypeCode>LAN</TypeCode>\n" +
                "<RelatedFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</TypeCode>\n" +
                "<CountryID schemeID=\"TERRITORY\">BEL_MOD</CountryID>\n" +
                "<ID schemeID=\"LOCATION\">BEOST</ID>\n" +
                "</RelatedFLUXLocation>\n" +
                "<SpecifiedDelimitedPeriod>\n" +
                "<StartDateTime>\n" +
                "<ns2:DateTime>2017-05-10T05:32:30Z</ns2:DateTime>\n" +
                "</StartDateTime>\n" +
                "</SpecifiedDelimitedPeriod>\n" +
                "<SpecifiedFishingTrip>\n" +
                "<ID schemeID=\"EU_TRIP_ID\">BEL-TRP-20171610</ID>\n" +
                "</SpecifiedFishingTrip>\n" +
                "<RelatedVesselTransportMeans>\n" +
                "<ID schemeID=\"CFR\">BEL123456799</ID>\n" +
                "<Name>FAKE VESSEL2</Name>\n" +
                "<RegistrationVesselCountry>\n" +
                "<ID schemeID=\"TERRITORY\">BEL</ID>\n" +
                "</RegistrationVesselCountry>\n" +
                "<SpecifiedContactParty>\n" +
                "<RoleCode listID=\"FLUX_CONTACT_ROLE\">MASTER</RoleCode>\n" +
                "<SpecifiedContactPerson>\n" +
                "<GivenName>Henrick</GivenName>\n" +
                "<MiddleName>Jan</MiddleName>\n" +
                "<FamilyName>JANSEN</FamilyName>\n" +
                "</SpecifiedContactPerson>\n" +
                "</SpecifiedContactParty>\n" +
                "</RelatedVesselTransportMeans>\n" +
                "</SpecifiedFishingActivity>\n" +
                "<SpecifiedFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</TypeCode>\n" +
                "<CountryID schemeID=\"TERRITORY\">BEL</CountryID>\n" +
                "<ID schemeID=\"LOCATION\">BEOST</ID>\n" +
                "</SpecifiedFLUXLocation>\n" +
                "<SpecifiedSalesParty>\n" +
                "<ID schemeID=\"MS\">123456</ID>\n" +
                "<Name>Mr SENDER</Name>\n" +
                "<RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">SENDER</RoleCode>\n" +
                "</SpecifiedSalesParty>\n" +
                "<SpecifiedSalesParty>\n" +
                "<ID schemeID=\"VAT\">0679223791</ID>\n" +
                "<Name>Mr BUYER</Name>\n" +
                "<RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">BUYER</RoleCode>\n" +
                "</SpecifiedSalesParty>\n" +
                "<SpecifiedSalesParty>\n" +
                "<Name>Mr PROVIDER</Name>\n" +
                "<RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">PROVIDER</RoleCode>\n" +
                "</SpecifiedSalesParty>\n" +
                "</IncludedSalesDocument>\n" +
                "</ns3:SalesReport>\n" +
                "</ns4:FLUXSalesReportMessage>\n" +
                "<ns4:AuctionSale>\n" +
                "<ns4:CountryCode>BEL</ns4:CountryCode>\n" +
                "<ns4:SalesCategory>FIRST_SALE</ns4:SalesCategory>\n" +
                "</ns4:AuctionSale>\n" +
                "</ns4:Report>\n";
    }

    public String composeFLUXSalesReportMessageNoRedeliveryAsString() {
        return "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"yes\"?>\n" +
                "<ns4:Report xmlns=\"urn:un:unece:uncefact:data:standard:ReusableAggregateBusinessInformationEntity:20\" xmlns:ns2=\"urn:un:unece:uncefact:data:standard:UnqualifiedDataType:20\" xmlns:ns4=\"eu.europa.ec.fisheries.schema.sales\" xmlns:ns3=\"eu.europa.ec.fisheries.schema.sales.flux\">\n" +
                "<ns4:FLUXSalesReportMessage>\n" +
                "<ns3:FLUXReportDocument>\n" +
                "<ID schemeID=\"UUID\">d5da24ff-42b4-5e76-967f-ad97762a0311</ID>\n" +
                "<ReferencedID schemeID=\"UUID\">d5da24ff-c3b3-4e76-9785-ac97762a0311</ReferencedID>\n" +
                "<CreationDateTime>\n" +
                "<ns2:DateTime>2017-05-11T12:10:38Z</ns2:DateTime>\n" +
                "</CreationDateTime>\n" +
                "<PurposeCode listID=\"FLUX_GP_PURPOSE\">5</PurposeCode>\n" +
                "<Purpose>Test correction post</Purpose>\n" +
                "<OwnerFLUXParty>\n" +
                "<ID schemeID=\"FLUX_GP_PARTY\">BE1</ID>\n" +
                "</OwnerFLUXParty>\n" +
                "</ns3:FLUXReportDocument>\n" +
                "<ns3:SalesReport>\n" +
                "<ItemTypeCode listID=\"FLUX_SALES_TYPE\">SN</ItemTypeCode>\n" +
                "<IncludedSalesDocument>\n" +
                "<ID schemeID=\"EU_SALES_ID\">BEL-SN-2007-7777777</ID>\n" +
                "<CurrencyCode listID=\"TERRITORY_CURR\">DKK</CurrencyCode>\n" +
                "<SpecifiedSalesBatch>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">PLE</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">6</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">123456789</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>1.31</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">1</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">PLE</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">36</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>1.29</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">DAB</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">517</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>1.12</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">COD</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">13</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">GUT</TypeCode>\n" +
                "<ConversionFactorNumeric>20</ConversionFactorNumeric>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>2</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">3</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">FLE</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">102</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">A</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">WHL</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>0.82</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">2</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.3.D.24</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "<SpecifiedAAPProduct>\n" +
                "<SpeciesCode listID=\"FAO_SPECIES\">LIN</SpeciesCode>\n" +
                "<WeightMeasure unitCode=\"KGM\">9</WeightMeasure>\n" +
                "<UsageCode listID=\"PROD_USAGE\">HCN</UsageCode>\n" +
                "<AppliedAAPProcess>\n" +
                "<TypeCode listID=\"FISH_FRESHNESS\">E</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESERVATION\">FRE</TypeCode>\n" +
                "<TypeCode listID=\"FISH_PRESENTATION\">GUT</TypeCode>\n" +
                "</AppliedAAPProcess>\n" +
                "<TotalSalesPrice>\n" +
                "<ChargeAmount>3.55</ChargeAmount>\n" +
                "</TotalSalesPrice>\n" +
                "<SpecifiedSizeDistribution>\n" +
                "<CategoryCode listID=\"FISH_SIZE_CATEGORY\">3</CategoryCode>\n" +
                "<ClassCode listID=\"FISH_SIZE_CLASS\">LSC</ClassCode>\n" +
                "</SpecifiedSizeDistribution>\n" +
                "<OriginFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">AREA</TypeCode>\n" +
                "<ID schemeID=\"FAO_AREA\">27.7.A</ID>\n" +
                "</OriginFLUXLocation>\n" +
                "</SpecifiedAAPProduct>\n" +
                "</SpecifiedSalesBatch>\n" +
                "<SpecifiedSalesEvent>\n" +
                "<OccurrenceDateTime>\n" +
                "<ns2:DateTime>2017-10-16T07:05:22Z</ns2:DateTime>\n" +
                "</OccurrenceDateTime>\n" +
                "</SpecifiedSalesEvent>\n" +
                "<SpecifiedFishingActivity>\n" +
                "<TypeCode>LAN</TypeCode>\n" +
                "<RelatedFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</TypeCode>\n" +
                "<CountryID schemeID=\"TERRITORY\">BE2</CountryID>\n" +
                "<ID schemeID=\"LOCATION\">BEOST</ID>\n" +
                "</RelatedFLUXLocation>\n" +
                "<SpecifiedDelimitedPeriod>\n" +
                "<StartDateTime>\n" +
                "<ns2:DateTime>2017-05-10T05:32:30Z</ns2:DateTime>\n" +
                "</StartDateTime>\n" +
                "</SpecifiedDelimitedPeriod>\n" +
                "<SpecifiedFishingTrip>\n" +
                "<ID schemeID=\"EU_TRIP_ID\">BEL-TRP-20171610</ID>\n" +
                "</SpecifiedFishingTrip>\n" +
                "<RelatedVesselTransportMeans>\n" +
                "<ID schemeID=\"CFR\">BEL123456799</ID>\n" +
                "<Name>FAKE VESSEL2</Name>\n" +
                "<RegistrationVesselCountry>\n" +
                "<ID schemeID=\"TERRITORY\">BE3</ID>\n" +
                "</RegistrationVesselCountry>\n" +
                "<SpecifiedContactParty>\n" +
                "<RoleCode listID=\"FLUX_CONTACT_ROLE\">MASTER</RoleCode>\n" +
                "<SpecifiedContactPerson>\n" +
                "<GivenName>Henrick</GivenName>\n" +
                "<MiddleName>Jan</MiddleName>\n" +
                "<FamilyName>JANSEN</FamilyName>\n" +
                "</SpecifiedContactPerson>\n" +
                "</SpecifiedContactParty>\n" +
                "</RelatedVesselTransportMeans>\n" +
                "</SpecifiedFishingActivity>\n" +
                "<SpecifiedFLUXLocation>\n" +
                "<TypeCode listID=\"FLUX_LOCATION_TYPE\">LOCATION</TypeCode>\n" +
                "<CountryID schemeID=\"TERRITORY\">BEL</CountryID>\n" +
                "<ID schemeID=\"LOCATION\">BEOST</ID>\n" +
                "</SpecifiedFLUXLocation>\n" +
                "<SpecifiedSalesParty>\n" +
                "<ID schemeID=\"MS\">123456</ID>\n" +
                "<Name>Mr SENDER</Name>\n" +
                "<RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">SENDER</RoleCode>\n" +
                "</SpecifiedSalesParty>\n" +
                "<SpecifiedSalesParty>\n" +
                "<ID schemeID=\"VAT\">0679223791</ID>\n" +
                "<Name>Mr BUYER</Name>\n" +
                "<RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">BUYER</RoleCode>\n" +
                "</SpecifiedSalesParty>\n" +
                "<SpecifiedSalesParty>\n" +
                "<Name>Mr PROVIDER</Name>\n" +
                "<RoleCode listID=\"FLUX_SALES_PARTY_ROLE\">PROVIDER</RoleCode>\n" +
                "</SpecifiedSalesParty>\n" +
                "</IncludedSalesDocument>\n" +
                "</ns3:SalesReport>\n" +
                "</ns4:FLUXSalesReportMessage>\n" +
                "<ns4:AuctionSale>\n" +
                "<ns4:CountryCode>BE5</ns4:CountryCode>\n" +
                "<ns4:SalesCategory>FIRST_SALE</ns4:SalesCategory>\n" +
                "</ns4:AuctionSale>\n" +
                "</ns4:Report>\n";
    }

    public String composeFLUXSalesReportMessageOriginalAsString() {
        return getTestDataFromFile("test-data/report_original.txt");
    }

    public String composeFLUXSalesReportMessageBeforeCorrectionsAsString() {
        return getTestDataFromFile("test-data/before_corrections.txt");
    }

    public String composeFLUXSalesReportMessageCorrectionsAsString() {
        return getTestDataFromFile("test-data/corrections.txt");
    }

    private String getTestDataFromFile(String fileName) {
        try {
            InputStream is = this.getClass().getClassLoader().getResourceAsStream(fileName);
            return IOUtils.toString(is);

        } catch (IOException e) {
            throw new IllegalStateException("Unable to retrieve test data: " + fileName + ". Reason: " + e.getMessage());
        }
    }

}
