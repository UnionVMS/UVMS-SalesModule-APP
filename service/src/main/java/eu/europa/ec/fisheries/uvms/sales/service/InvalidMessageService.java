package eu.europa.ec.fisheries.uvms.sales.service;

import eu.europa.ec.fisheries.schema.sales.ValidationQualityAnalysisType;
import eu.europa.ec.fisheries.uvms.exception.ServiceException;

import javax.ejb.Local;
import java.util.Collection;

@Local
public interface InvalidMessageService {

    /**
     * Respond to invalid incoming message
     * @param messageGuid of the incoming message
     * @param validationResults the validation results, from which can be concluded that the incoming message is invalid
     * @param recipient the receiver of the message
     * @param plugin the plugin over which the message should be sent
     * @throws ServiceException when something goes wrong
     */
    void sendResponseToInvalidIncomingMessage(String messageGuid, Collection<ValidationQualityAnalysisType> validationResults, String recipient, String plugin) throws ServiceException;
}