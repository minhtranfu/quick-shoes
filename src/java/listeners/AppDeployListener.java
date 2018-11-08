/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package listeners;

import javax.persistence.EntityManager;
import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import utils.ColorUtils;
import utils.DBUtils;

/**
 * Web application lifecycle listener.
 *
 * @author MinhTran
 */
public class AppDeployListener implements ServletContextListener {
    private final String colorsXMLPath = "WEB-INF/colors.xml";
    private final String replacementsXMLPath = "WEB-INF/alternative-colors.xml";

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        
        // init color utils data for crawler
        ServletContext context = sce.getServletContext();
        String realPath = context.getRealPath("/");
        String colorsRealPath = realPath + colorsXMLPath;
        String replacementsRealPath = realPath + replacementsXMLPath;
        ColorUtils.initData(colorsRealPath, replacementsRealPath);
        
        System.out.println("App started!");
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        EntityManager em = DBUtils.getEntityManager();
        if (em != null) {
            em.close();
        }
    }
}
