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
import jpa.entities.Color;
import utils.DBUtils;

/**
 *
 * @author MinhTran
 */
public class ColorDao extends BaseDao<Color, Long>{
    private ColorDao() {
    }
    
    private static ColorDao instance;
    private static final Object LOCK = new Object();
    
    public static ColorDao getInstance() {
        synchronized (LOCK) {
            if (instance == null) {
                instance = new ColorDao();
            }
        }
        
        return instance;
    }
    
    public Color getColorByName(String name) {
        EntityManager em = DBUtils.getEntityManager();
        
        try {
            List<Color> result = em.createNamedQuery("Color.findByName", Color.class)
                    .setParameter("name", name)
                    .setMaxResults(1)
                    .getResultList();
            
            if (result != null && !result.isEmpty()) {
                return result.get(0);
            }
        } catch (Exception e) {
            Logger.getLogger(ColorDao.class.getName()).log(Level.SEVERE, null, e);
        } finally {
            if (em != null) {
                em.close();
            }
        }
        
        return null;
    }
}
