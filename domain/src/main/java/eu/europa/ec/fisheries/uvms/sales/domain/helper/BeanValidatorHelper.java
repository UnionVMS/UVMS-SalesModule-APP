package eu.europa.ec.fisheries.uvms.sales.domain.helper;

import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesNonBlockingException;
import lombok.extern.slf4j.Slf4j;

import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import java.util.Set;

@Slf4j
public class BeanValidatorHelper {

    public static <T> void validateBean(T beanToValidate) {
        Set<ConstraintViolation<T>> constraintViolations = null;
        try {
            Validator validator = Validation.buildDefaultValidatorFactory().getValidator();
            constraintViolations = validator.validate(beanToValidate);
            if (constraintViolations.isEmpty()) {
                return;
            }

        } catch(Exception e) {
            log.warn("Unable to validate bean. Reason: " + e.getMessage());
            return;
        }

        throw new SalesNonBlockingException(composeErrorMessage(constraintViolations));
    }

    private static <T> String composeErrorMessage(Set<ConstraintViolation<T>> constraintViolations) {
        StringBuilder sbErrorMessages = new StringBuilder();
        for (ConstraintViolation<T> violation : constraintViolations) {
            sbErrorMessages.append(" Invalid bean: violating condition for bean: ");
            sbErrorMessages.append(violation.getPropertyPath().toString());
            sbErrorMessages.append(" error: ");
            sbErrorMessages.append(violation.getMessage());
            sbErrorMessages.append(" whereas it has value: ");
            sbErrorMessages.append(violation.getInvalidValue());
        }
        log.info(sbErrorMessages.toString());
        return sbErrorMessages.toString();
    }

}
