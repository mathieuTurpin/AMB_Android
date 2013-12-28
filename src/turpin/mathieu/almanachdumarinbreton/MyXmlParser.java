package turpin.mathieu.almanachdumarinbreton;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;

import org.mapsforge.android.maps.MapActivity;
import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import turpin.mathieu.almanachdumarinbreton.R;
import turpin.mathieu.almanachdumarinbreton.overlay.OverlayText;

import android.content.Context;

public class MyXmlParser {
	
	private final String serviceXML = "service.xml";
	private final String textXML = "text.xml";
	private final String soundingXML = "sounding.xml";

	private final Context context;
	
	/**
	 * Parse Xml to create items to use in Overlay
	 * 
	 * @param context
	 *            the enclosing {@link MapActivity} instance.
	 */
	public MyXmlParser(Context context){
		this.context = context;
	}
	
	/**
	 * return an ArrayList which represents Port Service to display on the map
	 */
	public ArrayList<OverlayItem> getService(){
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			    InputStream in_s = context.getAssets().open(serviceXML);
		        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in_s, null);

	            return parseXMLToService(parser);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * return an ArrayList which represents Text to display on the map
	 */
	public ArrayList<OverlayText> getText(){
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			    InputStream in_s = context.getAssets().open(textXML);
		        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in_s, null);

	            return parseXMLToText(parser,"text");

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	/**
	 * return an ArrayList which represents Sounding to display on the map
	 */
	public ArrayList<OverlayText> getSounding(){
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			    InputStream in_s = context.getAssets().open(soundingXML);
		        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in_s, null);

	            return parseXMLToText(parser,"sounding");

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private ArrayList<OverlayItem> parseXMLToService(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		ArrayList<OverlayItem> services = null;
        int eventType = parser.getEventType();
        OverlayItem currentService = null;
        double lat = 0.000000;
        double lon = 0.000000;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                	services = new ArrayList<OverlayItem>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("service")){
                        currentService = new OverlayItem();
                    } else if (currentService != null){
                        if (name.equals("description")){
                        	currentService.setSnippet(parser.nextText());
                        } else if (name.equals("lat")){
                        	lat = Double.parseDouble(parser.nextText());
                        } else if (name.equals("lon")){
                        	lon = Double.parseDouble(parser.nextText());
                        }  
                        else if (name.equals("type")){
                        	String type = parser.nextText();
                        	currentService.setTitle(type);
                        	int i = -1;
                        	if(type.equals("carburant")){
                        		i = R.drawable.carburants;
                        	}
                        	else if(type.equals("ordure")){
                        		i = R.drawable.ordures;
                        	}
                        	else if(type.equals("wc")){
                        		i = R.drawable.toilettes;
                        	}
                        	else if(type.equals("douche")){
                        		i = R.drawable.douches;
                        	}
                        	else if(type.equals("supermarche")){
                        		i = R.drawable.supermaches;
                        	}
                        	else if(type.equals("manutention")){
                        		i = R.drawable.manutention;
                        	}
                        	else if(type.equals("capitainerie")){
                        		i = R.drawable.capitainerie;
                        	}
                        	else if(type.equals("parking")){
                        		i = R.drawable.parking;
                        	}
                        	else if(type.equals("visiteur")){
                        		i = R.drawable.visiteurs;
                        	}
                        	else if(type.equals("administration")){
                        		i = R.drawable.administration;
                        	}
                        	if(i!=-1) currentService.setMarker(ItemizedOverlay.boundCenter(context.getResources().getDrawable(i)));
                        }  
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("service") && currentService != null && lat != 0.000000 && lon != 0.000000){
                    	currentService.setPoint(new GeoPoint(lat,lon));
                    	services.add(currentService);
                    	lat = 0.000000;
                    	lon = 0.000000;
                    } 
            }
            eventType = parser.next();
        }
        return services;
	}
	
	private ArrayList<OverlayText> parseXMLToText(XmlPullParser parser,String nameTag) throws XmlPullParserException,IOException
	{
		ArrayList<OverlayText> texts = null;
        int eventType = parser.getEventType();
        String txt = "";
        double lat = 0.000000;
        double lon = 0.000000;
        float rot = 0;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                	texts = new ArrayList<OverlayText>();
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals(nameTag)){
                        txt = "";
                        lat = 0.000000;
                    	lon = 0.000000;
                    	rot = 0;
                    } else if (name.equals("description")){
                        	txt = parser.nextText();
                    } else if (name.equals("lat")){
                    	lat = Double.parseDouble(parser.nextText());
                    } else if (name.equals("lon")){
                    	lon = Double.parseDouble(parser.nextText());
                    } else if (name.equals("rot")){
                    	rot = Float.parseFloat(parser.nextText());
                    } 
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase(nameTag) && lat != 0.000000 && lon != 0.000000 && txt != ""){
                    	OverlayText currentText = new OverlayText(new GeoPoint(lat,lon),txt);
                    	currentText.setRotation(rot);
                    	texts.add(currentText);
                    } 
            }
            eventType = parser.next();
        }
        return texts;
	}	
}
