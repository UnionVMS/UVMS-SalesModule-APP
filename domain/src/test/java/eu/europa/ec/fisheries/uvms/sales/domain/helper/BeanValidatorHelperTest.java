package eu.europa.ec.fisheries.uvms.sales.domain.helper;

import eu.europa.ec.fisheries.uvms.sales.domain.entity.AuctionSale;
import eu.europa.ec.fisheries.uvms.sales.domain.entity.FluxReport;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;

import javax.validation.ConstraintViolation;
import javax.validation.Path;
import javax.validation.Validator;
import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class BeanValidatorHelperTest {

    @InjectMocks
    BeanValidatorHelper beanValidatorHelper;

    @Mock
    private Validator validator;

    @Test
    public void testValidateBeanForFluxReport() {
        //data
        FluxReport fluxReport = new FluxReport();
        Set<ConstraintViolation<FluxReport>> constraintViolations = new HashSet<>();

        //mock
        doReturn(constraintViolations).when(validator).validate(fluxReport);

        //execute
        beanValidatorHelper.validateBean(fluxReport);

        //assert
        verify(validator).validate(fluxReport);
        verifyNoMoreInteractions(validator);
    }

    @Test
    public void tryValidateBeanWithConstraintViolations() {
        //data
        AuctionSale auctionSale = new AuctionSale();

        //mock
        ConstraintViolation<FluxReport> violationMock = mock(ConstraintViolation.class);
        Set<ConstraintViolation<FluxReport>> constraintViolations = new HashSet<>();
        constraintViolations.add(violationMock);
        Path pathMock = mock(Path.class);
        doReturn(pathMock).when(violationMock).getPropertyPath();
        doReturn("MyPath").when(pathMock).toString();
        doReturn("MyMessage").when(violationMock).getMessage();
        doReturn("MyInvalidValue").when(violationMock).getInvalidValue();
        doReturn(constraintViolations).when(validator).validate(auctionSale);

        //execute
        try {
            beanValidatorHelper.validateBean(auctionSale);
            fail("should fail for bean validation error");

        } catch(SalesNonBlockingException e) {
            assertEquals(" Invalid bean: violating condition for bean: MyPath error: MyMessage whereas it has value: MyInvalidValue", e.getMessage());
        }

        //assert
        verify(validator).validate(auctionSale);
        verify(violationMock).getPropertyPath();
        verify(violationMock).getMessage();
        verify(violationMock).getInvalidValue();
        verifyNoMoreInteractions(validator, violationMock, pathMock);
    }
}
