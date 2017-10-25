package eu.europa.ec.fisheries.uvms.sales.domain.bean;

import com.google.common.base.Optional;
import eu.europa.ec.fisheries.schema.sales.*;
import eu.europa.ec.fisheries.uvms.sales.domain.UnsavedMessageDomainModel;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.FluxReportItemType;
import eu.europa.ec.fisheries.uvms.sales.domain.constant.Purpose;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.FluxReportDao;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.domain.helper.ReportHelper;
import eu.europa.ec.fisheries.uvms.sales.domain.mother.ReportMother;
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
        when(fluxReportDao.create(fluxReportEntity)).thenReturn(fluxReportEntity);
        when(mapper.map(fluxReportEntity, Report.class)).thenReturn(report);

        //execute
        reportDomainModelBean.create(report);

        //assert and verify
        verify(reportHelper).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(fluxReportDao).create(fluxReportEntity);
        verify(mapper).map(fluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper);
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
        verify(fluxReportDao).create(fluxReportEntity);
        verify(mapper).map(fluxReportEntity, Report.class);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper);

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
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper);

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
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper);

        assertSame(mappedAndPersistedReportFromDao, persistedReport);
        //check if date of deletion matches the first date of deletion, not the date of the 2nd received deletion
        assertEquals(deletionDate, persistedReport.getDeletion());
    }

    @Test
    public void testCreateWhenReportIsACorrection() throws Exception {
        //data set
        FLUXReportDocumentType fluxReportDocumentType = new FLUXReportDocumentType()
                .withReferencedID(new IDType().withValue("hello"))
                .withPurposeCode(new CodeType().withValue(Purpose.CORRECTION.getNumericCode() + ""));

        FLUXSalesReportMessage fluxSalesReportMessage = new FLUXSalesReportMessage()
                .withFLUXReportDocument(fluxReportDocumentType);

        Report report = new Report()
                .withFLUXSalesReportMessage(fluxSalesReportMessage);

        FluxReport newFluxReportEntity = new FluxReport().extId("world").purpose(Purpose.CORRECTION);
        FluxReport oldFluxReportEntity = new FluxReport().extId("hello");

        //mock
        when(reportHelper.isReportDeleted(report)).thenReturn(false);
        when(mapper.map(report, FluxReport.class)).thenReturn(newFluxReportEntity);
        when(reportHelper.isReportCorrected(report)).thenReturn(true);
        when(reportHelper.hasReferencesToTakeOverDocuments(report)).thenReturn(false);
        when(reportHelper.getFLUXReportDocumentReferencedId(report)).thenReturn("hello");
        when(fluxReportDao.findByExtId("hello")).thenReturn(Optional.of(oldFluxReportEntity));

        when(fluxReportDao.create(newFluxReportEntity)).thenReturn(newFluxReportEntity);
        when(mapper.map(newFluxReportEntity, Report.class)).thenReturn(report);

        //execute
        Report persistedReport = reportDomainModelBean.create(report);

        //assert and verify
        verify(reportHelper).isReportDeleted(report);
        verify(mapper).map(report, FluxReport.class);
        verify(reportHelper).isReportCorrected(report);
        verify(reportHelper).getFLUXReportDocumentReferencedId(report);
        verify(fluxReportDao).findByExtId("hello");
        verify(reportHelper).hasReferencesToTakeOverDocuments(report);
        verify(mapper).map(newFluxReportEntity, Report.class);
        verify(fluxReportDao).create(newFluxReportEntity);
        verifyNoMoreInteractions(mapper, fluxReportDao, reportHelper);

        assertEquals("hello", persistedReport.getFLUXSalesReportMessage().getFLUXReportDocument().getReferencedID().getValue());
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
        verifyNoMoreInteractions(fluxReportDao, mapper);

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
        reportDomainModelBean.search(null);
    }

    @Test
    public void testSearchWhenSuccess() {
        ReportQuery reportQuery = new ReportQuery();
        List<FluxReport> fluxReports = new ArrayList<>();
        List<Report> expected = new ArrayList<>();

        when(fluxReportDao.search(reportQuery)).thenReturn(fluxReports);
        when(mapper.mapAsList(fluxReports, Report.class)).thenReturn(expected);

        List<Report> actual = reportDomainModelBean.search(reportQuery);

        verify(fluxReportDao).search(reportQuery);
        verify(mapper).mapAsList(fluxReports, Report.class);
        verifyNoMoreInteractions(fluxReportDao, mapper);

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
        verifyNoMoreInteractions(fluxReportDao);

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
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertEquals(Optional.of(report), result);
    }

    @Test
    public void testFindReportWhichRefersToWhenNothingFound() {
        String extId = "extId";

        doReturn(Optional.absent()).when(fluxReportDao).findCorrectionOrDeletionOf(extId);

        Optional<Report> result = reportDomainModelBean.findCorrectionOrDeletionOf(extId);

        verify(fluxReportDao).findCorrectionOrDeletionOf(extId);
        verifyNoMoreInteractions(fluxReportDao, mapper);

        assertEquals(Optional.<Report>absent(), result);
    }

    @Test
    public void findOlderVersionsOrderedByCreationDateDescending() {
        //data set
        String id1 = "1";
        String id2 = "2";

        FluxReport fluxReport1 = new FluxReport().id(1);
        FluxReport fluxReport2 = new FluxReport().id(2);

        Report report1 = ReportMother.with(id1, id2);
        Report report2 = ReportMother.with(id2, null);

        //mock
        doReturn(Optional.of(fluxReport1)).when(fluxReportDao).findByExtId(id1);
        doReturn(report1).when(mapper).map(fluxReport1, Report.class);
        doReturn(id2).when(reportHelper).getFLUXReportDocumentReferencedIdOrNull(report1);

        doReturn(Optional.of(fluxReport2)).when(fluxReportDao).findByExtId(id2);
        doReturn(report2).when(mapper).map(fluxReport2, Report.class);
        doReturn(null).when(reportHelper).getFLUXReportDocumentReferencedIdOrNull(report2);

        doReturn(new DateTime(2017, 6, 8, 13, 45)).when(reportHelper).getCreationDate(report1);
        doReturn(new DateTime(2017, 6, 9, 13, 45)).when(reportHelper).getCreationDate(report2);

        //execute
        List<Report> allReferencedReports = reportDomainModelBean.findOlderVersionsOrderedByCreationDateDescending(id1);

        //verify and assert
        verify(fluxReportDao).findByExtId(id1);
        verify(mapper).map(fluxReport1, Report.class);
        verify(reportHelper).getFLUXReportDocumentReferencedIdOrNull(report1);


        verify(fluxReportDao).findByExtId(id2);
        verify(mapper).map(fluxReport2, Report.class);
        verify(reportHelper).getFLUXReportDocumentReferencedIdOrNull(report2);

        verify(reportHelper).getCreationDate(report1);
        verify(reportHelper).getCreationDate(report2);

        verifyNoMoreInteractions(fluxReportDao, reportHelper, mapper);

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
        verifyNoMoreInteractions(reportHelper, fluxReportDao);
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
        verifyNoMoreInteractions(reportHelper, fluxReportDao);
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
        verifyNoMoreInteractions(reportHelper, fluxReportDao, mapper);
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
        verifyNoMoreInteractions(reportHelper, fluxReportDao, mapper);
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
        verifyNoMoreInteractions(reportHelper, fluxReportDao, mapper);

        assertSame(relatedReports, result);
    }

}