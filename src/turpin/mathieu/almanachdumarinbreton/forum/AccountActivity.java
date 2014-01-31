package turpin.mathieu.almanachdumarinbreton.forum;

import turpin.mathieu.almanachdumarinbreton.MyActivity;
import turpin.mathieu.almanachdumarinbreton.R;
import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;

public class AccountActivity extends MyActivity{

	private EditText pseudoEdit;
	private RadioGroup radioGroup;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account);
		
		String myPseudo = accountManager.getPseudo();
		pseudoEdit = (EditText) findViewById(R.id.pseudo);
		pseudoEdit.setText(myPseudo);

		radioGroup = (RadioGroup) findViewById(R.id.radioPartageGroup); 
		int sharedMode = accountManager.getPartage();
		getShareButtonByMode(sharedMode).setChecked(true);

		Button btnSave = (Button) findViewById(R.id.boutonSave);
		btnSave.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				String newPseudo = pseudoEdit.getText().toString();
				accountManager.setPseudo(newPseudo);

				int idButtonChecked = radioGroup.getCheckedRadioButtonId();
				int sharedMode = getSharedModeByButton(idButtonChecked);
				accountManager.setPartage(sharedMode);
				setResult(Activity.RESULT_OK);
				finish();
			}
		});

		Button btnLogOut = (Button) findViewById(R.id.boutonLogout);
		btnLogOut.setOnClickListener(new OnClickListener()
		{
			@Override
			public void onClick(View v)
			{
				accountManager.logOut();
				setResult(Activity.RESULT_CANCELED);
				finish();
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.account, menu);

		return super.onCreateOptionsMenu(menu);
	}

	private int getSharedModeByButton(int id){
		switch(id){
		case R.id.radioNoPartage:
			return AccountManager.NO_PARTAGE;
		case R.id.radioPartagePublic:
			return AccountManager.PARTAGE_PUBLIC;
		case R.id.radioPartagePrivate:
			return AccountManager.PARTAGE_PRIVATE;
		default:
			return AccountManager.NO_PARTAGE;
		}
	}

	private RadioButton getShareButtonByMode(int sharedMode){
		switch(sharedMode){
		case AccountManager.NO_PARTAGE:
			return (RadioButton) findViewById(R.id.radioNoPartage);
		case AccountManager.PARTAGE_PUBLIC:
			return (RadioButton) findViewById(R.id.radioPartagePublic);
		case AccountManager.PARTAGE_PRIVATE:
			return (RadioButton) findViewById(R.id.radioPartagePrivate);
		default:
			return null;
		}
	}
}
