package turpin.mathieu.almanachdumarinbreton.asynctask.utilisateur;

import java.io.IOException;
import java.util.Map;

import org.apache.http.client.ClientProtocolException;

import turpin.mathieu.almanachdumarinbreton.asynctask.MyAsyncTask;
import android.content.Context;
import android.widget.Toast;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.UtilisateurController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateurDTO;

public class CreateAccountAsyncTask extends MyAsyncTask
{		
	private CreateAccountListener listener;
	
	public interface CreateAccountListener {
		void createAccount(UtilisateurDTO user);
	}
	
	public CreateAccountAsyncTask(Context context, String title,CreateAccountListener listener) {
		super(context, title);
		this.listener = listener;
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
				UtilisateurDTO utilisateur = utilisateurController.createAccount(params[0]);
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
			listener.createAccount(user);
		}
		else{
			Toast.makeText(context, "Erreur lors de la création du compte", Toast.LENGTH_SHORT).show();
		}
	}
}