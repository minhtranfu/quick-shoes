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
import jpa.entities.Image;
import utils.DBUtils;

/**
 *
 * @author MinhTran
 */
public class ImageDao extends BaseDao<Image, Long>{
    private ImageDao() {
    }
    
    private static ImageDao instance;
    private static final Object LOCK = new Object();
    
    public static ImageDao getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ImageDao();
            }
        }
        
        return instance;
    }
    
    public List<Image> getImagesByProductId(long productId) {
        EntityManager em = DBUtils.getEntityManager();
        
        try {
            List<Image> result = em.createNamedQuery("Image.findByProductId", Image.class)
                    .setParameter("productId", productId)
                    .getResultList();
            
            return result;
        } catch (Exception e) {
            Logger.getLogger(ImageDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return null;
    }
}
