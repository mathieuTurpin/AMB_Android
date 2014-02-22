package turpin.mathieu.almanachdumarinbreton.asynctask;

import java.io.IOException;
import java.util.Map;

import android.content.Context;
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
		UtilisateurDTO user = (UtilisateurDTO) object;
		
		UpdateUtilisateurListener listener = (UpdateUtilisateurListener) context;
		listener.updateUtilisateur(user);
	}
}