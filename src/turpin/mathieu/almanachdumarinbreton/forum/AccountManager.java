package turpin.mathieu.almanachdumarinbreton.forum;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class AccountManager {
	// Shared Preferences
	private final SharedPreferences pref;

	// Editor for Shared preferences
	private final Editor editor;

	// Context
	private final Context _context;

	// Shared pref mode
	private final int PRIVATE_MODE = 0;

	// Sharedpref file name
	private static final String PREF_NAME = "AMB_ACCOUNT";

	// User pseudo
	public static final String KEY_PSEUDO = "pseudo";
	
	// User email
	private static final String KEY_EMAIL = "email";
	
	// User is logged in
	private static final String KEY_IS_LOGGED_IN = "isLogin";

	// User shared location
	public static final String KEY_SHARED = "shared_location";

	// Shared Location mode
	public static final int NO_PARTAGE = 0;
	public static final int PARTAGE_PUBLIC = 1;
	public static final int PARTAGE_PRIVATE = 2;

	// Constructor
	@SuppressLint("CommitPrefEdits")
	public AccountManager(Context context){
		this._context = context;
		pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
		editor = pref.edit();
	}

	public void setPseudo(String pseudo){
		// Storing pseudo in pref
		editor.putString(KEY_PSEUDO, pseudo);

		// commit changes
		editor.commit();
	}   

	public String getPseudo(){
		return pref.getString(KEY_PSEUDO, "");
	}
	
	public void setPartage(int partage){
		// Storing partage in pref
		editor.putInt(KEY_SHARED, partage);

		// commit changes
		editor.commit();
	}

	public int getPartage(){
		return pref.getInt(KEY_SHARED, NO_PARTAGE);
	}
	
	public void logIn(String email){
		setEmail(email);
		setIsLoggedIn(true);
	}
	
	public void logOut(){
		editor.clear();
		
		editor.commit();
	}
	
	private void setEmail(String email){
		// Storing email in pref
		editor.putString(KEY_EMAIL, email);

		// commit changes
		editor.commit();
	}
	
	public String getEmail(){
		return pref.getString(KEY_EMAIL, null);
	}
	
	private void setIsLoggedIn(boolean logged){
		editor.putBoolean(KEY_IS_LOGGED_IN, logged);

		// commit changes
		editor.commit();
	}

	public boolean isLoggedIn(){
		return pref.getBoolean(KEY_IS_LOGGED_IN, false);
	}
}
