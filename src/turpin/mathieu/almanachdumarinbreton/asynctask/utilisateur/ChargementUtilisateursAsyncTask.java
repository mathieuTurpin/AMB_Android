package turpin.mathieu.almanachdumarinbreton.asynctask.utilisateur;

import java.io.IOException;
import java.util.Map;

import turpin.mathieu.almanachdumarinbreton.asynctask.MyAsyncTask;
import android.content.Context;
import android.util.Log;
import eu.telecom_bretagne.ambSocialNetwork.data.controller.UtilisateurController;
import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateursDTOList;

public class ChargementUtilisateursAsyncTask extends MyAsyncTask
{
	public interface ChargementUtilisateurListener {
		void chargementUtilisateur(UtilisateursDTOList utilisateurs);
	}
	
	public ChargementUtilisateursAsyncTask(Context context, String title) {
		super(context, title);
	}
	
	@Override
	protected UtilisateursDTOList doInBackground(Map<String,String>... params)
	{
		UtilisateurController utilisateurController = UtilisateurController.getInstance();
		try
		{
			Log.d("AMBSocialNetwork", "-----------------------------> " + utilisateurController.findAllJson().getClass().getName());
			return utilisateurController.findAllJson();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	protected void onPostExecute(Object object)
	{
		super.onPostExecute(object);
		UtilisateursDTOList utilisateurs = (UtilisateursDTOList) object;
		
		ChargementUtilisateurListener listener = (ChargementUtilisateurListener) context;
		listener.chargementUtilisateur(utilisateurs);
	}
}
