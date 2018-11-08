/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package daos;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.EntityManager;
import jpa.entities.ProductVersion;
import utils.DBUtils;

/**
 *
 * @author MinhTran
 */
public class ProductVersionDao extends BaseDao<ProductVersion, Long>{
    private ProductVersionDao() {
    }
    
    private static ProductVersionDao instance;
    private static final Object LOCK = new Object();
    
    public static ProductVersionDao getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ProductVersionDao();
            }
        }
        
        return instance;
    }
    
    public ProductVersion getProductVersion(long productId, int colorId, int size) {
        EntityManager em = DBUtils.getEntityManager();
        
        try {
            List<ProductVersion> result = em.createNamedQuery("ProductVersion.findByProductIdColorIdSize", ProductVersion.class)
                    .setParameter("productId", productId)
                    .setParameter("colorId", colorId)
                    .setParameter("size", size)
                    .setMaxResults(1)
                    .getResultList();
            
            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }
        } catch (Exception e) {
            Logger.getLogger(ProductVersionDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return null;
    }
}
