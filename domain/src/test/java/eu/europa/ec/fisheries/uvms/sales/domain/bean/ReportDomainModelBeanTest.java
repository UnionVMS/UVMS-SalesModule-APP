package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.UnsavedMessageDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.BeanValidatorHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.mother.ReportMother;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import ma.glasnost.orika.MapperFacade;
import org.joda.time.DateTime;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.persistence.NoResultException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ReportDomainModelBeanTest {
    @Mock
    private MapperFacade mapper;

    @Mock
    private FluxReportDao fluxReportDao;

    @Mock
    private ReportHelper reportHelper;

    @Mock
    private UnsavedMessageDomainModel unsavedMessageDomainModel;

    @Mock
    private BeanValidatorHelper beanValidatorHelper;

    @Rule
    public ExpectedException exception = ExpectedException.none();

    @InjectMocks
    private ReportDomainModelBean reportDomainModelBean;

    @Test(expected = NullPointerException.class)
    public void testCreateWhenArgumentIsNull() throws Exception {
        reportDomainModelBean.create(null);
    }

    @Test
    public void testCreateWhenReportIsACreationAndNoReferencesToTakeOverDocumentsExist() throws Exception {
        //data set
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType();
        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
        FluxReport fluxReportEntity = new FluxReport();

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(false);
        when(mapper.map(report, FluxReport.class)).thenReturn(fluxReportEntity);
        when(reportHelper.isReportCorrected(report)).thenReturn(false);
        when(reportHelper.hasReferencesToTakeOverDocuments(report)).thenReturn(false);
        doNothing().when(beanValidatorHelper).validateBean(fluxReportEntity);
        when(fluxReportDao.create(fluxReportEntity)).thenReturn(fluxReportEntity);
        when(mapper.map(fluxReportEntity, Report.class)).thenReturn(report);

        //execute
        reportDomainModelBean.create(report);

        //assert and verify
        verify(reportHelper).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
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
        FluxReport fluxReportEntity = new FluxReport();
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
        doNothing().when(beanValidatorHelper).validateBean(fluxReportEntity);
        when(fluxReportDao.create(fluxReportEntity)).thenReturn(fluxReportEntity);
        when(mapper.map(fluxReportEntity, Report.class)).thenReturn(report);

        //execute
        reportDomainModelBean.create(report);

        //assert and verify
        verify(reportHelper).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(reportHelper).getReferenceIdsToTakeOverDocuments(report);
        verify(fluxReportDao).findByExtId("a");
        verify(fluxReportDao).findByExtId("b");
        verify(fluxReportDao).findByExtId("c");
        verify(beanValidatorHelper).validateBean(fluxReportEntity);
        verify(fluxReportDao).create(fluxReportEntity);
        verify(mapper).map(fluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals(Arrays.asList(takeOverDocumentA, takeOverDocumentB, takeOverDocumentC), fluxReportEntity.getRelatedTakeOverDocuments());
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

        FluxReport oldFluxReportEntity = new FluxReport().extId("hello")
                .itemType(FluxReportItemType.SALES_NOTE);

        Report mappedAndPersistedReportFromDao = new Report()
                .withFLUXSalesReportMessage(new FLUXSalesReportMessage().withSalesReports(new SalesReportType().withItemTypeCode(new CodeType().withValue("SN"))));

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(true);
        when(reportHelper.getCreationDate(report)).thenReturn(deletionDate);
        when(reportHelper.getFLUXReportDocumentReferencedId(report)).thenReturn("hello");
        when(reportHelper.getFLUXReportDocumentId(report)).thenReturn("id");
        when(fluxReportDao.findByExtId("hello")).thenReturn(Optional.of(oldFluxReportEntity));
        when(mapper.map(oldFluxReportEntity, Report.class)).thenReturn(mappedAndPersistedReportFromDao);

        //execute
        Report persistedReport = reportDomainModelBean.create(report);

        //assert and verify
        verify(reportHelper).isReportDeleted(report);
        verify(reportHelper).getCreationDate(report);
        verify(reportHelper).getFLUXReportDocumentReferencedId(report);
        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(fluxReportDao).findByExtId("hello");
        verify(mapper).map(oldFluxReportEntity, Report.class);
        verify(unsavedMessageDomainModel).save("id");
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertSame(mappedAndPersistedReportFromDao, persistedReport);
        assertEquals(deletionDate, oldFluxReportEntity.getDeletion());
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

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(true);
        when(reportHelper.getCreationDate(report)).thenReturn(now);
        when(reportHelper.getFLUXReportDocumentReferencedId(report)).thenReturn("hello");
        when(reportHelper.getFLUXReportDocumentId(report)).thenReturn("id");
        when(fluxReportDao.findByExtId("hello")).thenReturn(Optional.of(oldFluxReportEntity));
        when(mapper.map(oldFluxReportEntity, Report.class)).thenReturn(mappedAndPersistedReportFromDao);

        //execute
        Report persistedReport = reportDomainModelBean.create(report);

        //assert and verify
        verify(reportHelper).isReportDeleted(report);
        verify(reportHelper).getCreationDate(report);
        verify(reportHelper).getFLUXReportDocumentReferencedId(report);
        verify(fluxReportDao).findByExtId("hello");
        verify(mapper).map(oldFluxReportEntity, Report.class);
        verify(unsavedMessageDomainModel).save("id");
        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(unsavedMessageDomainModel).save("id");
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

        FluxReport newFluxReportEntity = new FluxReport().extId("world").purpose(Purpose.CORRECTION);
        FluxReport oldFluxReportEntity = new FluxReport().extId("hello");

        //mock
        doReturn(false).when(reportHelper).isReportDeleted(correctionReport);
        doReturn(newFluxReportEntity).when(mapper).map(correctionReport, FluxReport.class);
        doReturn(true).when(reportHelper).isReportCorrected(correctionReport);
        doReturn(false).when(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        doReturn("hello").when(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        doReturn(creationDateCorrection).when(reportHelper).getCreationDate(correctionReport);
        doReturn(Optional.of(oldFluxReportEntity)).when(fluxReportDao).findByExtId("hello");

        doNothing().when(beanValidatorHelper).validateBean(newFluxReportEntity);
        when(fluxReportDao.create(newFluxReportEntity)).thenReturn(newFluxReportEntity);
        when(mapper.map(newFluxReportEntity, Report.class)).thenReturn(correctionReport);

        //execute
        Report persistedReport = reportDomainModelBean.create(correctionReport);

        //assert and verify
        verify(reportHelper).isReportDeleted(correctionReport);
        verify(mapper).map(correctionReport, FluxReport.class);
        verify(reportHelper).isReportCorrected(correctionReport);
        verify(reportHelper).getFLUXReportDocumentReferencedId(correctionReport);
        verify(reportHelper).getCreationDate(correctionReport);
        verify(fluxReportDao).findByExtId("hello");
        verify(reportHelper).hasReferencesToTakeOverDocuments(correctionReport);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verify(beanValidatorHelper).validateBean(newFluxReportEntity);
        verify(fluxReportDao).create(newFluxReportEntity);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);

        assertEquals("hello", persistedReport.getFLUXSalesReportMessage().getFLUXReportDocument().getReferencedID().getValue());
        assertEquals(creationDateCorrection, oldFluxReportEntity.getCorrection());
    }

    @Test
    public void testFindSalesDetailsWhenReport() {
        String extId = "extId";
        FluxReport fluxReportWithDetails = new FluxReport()
                .extId(extId)
                .purpose(Purpose.CORRECTION);
        Report expected = new Report()
            .withFLUXSalesReportMessage(new FLUXSalesReportMessage()
                    .withFLUXReportDocument(new FLUXReportDocumentType().withIDS(new IDType().withValue(extId))));

        when(fluxReportDao.findDetailsByExtId(extId)).thenReturn(fluxReportWithDetails);
        when(mapper.map(fluxReportWithDetails, Report.class)).thenReturn(expected);

        Report actual = reportDomainModelBean.findSalesDetails(extId);

        verify(fluxReportDao).findDetailsByExtId(extId);
        verify(mapper).map(fluxReportWithDetails, Report.class);
        verifyNoMoreInteractions(fluxReportDao, mapper, beanValidatorHelper);

        assertSame(expected, actual);
    }

    @Test
    public void testFindSalesDetailsWhenExtIdIsBlank() {
        exception.expect(IllegalArgumentException.class);
        exception.expectMessage("extId cannot be null or blank");

        reportDomainModelBean.findSalesDetails("");
    }

    @Test(expected = NullPointerException.class)
    public void testSearchWhenArgumentIsNull() {
        reportDomainModelBean.search(null, false);
    }

    @Test
    public void testSearchWhenSuccess() {
        ReportQuery reportQuery = new ReportQuery();
        List<FluxReport> fluxReports = new ArrayList<>();
        List<ReportSummary> expected = new ArrayList<>();

        when(fluxReportDao.search(reportQuery, false)).thenReturn(fluxReports);
        when(mapper.mapAsList(fluxReports, ReportSummary.class)).thenReturn(expected);

        List<ReportSummary> actual = reportDomainModelBean.search(reportQuery, false);

        verify(fluxReportDao).search(reportQuery, false);
        verify(mapper).mapAsList(fluxReports, ReportSummary.class);
        verifyNoMoreInteractions(fluxReportDao, mapper, beanValidatorHelper);

        assertSame(expected, actual);
    }

    @Test(expected = NullPointerException.class)
    public void searchIncludingDetailsWhenArgumentIsNull() {
        reportDomainModelBean.searchIncludingDetails(null);
    }

    @Test
    public void searchIncludingDetailsWhenSuccess() {
        ReportQuery reportQuery = new ReportQuery();
        List<FluxReport> fluxReports = new ArrayList<>();
        List<Report> expected = new ArrayList<>();

        when(fluxReportDao.search(reportQuery)).thenReturn(fluxReports);
        when(mapper.mapAsList(fluxReports, Report.class)).thenReturn(expected);

        List<Report> actual = reportDomainModelBean.searchIncludingDetails(reportQuery);

        verify(fluxReportDao).search(reportQuery);
        verify(mapper).mapAsList(fluxReports, Report.class);
        verifyNoMoreInteractions(fluxReportDao, mapper, beanValidatorHelper);

        assertSame(expected, actual);
    }

    @Test
    public void testFindByExtIdWhenSuccess() {
        String extId = "extId";
        FluxReport fluxReport = new FluxReport();
        Report report = new Report();

        doReturn(Optional.of(fluxReport)).when(fluxReportDao).findByExtId(extId);
        doReturn(report).when(mapper).map(fluxReport, Report.class);

        Optional<Report> result = reportDomainModelBean.findByExtId(extId);

        verify(fluxReportDao).findByExtId(extId);
        verify(mapper).map(fluxReport, Report.class);
        verifyNoMoreInteractions(fluxReportDao, beanValidatorHelper);

        assertTrue(result.isPresent());
        assertSame(report, result.get());
    }

    @Test(expected = NoResultException.class)
    public void testFindByExtIdWhenNothingFound() {
        String extId = "extId";

        doThrow(new NoResultException()).when(fluxReportDao).findByExtId(extId);

        reportDomainModelBean.findByExtId(extId);
    }

    @Test
    public void testFindReportWhichRefersToWhenSomethingFound() {
        String extId = "extId";
        FluxReport fluxReport = new FluxReport();
        Report report = new Report();

        doReturn(Optional.of(fluxReport)).when(fluxReportDao).findCorrectionOrDeletionOf(extId);
        doReturn(report).when(mapper).map(fluxReport, Report.class);

        Optional<Report> result = reportDomainModelBean.findCorrectionOrDeletionOf(extId);

        verify(fluxReportDao).findCorrectionOrDeletionOf(extId);
        verify(mapper).map(fluxReport, Report.class);
        verifyNoMoreInteractions(fluxReportDao, mapper, beanValidatorHelper);

        assertEquals(Optional.of(report), result);
    }

    @Test
    public void testFindReportWhichRefersToWhenNothingFound() {
        String extId = "extId";

        doReturn(Optional.absent()).when(fluxReportDao).findCorrectionOrDeletionOf(extId);

        Optional<Report> result = reportDomainModelBean.findCorrectionOrDeletionOf(extId);

        verify(fluxReportDao).findCorrectionOrDeletionOf(extId);
        verifyNoMoreInteractions(fluxReportDao, mapper, beanValidatorHelper);

        assertEquals(Optional.<Report>absent(), result);
    }

    @Test
    public void findOlderVersionsOrderedByCreationDateDescending() {
        //data set
        String id1 = "1";
        String id2 = "2";

        DateTime creationReport1 = new DateTime(2017, 6, 8, 13, 45);
        DateTime creationReport2 = new DateTime(2017, 6, 9, 13, 45);

        FluxReport fluxReport2 = new FluxReport()
                .id(2)
                .previousFluxReport(null)
                .creation(creationReport2);
        FluxReport fluxReport1 = new FluxReport()
                .id(1)
                .previousFluxReport(fluxReport2)
                .creation(creationReport1);

        ReportSummary report1 = new ReportSummary()
                .withExtId(id1)
                .withReferencedId(id2)
                .withCreation(creationReport1);
        ReportSummary report2 = new ReportSummary()
                .withExtId(id2)
                .withReferencedId(null)
                .withCreation(creationReport2);

        //mock
        doReturn(Optional.of(fluxReport1)).when(fluxReportDao).findByExtId(id1);
        doReturn(report1).when(mapper).map(fluxReport1, ReportSummary.class);
        doReturn(report2).when(mapper).map(fluxReport2, ReportSummary.class);

        //execute
        List<ReportSummary> allReferencedReports = reportDomainModelBean.findOlderVersionsOrderedByCreationDateDescending(id1);

        //verify and assert
        verify(fluxReportDao).findByExtId(id1);
        verify(mapper).map(fluxReport1, ReportSummary.class);
        verify(mapper).map(fluxReport2, ReportSummary.class);

        verifyNoMoreInteractions(fluxReportDao, reportHelper, mapper, beanValidatorHelper);

        assertEquals(2, allReferencedReports.size());
        assertSame(report2, allReferencedReports.get(0));
        assertSame(report1, allReferencedReports.get(1));
    }

    @Test
    public void findOlderVersionsOrderedByCreationDateDescendingIncludingDetailsWhenArgumentIsNull() {
        //data set
        String id1 = "1";
        String id2 = "2";

        DateTime creationReport1 = new DateTime(2017, 6, 8, 13, 45);
        DateTime creationReport2 = new DateTime(2017, 6, 9, 13, 45);

        FluxReport fluxReport2 = new FluxReport()
                .id(2)
                .previousFluxReport(null)
                .creation(creationReport2);
        FluxReport fluxReport1 = new FluxReport()
                .id(1)
                .previousFluxReport(fluxReport2)
                .creation(creationReport1);

        Report report1 = ReportMother.with(id1, id2);
        Report report2 = ReportMother.withId(id2);

        //mock
        doReturn(id1).when(reportHelper).getFLUXReportDocumentReferencedIdOrNull(report1);
        doReturn(Optional.of(fluxReport1)).when(fluxReportDao).findByExtId(id1);
        doReturn(report1).when(mapper).map(fluxReport1, Report.class);
        doReturn(report2).when(mapper).map(fluxReport2, Report.class);
        doReturn(creationReport1).when(reportHelper).getCreationDate(report1);
        doReturn(creationReport2).when(reportHelper).getCreationDate(report2);

        //execute
        List<Report> allReferencedReports = reportDomainModelBean.findOlderVersionsOrderedByCreationDateDescendingIncludingDetails(report1);

        //verify and assert
        verify(reportHelper).getFLUXReportDocumentReferencedIdOrNull(report1);
        verify(fluxReportDao).findByExtId(id1);
        verify(mapper).map(fluxReport1, Report.class);
        verify(mapper).map(fluxReport2, Report.class);
        verify(reportHelper).getCreationDate(report1);
        verify(reportHelper).getCreationDate(report2);

        verifyNoMoreInteractions(fluxReportDao, reportHelper, mapper, beanValidatorHelper);

        assertEquals(2, allReferencedReports.size());
        assertSame(report2, allReferencedReports.get(0));
        assertSame(report1, allReferencedReports.get(1));
    }

    @Test
    public void isLatestVersionWhenFalse() {
        Report report = new Report();
        String extId = "extId";

        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(Optional.of(new FluxReport())).when(fluxReportDao).findCorrectionOrDeletionOf(extId);

        assertFalse(reportDomainModelBean.isLatestVersion(report));

        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(fluxReportDao).findCorrectionOrDeletionOf(extId);
        verifyNoMoreInteractions(reportHelper, fluxReportDao, beanValidatorHelper);
    }

    @Test
    public void isLatestVersionWhenTrue() {
        Report report = new Report();
        String extId = "extId";

        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(Optional.absent()).when(fluxReportDao).findCorrectionOrDeletionOf(extId);

        assertTrue(reportDomainModelBean.isLatestVersion(report));

        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(fluxReportDao).findCorrectionOrDeletionOf(extId);
        verifyNoMoreInteractions(reportHelper, fluxReportDao, beanValidatorHelper);
    }

    @Test
    public void findLatestVersionWhenProvidedReportIsLatest() {
        String extId = "extId";
        Report report = new Report();

        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(Optional.absent()).when(fluxReportDao).findCorrectionOrDeletionOf(extId);

        assertSame(report, reportDomainModelBean.findLatestVersion(report));

        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(fluxReportDao).findCorrectionOrDeletionOf(extId);
        verifyNoMoreInteractions(reportHelper, fluxReportDao, mapper, beanValidatorHelper);
    }

    @Test
    public void findLatestVersionWhenProvidedReportHasANewerVersion() {
        String extId = "extId";
        Report report1 = ReportMother.withExtId("1");
        Report report2 = ReportMother.withExtId("2");
        FluxReport fluxReport1 = new FluxReport().id(1);
        FluxReport fluxReport2 = new FluxReport().id(2);

        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report1);
        doReturn(Optional.of(fluxReport1)).when(fluxReportDao).findCorrectionOrDeletionOf(extId);
        doReturn(fluxReport2).when(fluxReportDao).findLatestVersion(fluxReport1);
        doReturn(report2).when(mapper).map(fluxReport2, Report.class);

        assertSame(report2, reportDomainModelBean.findLatestVersion(report1));

        verify(reportHelper).getFLUXReportDocumentId(report1);
        verify(fluxReportDao).findCorrectionOrDeletionOf(extId);
        verify(fluxReportDao).findLatestVersion(fluxReport1);
        verify(mapper).map(fluxReport2, Report.class);
        verifyNoMoreInteractions(reportHelper, fluxReportDao, mapper, beanValidatorHelper);
    }

    @Test
    public void testFindRelatedReportsOf() {
        //data set
        Report report = new Report();
        String extId = "extId";

        FluxReport relatedFluxReport1 = new FluxReport().extId("1");
        FluxReport relatedFluxReport2 = new FluxReport().extId("2");

        FluxReport fluxReport = new FluxReport().relatedSalesNotes(Arrays.asList(relatedFluxReport1))
                                                .relatedTakeOverDocuments(Arrays.asList(relatedFluxReport2));

        Report relatedReport1 = ReportMother.withExtId("1");
        Report relatedReport2 = ReportMother.withExtId("2");

        List<FluxReport> relatedFluxReports = Arrays.asList(relatedFluxReport1, relatedFluxReport2);
        List<Report> relatedReports = Arrays.asList(relatedReport1, relatedReport2);

        //mock
        doReturn(extId).when(reportHelper).getFLUXReportDocumentId(report);
        doReturn(Optional.of(fluxReport)).when(fluxReportDao).findByExtId(extId);
        doReturn(relatedReports).when(mapper).mapAsList(relatedFluxReports, Report.class);

        //execute
        List<Report> result = reportDomainModelBean.findRelatedReportsOf(report);

        //verify and assert
        verify(reportHelper).getFLUXReportDocumentId(report);
        verify(fluxReportDao).findByExtId(extId);
        verify(mapper).mapAsList(relatedFluxReports, Report.class);
        verifyNoMoreInteractions(reportHelper, fluxReportDao, mapper, beanValidatorHelper);

        assertSame(relatedReports, result);
    }

    @Test
    public void tryCreateWhenReportIsACreationAndNoReferencesToTakeOverDocumentsExistForBeanValidationSalesNonBlockingException() throws Exception {
        //data set
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType();
        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);
        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);
        FluxReport fluxReportEntity = new FluxReport();

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(false);
        when(mapper.map(report, FluxReport.class)).thenReturn(fluxReportEntity);
        when(reportHelper.isReportCorrected(report)).thenReturn(false);
        when(reportHelper.hasReferencesToTakeOverDocuments(report)).thenReturn(false);
        doThrow(new SalesNonBlockingException("MySalesNonBlockingException")).when(beanValidatorHelper).validateBean(fluxReportEntity);

        //execute
        try {
            reportDomainModelBean.create(report);
            fail("should fail for bean validation error");

        } catch (SalesNonBlockingException e) {
            assertEquals("MySalesNonBlockingException", e.getMessage());
        }

        //assert and verify
        verify(reportHelper).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(beanValidatorHelper).validateBean(fluxReportEntity);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper, beanValidatorHelper);
    }

}