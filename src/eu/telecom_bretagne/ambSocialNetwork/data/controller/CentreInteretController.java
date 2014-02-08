package eu.telecom_bretagne.ambSocialNetwork.data.controller;

import java.io.IOException;

import com.fasterxml.jackson.core.JsonParser;

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
		JsonParser   jParser  = jFactory.createParser(jsonData);
		CommentairesDTOList ul = oMapper.readValue(jParser, CommentairesDTOList.class);
		return ul;
	}
}
