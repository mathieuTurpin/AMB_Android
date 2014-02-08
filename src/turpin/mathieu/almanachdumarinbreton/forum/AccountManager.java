package turpin.mathieu.almanachdumarinbreton.forum;

import eu.telecom_bretagne.ambSocialNetwork.data.model.dto.UtilisateurDTO;
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

	// User name
	public static final String KEY_NAME = "name";

	// User prenom
	public static final String KEY_FIRST_NAME = "first_name";

	// User descrition
	public static final String KEY_DESCRIPTION = "description";

	// User id
	public static final String KEY_ID = "id";

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

	public void setName(String name){
		// Storing name in pref
		editor.putString(KEY_NAME, name);

		// commit changes
		editor.commit();
	}
	
	public void setFirstName(String firstName){
		// Storing name in pref
		editor.putString(KEY_FIRST_NAME, firstName);

		// commit changes
		editor.commit();
	}
	
	public void setDescription(String description){
		// Storing name in pref
		editor.putString(KEY_DESCRIPTION, description);

		// commit changes
		editor.commit();
	}

	public Integer getId(){
		return pref.getInt(KEY_ID, -1);
	}

	public void setId(Integer id){
		// Storing id in pref
		editor.putInt(KEY_ID, id);

		// commit changes
		editor.commit();
	}   

	public String getName(){
		return pref.getString(KEY_NAME, "");
	}
	
	public String getFirstName(){
		return pref.getString(KEY_FIRST_NAME, "");
	}
	
	public String getDescription(){
		return pref.getString(KEY_DESCRIPTION, "");
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

	public void logIn(UtilisateurDTO user){
		if(user.getPartagePosition()){
			if(user.getPartagePositionPublic()){
				setPartage(PARTAGE_PUBLIC);
			}
			else{
				setPartage(PARTAGE_PRIVATE);
			}
		}
		else{
			setPartage(NO_PARTAGE);
		}
		setName(user.getNom());
		setFirstName(user.getPrenom());
		setDescription(user.getDescription());
		setEmail(user.getEmail());
		setId(user.getId());
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
	
	public boolean getNoPartage(){
		return getPartage() == NO_PARTAGE;		
	}
	
	public boolean getPartagePublic(){
		return getPartage() == PARTAGE_PUBLIC;		
	}

}
