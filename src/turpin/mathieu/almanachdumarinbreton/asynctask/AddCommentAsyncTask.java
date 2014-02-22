package turpin.mathieu.almanachdumarinbreton.asynctask;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import android.content.Context;
import android.widget.Toast;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.PoiController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.CommentaireDTO;

public class AddCommentAsyncTask extends MyAsyncTask
{
	public AddCommentAsyncTask(Context context, String title) {
		super(context, title);
	}

	@Override
	protected CommentaireDTO doInBackground(Map<String,String>... params)
	{
		PoiController centreInteretController = PoiController.getInstance();
		try
		{
			if(params.length < 1){
				return null;
			}
			else{
				CommentaireDTO commentaire = centreInteretController.addComment(params[0]);
				return commentaire;
			}
		}
		catch (ClientProtocolException e)
		{
			e.printStackTrace();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	@Override
	protected void onPostExecute (Object object) {
		super.onPostExecute(object);
		CommentaireDTO commentaire = (CommentaireDTO) object;
		if(commentaire != null){
			Toast.makeText(context, "Commentaire ajouté", Toast.LENGTH_SHORT).show();
		}
		else{
			Toast.makeText(context, "Erreur lors de l'ajout du commentaire", Toast.LENGTH_SHORT).show();
		}
	}
}
