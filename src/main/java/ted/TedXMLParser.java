package ted;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import ted.datastructures.SimpleTedSerie;
import ted.ui.editshowdialog.FeedPopupItem;

public class TedXMLParser 
{
	/**
	 * @param args
	 */
	public static void main(InputStream args)
	{
	}

	/**
	 * Use this to read the XML file from a file for further use
	 * @param args The location of the XML file (Location in filesystem)
	 * @return Element with contents of the XML file or null if file not exists
	 */
	public Element readXMLFromFile(String args)
	{	
		// get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// using factory get an instance of document builder
		DocumentBuilder db;
		
		try 
		{
			File file = new File(args);
			db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			Document dom = db.parse(file);
			
			return dom.getDocumentElement();
		} 
		catch (SAXException e1) 
		{
			TedLog.error(e1, Lang.getString("TedXmlParser.GeneralError") + ", " + Lang.getString("TedXmlParser.NotFound"));
		} 
		catch (IOException e1) 
		{
			TedLog.error(e1, Lang.getString("TedXmlParser.GeneralError") + ", " + Lang.getString("TedXmlParser.NoRead"));
		} 
		catch (ParserConfigurationException e1) 
		{
			TedLog.error(e1, Lang.getString("TedXmlParser.GeneralError"));
		}
		
		// if file not exists return null
		return null;
	}
	
	/**
	 * Use this to read the XML file from a url for further use
	 * @param args The location of the XML file (URL!!)
	 * @return Element with contents of the XML file or null if file not exists
	 */
	public Element readXMLFromURL(String url)
	{
//		 get the factory
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();

		// using factory get an instance of document builder
		DocumentBuilder db;
		
		try 
		{
			db = dbf.newDocumentBuilder();

			// parse using builder to get DOM representation of the XML file
			InputStream stream = TedIO.makeBufferedInputStream(new URL(url), TedConfig.getInstance().getTimeOutInSecs());
			Document dom = db.parse(stream);
			
			return dom.getDocumentElement();
		} 
		catch (SAXException e1) 
		{
			TedLog.error(e1, Lang.getString("TedXmlParser.GeneralError") + ", " + Lang.getString("TedXmlParser.NotFound"));
		} 
		catch (IOException e1) 
		{
			TedLog.error(e1, Lang.getString("TedXmlParser.GeneralError") + ", " + Lang.getString("TedXmlParser.NoRead"));
		} 
		catch (ParserConfigurationException e1) 
		{
			TedLog.error(e1, Lang.getString("TedXmlParser.GeneralError"));
		}
		
		// if file not exists return null
		return null;
	}
	
	/**
	 * Find the serie in the nodelist archieved with readXMLFile
	 * @param nodelist Element got by readXMLFile
	 * @param name The name of the serie
	 * @return TedSerie or null if serie not exists in XML file
	 */
	public TedSerie getSerie(Element nodelist, String name)
	{
		// serie to be returned
		TedSerie serie;// = new TedSerie();
		
		// get the shows from the element 
		NodeList shows = nodelist.getElementsByTagName("show");
		
		// if there are shows
		if(shows!=null && shows.getLength()>0)
		{
			for(int i=0; i<shows.getLength(); i++)
			{
				Element show = (Element)shows.item(i);
				String elName = getTextValue(show, "name");
				
				// if we've found the show
				if(name.equals(elName))
				{
					int temp = -1;
					String tempS = "";
					
					// first check to see if it is a daily show
					// before creating the serie object
					tempS = getTextValue(show, "daily");
					if(tempS.equals("True"))
						serie = new TedDailySerie();
					else
						serie = new TedSerie();
					
					// set the properties of the serie
					serie.setName(elName);
					
					temp = getIntValue(show, "minimumsize");
					if(temp!=-1)
						serie.setMinSize(temp);
					
					temp = getIntValue(show, "maximumsize");
					if(temp!=-1)
						serie.setMaxSize(temp);
					
					tempS = getTextValue(show, "keywords");
					if(!tempS.equals(""))
						serie.setKeywords(tempS);
					
					tempS = getTextValue(show, "tv_com");
					if(!tempS.equals(""))
						serie.setTVcom(tempS);
					
					tempS = getTextValue(show, "epguides");
					if(!tempS.equals(""))
						serie.setEpguidesName(tempS);
					
					temp = getIntValue(show, "seeders");
					if(temp!=-1)
						serie.setMinNumOfSeeders(temp);
					
					temp = getIntValue(show, "timezone");
					if (temp!=-1)
						serie.setTimeZone(temp);
					
					// set the feeds
					Vector tempF = getVectorValue(show, "feeds", "feed");
					Vector feeds = new Vector();
					for(int j=0; j<tempF.size(); j++)
					{
						TedSerieFeed f = new TedSerieFeed((String)tempF.elementAt(j), 0);
						feeds.addElement(f);
					}
					serie.setFeeds(feeds);
										
					// check to see if the from break option has to be used
					String searchName = getTextValue(show, "searchname");
					serie.setSearchName(searchName);
					
					return serie;
				}
			}
		}
		
		return null;
	}
	
	private Calendar StringToCalendar(String date)
	{
		Calendar c = new GregorianCalendar();
		
		if(date.equals(""))
			date = "00-00-0000";
		
		try
		{
			int k = Integer.parseInt(date.substring(0,2));
			c.set(Calendar.DAY_OF_MONTH, k);
			
			k = Integer.parseInt(date.substring(3,5));
			c.set(Calendar.MONTH, k-1);
			
			k = Integer.parseInt(date.substring(6,10));
			c.set(Calendar.YEAR, k);
		}
		catch(Exception e)
		{
			TedLog.error("Malformed date in xml file");
		}
		
		return c;
	}
	
	/**
	 * Get the names of the shows from Element for Episode dialog
	 * @param el Element obtainend from readXMLFile
	 * @param shows Vector to put shows in
	 */
	public Vector<SimpleTedSerie> getNames(Element el)
	{
		Vector<SimpleTedSerie> shows = new Vector<SimpleTedSerie>();
		NodeList nl = el.getElementsByTagName("show");
	
		// Load the show names in a sorted set.
		SortedSet<SimpleTedSerie> sortedShows = new TreeSet<SimpleTedSerie>();
		for(int i=0; i<nl.getLength(); i++)
		{
			Element show = (Element)nl.item(i);
			SimpleTedSerie serie = new SimpleTedSerie();
			serie.setName(getTextValue(show, "name"));
			sortedShows.add(serie);
		}
		
		// Copy the sorted set into a vector as this is the return type of this function.
		Iterator<SimpleTedSerie> iterator = sortedShows.iterator();		
		while (iterator.hasNext())
		{
			shows.addElement(iterator.next());
		}
		
		return shows;
	}
	
	/**
	 * Get string object value from the nodelist
	 * @param el The nodelist
	 * @param s Name of the object
	 * @return The string value
	 */
	public String getTextValue(Element el, String s)
	{
		try
		{
			String rString = "";
			NodeList nl = el.getElementsByTagName(s);
			if(nl!=null && nl.getLength()>0)
			{
				Element el1 = (Element)nl.item(0);
				if(el1.getFirstChild()!=null)
					rString = el1.getFirstChild().getNodeValue();
			}
			return rString;
		}
		catch(Exception e)
		{
			return "";
		}
	}
	
	/**
	 * Same as getTextValue() only returns integer
	 * @param el The nodelist
	 * @param s Name of the object
	 * @return The integer value	 
	 * */
	public int getIntValue(Element el, String s)
	{
		try
		{
			return Integer.parseInt(getTextValue(el, s));
		}
		catch(NumberFormatException e)
		{
			return -1;
		}
	}
	
	/**
	 * Get a vector with the values for this object
	 * @param el The nodelist
	 * @param s Name of the object
	 * @return The vector
	**/
	private Vector getVectorValue(Element el, String parent, String child)
	{
		Vector v = new Vector();
		
		NodeList nl = el.getElementsByTagName(parent);
		
		if(nl!=null && nl.getLength()>0)
		{
			Element el1 = (Element)nl.item(0);
			NodeList nl1 = el1.getElementsByTagName(child);
			
			for(int i=0; i<nl1.getLength(); i++)
			{
				Element el2 = (Element)nl1.item(i);
				v.addElement(el2.getFirstChild().getNodeValue());
			}
		}
		
		return v;
	}
	
	/**
	 * Return the version of the XML file
	 * @param el NodeList obtained by readXMLFile
	 * @return version of XML file
	 */
	public int getVersion(Element el)
	{
		int version = -1;
		String temp;
		
		NodeList nl = el.getElementsByTagName("version");
		
		if(nl!=null && nl.getLength()>0)
		{
			temp = ((Element)nl.item(0)).getFirstChild().getNodeValue();
			version = Integer.parseInt(temp);
		}
		
		return version;
	}
	
	public Vector<FeedPopupItem> getPopupItems(Element nodelist)
	{
		Vector<FeedPopupItem> v = new Vector<FeedPopupItem>();
		FeedPopupItem pi;
		NodeList nl = nodelist.getElementsByTagName("rsslocations");
		
		if(nl!=null && nl.getLength()>0)
		{
			Element el1 = (Element)nl.item(0);
			NodeList nl1 = el1.getElementsByTagName("location");
			
			for(int i=0; i<nl1.getLength(); i++)
			{
				Element e = (Element)nl1.item(i);
				String elName = getTextValue(e, "name");
				String location = getTextValue(e, "feed");
				String website = getTextValue(e, "website");
				int type = getIntValue(e, "type");
				
				pi = new FeedPopupItem(elName, location, website, type);
				v.add(pi);
			}
		}
		
		return v;
	}
	
	public Vector<FeedPopupItem> getAutoFeedLocations(Element nodelist)
	{
		Vector<FeedPopupItem> v = new Vector<FeedPopupItem>();
		FeedPopupItem pi;
		NodeList nl = nodelist.getElementsByTagName("rsslocations");
		
		if(nl!=null && nl.getLength()>0)
		{
			Element el1 = (Element)nl.item(0);
			NodeList nl1 = el1.getElementsByTagName("location");
			
			for(int i=0; i<nl1.getLength(); i++)
			{
				Element e = (Element)nl1.item(i);
				int type = getIntValue(e, "type");
				if (type == FeedPopupItem.IS_SEARCH_AND_AUTO)
				{
					String elName = getTextValue(e, "name");
					String location = getTextValue(e, "feed");
					String website = getTextValue(e, "website");
					
					
					pi = new FeedPopupItem(elName, location, website, type);
					v.add(pi);
				}
			}
		}
		
		return v;
	}
	
	public int getPopupIndex(Element nodelist, String name)
	{
		NodeList nl = nodelist.getElementsByTagName("rsslocations");
		
		if(nl!=null && nl.getLength()>0)
		{
			Element el1 = (Element)nl.item(0);
			NodeList nl1 = el1.getElementsByTagName("location");
			
			for(int i=0; i<nl1.getLength(); i++)
			{
				Element e = (Element)nl1.item(i);
				String elName = getTextValue(e, "name");
				
				if(elName.equals(name))
					return i;
			}
		}
		
		return nl.getLength();
	}
	
	/**
	 * Returns the Amazons URL locations in a Vector as defined
	 * in the shows.xml file
	 * @param nodelist
	 * @return
	 */
	public Vector getAmazonURLs(Element nodelist)
	{
		Vector v = new Vector();
		NodeList nl = nodelist.getElementsByTagName("weblocations");
		
		if(nl!=null && nl.getLength()>0)
		{
			Element el1 = (Element)nl.item(0);
			NodeList nl1 = el1.getElementsByTagName("Amazon");
			
			for(int i=0; i<nl1.getLength(); i++)
			{
				Element e = (Element)nl1.item(i);
				v.add(getTextValue(e, "firsthalf"));
				v.add(getTextValue(e, "secondhalf"));
				v.add(getTextValue(e, "complete"));
			}
		}
		
		return v;
	}
	
	public String getShowInfoURL(Element nodelist)
	{
		NodeList nl = nodelist.getElementsByTagName("weblocations");
		
		if(nl!=null && nl.getLength()>0)
		{
			Element el1 = (Element)nl.item(0);
			NodeList nl1 = el1.getElementsByTagName("showinfo");
			
			Element e = (Element)nl1.item(0);
			return getTextValue(e, "location");
		}
		
		return "";
	}
	
	public Set<String> getPrivateTrackers(Element nodelist)
	{
		NodeList mainNode = nodelist.getElementsByTagName("private_trackers");
		
		Set<String> trackers = new HashSet<String>();
		if (mainNode != null && mainNode.getLength() > 0)
		{			
			NodeList trackersList = ((Element)mainNode.item(0)).getElementsByTagName("tracker_url");
			for (int trackerIndex = 0; trackerIndex < trackersList.getLength(); ++trackerIndex)
			{
				Element trackerElement = (Element)trackersList.item(trackerIndex);
				String trackerUrl = trackerElement.getTextContent();
				trackers.add(trackerUrl);
			}
		}
		
		return trackers;
	}
	
	public String getHDKeywords(Element nodelist)
	{
		String keywords = getTextValue(nodelist, "hdkeywords");
		return keywords;
	}
}
