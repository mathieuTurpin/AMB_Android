package turpin.mathieu.almanachdumarinbreton.asynctask;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.PowerManager;
import android.widget.Toast;

public class GetMapAsyncTask extends AsyncTask<String, Integer, String>
{
	public interface GetMapListener{
		public void setConnectionOffline();
	}
	
	private ProgressDialog mProgressDialog;

	private Context context;
	private PowerManager.WakeLock mWakeLock;
	
	private String pathFile;
	private int fileLength;
	
	private GetMapListener listener;

	public GetMapAsyncTask(Context context, GetMapListener listener) {
		this.context = context;
		this.listener = listener;
	}
	
	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mProgressDialog = new ProgressDialog(context);
		mProgressDialog.setMessage("Téléchargement de la carte");
		mProgressDialog.setIndeterminate(true);
		mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
		mProgressDialog.setCancelable(true);

		final GetMapAsyncTask me = this;
		mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
			@Override
			public void onCancel(DialogInterface dialog) {
				me.cancel(true);

			}
		});

		// take CPU lock to prevent CPU from going off if the user 
		// presses the power button during download
		PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
		mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
				getClass().getName());
		mWakeLock.acquire();
		mProgressDialog.show();
	}

	@Override
	protected String doInBackground(String... params) {
		InputStream input = null;
		OutputStream output = null;
		HttpURLConnection connection = null;
		this.pathFile = params[1];
		
		try {
			URL url = new URL(params[0]);
			connection = (HttpURLConnection) url.openConnection();
			connection.connect();

			// expect HTTP 200 OK, so we don't mistakenly save error report
			// instead of the file
			if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
				return "Server returned HTTP " + connection.getResponseCode()
						+ " " + connection.getResponseMessage();
			}

			// this will be useful to display download percentage
			// might be -1: server did not report the length
			fileLength = connection.getContentLength();

			// download the file
			input = connection.getInputStream();
			output = new FileOutputStream(pathFile);

			byte data[] = new byte[4096];
			long total = 0;
			int count;
			while ((count = input.read(data)) != -1) {
				// allow canceling with back button
				if (isCancelled()) {
					input.close();
					checkFileDownload();
					return null;
				}
				total += count;
				// publishing the progress....
				if (fileLength > 0) // only if total length is known
					publishProgress((int) (total * 100 / fileLength));
				output.write(data, 0, count);
			}
		} catch (Exception e) {
			return e.toString();
		} finally {
			try {
				if (output != null)
					output.close();
				if (input != null)
					input.close();
			} catch (IOException ignored) {
			}

			if (connection != null)
				connection.disconnect();
		}
		return null;
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		// if we get here, length is known, now set indeterminate to false
		mProgressDialog.setIndeterminate(false);
		mProgressDialog.setMax(100);
		mProgressDialog.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		mWakeLock.release();
		mProgressDialog.dismiss();
		
		String fileOk = checkFileDownload();

		if(result == null && fileOk == null){
			Toast.makeText(context,"Carte télécharger", Toast.LENGTH_SHORT).show();
			GetMapListener activity = (GetMapListener) context;
			activity.setConnectionOffline();
			listener.setConnectionOffline();
		}
		else{
			Toast.makeText(context, "Erreur lors du téléchargement de la carte "+result + fileOk, Toast.LENGTH_SHORT).show();;
		}
	}
	
	//Vérifie que tout le fichier a été téléchargé sinon le supprimer
	private String checkFileDownload(){
		File file = new File(this.pathFile);
		long length = file.length();
		if(length != fileLength){
			file.delete();
			return "fichier non téléchargé intégralement";
		}
		return null;
	}
}
