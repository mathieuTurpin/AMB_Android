package eu.telecom_bretagne.ambSocialNetwork.data.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentaireDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentairesDTOList;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoiDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.PoisDTOList;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.ServiceDTO;

public class PoiController extends Controller
{
	//-----------------------------------------------------------------------------
	private static PoiController instance = null;
	public static final String KEY_CONTENU = "contenu";

	private static final String URL_CENTRE_INTERET = URL + "/point";
	//-----------------------------------------------------------------------------
	private PoiController()
	{
		super();
	}
	//-----------------------------------------------------------------------------
	public static PoiController getInstance()
	{
		if(instance == null)
			instance = new PoiController();
		return instance;
	}
	//-----------------------------------------------------------------------------
	public String findAllCommentairesText() throws IOException
	{
		String result = downloadContent(URL_CENTRE_INTERET + "/comm_text");
		return result;
	}
	//-----------------------------------------------------------------------------
	public CommentairesDTOList findAllCommentairesJson() throws IOException
	{
		String jsonData = downloadContent(URL_CENTRE_INTERET + "/comm");

		// Décodage de la réponse.
		CommentairesDTOList ul = null;
		if(jsonData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonData);
			try
			{
				ul = oMapper.readValue(jParser, CommentairesDTOList.class);
			}
			catch(JsonParseException jpe)
			{
			}
		}
		return ul;
	}
	
	//-----------------------------------------------------------------------------
	public PoisDTOList findAllPoiJson() throws IOException
	{
		String jsonData = downloadContent(URL_CENTRE_INTERET + "/poi");

		// Décodage de la réponse.
		PoisDTOList ul = null;
		if(jsonData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonData);
			try
			{
				ul = oMapper.readValue(jParser, PoisDTOList.class);
			}
			catch(JsonParseException jpe)
			{
			}
		}
		return ul;
	}

	//-----------------------------------------------------------------------------
	public PoiDTO findPoiByPosition(Map<String,String> formValues) throws IOException
	{
		String jsonData = downloadContent(URL_CENTRE_INTERET + "/getPoiByPosition", formValues);

		// Décodage de la réponse.
		PoiDTO ul = null;
		if(jsonData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonData);
			try
			{
				ul = oMapper.readValue(jParser, PoiDTO.class);
			}
			catch(JsonParseException jpe)
			{
			}
		}
		return ul;
	}

	public Map<String,String> prepareGetByPosition(String latitude,String longitude){
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put("latitude", latitude);
		formValues.put("longitude", longitude);

		return formValues;
	}
	

	//-----------------------------------------------------------------------------
	public ServiceDTO findServiceByPosition(Map<String,String> formValues) throws IOException
	{
		String jsonData = downloadContent(URL_CENTRE_INTERET + "/getServiceByPosition", formValues);

		// Décodage de la réponse.
		ServiceDTO ul = null;
		if(jsonData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonData);
			try
			{
				ul = oMapper.readValue(jParser, ServiceDTO.class);
			}
			catch(JsonParseException jpe)
			{
			}
		}
		return ul;
	}

	//-----------------------------------------------------------------------------
	public CommentairesDTOList listeDesCommentairesPourUnPoint(String id) throws IOException
	{
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put("id_point", id);

		String jsonData = downloadContent(URL_CENTRE_INTERET + "/comm_point", formValues);

		// Décodage de la réponse.
		CommentairesDTOList ul = null;
		if(jsonData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonData);
			try
			{
				ul = oMapper.readValue(jParser, CommentairesDTOList.class);
			}
			catch(JsonParseException jpe)
			{
			}
		}
		return ul;
	}

	//-----------------------------------------------------------------------------
	public CommentairesDTOList listeDesCommentairesPourUnUtilisateur(String id) throws IOException
	{
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put("id_utilisateur", id);

		String jsonData = downloadContent(URL_CENTRE_INTERET + "/comm_ut", formValues);

		// Décodage de la réponse.
		CommentairesDTOList ul = null;
		if(jsonData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonData);
			try
			{
				ul = oMapper.readValue(jParser, CommentairesDTOList.class);
			}
			catch(JsonParseException jpe)
			{
			}
		}
		return ul;
	}

	public Map<String,String> prepareAddComment(String idUtilisateur, String idPoint, String contenu, String partagePublic){
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put("id_utilisateur", idUtilisateur);
		formValues.put("id_point", idPoint);
		formValues.put("contenu", contenu);
		formValues.put("partage_public", partagePublic);

		return formValues;
	}

	//-----------------------------------------------------------------------------
	public CommentaireDTO addComment(Map<String,String> formValues) throws IOException
	{
		String jsonData = downloadContent(URL_CENTRE_INTERET + "/new_comment", formValues);

		// Décodage de la réponse.
		CommentaireDTO ul = null;
		if(jsonData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonData);
			try
			{
				ul = oMapper.readValue(jParser, CommentaireDTO.class);
			}
			catch(JsonParseException jpe)
			{
			}
		}
		return ul;
	}

	public Map<String,String> prepareAddPoi(String latitude, String longitude, String type, String idUtilisateur, String contenu, String partagePublic){
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put("latitude", latitude);
		formValues.put("longitude", longitude);
		formValues.put("type", type);
		formValues.put("id_utilisateur", idUtilisateur);
		formValues.put(KEY_CONTENU, contenu);
		formValues.put("partage_public", partagePublic);

		return formValues;
	}
	
	//-----------------------------------------------------------------------------
	public PoiDTO addPoi(Map<String,String> formValues) throws IOException
	{
		String jsonData = downloadContent(URL_CENTRE_INTERET + "/new_poi_comment", formValues);

		// Décodage de la réponse.
		PoiDTO ul = null;
		if(jsonData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonData);
			try
			{
				ul = oMapper.readValue(jParser, PoiDTO.class);
			}
			catch(JsonParseException jpe)
			{
			}
		}
		return ul;
	}
}
