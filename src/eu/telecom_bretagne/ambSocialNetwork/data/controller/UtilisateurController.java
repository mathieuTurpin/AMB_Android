package eu.telecom_bretagne.ambSocialNetwork.data.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;

import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateurDTO;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateursDTOList;

public class UtilisateurController extends Controller
{
	public static final String KEY_ID = "id";
	public static final String KEY_NOM = "nom";
	public static final String KEY_PRENOM = "prenom";
	public static final String KEY_EMAIL = "email";
	public static final String KEY_PASSWORD = "mot_de_passe";
	public static final String KEY_URL_AVATAR = "url_avatar";
	public static final String KEY_DESCRIPTION = "description";
	public static final String KEY_PARTAGE_POSITION = "partage_position";
	public static final String KEY_PARTAGE_POSITION_PUBLIC = "partage_position_public";

	//-----------------------------------------------------------------------------
	private static UtilisateurController instance = null;

	private static final String URL_UTILISATEUR = URL + "/utilisateur";
	//-----------------------------------------------------------------------------
	private UtilisateurController()
	{
		super();
	}
	//-----------------------------------------------------------------------------
	public static UtilisateurController getInstance()
	{
		if(instance == null)
			instance = new UtilisateurController();
		return instance;
	}
	//-----------------------------------------------------------------------------
	public String findAllText() throws IOException
	{
		String result = downloadContent(URL_UTILISATEUR + "/text");
		return result;
	}
	//-----------------------------------------------------------------------------
	public UtilisateursDTOList findAllJson() throws IOException
	{
		String jsonData = downloadContent(URL_UTILISATEUR);
		JsonParser   jParser  = jFactory.createParser(jsonData);
		UtilisateursDTOList ul = oMapper.readValue(jParser, UtilisateursDTOList.class);
		return ul;
	}
	
	//-----------------------------------------------------------------------------
	public UtilisateurDTO getUserById(Map<String,String> formValues) throws ClientProtocolException, IOException
	{
		String id = formValues.get(KEY_ID);
		String jsonUtilisateurData = downloadContent(URL_UTILISATEUR + "/" + id);

		// Décodage de la réponse.
		UtilisateurDTO utilisateur = null;
		if(jsonUtilisateurData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonUtilisateurData);
			try
			{
				utilisateur = oMapper.readValue(jParser, UtilisateurDTO.class);
			}
			catch(JsonParseException jpe)
			{
			}
		}
		return utilisateur;
	}
	
	//-----------------------------------------------------------------------------
	public UtilisateurDTO authentification(Map<String,String> formValues) throws ClientProtocolException, IOException
	{
		String jsonUtilisateurData = downloadContent(URL_UTILISATEUR + "/authentification", formValues);

		// Décodage de la réponse.
		UtilisateurDTO utilisateur = null;
		if(jsonUtilisateurData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonUtilisateurData);
			try
			{
				utilisateur = oMapper.readValue(jParser, UtilisateurDTO.class);
				//Log.d("AMBSocialNetwork", "-----------------------------> authentification(" + email + ", " + password + ") = " + utilisateur);
			}
			catch(JsonParseException jpe)
			{
				//Log.e("AMBSocialNetwork", "-----------------------------> authentification(" + email + ", " + password + ") = erreur, l'utilisateur n'existe pas.");
			}
		}
		return utilisateur;
	}
	//-----------------------------------------------------------------------------

	public Map<String,String> prepareLogin(String email,String password){
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put(KEY_EMAIL,        email);
		formValues.put(KEY_PASSWORD, password);

		return formValues;
	}

	public Map<String,String> prepareCreateAccount(String nom, String prenom, String email,String password, String description){
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put(KEY_NOM, nom);
		formValues.put(KEY_PRENOM, prenom);
		formValues.put(KEY_EMAIL, email);
		formValues.put(KEY_PASSWORD, password);
		formValues.put(KEY_DESCRIPTION, description);

		return formValues;
	}

	//-----------------------------------------------------------------------------
	public UtilisateurDTO createAccount(Map<String,String> formValues) throws ClientProtocolException, IOException
	{
		String jsonUtilisateurData = downloadContent(URL_UTILISATEUR + "/new", formValues);

		// Décodage de la réponse.
		UtilisateurDTO utilisateur = null;
		if(jsonUtilisateurData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonUtilisateurData);
			try
			{
				utilisateur = oMapper.readValue(jParser, UtilisateurDTO.class);
				//Log.d("AMBSocialNetwork", "-----------------------------> authentification(" + email + ", " + password + ") = " + utilisateur);
			}
			catch(JsonParseException jpe)
			{
				//Log.e("AMBSocialNetwork", "-----------------------------> authentification(" + email + ", " + password + ") = erreur, l'utilisateur n'existe pas.");
			}
		}
		return utilisateur;
	}
	//-----------------------------------------------------------------------------

	//-----------------------------------------------------------------------------
	public UtilisateurDTO update(Map<String,String> formValues) throws ClientProtocolException, IOException
	{
		String jsonUtilisateurData = downloadContent(URL_UTILISATEUR + "/update", formValues);

		// Décodage de la réponse.
		UtilisateurDTO utilisateur = null;
		if(jsonUtilisateurData != null)
		{
			JsonParser   jParser  = jFactory.createParser(jsonUtilisateurData);
			try
			{
				utilisateur = oMapper.readValue(jParser, UtilisateurDTO.class);
				//Log.d("AMBSocialNetwork", "-----------------------------> authentification(" + email + ", " + password + ") = " + utilisateur);
			}
			catch(JsonParseException jpe)
			{
				//Log.e("AMBSocialNetwork", "-----------------------------> authentification(" + email + ", " + password + ") = erreur, l'utilisateur n'existe pas.");
			}
		}
		return utilisateur;
	}
	//-----------------------------------------------------------------------------

	public Map<String,String> prepareUpdate(String id, String nom, String prenom, String email, String description, String partagePosition, String partagePositionPublic){
		Map<String,String> formValues = new HashMap<String, String>();
		formValues.put(KEY_ID, id);
		formValues.put(KEY_NOM, nom);
		formValues.put(KEY_PRENOM, prenom);
		formValues.put(KEY_EMAIL, email);
		formValues.put(KEY_PASSWORD, "amb");
		formValues.put(KEY_URL_AVATAR, "");
		formValues.put(KEY_DESCRIPTION, description);
		formValues.put(KEY_PARTAGE_POSITION, partagePosition);
		formValues.put(KEY_PARTAGE_POSITION_PUBLIC, partagePositionPublic);

		return formValues;
	}

}
