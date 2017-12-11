package eu.europa.ec.fisheries.uvms.sales.service.arquillian;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.commons.message.api.MessageConstants;
import eu.europa.ec.fisheries.uvms.message.JMSUtils;
import eu.europa.ec.fisheries.uvms.sales.domain.ReportDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.UnsavedMessageDomainModel;
import eu.europa.ec.fisheries.uvms.sales.message.event.carrier.EventMessage;
import eu.europa.ec.fisheries.uvms.sales.model.mapper.SalesModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.sales.service.EventService;
import eu.europa.ec.fisheries.uvms.sales.service.ReportService;
import eu.europa.ec.fisheries.uvms.sales.service.dto.SalesDetailsDto;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.arquillian.persistence.DataSource;
import org.jboss.arquillian.transaction.api.annotation.TransactionMode;
import org.jboss.arquillian.transaction.api.annotation.Transactional;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.ejb.EJB;
import javax.jms.*;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;

@RunWith(Arquillian.class)
public class SalesServiceTestIT extends TransactionalTests {

    private final static String TEST_USER_NAME = "SalesServiceTestITUser";

	private static final long TIMEOUT = 30000;

    static final Logger LOG = LoggerFactory.getLogger(SalesServiceTestIT.class);

    private Queue rulesEventQueue;
	private Queue salesEventQueue;

    private ConnectionFactory connectionFactory;

    @Before
    public void setup() {
          connectionFactory = JMSUtils.lookupConnectionFactory();
          rulesEventQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_MODULE_RULES);
		  salesEventQueue = JMSUtils.lookupQueue(MessageConstants.QUEUE_SALES_EVENT);
    }

    @EJB
    EventService eventService;
	
    @EJB
    private UnsavedMessageDomainModel unsavedMessageDomainModel;

	@EJB
    private ReportDomainModel reportDomainModel;

	@EJB
	ReportService reportService;

	@Ignore
	@Test
	@Transactional(TransactionMode.COMMIT)
	@DataSource("java:/jdbc/uvms_sales")
    public void testSalesEventRespondToInvalidMessage() {
		assertNotNull(em);
		assertNotNull(this.connectionFactory);
		assertNotNull(this.rulesEventQueue);

		// Test data
		String messageGuid = "d0c749bf-50d6-479a-b12e-61c2f2d66469";
        RespondToInvalidMessageRequest respondToInvalidMessageRequest = new RespondToInvalidMessageRequest();
        respondToInvalidMessageRequest.setPluginToSendResponseThrough("BELGIAN_SALES");
        respondToInvalidMessageRequest.setSender("BEL");
        respondToInvalidMessageRequest.setMessageGuid("d0c749bf-50d6-479a-b12e-61c2f2d66469");
        respondToInvalidMessageRequest.setSchemeId("UUID");

		// Execute
        EventMessage eventMessage = new EventMessage(respondToInvalidMessageRequest);
        eventService.respondToInvalidMessage(eventMessage);

		// Assert
		Connection connection = null;
		Session session = null;
        try {
            connection = connectionFactory.createConnection();
            assertNotNull(connection);
			session = JMSUtils.connectToQueue(connection);
			assertNotNull(session);
			Message jmsMessage = session.createConsumer(rulesEventQueue).receive(TIMEOUT);
			assertNotNull(jmsMessage);
			TextMessage textMessage = (TextMessage) jmsMessage;
			assertNotNull(textMessage);
			String strMessage = textMessage.getText();
			assertNotNull(strMessage);
			assertTrue(strMessage.contains("SendSalesResponseRequest"));
			assertTrue(strMessage.contains(messageGuid));

        } catch (Exception e) {
			fail("Test should not fail for consume JMS message exception: " + e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection);
        }

		assertTrue(unsavedMessageDomainModel.exists(messageGuid));
	}

    @Test
	@Transactional(TransactionMode.COMMIT)
	@DataSource("java:/jdbc/uvms_sales")
    public void testSalesEvent() {
		assertNotNull(em);
		assertNotNull(this.connectionFactory);
		assertNotNull(this.rulesEventQueue);
		String messageGuid2 = "d5da24ff-42b4-5e76-967f-ad97762a0311";

		// Test data
		SalesReportRequest salesReportRequest = new SalesReportRequest();
        salesReportRequest.setPluginToSendResponseThrough("BELGIAN_SALES");
		salesReportRequest.setReport(composeFLUXSalesReportMessageAsString());
		EventMessage eventMessage2 = new EventMessage(salesReportRequest);

		// Execute
		eventService.createReport(eventMessage2);

		// Assert
		Connection connection2 = null;
		Session session2 = null;
        try {
            connection2 = connectionFactory.createConnection();
            assertNotNull(connection2);
			session2 = JMSUtils.connectToQueue(connection2);
			assertNotNull(session2);
			Message jmsMessage2 = session2.createConsumer(rulesEventQueue).receive(TIMEOUT);
			assertNotNull(jmsMessage2);
			TextMessage textMessage2 = (TextMessage) jmsMessage2;
			assertNotNull(textMessage2);
			String strMessage2 = textMessage2.getText();
			assertNotNull(strMessage2);
			assertTrue(strMessage2.contains("SendSalesResponseRequest"));
			assertTrue(strMessage2.contains(messageGuid2));

        } catch (Exception e) {
			fail("Test should not fail for consume JMS message exception: " + e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection2);
        }

		Report foundReport = reportDomainModel.findByExtId(messageGuid2).orNull();
		assertNotNull(foundReport);
		FLUXSalesReportMessage fluxSalesReportMessage = foundReport.getFLUXSalesReportMessage();
		FLUXReportDocumentType fluxReportDocument = fluxSalesReportMessage.getFLUXReportDocument();
		assertEquals(messageGuid2, fluxReportDocument.getIDS().get(0).getValue());
		assertEquals(1, fluxSalesReportMessage.getSalesReports().size());
		SalesReportType salesReport = fluxSalesReportMessage.getSalesReports().get(0);
		assertEquals(1, salesReport.getIncludedSalesDocuments().size());
		AuctionSaleType auctionSale = foundReport.getAuctionSale();
		assertEquals("BEL", auctionSale.getCountryCode());

		// Use case: Intermodule communication. EcbProxyServiceBean.findExchangeRate
		SalesDetailsDto salesDetailsDto = reportService.findSalesDetails(messageGuid2);

		assertNotNull(salesDetailsDto);

		assertNotNull(salesDetailsDto.getFishingTrip());
		assertNotNull(salesDetailsDto.getSalesReport());
    }
	
	private String composeFLUXSalesReportMessageAsString() {
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
                        				"<CountryID schemeID=\"TERRITORY\">BEL</CountryID>\n" +
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

	@Ignore
	@Test
	@Transactional(TransactionMode.COMMIT)
	@DataSource("java:/jdbc/uvms_sales")
    public void testSalesMessageConsumerBean() throws Exception {
		// Data
		String request = composeFLUXSalesReportMessageAsString();
		String messageValidationStatus = "OK";
		String pluginToSendResponseThrough = "BELGIAN_SALES";
        List<ValidationQualityAnalysisType> validationQualityAnalysisList = new ArrayList<>();

		String salesReportRequest = SalesModuleRequestMapper.createSalesReportRequest(request, messageValidationStatus, validationQualityAnalysisList, pluginToSendResponseThrough);

		//Execute, trigger MessageConsumerBean
		Connection connection = null;
		Session session = null;
        try {
            connection = connectionFactory.createConnection();
            assertNotNull(connection);
			session = JMSUtils.connectToQueue(connection);
			assertNotNull(session);
			TextMessage salesReportRequestMessage = session.createTextMessage(salesReportRequest);
			getProducer(session, salesEventQueue).send(salesReportRequestMessage);

        } catch (Exception e) {
			fail("Test should not fail for consume JMS message exception: " + e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection);
        }

		// Assert		
		Connection connection2 = null;
		Session session2 = null;
		String messageGuid2 = "d5da24ff-42b4-5e76-967f-ad97762a0311";
        try {
            connection2 = connectionFactory.createConnection();
            assertNotNull(connection2);
			session2 = JMSUtils.connectToQueue(connection2);
			assertNotNull(session2);
			Message jmsMessage2 = session2.createConsumer(rulesEventQueue).receive(TIMEOUT);
			assertNotNull(jmsMessage2);
			TextMessage textMessage2 = (TextMessage) jmsMessage2;
			assertNotNull(textMessage2);
			String strMessage2 = textMessage2.getText();
			assertNotNull(strMessage2);
			assertTrue(strMessage2.contains("SendSalesResponseRequest"));
			assertTrue(strMessage2.contains(messageGuid2));

        } catch (Exception e) {
			fail("Test should not fail for consume JMS message exception: " + e.getMessage());
        } finally {
            JMSUtils.disconnectQueue(connection2);
        }

		Report foundReport = reportDomainModel.findByExtId(messageGuid2).orNull();
		assertNotNull(foundReport);
		FLUXSalesReportMessage fluxSalesReportMessage = foundReport.getFLUXSalesReportMessage();
		FLUXReportDocumentType fluxReportDocument = fluxSalesReportMessage.getFLUXReportDocument();
		assertEquals(messageGuid2, fluxReportDocument.getIDS().get(0).getValue());
		assertEquals(1, fluxSalesReportMessage.getSalesReports().size());
		SalesReportType salesReport = fluxSalesReportMessage.getSalesReports().get(0);
		assertEquals(1, salesReport.getIncludedSalesDocuments().size());
		AuctionSaleType auctionSale = foundReport.getAuctionSale();
		assertEquals("BEL", auctionSale.getCountryCode());
	}

	private javax.jms.MessageProducer getProducer(Session session, Destination destination) throws JMSException {
        javax.jms.MessageProducer producer = session.createProducer(destination);
        producer.setDeliveryMode(DeliveryMode.NON_PERSISTENT);
        producer.setTimeToLive(60000L);
        return producer;
    }


}
