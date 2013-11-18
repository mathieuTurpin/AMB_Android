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
                    if (name.equals("balise")){
                        currentBalise = new OverlayItem();
                    } else if (currentBalise != null){
                        if (name.equals("description")){
                        	currentBalise.setSnippet(parser.nextText());
                        } else if (name.equals("lat")){
                        	lat = Float.parseFloat(parser.nextText());
                        } else if (name.equals("lon")){
                        	lon = Float.parseFloat(parser.nextText());
                        }  
                        else if (name.equals("type")){
                        	currentBalise.setTitle(parser.nextText());
                        	currentBalise.setMarker(ItemizedOverlay.boundCenterBottom(context.getResources().getDrawable(R.drawable.marker_green)));
                        }  
                    }
                    break;
                case XmlPullParser.END_TAG:
                    name = parser.getName();
                    if (name.equalsIgnoreCase("balise") && currentBalise != null && lat != 0.000000 && lon != 0.000000){
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
}
