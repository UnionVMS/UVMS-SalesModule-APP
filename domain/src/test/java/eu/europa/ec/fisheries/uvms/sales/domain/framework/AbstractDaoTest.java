package eu.europa.ec.fisheries.uvms.sales.domain.framework;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.DaoForSales;
import eu.europa.ec.fisheries.uvms.sales.domain.dao.bean.BaseDaoForSales;
import org.apache.commons.lang.StringUtils;
import org.dbunit.Assertion;
import org.dbunit.DatabaseUnitException;
import org.dbunit.IDatabaseTester;
import org.dbunit.JdbcDatabaseTester;
import org.dbunit.database.DatabaseSequenceFilter;
import org.dbunit.database.IDatabaseConnection;
import org.dbunit.dataset.*;
import org.dbunit.dataset.filter.DefaultColumnFilter;
import org.dbunit.dataset.xml.FlatXmlDataSetBuilder;
import org.dbunit.operation.DatabaseOperation;
import org.junit.*;
import org.junit.rules.TestName;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.sql.SQLException;
import java.util.Map;

public abstract class AbstractDaoTest<T extends BaseDaoForSales> {

    private static final String PERSISTENCE_UNIT = "h2";

    @Rule
    public TestName testName = new TestName();

    private static EntityManagerFactory entityManagerFactory;
    private static IDatabaseTester databaseTester;
    private static IDatabaseConnection databaseConnection;
    private static IDataSet schemaOfEntireDatabase;

    private EntityManager entityManager;
    protected T dao;

    @BeforeClass
    public static void setUpTest() throws Exception {
        createEntityManagerFactory();
        createDatabaseTester();
        getSchemaOfTheEntireDatabase();
    }

    private static void getSchemaOfTheEntireDatabase() throws Exception {
        databaseConnection = databaseTester.getConnection();

        //read entire database, in an order which respects foreign keys
        DatabaseSequenceFilter filter = new DatabaseSequenceFilter(databaseConnection);
        schemaOfEntireDatabase = new FilteredDataSet(filter, databaseConnection.createDataSet());
    }

    @Before
    public void createDaoAndInsertDataSet() throws Exception {
        dao = createDao();
        createAndInjectEntityManager(dao);
        loadDataSet();
        entityManager.getTransaction().begin();
    }

    @After
    public void validateDataSetAndCleanUp() throws DatabaseUnitException, SQLException, NoSuchMethodException {
        EntityTransaction transaction = entityManager.getTransaction();
        if(transaction.isActive()) {
            transaction.commit();
        }

        assertThatDatabaseContainsExpectedDataSet();
        cleanDatabase();
    }

    @AfterClass
    public static void closeConnections() throws Exception {
        entityManagerFactory.close();
        databaseConnection.close();
    }

    protected void commitTransaction() {
        entityManager.getTransaction().commit();
    }

    private static void createDatabaseTester() throws ClassNotFoundException {
        Map<String, Object> e = entityManagerFactory.getProperties();
        String driver = (String) e.get("hibernate.connection.driver_class");
        String url = (String) e.get("hibernate.connection.url");
        String username = (String) e.get("hibernate.connection.user");
        String password = (String) e.get("hibernate.connection.password");
        databaseTester = new JdbcDatabaseTester(driver, url, username, password);
    }

    private static void createEntityManagerFactory() {
        entityManagerFactory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
    }

    private void createAndInjectEntityManager(DaoForSales dao) {
        entityManager = entityManagerFactory.createEntityManager();
        dao.setEntityManager(entityManager);
    }

    private T createDao() throws InstantiationException, IllegalAccessException {
        Class<T> daoClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
        return daoClass.newInstance();
    }

    private void loadDataSet() throws Exception {
        DataSet dataSetLocation = getDataSetAnnotation();

        if (dataSetLocation != null && StringUtils.isNotBlank(dataSetLocation.initialData())) {
            InputStream dataSetStream = Thread.currentThread().getContextClassLoader().getResourceAsStream
                    (dataSetLocation.initialData());
            IDataSet dataSet = createDataSet(dataSetStream);
            DatabaseOperation.CLEAN_INSERT.execute(databaseConnection, dataSet);
        }
    }

    private ReplacementDataSet createDataSet(InputStream datasetStream) {
        try {
            ReplacementDataSet dataSet = new ReplacementDataSet((new FlatXmlDataSetBuilder()).build(datasetStream));
            dataSet.addReplacementObject("[NULL]", null);
            return dataSet;
        } catch (DataSetException exception) {
            throw new RuntimeException("Could not create the data set.", exception);
        }
    }

    private DataSet getDataSetAnnotation() throws NoSuchMethodException {
        Class clazz = this.getClass();
        Method method = clazz.getMethod(testName.getMethodName());
        return method.getAnnotation(DataSet.class);
    }

    private void assertThatDatabaseContainsExpectedDataSet() throws NoSuchMethodException {
        DataSet dataSetLocation = getDataSetAnnotation();

        if (dataSetLocation != null && StringUtils.isNotBlank(dataSetLocation.expectedData())) {
            try {
                InputStream expectedDataSetStream = Thread.currentThread().getContextClassLoader().getResourceAsStream(dataSetLocation.expectedData());
                IDataSet expectedDataSet = createDataSet(expectedDataSetStream);
                IDataSet actualDataSet = databaseConnection.createDataSet();

                //compare
                String[] tableNames = expectedDataSet.getTableNames();
                for (String table : tableNames) {
                    ITable filteredTable = DefaultColumnFilter.includedColumnsTable(actualDataSet.getTable(table),
                            expectedDataSet.getTableMetaData(table).getColumns());
                    Assertion.assertEquals(expectedDataSet.getTable(table), filteredTable);
                }
            } catch (SQLException | DatabaseUnitException e) {
                throw new RuntimeException("Could not compare the database contents with the expected dataset.", e);
            }
        }
    }

    private void cleanDatabase() throws DatabaseUnitException, SQLException {
        DatabaseOperation.DELETE_ALL.execute(databaseConnection, schemaOfEntireDatabase);
    }
}