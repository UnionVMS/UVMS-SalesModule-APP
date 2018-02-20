package eu.europa.ec.fisheries.uvms.sales.service.arquillian.deployment;

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
        standardTestWebArchive.addAsResource("report_original.txt", "report_original.txt");
        standardTestWebArchive.addAsResource("before_corrections.txt", "before_corrections.txt");
        standardTestWebArchive.addAsResource("corrections.txt", "corrections.txt");

        return standardTestWebArchive;
    }

}
