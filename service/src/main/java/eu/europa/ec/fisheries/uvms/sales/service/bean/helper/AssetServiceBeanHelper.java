package eu.europa.ec.fisheries.uvms.sales.service.bean.helper;

import eu.europa.ec.fisheries.uvms.asset.model.exception.AssetModelMapperException;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.AssetModuleRequestMapper;
import eu.europa.ec.fisheries.uvms.asset.model.mapper.JAXBMarshaller;
import eu.europa.ec.fisheries.uvms.message.MessageException;
import eu.europa.ec.fisheries.uvms.sales.message.constants.Union;
import eu.europa.ec.fisheries.uvms.sales.message.consumer.SalesMessageConsumer;
import eu.europa.ec.fisheries.uvms.sales.message.producer.SalesMessageProducer;
import eu.europa.ec.fisheries.uvms.sales.model.exception.SalesServiceException;
import eu.europa.ec.fisheries.wsdl.asset.types.*;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.jms.JMSException;
import javax.jms.TextMessage;

/**
 * Class who's only purpose is to hide low-level logic from the AssetServiceBean.
 * Should not be used by any other class!
 */
@Stateless
public class AssetServiceBeanHelper {

    @EJB
    private SalesMessageProducer messageProducer;

    @EJB
    private SalesMessageConsumer receiver;

    public <T> T callAssetModule(String assetListModuleRequest, Class<T> returnType) {
        try {
            String messageId = messageProducer.sendModuleMessage(assetListModuleRequest, Union.ASSET);
            TextMessage responseText = receiver.getMessage(messageId, TextMessage.class);
            return unmarshallTextMessage(responseText, returnType);
        } catch (MessageException e) {
            throw new SalesServiceException("Could not contact the Asset Module", e);
        }
    }

    private <T> T unmarshallTextMessage(TextMessage responseText, Class<T> returnType) {
        try {
            return JAXBMarshaller.unmarshallTextMessage(responseText, returnType);
        } catch (AssetModelMapperException e) {
            try {
                throw new SalesServiceException("Could not parse the response from the the Asset Module. The response was " + responseText.getText(), e);
            } catch (JMSException anotherException) {
                throw new SalesServiceException("Could not parse the response from the the Asset Module.", e);
            }
        }
    }

    public String createRequestToFindAssetsByNameOrCFROrIRCS(String searchString) {
        try {
            AssetListQuery query = createAssetListQueryToSearchOnNameOrCFROrIRCS(searchString);
            return AssetModuleRequestMapper.createAssetListModuleRequest(query);
        } catch (AssetModelMapperException e) {
            throw new SalesServiceException("Could not create a request to query the Asset Module.", e);
        }
    }

    public String createRequestToFindAssetByCFR(String cfr) {
        try {
            return AssetModuleRequestMapper.createGetAssetModuleRequest(cfr, AssetIdType.CFR);
        } catch (AssetModelMapperException e) {
            throw new SalesServiceException("Could not create a request to query the Asset Module.", e);
        }
    }

    public AssetListQuery createAssetListQueryToSearchOnNameOrCFROrIRCS(String searchString) {
        AssetListCriteria criteria = new AssetListCriteria();
        criteria.getCriterias().add(createAssetListCriteriaPair(searchString, ConfigSearchField.NAME));
        criteria.getCriterias().add(createAssetListCriteriaPair(searchString, ConfigSearchField.CFR));
        criteria.getCriterias().add(createAssetListCriteriaPair(searchString, ConfigSearchField.IRCS));
        criteria.setIsDynamic(false); //by setting dynamic to false, only one of the criteria should be met (use of OR operator instead of AND).

        AssetListQuery query = new AssetListQuery();
        query.setAssetSearchCriteria(criteria);
        query.setPagination(new AssetListPagination());
        return query;
    }

    public AssetListCriteriaPair createAssetListCriteriaPair(String searchString, ConfigSearchField name) {
        AssetListCriteriaPair searchOnName = new AssetListCriteriaPair();
        searchOnName.setKey(name);
        searchOnName.setValue(searchString);
        return searchOnName;
    }

}
