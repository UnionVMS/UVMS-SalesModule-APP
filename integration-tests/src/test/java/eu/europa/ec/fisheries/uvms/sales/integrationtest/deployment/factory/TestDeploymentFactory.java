package eu.europa.ec.fisheries.uvms.sales.integrationtest.deployment.factory;

import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.consumer.bean.MessageConsumerBean;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.message.producer.bean.SalesMessageProducerBean;
import eu.europa.ec.fisheries.uvms.sales.service.*;
import eu.europa.ec.fisheries.uvms.sales.service.bean.*;
import eu.europa.ec.fisheries.uvms.sales.service.bean.helper.*;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ExchangeRateCache;
import eu.europa.ec.fisheries.uvms.sales.service.cache.ReferenceDataCache;
import eu.europa.ec.fisheries.uvms.sales.service.constants.MDRCodeListKey;
import eu.europa.ec.fisheries.uvms.sales.service.factory.FLUXSalesResponseMessageFactory;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.asset.EmptyAsset;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.jboss.shrinkwrap.resolver.api.maven.Maven;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class TestDeploymentFactory {

    final static Logger LOG = LoggerFactory.getLogger(TestDeploymentFactory.class);

    private static WebArchive createBasicTestArchive(final String archiveName) {
        File[] files = Maven.resolver().loadPomFromFile("pom.xml")
                .importRuntimeDependencies().resolve().withTransitivity().asFile();

        printFiles(files);

        // Embedding war package which contains the test class is needed
        // So that Arquillian can invoke test class through its servlet test runner
        WebArchive testWar = ShrinkWrap.create(WebArchive.class, archiveName + ".war");

        testWar.addClass(SalesConfigHelperBean.class);

        // Empty beans for EE6 CDI
        testWar.addAsWebInfResource(EmptyAsset.INSTANCE, "beans.xml");
        testWar.addAsResource("persistence-integration.xml", "META-INF/persistence.xml");
        testWar.addAsResource("logback-test.xml", "logback.xml");
        testWar.addAsResource("logging.properties", "logging.properties");
        testWar.addAsManifestResource("jboss-deployment-structure.xml","jboss-deployment-structure.xml");

        testWar.addAsResource("test-data/report_original.txt", "test-data/report_original.txt");
        testWar.addAsResource("test-data/before_corrections.txt", "test-data/before_corrections.txt");
        testWar.addAsResource("test-data/corrections.txt", "test-data/corrections.txt");

        testWar.addAsLibraries(files);

        testWar.addPackages(true, "eu.europa.ec.fisheries.uvms.sales.integrationtest");

        return testWar;
    }

    public static WebArchive createBasicTestDeployment(final String testArchiveName) {
        WebArchive archive = createBasicTestArchive(testArchiveName);


        archive.addClass(eu.europa.ec.fisheries.uvms.commons.message.impl.JMSUtils.class);
        archive.addClass(eu.europa.ec.fisheries.uvms.commons.message.api.MessageException.class);

        boolean isAddRecursivelyTrue = true;
        archive.addPackages(isAddRecursivelyTrue, "eu.europa.ec.fisheries.uvms.commons.message");
        archive.addPackages(isAddRecursivelyTrue, "eu.europa.ec.fisheries.uvms.sales.domain");
        archive.addPackages(isAddRecursivelyTrue, "eu.europa.ec.fisheries.uvms.sales.message");
        archive.addPackages(isAddRecursivelyTrue, "eu.europa.ec.fisheries.uvms.sales.service.dto");
        archive.addPackages(isAddRecursivelyTrue, "eu.europa.ec.fisheries.uvms.sales.service.mapper");
        archive.addPackages(isAddRecursivelyTrue, "eu.europa.ec.fisheries.uvms.sales.service.converter");
        archive.addClass(QueryService.class).addClass(QueryServiceBean.class);
        archive.addClass(UniqueIdService.class).addClass(UniqueIdServiceBean.class);
        archive.addClass(ReportService.class).addClass(ReportServiceBean.class);
        archive.addClass(eu.europa.ec.fisheries.uvms.sales.service.AssetCache.class).addClass(eu.europa.ec.fisheries.uvms.sales.service.bean.AssetCacheBean.class);
        archive.addClass(ReportServiceHelper.class);
        archive.addClass(EcbProxyService.class).addClass(EcbProxyServiceBean.class);
        archive.addClass(MDRCodeListKey.class);
        archive.addClass(MDRService.class).addClass(MDRServiceBean.class);
        archive.addClass(ReferenceDataCache.class);
        archive.addClass(SalesDetailsHelper.class);
        archive.addClass(AssetServiceBeanHelper.class);
        archive.addClass(AssetService.class).addClass(AssetServiceBean.class);
        archive.addClass(SearchReportsHelper.class);
        archive.addClass(ReportServiceExportHelper.class);
        archive.addClass(ReportService.class).addClass(ReportServiceBean.class);
        archive.addClass(Union.class);
        archive.addClass(SalesMessageProducer.class).addClass(SalesMessageProducerBean.class);
        archive.addClass(RulesService.class).addClass(RulesServiceBean.class);
        archive.addClass(ConfigService.class).addClass(ConfigServiceBean.class);
        archive.addClass(FLUXSalesResponseMessageFactory.class);
        archive.addClass(UnsavedMessageService.class).addClass(UnsavedMessageServiceBean.class);
        archive.addClass(EventService.class).addClass(EventServiceBean.class);
        archive.addClass(ExchangeService.class).addClass(ExchangeServiceBean.class);
        archive.addClass(OutgoingMessageService.class).addClass(OutgoingMessageServiceBean.class);
        archive.addClass(MessageConsumerBean.class);

        archive.addClass(ResponseService.class).addClass(ResponseServiceBean.class);
        archive.addClass(ExchangeRateCache.class);

        return archive;
    }

    private static void printFiles(File[] files) {

        List<File> filesSorted = new ArrayList<>();
        for(File f : files){
            filesSorted.add(f);
        }

        Collections.sort(filesSorted, new Comparator<File>() {
            @Override
            public int compare(File o1, File o2) {
                return o1.getName().compareTo(o2.getName());
            }
        });

        LOG.info("FROM POM - begin");
        for(File f : filesSorted){
            LOG.info("       --->>>   "   +   f.getName());
        }
        LOG.info("FROM POM - end");
    }
}
