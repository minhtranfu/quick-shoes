/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daos;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import utils.DBUtils;

/**
 *
 * @author MinhTran
 */
public class BaseDao<T, PK extends Serializable> implements IGenericDao<T, PK>{

    protected Class<T> entityClass;

    public BaseDao() {
        ParameterizedType genericSuperClass = (ParameterizedType) getClass().getGenericSuperclass();
        this.entityClass = (Class<T>) genericSuperClass.getActualTypeArguments()[0];
    }
    
    @Override
    public T create(T t) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            EntityTransaction ts = em.getTransaction();
            ts.begin();
            em.persist(t);
            ts.commit();
            
            return t;
        } catch (Exception e) {
            Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return null;
    }

    @Override
    public T get(PK id) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            EntityTransaction ts = em.getTransaction();
            ts.begin();
            T t = em.find(entityClass, id);
            ts.commit();
            
            return t;
        } catch (Exception e) {
            Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return null;
    }

    @Override
    public T update(T t) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            EntityTransaction ts = em.getTransaction();
            ts.begin();
            t = em.merge(t);
            ts.commit();
            
            return t;
        } catch (Exception e) {
            Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return null;
    }

    @Override
    public boolean delete(T t) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            EntityTransaction ts = em.getTransaction();
            ts.begin();
            em.remove(t);
            ts.commit();
            
            return true;
        } catch (Exception e) {
            Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return false;
    }

    @Override
    public List<T> getAll(String namedQuery) {
        EntityManager em = DBUtils.getEntityManager();
        try {
            EntityTransaction ts = em.getTransaction();
            ts.begin();
            List<T> result = em.createNamedQuery(namedQuery, entityClass).getResultList();
            ts.commit();
            
            return result;
        } catch (Exception e) {
            Logger.getLogger(BaseDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return null;
    }
    
}
