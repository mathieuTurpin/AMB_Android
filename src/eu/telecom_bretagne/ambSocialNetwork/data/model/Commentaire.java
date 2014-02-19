package eu.telecom_bretagne.ambSocialNetwork.data.model;

import java.io.Serializable;
import java.sql.Timestamp;

public class Commentaire implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private Integer id;

  private String contenu;

  private Timestamp datePublication;

  private Boolean partageCommentairePublic;

  private String urlPhoto;

  private Point pointBean;

  private Utilisateur utilisateurBean;

  public Commentaire()
  {}

  public Integer getId()
  {
    return this.id;
  }

  public void setId(Integer id)
  {
    this.id = id;
  }

  public String getContenu()
  {
    return this.contenu;
  }

  public void setContenu(String contenu)
  {
    this.contenu = contenu;
  }

  public Timestamp getDatePublication()
  {
    return this.datePublication;
  }

  public void setDatePublication(Timestamp datePublication)
  {
    this.datePublication = datePublication;
  }

  public Boolean getPartageCommentairePublic()
  {
    return this.partageCommentairePublic;
  }

  public void setPartageCommentairePublic(Boolean partageCommentairePublic)
  {
    this.partageCommentairePublic = partageCommentairePublic;
  }

  public String getUrlPhoto()
  {
    return this.urlPhoto;
  }

  public void setUrlPhoto(String urlPhoto)
  {
    this.urlPhoto = urlPhoto;
  }

  public Point getPointBean()
  {
    return this.pointBean;
  }

  public void setPointBean(Point pointBean)
  {
    this.pointBean = pointBean;
  }

  public Utilisateur getUtilisateurBean()
  {
    return this.utilisateurBean;
  }

  public void setUtilisateurBean(Utilisateur utilisateurBean)
  {
    this.utilisateurBean = utilisateurBean;
  }

}