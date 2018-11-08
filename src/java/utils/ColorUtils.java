/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package utils;

import crawlers.GiayBQCrawler;
import daos.CategoryDao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import jpa.entities.Category;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import threads.GiayBQThread;

/**
 *
 * @author MinhTran
 */
public class ColorUtils {

    public static List<String> colors;

    public static Map<String, String> colorReplacements;

    public static void initData(String colorsRealPath, String replacementsRealPath) {
        initColors(colorsRealPath);
        initReplacements(replacementsRealPath);
    }

    public static void initColors(String colorsRealPath) {
        colors = new ArrayList<>();
        try {
            Document document = XMLUtilities.parseFileToDom(colorsRealPath);
            XPath xPath = XMLUtilities.createXpath();
            String expression = "/colors/color";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
                    document, XPathConstants.NODESET);

            try {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String color = element.getAttribute("name");
                        colors.add(color);
                    }
                }
            } catch (NumberFormatException e) {
                Logger.getLogger(GiayBQThread.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (Exception e) {
            Logger.getLogger(ColorUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public static void initReplacements(String colorsRealPath) {
        colorReplacements = new HashMap<>();
        try {
            Document document = XMLUtilities.parseFileToDom(colorsRealPath);
            XPath xPath = XMLUtilities.createXpath();
            String expression = "/alternative-colors/alternative-color";
            NodeList nodeList = (NodeList) xPath.compile(expression).evaluate(
                    document, XPathConstants.NODESET);

            try {
                for (int i = 0; i < nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element element = (Element) node;
                        String origin = element.getAttribute("origin");
                        String replacement = element.getAttribute("replacement");
                        colorReplacements.put(origin, replacement);
                    }
                }
            } catch (NumberFormatException e) {
                Logger.getLogger(GiayBQThread.class.getName()).log(Level.SEVERE, null, e);
            }
        } catch (Exception e) {
            Logger.getLogger(ColorUtils.class.getName()).log(Level.SEVERE, null, e);
        }
    }
    
    public static String getColor(String origin) {
        origin = origin.trim();
        origin = getAlternativeColor(origin);
        String firstColor = origin.split(" ")[0];
        System.out.println("firstColor: " + firstColor);
        for (String color : colors) {
            if (color.equalsIgnoreCase(firstColor)) {
                return color;
            }
        }
        
        return origin;
    }
    
    public static String getAlternativeColor(String originColor) {
        System.out.println("In originColor: " + originColor);
        for (Map.Entry<String, String> alt : colorReplacements.entrySet()) {
            String colorName = alt.getKey();
            String replacement = alt.getValue();
            
            originColor = originColor.replaceAll("(?i)" + colorName, replacement);
        }
        
        System.out.println("Out: " + originColor);
        
        return originColor;
        
    }
}
