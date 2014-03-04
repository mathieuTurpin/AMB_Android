package turpin.mathieu.almanachdumarinbreton.asynctask.utilisateur;

import java.io.IOException;
import java.util.Map;

import turpin.mathieu.almanachdumarinbreton.asynctask.MyAsyncTask;
import android.content.Context;
import android.widget.Toast;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.UtilisateurController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateurDTO;

public class UpdateUtilisateurAsyncTask extends MyAsyncTask
{
	public interface UpdateUtilisateurListener {
		void updateUtilisateur(UtilisateurDTO user);
	}
	
	public UpdateUtilisateurAsyncTask(Context context, String title) {
		super(context, title);
	}

	@Override
	protected UtilisateurDTO doInBackground(Map<String,String>... formValues)
	{
		UtilisateurController utilisateurController = UtilisateurController.getInstance();
		try
		{
			if(formValues.length < 1){
				return null;
			}
			else{			
				return utilisateurController.update(formValues[0]);
			}
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
		if(object != null){
			UtilisateurDTO user = (UtilisateurDTO) object;
			
			UpdateUtilisateurListener listener = (UpdateUtilisateurListener) context;
			listener.updateUtilisateur(user);
		}
		else{
			Toast.makeText(context, "Erreur lors de la modification du compte", Toast.LENGTH_SHORT).show();
		}
		
	}
}