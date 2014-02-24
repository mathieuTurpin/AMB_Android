package turpin.mathieu.almanachdumarinbreton.asynctask;

import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public abstract class MyAsyncTask extends AsyncTask<Map<String,String>, Object, Object>
{
	protected final Context context;
	protected ProgressDialog progressDialog;
	
	/**
	 * 
	 * @param context
	 * @param title
	 * 		title used to set the title text for this dialog's window
	 */
	public MyAsyncTask(Context context,String title){
		super();
		this.context = context;
		this.progressDialog = new ProgressDialog(context);
		this.progressDialog.setTitle(title);
	}
	
	@Override
	protected void onPreExecute()
	{
		super.onPreExecute();
		progressDialog.setMessage("En cours...");
		progressDialog.setCancelable(true);
		progressDialog.show();
	}
	

	@Override
	protected void onPostExecute (Object object) {
		progressDialog.dismiss();
	}
}
