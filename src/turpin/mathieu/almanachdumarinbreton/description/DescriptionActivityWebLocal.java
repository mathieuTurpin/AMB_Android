package turpin.mathieu.almanachdumarinbreton.description;

import java.io.IOException;
import java.io.InputStream;

import turpin.mathieu.almanachdumarinbreton.R;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.webkit.WebView;

@SuppressLint("SetJavaScriptEnabled")
public class DescriptionActivityWebLocal extends DescriptionActivity{

	private WebView mWebView = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_description_web);

		mWebView = (WebView) findViewById(R.id.webView);

		mWebView.getSettings().setJavaScriptEnabled(true);
		mWebView.getSettings().setBuiltInZoomControls(true);

		String html = LoadData(this.courtNamePort+".html");
		mWebView.loadDataWithBaseURL("file:///android_asset/html/", html, "text/html",
				"UTF-8", null);

	}

	public String LoadData(String inFile) {
		String tContents = "";

		try {
			InputStream stream = getAssets().open("html/"+inFile);

			int size = stream.available();
			byte[] buffer = new byte[size];
			stream.read(buffer);
			stream.close();
			tContents = new String(buffer);
		} catch (IOException e) {
			// Handle exceptions here
		}

		return tContents;

	}

}
