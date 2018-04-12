package eu.europa.ec.fisheries.uvms.sales.integrationtest.deployment;

import eu.europa.ec.fisheries.uvms.sales.integrationtest.deployment.factory.TestDeploymentFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;

public abstract class StandardTestDeployment {

    private static final String TEST_ARCHIVE_NAME = "sales_service_standard_test";

    @Deployment(name = "salesservice", order = 1)
    public static WebArchive createStandardDeployment() {
        WebArchive standardTestWebArchive = TestDeploymentFactory.createBasicTestDeployment(TEST_ARCHIVE_NAME);

        // Alternative ejb bean
        standardTestWebArchive.addAsWebInfResource("ejb-jar-test.xml", "ejb-jar.xml");

        // Test data
        standardTestWebArchive.addAsResource("test-data/report_original.txt", "test-data/report_original.txt");
        standardTestWebArchive.addAsResource("test-data/before_corrections.txt", "test-data/before_corrections.txt");
        standardTestWebArchive.addAsResource("test-data/corrections.txt", "test-data/corrections.txt");

        return standardTestWebArchive;
    }

}
