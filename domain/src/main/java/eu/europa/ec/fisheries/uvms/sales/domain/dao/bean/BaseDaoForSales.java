package eu.europa.ec.fisheries.uvms.sales.domain.dao.bean;

import eu.europa.ec.fisheries.uvms.sales.domain.dao.DaoForSales;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import java.io.Serializable;
import java.lang.reflect.ParameterizedType;

/**
 * The base for every DAO in the Sales module. Provides the most used methods.
 */
public abstract class BaseDaoForSales<T, PK extends Serializable> implements DaoForSales<T, PK> {

    @PersistenceContext(unitName = "domainPU")
    protected EntityManager em;

    public BaseDaoForSales() {
    }

    public T findByIdOrNull(PK id) {
        return em.find(getEntityClass(), id);
    }

    public T create(T entity) {
        em.persist(entity);
        return entity;
    }

    public void delete(T entity) {
        em.remove(entity);
    }

    public void setEntityManager(EntityManager em) {
        this.em = em;
    }

    @SuppressWarnings("unchecked")
    public Class<T> getEntityClass() {
        return (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

}
