package eu.europa.ec.fisheries.uvms.sales.domain.helper;

import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Document;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.Product;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class CreateReportHelperTest {

    private final String LOCAL_CURRENCY = "EUR";

    @Mock
    private MapperFacade mapper;

    @Mock
    private FluxReportDao fluxReportDao;

    @Mock
    private ReportHelper reportHelper;

    @Mock
    private BeanValidatorHelper beanValidatorHelper;

    @InjectMocks
    private CreateReportHelper createReportHelper;


    @Test(expected = NullPointerException.class)
    public void testCreateWhenArgumentIsNull() throws Exception {
        createReportHelper.create(null, LOCAL_CURRENCY, BigDecimal.ONE);
    }

    @Test
    public void testCreateWhenReportIsACreationAndNoReferencesToTakeOverDocumentsExistAndItIsNotAlreadyCorrectedOrDeleted() throws Exception {
        //data set
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType();
        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
        FluxReport fluxReportEntity = new FluxReport()
                .extId("extId")
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(false);
        when(mapper.map(report, FluxReport.class)).thenReturn(fluxReportEntity);
        when(reportHelper.isReportCorrected(report)).thenReturn(false);
        when(reportHelper.hasReferencesToTakeOverDocuments(report)).thenReturn(false);
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(fluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(fluxReportEntity);
        when(fluxReportDao.create(fluxReportEntity)).thenReturn(fluxReportEntity);
        when(mapper.map(fluxReportEntity, Report.class)).thenReturn(report);

        //execute
        createReportHelper.create(report, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(fluxReportDao, times(2)).findCorrectionOf(fluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(fluxReportEntity);
        verify(fluxReportDao).create(fluxReportEntity);
        verify(mapper).map(fluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);
    }

    @Test
    public void testCreateWhenReportIsACreationAndReferencesToTakeOverDocumentsExist() throws Exception {
        //data set
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType();
        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
        FluxReport fluxReportEntity = new FluxReport()
                .extId("extId")
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));

        List<String> takeOverDocumentsIds = Arrays.asList("a", "b", "c");
        FluxReport takeOverDocumentA = new FluxReport().extId("a");
        FluxReport takeOverDocumentB = new FluxReport().extId("b");
        FluxReport takeOverDocumentC = new FluxReport().extId("c");

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(false);
        when(mapper.map(report, FluxReport.class)).thenReturn(fluxReportEntity);
        when(reportHelper.isReportCorrected(report)).thenReturn(false);
        when(reportHelper.hasReferencesToTakeOverDocuments(report)).thenReturn(true);
        when(reportHelper.getReferenceIdsToTakeOverDocuments(report)).thenReturn(takeOverDocumentsIds);
        when(fluxReportDao.findByExtId("a")).thenReturn(Optional.of(takeOverDocumentA));
        when(fluxReportDao.findByExtId("b")).thenReturn(Optional.of(takeOverDocumentB));
        when(fluxReportDao.findByExtId("c")).thenReturn(Optional.of(takeOverDocumentC));
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(fluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(fluxReportEntity);
        when(fluxReportDao.create(fluxReportEntity)).thenReturn(fluxReportEntity);
        when(mapper.map(fluxReportEntity, Report.class)).thenReturn(report);

        //execute
        createReportHelper.create(report, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(reportHelper).getReferenceIdsToTakeOverDocuments(report);
        verify(fluxReportDao).findByExtId("a");
        verify(fluxReportDao).findByExtId("b");
        verify(fluxReportDao).findByExtId("c");
        verify(fluxReportDao, times(2)).findCorrectionOf(fluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(fluxReportEntity);
        verify(fluxReportDao).create(fluxReportEntity);
        verify(mapper).map(fluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals(Arrays.asList(takeOverDocumentA, takeOverDocumentB, takeOverDocumentC), fluxReportEntity.getRelatedTakeOverDocuments());
    }

    @Test
    public void testCreateWhenReportIsACreationItIsAlreadyCorrected() throws Exception {
        //data set
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType();
        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
        FluxReport fluxReportEntity = new FluxReport()
                .extId("extId")
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));

        DateTime correctionDate = new DateTime();
        FluxReport correction = new FluxReport()
                .extId("correction")
                .creation(correctionDate);

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(false);
        when(mapper.map(report, FluxReport.class)).thenReturn(fluxReportEntity);
        when(reportHelper.isReportCorrected(report)).thenReturn(false);
        when(reportHelper.hasReferencesToTakeOverDocuments(report)).thenReturn(false);
        doReturn(Optional.of(correction)).when(fluxReportDao).findCorrectionOf(fluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(fluxReportEntity);
        when(fluxReportDao.create(fluxReportEntity)).thenReturn(fluxReportEntity);
        when(mapper.map(fluxReportEntity, Report.class)).thenReturn(report);

        //execute
        createReportHelper.create(report, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(fluxReportDao, times(2)).findCorrectionOf(fluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(fluxReportEntity);
        verify(fluxReportDao).create(fluxReportEntity);
        verify(mapper).map(fluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals(correctionDate, fluxReportEntity.getCorrection());
    }

    @Test
    public void testCreateWhenReportIsACreationItIsAlreadyDeleted() throws Exception {
        //data set
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType();
        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
        FluxReport fluxReportEntity = new FluxReport()
                .extId("extId")
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));

        DateTime deletionDate = new DateTime();
        FluxReport deletion = new FluxReport()
                .extId("deletion")
                .creation(deletionDate);

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(false);
        when(mapper.map(report, FluxReport.class)).thenReturn(fluxReportEntity);
        when(reportHelper.isReportCorrected(report)).thenReturn(false);
        when(reportHelper.hasReferencesToTakeOverDocuments(report)).thenReturn(false);
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(fluxReportEntity.getExtId());
        doReturn(Optional.of(deletion)).when(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(fluxReportEntity);
        when(fluxReportDao.create(fluxReportEntity)).thenReturn(fluxReportEntity);
        when(mapper.map(fluxReportEntity, Report.class)).thenReturn(report);

        //execute
        createReportHelper.create(report, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(fluxReportDao).findCorrectionOf(fluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(fluxReportEntity);
        verify(fluxReportDao).create(fluxReportEntity);
        verify(mapper).map(fluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals(deletionDate, fluxReportEntity.getDeletion());
    }

    @Test
    public void testCreateWhenReportsPurposeIsDeletion() throws Exception {
        //data set
        DateTime deletionDate = new DateTime();

        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue("id"))
                .withReferencedID(new IDType().withValue("hello"))
                .withPurposeCode(new CodeType().withValue(Purpose.DELETE.getNumericCode() + ""))
                .withCreationDateTime(new DateTimeType().withDateTime(deletionDate));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        FluxReport newFluxReportEntity = new FluxReport().extId("deletion")
                .itemType(FluxReportItemType.SALES_NOTE)
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));

        FluxReport oldFluxReportEntity = new FluxReport().extId("hello")
                .itemType(FluxReportItemType.SALES_NOTE);

        FluxReport evenOlderFluxReportEntity = new FluxReport().extId("hi")
                .itemType(FluxReportItemType.SALES_NOTE);

        Report mappedAndPersistedReportFromDao = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage().withSalesReports(new SalesReportType().withItemTypeCode(new CodeType().withValue("SN"))));

        //mock
        doReturn(newFluxReportEntity).when(mapper).map(report, FluxReport.class);
        when(reportHelper.isReportDeleted(report)).thenReturn(true);
        when(reportHelper.getCreationDate(report)).thenReturn(deletionDate);
        when(reportHelper.getFLUXReportDocumentReferencedId(report)).thenReturn("hello");
        when(fluxReportDao.findByExtId("hello")).thenReturn(Optional.of(oldFluxReportEntity));
        doReturn(Arrays.asList(evenOlderFluxReportEntity)).when(fluxReportDao).findOlderVersions(oldFluxReportEntity);
        doReturn(false).when(reportHelper).hasReferencesToTakeOverDocuments(report);
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(newFluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(newFluxReportEntity);
        doReturn(newFluxReportEntity).when(fluxReportDao).create(newFluxReportEntity);
        doReturn(mappedAndPersistedReportFromDao).when(mapper).map(newFluxReportEntity, Report.class);

        //execute
        Report persistedReport = createReportHelper.create(report, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(reportHelper).getCreationDate(report);
        verify(reportHelper).getFLUXReportDocumentReferencedId(report);
        verify(fluxReportDao).findByExtId("hello");
        verify(fluxReportDao).findOlderVersions(oldFluxReportEntity);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(fluxReportDao, times(2)).findCorrectionOf(newFluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(newFluxReportEntity);
        verify(fluxReportDao).create(newFluxReportEntity);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertSame(mappedAndPersistedReportFromDao, persistedReport);
        assertEquals(deletionDate, oldFluxReportEntity.getDeletion());
        assertEquals(deletionDate, evenOlderFluxReportEntity.getDeletion());
    }

    @Test
    public void testCreateWhenReportsPurposeIsDeletionButTheOriginalReportHasNotComeInYet() throws Exception {
        //data set
        DateTime deletionDate = new DateTime();

        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue("id"))
                .withReferencedID(new IDType().withValue("hello"))
                .withPurposeCode(new CodeType().withValue(Purpose.DELETE.getNumericCode() + ""))
                .withCreationDateTime(new DateTimeType().withDateTime(deletionDate));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        FluxReport newFluxReportEntity = new FluxReport().extId("deletion")
                .itemType(FluxReportItemType.SALES_NOTE)
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));

        Report mappedAndPersistedReportFromDao = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage().withSalesReports(new SalesReportType().withItemTypeCode(new CodeType().withValue("SN"))));

        //mock
        doReturn(newFluxReportEntity).when(mapper).map(report, FluxReport.class);
        when(reportHelper.isReportDeleted(report)).thenReturn(true);
        when(reportHelper.getCreationDate(report)).thenReturn(deletionDate);
        when(reportHelper.getFLUXReportDocumentReferencedId(report)).thenReturn("hello");
        doReturn(Optional.empty()).when(fluxReportDao).findByExtId("hello");
        doReturn(false).when(reportHelper).hasReferencesToTakeOverDocuments(report);
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(newFluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(newFluxReportEntity);
        doReturn(newFluxReportEntity).when(fluxReportDao).create(newFluxReportEntity);
        doReturn(mappedAndPersistedReportFromDao).when(mapper).map(newFluxReportEntity, Report.class);

        //execute
        Report persistedReport = createReportHelper.create(report, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(reportHelper).getCreationDate(report);
        verify(reportHelper).getFLUXReportDocumentReferencedId(report);
        verify(fluxReportDao).findByExtId("hello");
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(fluxReportDao, times(2)).findCorrectionOf(newFluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(newFluxReportEntity);
        verify(fluxReportDao).create(newFluxReportEntity);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertSame(mappedAndPersistedReportFromDao, persistedReport);
    }

    @Test
    public void testCreateWhenReportsPurposeIsDeletionAndTheReportWasAlreadyDeleted() throws Exception {
        //data set
        DateTime deletionDate = DateTime.parse("2017-01-01");
        DateTime now = DateTime.now();

        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withIDS(new IDType().withValue("id"))
                .withReferencedID(new IDType().withValue("hello"))
                .withPurposeCode(new CodeType().withValue(Purpose.DELETE.getNumericCode() + ""))
                .withCreationDateTime(new DateTimeType().withDateTime(now));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        FluxReport oldFluxReportEntity = new FluxReport().extId("hello")
                .itemType(FluxReportItemType.SALES_NOTE)
                .deletion(deletionDate);

        Report mappedAndPersistedReportFromDao = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage().withSalesReports(new SalesReportType().withItemTypeCode(new CodeType().withValue("SN"))))
                .withDeletion(deletionDate);

        FluxReport newFluxReportEntity = new FluxReport().extId("deletion")
                .itemType(FluxReportItemType.SALES_NOTE)
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));

        //mock
        doReturn(newFluxReportEntity).when(mapper).map(report, FluxReport.class);
        when(reportHelper.isReportDeleted(report)).thenReturn(true);
        when(reportHelper.getCreationDate(report)).thenReturn(deletionDate);
        when(reportHelper.getFLUXReportDocumentReferencedId(report)).thenReturn("hello");
        when(fluxReportDao.findByExtId("hello")).thenReturn(Optional.of(oldFluxReportEntity));
        doReturn(new ArrayList<Report>()).when(fluxReportDao).findOlderVersions(oldFluxReportEntity);
        doReturn(false).when(reportHelper).hasReferencesToTakeOverDocuments(report);
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(newFluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(newFluxReportEntity);
        doReturn(newFluxReportEntity).when(fluxReportDao).create(newFluxReportEntity);
        doReturn(mappedAndPersistedReportFromDao).when(mapper).map(newFluxReportEntity, Report.class);

        //execute
        Report persistedReport = createReportHelper.create(report, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(reportHelper).getCreationDate(report);
        verify(reportHelper).getFLUXReportDocumentReferencedId(report);
        verify(fluxReportDao).findByExtId("hello");
        verify(fluxReportDao).findOlderVersions(oldFluxReportEntity);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(fluxReportDao, times(2)).findCorrectionOf(newFluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(newFluxReportEntity);
        verify(fluxReportDao).create(newFluxReportEntity);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertSame(mappedAndPersistedReportFromDao, persistedReport);
        //check if date of deletion matches the first date of deletion, not the date of the 2nd received deletion
        assertEquals(deletionDate, persistedReport.getDeletion());
    }

    @Test
    public void testCreateWhenReportIsACorrection() throws Exception {
        //data set
        DateTime creationDateCorrection = DateTime.now();

        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withReferencedID(new IDType().withValue("hello"))
                .withPurposeCode(new CodeType().withValue(Purpose.CORRECTION.getNumericCode() + ""))
                .withCreationDateTime(new DateTimeType().withDateTime(creationDateCorrection));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report correctionReport = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        FluxReport newFluxReportEntity = new FluxReport()
                .extId("world")
                .purpose(Purpose.CORRECTION)
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));
        FluxReport oldFluxReportEntity = new FluxReport().extId("hello");

        //mock
        doReturn(newFluxReportEntity).when(mapper).map(correctionReport, FluxReport.class);
        doReturn(false).when(reportHelper).isReportDeleted(correctionReport);
        doReturn(true).when(reportHelper).isReportCorrected(correctionReport);
        doReturn(false).when(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        doReturn("hello").when(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        doReturn(creationDateCorrection).when(reportHelper).getCreationDate(correctionReport);
        doReturn(Optional.of(oldFluxReportEntity)).when(fluxReportDao).findByExtId("hello");
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(newFluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(newFluxReportEntity);
        doReturn(newFluxReportEntity).when(fluxReportDao).create(newFluxReportEntity);
        doReturn(correctionReport).when(mapper).map(newFluxReportEntity, Report.class);

        //execute
        Report persistedReport = createReportHelper.create(correctionReport, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(correctionReport);
        verify(mapper).map(correctionReport, FluxReport.class);
        verify(reportHelper).isReportCorrected(correctionReport);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        verify(reportHelper).getCreationDate(correctionReport);
        verify(fluxReportDao).findByExtId("hello");
        verify(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        verify(fluxReportDao, times(2)).findCorrectionOf(newFluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(newFluxReportEntity);
        verify(fluxReportDao).create(newFluxReportEntity);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals("hello", persistedReport.getFLUXSalesReportMessage().getFLUXReportDocument().getReferencedID().getValue());
        assertEquals(creationDateCorrection, oldFluxReportEntity.getCorrection());
    }

    @Test
    public void testCreateWhenReportIsACorrectionButThisCorrectionIsAlreadyCorrectedByAnotherReportThatCameInTooEarlier() throws Exception {
        //data set
        DateTime creationDateCorrectionOfCorrection = DateTime.now();
        DateTime creationDateCorrection = new DateTime(2017, 1, 1, 1, 1);

        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withReferencedID(new IDType().withValue("hello"))
                .withPurposeCode(new CodeType().withValue(Purpose.CORRECTION.getNumericCode() + ""))
                .withCreationDateTime(new DateTimeType().withDateTime(creationDateCorrection));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report correctionReport = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        FluxReport correctionOfCorrection = new FluxReport()
                .extId("everyone")
                .purpose(Purpose.CORRECTION)
                .creation(creationDateCorrectionOfCorrection);
        FluxReport newFluxReportEntity = new FluxReport()
                .extId("world")
                .purpose(Purpose.CORRECTION)
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));
        FluxReport oldFluxReportEntity = new FluxReport().extId("hello");

        //mock
        doReturn(newFluxReportEntity).when(mapper).map(correctionReport, FluxReport.class);
        doReturn(false).when(reportHelper).isReportDeleted(correctionReport);
        doReturn(true).when(reportHelper).isReportCorrected(correctionReport);
        doReturn(false).when(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        doReturn("hello").when(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        doReturn(creationDateCorrection).when(reportHelper).getCreationDate(correctionReport);
        doReturn(Optional.of(oldFluxReportEntity)).when(fluxReportDao).findByExtId("hello");
        doReturn(Optional.of(correctionOfCorrection)).when(fluxReportDao).findCorrectionOf(newFluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(newFluxReportEntity);
        doReturn(newFluxReportEntity).when(fluxReportDao).create(newFluxReportEntity);
        doReturn(correctionReport).when(mapper).map(newFluxReportEntity, Report.class);

        //execute
        Report persistedReport = createReportHelper.create(correctionReport, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(correctionReport);
        verify(mapper).map(correctionReport, FluxReport.class);
        verify(reportHelper).isReportCorrected(correctionReport);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        verify(reportHelper).getCreationDate(correctionReport);
        verify(fluxReportDao).findByExtId("hello");
        verify(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        verify(fluxReportDao, times(2)).findCorrectionOf(newFluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(newFluxReportEntity);
        verify(fluxReportDao).create(newFluxReportEntity);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals("hello", persistedReport.getFLUXSalesReportMessage().getFLUXReportDocument().getReferencedID().getValue());
        assertEquals(creationDateCorrection, oldFluxReportEntity.getCorrection());
        assertEquals(creationDateCorrectionOfCorrection, newFluxReportEntity.getCorrection());
    }

    @Test
    public void testCreateWhenReportIsACorrectionButThisCorrectionIsAlreadyDeletedByAnotherReportThatCameInTooEarlier() throws Exception {
        //data set
        DateTime creationDateDeletionOfCorrection = DateTime.now();
        DateTime creationDateCorrection = new DateTime(2017, 1, 1, 1, 1);

        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withReferencedID(new IDType().withValue("hello"))
                .withPurposeCode(new CodeType().withValue(Purpose.CORRECTION.getNumericCode() + ""))
                .withCreationDateTime(new DateTimeType().withDateTime(creationDateCorrection));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report correctionReport = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        FluxReport deletionOfCorrection = new FluxReport()
                .extId("everyone")
                .purpose(Purpose.CORRECTION)
                .creation(creationDateDeletionOfCorrection);
        FluxReport newFluxReportEntity = new FluxReport()
                .extId("world")
                .purpose(Purpose.CORRECTION)
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));
        FluxReport oldFluxReportEntity = new FluxReport().extId("hello");

        //mock
        doReturn(newFluxReportEntity).when(mapper).map(correctionReport, FluxReport.class);
        doReturn(false).when(reportHelper).isReportDeleted(correctionReport);
        doReturn(true).when(reportHelper).isReportCorrected(correctionReport);
        doReturn(false).when(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        doReturn("hello").when(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        doReturn(creationDateCorrection).when(reportHelper).getCreationDate(correctionReport);
        doReturn(Optional.of(oldFluxReportEntity)).when(fluxReportDao).findByExtId("hello");
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(newFluxReportEntity.getExtId());
        doReturn(Optional.of(deletionOfCorrection)).when(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(newFluxReportEntity);
        doReturn(newFluxReportEntity).when(fluxReportDao).create(newFluxReportEntity);
        doReturn(correctionReport).when(mapper).map(newFluxReportEntity, Report.class);

        //execute
        Report persistedReport = createReportHelper.create(correctionReport, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(correctionReport);
        verify(mapper).map(correctionReport, FluxReport.class);
        verify(reportHelper).isReportCorrected(correctionReport);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        verify(reportHelper).getCreationDate(correctionReport);
        verify(fluxReportDao).findByExtId("hello");
        verify(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        verify(fluxReportDao).findCorrectionOf(newFluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(newFluxReportEntity);
        verify(fluxReportDao).create(newFluxReportEntity);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals("hello", persistedReport.getFLUXSalesReportMessage().getFLUXReportDocument().getReferencedID().getValue());
        assertEquals(creationDateCorrection, oldFluxReportEntity.getCorrection());
        assertEquals(creationDateDeletionOfCorrection, newFluxReportEntity.getDeletion());
    }


    @Test
    public void testCreateWhenReportIsACorrectionButThisCorrectionIsAlreadyCorrectedByAotherReportAndThatReportHasBeenDeleted() throws Exception {
        //data set
        DateTime creationDateDeletionOfCorrection = DateTime.now();
        DateTime creationDateCorrection = new DateTime(2017, 1, 1, 1, 1);

        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withReferencedID(new IDType().withValue("hello"))
                .withPurposeCode(new CodeType().withValue(Purpose.CORRECTION.getNumericCode() + ""))
                .withCreationDateTime(new DateTimeType().withDateTime(creationDateCorrection));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report correctionReport = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        FluxReport newFluxReportEntity = new FluxReport()
                .extId("world")
                .purpose(Purpose.CORRECTION)
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));
        FluxReport oldFluxReportEntity = new FluxReport()
                .extId("hello")
                .deletion(creationDateDeletionOfCorrection);

        //mock
        doReturn(newFluxReportEntity).when(mapper).map(correctionReport, FluxReport.class);
        doReturn(false).when(reportHelper).isReportDeleted(correctionReport);
        doReturn(true).when(reportHelper).isReportCorrected(correctionReport);
        doReturn(false).when(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        doReturn("hello").when(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        doReturn(creationDateCorrection).when(reportHelper).getCreationDate(correctionReport);
        doReturn(Optional.of(oldFluxReportEntity)).when(fluxReportDao).findByExtId("hello");
        doReturn(Optional.of(oldFluxReportEntity)).when(fluxReportDao).findCorrectionOf(newFluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(newFluxReportEntity);
        doReturn(newFluxReportEntity).when(fluxReportDao).create(newFluxReportEntity);
        doReturn(correctionReport).when(mapper).map(newFluxReportEntity, Report.class);

        //execute
        Report persistedReport = createReportHelper.create(correctionReport, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(correctionReport);
        verify(mapper).map(correctionReport, FluxReport.class);
        verify(reportHelper).isReportCorrected(correctionReport);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        verify(reportHelper).getCreationDate(correctionReport);
        verify(fluxReportDao).findByExtId("hello");
        verify(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        verify(fluxReportDao, times(2)).findCorrectionOf(newFluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(newFluxReportEntity);
        verify(fluxReportDao).create(newFluxReportEntity);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals("hello", persistedReport.getFLUXSalesReportMessage().getFLUXReportDocument().getReferencedID().getValue());
        assertEquals(creationDateCorrection, oldFluxReportEntity.getCorrection());
        assertEquals(creationDateDeletionOfCorrection, newFluxReportEntity.getDeletion());
    }

    @Test
    public void testCreateWhenReportIsACorrectionButCorrectedReportDoesNotExist() throws Exception {
        //data set
        DateTime creationDateCorrection = DateTime.now();

        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withReferencedID(new IDType().withValue("hello"))
                .withPurposeCode(new CodeType().withValue(Purpose.CORRECTION.getNumericCode() + ""))
                .withCreationDateTime(new DateTimeType().withDateTime(creationDateCorrection));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report correctionReport = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        FluxReport newFluxReportEntity = new FluxReport()
                .extId("world")
                .purpose(Purpose.CORRECTION)
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));

        //mock
        doReturn(false).when(reportHelper).isReportDeleted(correctionReport);
        doReturn(newFluxReportEntity).when(mapper).map(correctionReport, FluxReport.class);
        doReturn(true).when(reportHelper).isReportCorrected(correctionReport);
        doReturn(false).when(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        doReturn("hello").when(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        doReturn(creationDateCorrection).when(reportHelper).getCreationDate(correctionReport);
        doReturn(Optional.empty()).when(fluxReportDao).findByExtId("hello");
        doReturn("world").when(reportHelper).getId(correctionReport.getFLUXSalesReportMessage());
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(newFluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        doNothing().when(beanValidatorHelper).validateBean(newFluxReportEntity);
        doReturn(newFluxReportEntity).when(fluxReportDao).create(newFluxReportEntity);
        doReturn(correctionReport).when(mapper).map(newFluxReportEntity, Report.class);

        //execute
        Report persistedReport = createReportHelper.create(correctionReport, LOCAL_CURRENCY, BigDecimal.ONE);

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(correctionReport);
        verify(mapper).map(correctionReport, FluxReport.class);
        verify(reportHelper).isReportCorrected(correctionReport);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        verify(reportHelper).getCreationDate(correctionReport);
        verify(fluxReportDao).findByExtId("hello");
        verify(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        verify(fluxReportDao, times(2)).findCorrectionOf(newFluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(newFluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(newFluxReportEntity);
        verify(fluxReportDao).create(newFluxReportEntity);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verify(reportHelper).getId(correctionReport.getFLUXSalesReportMessage());
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals("hello", persistedReport.getFLUXSalesReportMessage().getFLUXReportDocument().getReferencedID().getValue());
    }

    @Test
    public void tryCreateReportForBeanValidationSalesNonBlockingException() throws Exception {
        //data set
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType();
        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
        FluxReport fluxReportEntity = new FluxReport()
                .extId("extId")
                .document(new Document().currency("USD").totalPrice(BigDecimal.TEN).products(Arrays.asList(new Product().price(BigDecimal.ONE))));

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(false);
        when(mapper.map(report, FluxReport.class)).thenReturn(fluxReportEntity);
        when(reportHelper.isReportCorrected(report)).thenReturn(false);
        when(reportHelper.hasReferencesToTakeOverDocuments(report)).thenReturn(false);
        doReturn(Optional.empty()).when(fluxReportDao).findCorrectionOf(fluxReportEntity.getExtId());
        doReturn(Optional.empty()).when(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        doThrow(new SalesNonBlockingException("MySalesNonBlockingException")).when(beanValidatorHelper).validateBean(fluxReportEntity);

        //execute
        try {
            createReportHelper.create(report, LOCAL_CURRENCY, BigDecimal.ONE);

        } catch (SalesNonBlockingException e) {
            assertEquals("MySalesNonBlockingException", e.getMessage());
        }

        //assert and verify
        verify(reportHelper, times(2)).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(fluxReportDao, times(2)).findCorrectionOf(fluxReportEntity.getExtId());
        verify(fluxReportDao).findDeletionOf(fluxReportEntity.getExtId());
        verify(beanValidatorHelper).validateBean(fluxReportEntity);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);
    }

    @Test
    public void enrichWithLocalCurrency() {
        FluxReport fluxReport = new FluxReport()
                .document(new Document()
                        .currency("USD")
                        .products(Arrays.asList(
                                new Product().price(BigDecimal.TEN),
                                new Product().price(BigDecimal.TEN)))
                        .totalPrice(BigDecimal.valueOf(20)));

        createReportHelper.enrichWithLocalCurrency(fluxReport, LOCAL_CURRENCY, BigDecimal.valueOf(1.5));

        assertEquals("EUR", fluxReport.getDocument().getCurrencyLocal());
        assertEquals(new BigDecimal("13.34"), fluxReport.getDocument().getTotalPriceLocal());
        for (Product product : fluxReport.getDocument().getProducts()) {
            assertEquals(new BigDecimal("6.67"), product.getPriceLocal());
        }
    }

    @Test
    public void roundPricesToTwoDecimals() {
        FluxReport fluxReport = new FluxReport()
                .document(new Document()
                        .currency("USD")
                        .products(Arrays.asList(
                                new Product().price(new BigDecimal("12.345")).priceLocal(new BigDecimal("12.567"))))
                        .totalPrice(new BigDecimal("12.356")).totalPriceLocal(new BigDecimal("12.678")));
        createReportHelper.roundPricesToTwoDecimals(fluxReport);

        assertEquals(new BigDecimal("12.35"), fluxReport.getDocument().getProducts().get(0).getPrice());
        assertEquals(new BigDecimal("12.57"), fluxReport.getDocument().getProducts().get(0).getPriceLocal());
        assertEquals(new BigDecimal("12.36"), fluxReport.getDocument().getTotalPrice());
        assertEquals(new BigDecimal("12.68"), fluxReport.getDocument().getTotalPriceLocal());

    }

    @Test
    public void roundPriceWhenDocumentHasNoTotalPrice() {
        FluxReport fluxReport = new FluxReport()
                .document(new Document()
                        .currency("USD")
                        .products(Arrays.asList(
                                new Product().price(new BigDecimal("12.345")).priceLocal(new BigDecimal("12.567")))));
        createReportHelper.roundPricesToTwoDecimals(fluxReport);

        assertEquals(new BigDecimal("12.35"), fluxReport.getDocument().getProducts().get(0).getPrice());
        assertEquals(new BigDecimal("12.57"), fluxReport.getDocument().getProducts().get(0).getPriceLocal());
        assertNull(fluxReport.getDocument().getTotalPrice());
        assertNull(fluxReport.getDocument().getTotalPriceLocal());
    }

    @Test
    public void roundPricesToTwoDecimalsWhenProductHasNoPrice() {
        FluxReport fluxReport = new FluxReport()
                .document(new Document()
                        .currency("USD")
                        .products(Arrays.asList(
                                new Product()))
                        .totalPrice(new BigDecimal("12.356")).totalPriceLocal(new BigDecimal("12.678")));
        createReportHelper.roundPricesToTwoDecimals(fluxReport);

        assertNull(fluxReport.getDocument().getProducts().get(0).getPrice());
        assertNull(fluxReport.getDocument().getProducts().get(0).getPriceLocal());
        assertEquals(new BigDecimal("12.36"), fluxReport.getDocument().getTotalPrice());
        assertEquals(new BigDecimal("12.68"), fluxReport.getDocument().getTotalPriceLocal());
    }

    @Test
    public void roundPricesToTwoDecimalsWhenPricesAreAlreadyLimitedToTwoDecimals() {
        BigDecimal totalPrice = new BigDecimal("12.35");
        BigDecimal totalPriceLocal = new BigDecimal("12.67");

        Document documentMock = mock(Document.class);
        Product productMock = mock(Product.class);

        FluxReport fluxReport = new FluxReport()
                .document(documentMock);

        doReturn(totalPrice).when(productMock).getPrice();
        doReturn(totalPriceLocal).when(productMock).getPriceLocal();
        doReturn(totalPrice).when(documentMock).getTotalPrice();
        doReturn(totalPriceLocal).when(documentMock).getTotalPriceLocal();
        doReturn(Arrays.asList(productMock)).when(documentMock).getProducts();

        createReportHelper.roundPricesToTwoDecimals(fluxReport);

        verify(documentMock, never()).setTotalPrice(any(BigDecimal.class));
        verify(documentMock, never()).setTotalPriceLocal(any(BigDecimal.class));
        verify(productMock, never()).setPrice(any(BigDecimal.class));
        verify(productMock, never()).setPriceLocal(any(BigDecimal.class));

    }


}