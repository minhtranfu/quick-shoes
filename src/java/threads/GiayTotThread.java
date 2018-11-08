/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package threads;

import constants.WebConstants;
import crawlers.BazaCrawler;
import crawlers.GiayTotCrawler;
import daos.CategoryDao;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import jpa.entities.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import utils.XMLUtilities;

/**
 *
 * @author MinhTran
 */
public class GiayTotThread extends BaseThread implements Runnable {

    private ServletContext context;
    private final String configPath = "WEB-INF/targets/giaytotcom.xml";

    public GiayTotThread(ServletContext context) {
        this.context = context;
    }

    @Override
    public void run() {

        startCrawlFromXMLConfig();
    }
    
    private void startCrawlFromXMLConfig() {
        try {
            String realPath = context.getRealPath("/") + configPath;
            Document document = XMLUtilities.parseFileToDom(realPath);
            XPath xPath = XMLUtilities.createXpath();
            String expression = "/shop/categories/category";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
                    document, XPathConstants.NODESET);

            System.out.println("Categories length: " + nodeList.getLength());
            try {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String url = element.getAttribute("url");
                        String categoryName = element.getAttribute("name");
                        short sex = Short.parseShort(element.getAttribute("sex"));

                        // get or create new category
                        CategoryDao dao = CategoryDao.getInstance();
                        Category category = dao.existedOrNew(WebConstants.CAT_TYPE_TYPE, categoryName);
                        int categoryId = category.getId();

                        // start crawl product in category
                        System.out.println("Start " + url);
                        Thread thread = new Thread(new GiayTotCrawler(context, url, sex, categoryId));
                        thread.start();
                    }
                }
            } catch (NumberFormatException e) {
                Logger.getLogger(GiayTotThread.class.getName()).log(Level.SEVERE, null, e);
            }

        } catch (IOException | ParserConfigurationException | XPathExpressionException | SAXException e) {
            Logger.getLogger(GiayTotThread.class.getName()).log(Level.SEVERE, null, e);
        }
    }

}
