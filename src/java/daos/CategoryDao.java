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
import jpa.entities.Category;
import utils.DBUtils;

/**
 *
 * @author MinhTran
 */
public class CategoryDao extends BaseDao<Category, Long>{
    private CategoryDao() {
    }
    
    private static CategoryDao instance;
    private static final Object LOCK = new Object();
    
    public static CategoryDao getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new CategoryDao();
            }
        }
        
        return instance;
    }
    
    public synchronized Category existedOrNew(String type, String name) {
        EntityManager em = DBUtils.getEntityManager();
        
        try {
            
            // get existed
            List<Category> result = em.createNamedQuery("Category.findByTypeAndName", Category.class)
                    .setParameter("type", type)
                    .setParameter("name", name)
                    .setMaxResults(1)
                    .getResultList();
            
            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }
            
            // create new
            Category category = new Category(type, name);
            return instance.create(category);
        } catch (Exception e) {
            Logger.getLogger(CategoryDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return null;
    }
}
