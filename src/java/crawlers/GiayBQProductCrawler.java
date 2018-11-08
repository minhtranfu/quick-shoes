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
public class GiayBQProductCrawler extends BaseCrawler implements Runnable {

    private final int shopId = 3;
    private int categoryId;
    private String url;
    private long productId;
    private String name;
    private String img;
    private double price;
    private double oldPrice;
    private String code;
    private String color;
    private List<Integer> sizes;
    private short sex;
    private String manufactor = "BQ";
    private String material;
    private List<String> images;

    public GiayBQProductCrawler(ServletContext context, int categoryId, String url, String name, String img, int price, int oldPrice, short sex) {
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
        getProductInfo();
    }

    private void getProductCode() {
        String regex = "BQ([^-]+)-?(.*)-([^-]+)$";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(name);

        if (matcher.find()) {
            color = matcher.group(3).trim();
            color = ColorUtils.getColor(color);
            code = matcher.group(2);
            if (!code.trim().isEmpty()) {
                code = code.replaceAll("\\s", "");
                return;
            }

            code = "BQ" + matcher.group(1).trim();
        }
    }

    private void getProductInfo() {

        try {
            getProductCode();

            BufferedReader reader = getBufferedReaderForURL(url);
            String imagesSection = getHTMLSection(reader, "<div class=\"product-image-gallery\">", "</div>");

            // get all images with event reader
            getImagesFromSection(imagesSection);

            // Get version info (color, size)
            String sizesSection = getHTMLSection(reader, "var spConfig = new Product.Config", "</script>");
            if (!getSizesFromHTML(sizesSection)) {
                return;
            }

            // Get detail info (manufactor, material)
            String detailInfoSection = getHTMLSection(reader, "<meta property=\"og:description\"", "<meta property=\"og:url\"");
            if (!getProductDetail(detailInfoSection)) {
                return;
            }

            // Getting product info done, save data
            Product product = new Product(name, categoryId, sex, price, oldPrice, img, shopId, code, url, null, null);
            saveProduct(product, color, sizes, images, manufactor, material);
            System.out.println("Done " + url);
        } catch (Exception e) {
            System.out.println("URL product: " + url);
            Logger.getLogger(GiayBQProductCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    /**
     * Get product detail of product from HTML with pre-process and REGEX
     *
     * @param detailInfoSection HTML contain detail info of product
     * @return true after get data done unless return false
     */
    private boolean getProductDetail(String detailInfoSection) {
        String regex = "Chất liệu: ([^\\n]+)";
        Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(detailInfoSection);
        if (!matcher.find()) {
            return false;
        }

        material = matcher.group(1);
        material = material.trim();

        return !material.isEmpty();
    }

    /**
     * Get version info of product from HTML
     *
     * @param versionInfoSection HTML contain version info product
     * @return true after get data done unless return false
     */
    private boolean getSizesFromHTML(String versionInfoSection) {
        if (versionInfoSection == null || versionInfoSection.trim().isEmpty()) {
            return false;
        }

        String regex = "\"code\":\"size\",\"label\":\"Size\",\"options\":\\[(\\{.+\\})\\]";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(versionInfoSection);

        if (!matcher.find()) {
            return false;
        }

        // get sizes
        String listSizeStr = matcher.group(1);
        regex = "\"label\":\"(\\d+)\"";
        pattern = Pattern.compile(regex);
        matcher = pattern.matcher(versionInfoSection);

        sizes = new ArrayList<>();
        while (matcher.find()) {
            String sizeStr = matcher.group(1);
            int size = Integer.parseInt(sizeStr);
            sizes.add(size);
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
                Logger.getLogger(GiayBQProductCrawler.class.getName()).log(Level.SEVERE, null, e);
            }

            // process
            XMLEvent event = eventReader.nextEvent();

            if (event.isStartElement()) {
                StartElement startElement = event.asStartElement();
                String tagName = startElement.getName().getLocalPart();

                if (tagName.equals("img")) {
                    // skip main image
                    Attribute attrId = startElement.getAttributeByName(new QName("id"));
                    String id = attrId.getValue();
                    if (id.equals("image-main")) {
                        continue;
                    }

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
            List<Integer> sizes, List<String> images, String manufactor, String material) {

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
            boolean isProductVersionExisted = saveProductVersion(productId, color, sizes);

            // if existed a version then do not insert images of current version
            if (isProductVersionExisted) {
                return;
            }

            // Save product images
            ImageDao imageDao = ImageDao.getInstance();
            for (String src : images) {
                Image image = new Image(productId, src);
                imageDao.create(image);
            }

        } catch (Exception e) {
            Logger.getLogger(GiayBQProductCrawler.class.getName()).log(Level.SEVERE, null, e);
        }
    }

    private static synchronized boolean saveProductVersion(long productId, String colorName, List<Integer> sizes) {

        boolean isProductVersionExisted = false;
        try {
            // Set exists color or new
            CategoryDao catDao = CategoryDao.getInstance();
            Category color = catDao.existedOrNew(WebConstants.CAT_TYPE_COLOR, colorName);
            int colorId = color.getId();

            // Save product version
            ProductVersionDao psDao = ProductVersionDao.getInstance();
            for (int size : sizes) {
                if (psDao.getProductVersion(productId, colorId, size) != null) {
                    isProductVersionExisted = true;
                    continue;
                }

                ProductVersion ps = new ProductVersion(productId, colorId, size);
                psDao.create(ps);
            }

            return isProductVersionExisted;

        } catch (Exception e) {
            Logger.getLogger(GiayBQProductCrawler.class.getName()).log(Level.SEVERE, null, e);
        }

        return isProductVersionExisted;
    }

}
