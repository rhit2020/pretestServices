
import java.io.InputStream;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpServlet;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;

public class ConfigManager {
    
	
	// database connection parameters
    public String agg_dbstring;
    public String agg_dbuser;
    public String agg_dbpass;   
    public String um2_dbstring;
    public String um2_dbuser;
    public String um2_dbpass; 
	public String relative_resource_path;

	private static String config_string = "./WEB-INF/config.xml";

    public ConfigManager(HttpServlet servlet) {
        try {
            ServletContext context = servlet.getServletContext();
            // System.out.println(context.getContextPath());
            InputStream input = context.getResourceAsStream(config_string);
			if (input != null) {
				DocumentBuilderFactory dbFactory = DocumentBuilderFactory
						.newInstance();
				DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
				Document doc = dBuilder.parse(input);
				doc.getDocumentElement().normalize();
				// set database connection parameters
				agg_dbstring = doc.getElementsByTagName("agg_dbstring").item(0)
						.getTextContent().trim();
				agg_dbuser = doc.getElementsByTagName("agg_dbuser").item(0)
						.getTextContent().trim();
				agg_dbpass = doc.getElementsByTagName("agg_dbpass").item(0)
						.getTextContent().trim();
				um2_dbstring = doc.getElementsByTagName("um2_dbstring").item(0)
						.getTextContent().trim();
				um2_dbuser = doc.getElementsByTagName("um2_dbuser").item(0)
						.getTextContent().trim();
				um2_dbpass = doc.getElementsByTagName("um2_dbpass").item(0)
						.getTextContent().trim();
			}
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
