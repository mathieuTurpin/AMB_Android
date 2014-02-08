package eu.telecom_bretagne.ambSocialNetwork.data.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CentreInteretDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentaireDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentairesDTOList;

public class CentreInteretController extends Controller
{
	//-----------------------------------------------------------------------------
	private static CentreInteretController instance = null;

	private static final String URL_CENTRE_INTERET = URL + "/centre_interet";
	//-----------------------------------------------------------------------------
	private CentreInteretController()
	{
		super();
	}
	//-----------------------------------------------------------------------------
	public static CentreInteretController getInstance()
	{
		if(instance == null)
			instance = new CentreInteretController();
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
	public CentreInteretDTO findCentreInteretByPosition(Map<String,String> formValues) throws IOException
	{
		String jsonData = downloadContent(URL_CENTRE_INTERET + "/getByPosition", formValues);

		// Décodage de la réponse.
		CentreInteretDTO ul = null;
		if(jsonData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonData);
			try
			{
				ul = oMapper.readValue(jParser, CentreInteretDTO.class);
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
	public CommentairesDTOList listeDesCommentairesPourUnCentreInteret(String id) throws IOException
	{
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put("id_centre_interet", id);

		String jsonData = downloadContent(URL_CENTRE_INTERET + "/comm_ci", formValues);

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

	public Map<String,String> prepareAddComment(String idUtilisateur, String idCentreInteret, String contenu, String partagePublic){
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put("id_utilisateur", idUtilisateur);
		formValues.put("id_centre_interet", idCentreInteret);
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
}
