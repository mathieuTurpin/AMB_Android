package turpin.mathieu.almanachdumarinbreton.forum;

import turpin.mathieu.almanachdumarinbreton.R;
import android.app.Dialog;
import android.content.Context;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

public class LoginDialog extends Dialog{
		
	public LoginDialog(final Context context,final MenuItem item,final AccountManager accountManager) {
		super(context);
		
		this.setContentView(R.layout.login);
		this.setCancelable(false);

		final EditText emailaddr = (EditText) findViewById(R.id.email);
		final EditText password = (EditText) findViewById(R.id.password);
		this.show();

		Button login = (Button) findViewById(R.id.boutonLogin);
		login.setOnClickListener(new View.OnClickListener()
		{

			@Override
			public void onClick(View v)
			{
				String emailText = emailaddr.getText().toString();
				String passwordText = password.getText().toString();
				accountManager.logIn(emailText);
				item.setTitle(R.string.menu_compte);
				dismiss();
			}
		});

		Button cancel = (Button) findViewById(R.id.boutonCancel);
		cancel.setOnClickListener(new View.OnClickListener(){

			@Override
			public void onClick(View v){
				dismiss();
			}
		});
	}
}
