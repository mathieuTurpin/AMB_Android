package turpin.mathieu.almanachdumarinbreton.asynctask.utilisateur;
import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import turpin.mathieu.almanachdumarinbreton.asynctask.MyAsyncTask;
import android.content.Context;
import android.widget.Toast;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.UtilisateurController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateurDTO;

public class GetUserByIdAsyncTask extends MyAsyncTask
{
	public interface GetUserByIdListener {
		void setUser(UtilisateurDTO user, int index);
	}
	
	private final int index;

	public GetUserByIdAsyncTask(Context context, String title,int index){
		super(context,title);
		this.index = index;
	}

	@Override
	protected UtilisateurDTO doInBackground(Map<String,String>... params)
	{
		UtilisateurController utilisateurController = UtilisateurController.getInstance();
		try
		{
			if(params.length < 1){
				return null;
			}
			else{
				UtilisateurDTO utilisateur = utilisateurController.getUserById(params[0]);
				return utilisateur;
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
		UtilisateurDTO user = (UtilisateurDTO) object;
		if(user != null){ 
			GetUserByIdListener listener = (GetUserByIdListener) context;
			listener.setUser(user, index);
		}
		else{
			Toast.makeText(context, "Erreur lors de la recherche du compte", Toast.LENGTH_SHORT).show();
		}
	}
}