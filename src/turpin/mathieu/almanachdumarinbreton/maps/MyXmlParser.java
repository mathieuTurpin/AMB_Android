package turpin.mathieu.almanachdumarinbreton.maps;

import java.io.IOException;
import java.io.InputStream;

import org.mapsforge.android.maps.overlay.ItemizedOverlay;
import org.mapsforge.android.maps.overlay.OverlayItem;
import org.mapsforge.core.GeoPoint;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import turpin.mathieu.almanachdumarinbreton.R;

import android.content.Context;

public class MyXmlParser {
	
	private final String baliseXML = "balise.xml";
	private final String textXML = "text.xml";

	private Context context;
	
	public MyXmlParser(Context context){
		this.context = context;
	}
	
	public MyItemizedOverlay getBalises(){
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			    InputStream in_s = context.getAssets().open(baliseXML);
		        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in_s, null);

	            return parseXMLToBalise(parser);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public ArrayTextOverlay getText(){
		XmlPullParserFactory pullParserFactory;
		try {
			pullParserFactory = XmlPullParserFactory.newInstance();
			XmlPullParser parser = pullParserFactory.newPullParser();

			    InputStream in_s = context.getAssets().open(textXML);
		        parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
	            parser.setInput(in_s, null);

	            return parseXMLToText(parser);

		} catch (XmlPullParserException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	private MyItemizedOverlay parseXMLToBalise(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		MyItemizedOverlay balises = null;
        int eventType = parser.getEventType();
        OverlayItem currentBalise = null;
        double lat = 0.000000;
        double lon = 0.000000;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                	balises = new MyItemizedOverlay(null, context);
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("service")){
                        currentBalise = new OverlayItem();
                    } else if (currentBalise != null){
                        if (name.equals("description")){
                        	currentBalise.setSnippet(parser.nextText());
                        } else if (name.equals("lat")){
                        	lat = Double.parseDouble(parser.nextText());
                        } else if (name.equals("lon")){
                        	lon = Double.parseDouble(parser.nextText());
                        }  
                        else if (name.equals("type")){
                        	String type = parser.nextText();
                        	currentBalise.setTitle(type);
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
                        	else if(type.equals("inconnue1")){
                        		i = R.drawable.inconnu1;
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
                        	else if(type.equals("inconnue3")){
                        		i = R.drawable.inconnue3;
                        	}
                        	if(i!=-1) currentBalise.setMarker(ItemizedOverlay.boundCenterBottom(context.getResources().getDrawable(i)));
                        }  
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("service") && currentBalise != null && lat != 0.000000 && lon != 0.000000){
                    	currentBalise.setPoint(new GeoPoint(lat,lon));
                    	balises.addItem(currentBalise);
                    	lat = 0.000000;
                    	lon = 0.000000;
                    } 
            }
            eventType = parser.next();
        }
        return balises;
	}
	
	private ArrayTextOverlay parseXMLToText(XmlPullParser parser) throws XmlPullParserException,IOException
	{
		ArrayTextOverlay texts = null;
        int eventType = parser.getEventType();
        OverlayItem currentBalise = null;
        double lat = 0.000000;
        double lon = 0.000000;
        float rot = 0;

        while (eventType != XmlPullParser.END_DOCUMENT){
            String name = null;
            switch (eventType){
                case XmlPullParser.START_DOCUMENT:
                	texts = new ArrayTextOverlay(null);
                    break;
                case XmlPullParser.START_TAG:
                    name = parser.getName();
                    if (name.equals("text")){
                        currentBalise = new OverlayItem();
                    } else if (currentBalise != null){
                        if (name.equals("description")){
                        	TextDrawable textMarker = new TextDrawable(currentBalise, parser.nextText());
                        	currentBalise.setMarker(textMarker);
                        } else if (name.equals("lat")){
                        	lat = Double.parseDouble(parser.nextText());
                        } else if (name.equals("lon")){
                        	lon = Double.parseDouble(parser.nextText());
	                    } else if (name.equals("rot")){
	                    	rot = Float.parseFloat(parser.nextText());
	                    } 
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("text") && currentBalise != null && lat != 0.000000 && lon != 0.000000 && currentBalise.getMarker() != null){
                    	currentBalise.setPoint(new GeoPoint(lat,lon));
                    	((TextDrawable)(currentBalise.getMarker())).setRotation(rot);
                    	
                    	texts.addItem(currentBalise);
                    	lat = 0.000000;
                    	lon = 0.000000;
                    	rot = 0;
                    } 
            }
            eventType = parser.next();
        }
        return texts;
	}
}
