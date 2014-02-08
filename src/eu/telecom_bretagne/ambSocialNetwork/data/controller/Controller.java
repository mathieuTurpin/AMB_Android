package eu.telecom_bretagne.ambSocialNetwork.data.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;

public abstract class Controller
{
  //-----------------------------------------------------------------------------
  protected static final String hostname = "192.108.117.199";   // @IP srv-labs-006
  protected static final String port     = "80";
  //protected static final String hostname = "10.29.226.130";     // @IP Télécom Bretagne
  //protected static final String hostname = "192.168.1.7";       // @IP Maison
  //protected static final String port     = "8080";

  protected static final String       URL = "http://" + hostname + ":" + port + "/AMBSocialNetworkServerWeb/RestWebServices";
  protected static       ObjectMapper oMapper;
  protected static       JsonFactory  jFactory;
  protected static       HttpClient   client;
  //-----------------------------------------------------------------------------
  protected Controller()
  {
    // Instance de la classe assurant le mapping entre les représentations JSON
    // et les représentations objet.
    oMapper  = new ObjectMapper();
    // Factory pour la création des parseurs qui analyseront les données au
    // format JSON
    jFactory = new JsonFactory();
    client   = new DefaultHttpClient();
  }
  //-----------------------------------------------------------------------------
  protected String downloadContent(String url) throws ClientProtocolException, IOException
  {
    HttpGet      get = new HttpGet(url);
    HttpResponse response = client.execute(get);
    if(response != null)
    {
      return EntityUtils.toString(response.getEntity(), "UTF-8");
    }
    return null;
  }
  //-----------------------------------------------------------------------------
  protected String downloadContent(String url, Map<String,String> formValues) throws ClientProtocolException, IOException
  {
    /*
     * Plusieurs choses à faire :
     *   - Préparer une requête POST de type formulaire (la
     *     requête est l'équivalent d'un formulaire Web)
     *       . Définir l'URL
     *       . Encapsuler les paramètres 
     *   - Envoyer la requête sur le serveur
     *   - Récupérer le résultat (données au format JSON
     *     représentant une intance du model objet)
     *   - La convertion des données dans un format objet sera fait
     *     dans la méthode appelante.
     */
    
    // Réquête POST qui envrerra des données de type formulaire
    
    HttpPost post = new HttpPost(url);
    post.setHeader("Content-Type",
                   "application/x-www-form-urlencoded;charset=UTF-8");
    
    // Encapsulation des paramètres : le formulaire possède un certain
    // nombre de paramètres qui arrivent dans la méthode sous la forme
    // d'une Map (HashMap).
    // Chaque paramètre est représenté dans une instance de NameValuePair
    // (interface) qui contient un couple (nom du param, valeur). Chaque
    // couple est ajouté dans une liste (List<NameValuePair>) qui représente
    // l'ensemble des valeurs.
    // Ces valeur sont ensuite encodées avant d'être incorporées au POST. 

    List<NameValuePair> values = new ArrayList<NameValuePair>();
    for(String key : formValues.keySet())
    {
      values.add(new BasicNameValuePair(key, formValues.get(key)));
    }
    UrlEncodedFormEntity entity = new UrlEncodedFormEntity(values);
    post.setEntity(entity);

    // Envoi de la requête et récupération de la réponse.
    // On récupère dans la réponse le flux sous la forme d'une chaîne de
    // caractères (au format JSON).
    
    HttpResponse response = client.execute(post);
    if(response != null)
    {
      return EntityUtils.toString(response.getEntity(), "UTF-8");
    }
    return null;
  }
  //-----------------------------------------------------------------------------
}
