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
import jpa.entities.Product;
import utils.DBUtils;

/**
 *
 * @author MinhTran
 */
public class ProductDao extends BaseDao<Product, Long>{
    private ProductDao() {
    }
    
    private static ProductDao instance;
    private static final Object LOCK = new Object();
    
    public static ProductDao getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ProductDao();
            }
        }
        
        return instance;
    }
    
    public Product getProduct(int shopId, String code) {
        EntityManager em = DBUtils.getEntityManager();
        
        try {
            List<Product> result = em.createNamedQuery("Product.findByShopIdAndCode", Product.class)
                    .setParameter("shopId", shopId)
                    .setParameter("code", code)
                    .setMaxResults(1)
                    .getResultList();
            
            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }
        } catch (Exception e) {
            Logger.getLogger(ProductDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return null;
    }
    
    public synchronized Product saveProductAfterCrawling(Product product) {
        Product existedProduct = getProduct(product.getShopId(), product.getCode());
        if (existedProduct != null) {
//            product = update(product);
            return existedProduct;
        } else {
            product = create(product);
        }
        
        return product;
    }
}
