/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package crawlers;

import constants.WebConstants;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.servlet.ServletContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import threads.BaseThread;
import utils.XMLUtilities;

/**
 *
 * @author MinhTran
 */
public class BaseCrawler {
    
    ServletContext context;

    public BaseCrawler(ServletContext context) {
        this.context = context;
    }

    public ServletContext getContext() {
        return context;
    }
    
    protected BufferedReader getBufferedReaderForURL(String urlString) throws MalformedURLException, IOException {
        javax.net.ssl.HttpsURLConnection.setDefaultHostnameVerifier((String hostname, javax.net.ssl.SSLSession sslSession) -> true);
        System.setProperty("jsse.enableSNIExtension", "false");
        
        URL url = new URL(urlString);
        URLConnection connection = url.openConnection();
        connection.addRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/69.0.3497.100 Safari/537.36");
        InputStream is = connection.getInputStream();
        InputStreamReader isr = new InputStreamReader(is, "UTF-8");
        BufferedReader br = new BufferedReader(isr);
        
        return br;
    }
    
    protected XMLEventReader parseStringToXMLEventReader(String xmlSection) throws UnsupportedEncodingException, XMLStreamException {
        
        // get bytes and new bytes stream
        byte[] byteArray = xmlSection.getBytes("UTF-8");
        ByteArrayInputStream is = new ByteArrayInputStream(byteArray);
        
        // new xml input factory and create reader from factory
        XMLInputFactory factory = XMLInputFactory.newInstance();
        XMLEventReader reader = factory.createXMLEventReader(is);
        
        return reader;
    }
    
    protected String getHTMLSection(BufferedReader reader, String start, String end) throws IOException {
        return getHTMLSection(reader, start, end, true, true);
    }
    
    protected String getHTMLSection(BufferedReader reader, String start, String end, boolean includeStart, boolean includeEnd) throws IOException {
        String section = "";
        String line;
        boolean isStarted = false;
        reader.mark(1000000);
        while ((line = reader.readLine()) != null) {
                
            try {
                Thread instance;
                synchronized (instance = BaseThread.getInstance()) {
                    while (BaseThread.isSuspended()) {
                        System.out.println("Thread is waiting at getListProductHTML!");
                        instance.wait();
                    }
                }
            } catch (InterruptedException e) {
                Logger.getLogger(BazaCrawler.class.getName()).log(Level.SEVERE, null, e);
            }

            if (line.contains(start)) {
                isStarted = true;
                if (!includeStart) {
                    continue;
                }
            }

            if (isStarted) {
                if (line.contains(end) && !includeEnd) {
                    return section;
                }
                
                section += line;
                
                if (line.contains(end)) {
                    return section;
                }
            }

        }
        // not found the section so reset the reader
        reader.reset();

        return section;
    }
    
    protected Document parseXMLToDOM(String xml) throws ParserConfigurationException, SAXException, IOException {
        return XMLUtilities.parseXMLToDOM(xml);
    }
}
