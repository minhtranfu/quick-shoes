/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlers;

import constants.WebConstants;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import threads.BaseThread;
import utils.XMLUtilities;

/**
 *
 * @author MinhTran
 */
public class BazaCrawler extends BaseCrawler implements Runnable {

    private String url;
    private short sex;
    private int categoryId;
    private final String listProductStart = "<ul class=\"product_list\">";
    private final String listProductEnd = "</ul>";
    private final String pagingStart = "<a class=\"btn_nv_page pg_next\"";
    private final String pagingEnd = "</a>";

    private String xsdListProductsPath = "WEB-INF/xsd/baza-list-products.xsd";

    public BazaCrawler(ServletContext context, String url, short sex, int categoryId) {
        super(context);
        this.url = url;
        this.sex = sex;
        this.categoryId = categoryId;
    }

    @Override
    public void run() {

        try {
            getProductsInPage();

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private String getListProductHTML() {
        BufferedReader reader;

        try {
            reader = getBufferedReaderForURL(url);
            String listProductHTML = getHTMLSection(reader, listProductStart, listProductEnd);
            String pagingHTML = getHTMLSection(reader, pagingStart, pagingEnd);
            if (!pagingHTML.isEmpty()) {
                String regex = "href=\"([^\"]+)\"";
                Pattern pattern = Pattern.compile(regex);
                Matcher matcher = pattern.matcher(pagingHTML);

                if (matcher.find()) {
                    String nextPageUrl = matcher.group(1);
                    nextPageUrl = WebConstants.bazaShopURL + nextPageUrl;

                    // start get next page
                    System.out.println("Next page: " + nextPageUrl);
                    Thread thread = new Thread(new BazaCrawler(context, nextPageUrl, sex, categoryId));
                    thread.start();
                }
            }

            return listProductHTML;
        } catch (IOException e) {
            Logger.getLogger(BazaCrawler.class.getName()).log(Level.SEVERE, null, e);
        }

        return "";
    }

    private void getProductsInPage() {
        String section = getListProductHTML();

        if (section.isEmpty()) {
            return;
        }

        String xsdRealPath = context.getRealPath("/") + xsdListProductsPath;
        if (!XMLUtilities.validateXML(section, xsdRealPath)) {
            return;
        }

        try {
            XMLEventReader eventReader = parseStringToXMLEventReader(section);
            XMLEvent event;

            boolean startProduct = false;
            String nextIs = "";
            String productUrl = "";
            String name = "";
            String img = "";
            int price = 0;
            int oldPrice = 0;
            while (eventReader.peek() != null) {
                event = eventReader.nextEvent();

                try {
                    Thread instance;
                    synchronized (instance = BaseThread.getInstance()) {
                        while (BaseThread.isSuspended()) {
                            System.out.println("Thread is waiting at getProductsInPage!");
                            instance.wait();
                        }
                    }
                } catch (InterruptedException e) {
                    Logger.getLogger(BazaCrawler.class.getName()).log(Level.SEVERE, null, e);
                }

                if (event.isStartElement()) {
                    StartElement startElement = event.asStartElement();
                    String tagName = startElement.getName().getLocalPart();

                    if (!startProduct || tagName.equals("li")) {
                        startProduct = true;
                        continue;
                    }

                    if (tagName.equals("a")) {
                        Attribute attrHref = startElement.getAttributeByName(new QName("href"));
                        productUrl = attrHref.getValue();

                    } else if (tagName.equals("img")) {
                        Attribute attrSrc = startElement.getAttributeByName(new QName("src"));
                        img = attrSrc.getValue();

                        Attribute attrAlt = startElement.getAttributeByName(new QName("alt"));
                        name = attrAlt.getValue();

                    } else if (tagName.equals("span")) {
                        Attribute attrClass = startElement.getAttributeByName(new QName("class"));
                        String classStr = attrClass.getValue();

                        if (classStr.equals("price")) {
                            nextIs = "price";
                        }

                        if (classStr.equals("price_old")) {
                            nextIs = "price_old";
                        }
                    }

                } else if (event.isCharacters()) {

                    if (nextIs.equals("price")) {
                        price = charactersEventToInt(event);
                        nextIs = "";
                    } else if (nextIs.equals("price_old")) {
                        oldPrice = charactersEventToInt(event);
                        nextIs = "";

                        // start crawl other info from product detail page
                        Thread t = new Thread(new BazaProductCrawler(context, categoryId, productUrl, name, img, price, oldPrice, sex));
                        t.start();

                        startProduct = false;
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(BazaCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private int charactersEventToInt(XMLEvent event) {
        Characters characters = event.asCharacters();
        String str = characters.getData();

        str = str.replaceAll("[^\\d]", "");
        int price = Integer.parseInt(str);
        return price;
    }

}