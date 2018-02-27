package eu.europa.ec.fisheries.uvms.sales.service.integrationtest.deployment;

import eu.europa.ec.fisheries.uvms.sales.service.integrationtest.deployment.factory.TestDeploymentFactory;
import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.shrinkwrap.api.spec.WebArchive;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

public abstract class MessageRedeliveryTestDeployment {

    private static final String TEST_ARCHIVE_NAME = "sales_service_redelivery_test";

    @PersistenceContext
    protected EntityManager em;

    @Deployment(name = "salesservice_redelivery", order = 1)
    public static WebArchive createStandardDeployment() {
        WebArchive standardTestWebArchive = TestDeploymentFactory.createBasicTestDeployment(TEST_ARCHIVE_NAME);

        // Alternative ejb bean
        standardTestWebArchive.addAsWebInfResource("ejb-jar-test-redelivery.xml", "ejb-jar.xml");

        return standardTestWebArchive;
    }

}
