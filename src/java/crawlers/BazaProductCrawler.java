package crawlers;

import constants.WebConstants;
import daos.CategoryDao;
import daos.ImageDao;
import daos.ProductDao;
import daos.ProductVersionDao;
import java.io.BufferedReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import jpa.entities.Category;
import jpa.entities.Image;
import jpa.entities.Product;
import jpa.entities.ProductVersion;
import threads.BaseThread;
import org.apache.commons.text.StringEscapeUtils;
import utils.ColorUtils;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author MinhTran
 */
public class BazaProductCrawler extends BaseCrawler implements Runnable {

    private final int shopId = 1;
    private int categoryId;
    private final String shopVersionAPI = "https://search_api.baza.vn/api/product/list?List=";
    private String url;
    private long productId;
    private String name;
    private String img;
    private double price;
    private double oldPrice;
    private String code;
    private String color;
    private int size;
    private short sex;
    private String manufactor;
    private String material;
    private List<String> images;

    public BazaProductCrawler(ServletContext context, int categoryId, String url, String name, String img, int price, int oldPrice, short sex) {
        super(context);
        this.categoryId = categoryId;
        this.url = url;
        this.name = name;
        this.img = img;
        this.price = price;
        this.oldPrice = oldPrice;
        this.sex = sex;
    }

    @Override
    public void run() {

        // check product code is existed
        String regex = "bzSku=([^\\-]+)-([\\d]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            code = matcher.group(1);
            ProductDao productDao = ProductDao.getInstance();
            Product product = productDao.getProduct(shopId, code);

            if (product != null) {
                // process to get current version of product
                productId = product.getId();
                getVersionInfo();

                // save product version
                saveProductVersion(productId, color, size, true);

            } else {
                // process to get product images and informations
                getProductInfo();
            }
        }
    }

    private void getVersionInfo() {
        String productBazaId = getProductIdFromURL();
        if (productBazaId == null) {
            // invalid url
            return;
        }

        try {
            String versionInfoURL = shopVersionAPI + productBazaId;
            BufferedReader reader = getBufferedReaderForURL(versionInfoURL);

            String versionInfo = reader.readLine();
            String regex = "\"Attributes\":\"([^\"]+)";
            Pattern pattern = Pattern.compile(regex);
            Matcher matcher = pattern.matcher(versionInfo);

            if (matcher.find()) {
                String attributes = matcher.group(1);
                regex = "^(.+),? size ([\\d]+)";
                pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
                matcher = pattern.matcher(attributes);

                if (matcher.find()) {
                    color = matcher.group(1);
                    color = colorProcess(color);
                    size = Integer.parseInt(matcher.group(2).trim());
                }
            }

        } catch (Exception e) {
            Logger.getLogger(BazaProductCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private String getProductIdFromURL() {
        String regex = "/p/([\\d\\w]+)";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(url);

        if (matcher.find()) {
            String bazaId = matcher.group(1);
            return bazaId;
        }

        return null;
    }

    private void getProductInfo() {

        try {
            BufferedReader reader = getBufferedReaderForURL(url);
            String imagesSection = getHTMLSection(reader, "<div class=\"s_img_pdt\">", "</div>");

            // get all images with event reader
            getImagesFromSection(imagesSection);

            // Get version info (color, size)
            String versionInfoSection = getHTMLSection(reader, "<li id=\"sl_att_prd\"", "</li>");
            if (!getVersionInfoFromHTML(versionInfoSection)) {
                return;
            }

            // Get detail info (manufactor, material)
            String detailInfoSection = getHTMLSection(reader, "<div id=\"pdt_tdt\" class=\"box_pdt_detail\" style=\"border-top: 0;\">", "<img", false, false);
            if (!getProductDetail(detailInfoSection)) {
                return;
            }

            // Getting product info done, save data
            Product product = new Product(name, categoryId, sex, price, oldPrice, img, shopId, code, url, null, null);
            saveProduct(product, color, size, images, manufactor, material);
        } catch (Exception e) {
            System.out.println("URL product: " + url);
            Logger.getLogger(BazaProductCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Get product detail of product from HTML with pre-process and REGEX
     *
     * @param detailInfoSection HTML contain detail info of product
     * @return true after get data done unless return false
     */
    private boolean getProductDetail(String detailInfoSection) {
        detailInfoSection = StringEscapeUtils.unescapeHtml4(detailInfoSection);
        detailInfoSection = detailInfoSection.replace("Chất liệu đế", "");
        detailInfoSection = detailInfoSection.replace("Chất liệu lớp trong", "");
        detailInfoSection = detailInfoSection.replaceAll("<span[^<]*style=\"display:? ?none;?\">[^<]*<\\/[\\w]+>", "");
        detailInfoSection = detailInfoSection.replaceAll("<[\\w]+>[\\s]*<\\/[\\w]+>", "");

        String regex = "<strong[^>]*>(?:Nhãn|Thương)[^>]+>([^\\/]+)<[\\s\\S]*<strong[^>]*>Chất liệu(?:(?: bề)? mặt|)[^>]+>([^\\/]+)<";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE | Pattern.MULTILINE);
        Matcher matcher = pattern.matcher(detailInfoSection);
        if (!matcher.find()) {
            return false;
        }

        manufactor = matcher.group(1);
        manufactor = manufactor.replaceAll("<[^>]*>", "");
        manufactor = manufactor.replace(":", "").trim();
        manufactor = manufactor.replaceAll("^[^\\w\\d]*(.+)[^\\w\\d]*$", "$1");
        manufactor = manufactor.replaceAll("^ *([\\w\\d\\s]+) *$", "$1");
        manufactor = manufactor.trim();
        material = matcher.group(2);
        material = material.replaceAll("<[^>]*>", "");
        material = material.replace(":", "");
        material = material.replaceAll("^[^\\w\\d]*(.+)[^\\w\\d]*$", "$1");
        material = material.replaceAll("^ *([\\w\\d\\s]+) *$", "$1");
        material = material.trim();

        return true;
    }
    
    private String colorProcess(String color) {
        color = color.replace("Màu", "");
        color = color.replace("buộc dây", "");
        color = color.replaceAll("\\(.*\\)", "");
        String regex = "[^\\w\\s]";
        color = Pattern.compile(regex, Pattern.UNICODE_CHARACTER_CLASS).matcher(color).replaceAll("");
        color = color.trim().toLowerCase();
        color = ColorUtils.getColor(color);
        
        return color;
    }

    /**
     * Get version info of product from HTML
     *
     * @param versionInfoSection HTML contain version info product
     * @return true after get data done unless return false
     */
    private boolean getVersionInfoFromHTML(String versionInfoSection) {

        String regex = "<strong title=\"([^\"]+),? size ([\\d]+)[^\"]*\"";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(versionInfoSection);

        if (matcher.find()) {
            color = matcher.group(1);
            color = colorProcess(color);
            size = Integer.parseInt(matcher.group(2));
        } else {
            getVersionInfo();
        }

        if ((color == null || color.isEmpty()) || size == 0) {
            return false;
        }

        return true;
    }

    /**
     * Get all images from images section in HTML
     *
     * @param imagesSection HTML contain all images of product
     * @throws XMLStreamException
     * @throws UnsupportedEncodingException
     */
    private void getImagesFromSection(String imagesSection) throws XMLStreamException, UnsupportedEncodingException {
        images = new ArrayList<>();
        XMLEventReader eventReader = parseStringToXMLEventReader(imagesSection);
        while (eventReader.peek() != null) {
            // waiting for unsuspend
            try {
                Thread instance;
                synchronized (instance = BaseThread.getInstance()) {
                    while (BaseThread.isSuspended()) {
                        System.out.println("Thread is waiting at getProductInfo!");
                        instance.wait();
                    }
                }
            } catch (InterruptedException e) {
                Logger.getLogger(BazaProductCrawler.class.getName()).log(Level.SEVERE, null, e);
            }

            // process
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String tagName = startElement.getName().getLocalPart();

                if (tagName.equals("img")) {
                    Attribute attrSrc = startElement.getAttributeByName(new QName("src"));
                    String src = attrSrc.getValue();
                    images.add(src);
                }
            }
        }
    }

    /**
     * Save data after get data from target product URL
     */
    private static synchronized void saveProduct(Product product, String color,
            int size, List<String> images, String manufactor, String material) {

        
        try {
            CategoryDao catDao = CategoryDao.getInstance();
            
            // manufactor
            Category category = catDao.existedOrNew(WebConstants.CAT_TYPE_MANUFACTOR, manufactor);
            product.setManufactorId(category.getId());
            
            // material
            category = catDao.existedOrNew(WebConstants.CAT_TYPE_MATERIAL, material);
            product.setMaterialId(category.getId());

            // Save product
            ProductDao productDao = ProductDao.getInstance();
            product = productDao.saveProductAfterCrawling(product);
            long productId = product.getId();

            // Save product version
            saveProductVersion(productId, color, size, false);

            // Save product images
            ImageDao imageDao = ImageDao.getInstance();
            for (String src : images) {
                Image image = new Image(productId, src);
                imageDao.create(image);
            }
            
        } catch (Exception e) {
            Logger.getLogger(BazaProductCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static synchronized void saveProductVersion(long productId, String colorName, int size, boolean needCheckExists) {

        try {
            // Set exists color or new
            CategoryDao catDao = CategoryDao.getInstance();
            Category color = catDao.existedOrNew(WebConstants.CAT_TYPE_COLOR, colorName);
            int colorId = color.getId();

            // Save product version
            ProductVersionDao psDao = ProductVersionDao.getInstance();
            if (needCheckExists && psDao.getProductVersion(productId, colorId, size) != null) {
                return;
            }
            ProductVersion ps = new ProductVersion(productId, colorId, size);
            psDao.create(ps);
        } catch (Exception e) {
            Logger.getLogger(BazaProductCrawler.class.getName()).log(Level.SEVERE, null, e);
        }

    }

}
