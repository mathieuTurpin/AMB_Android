package eu.telecom_bretagne.ambSocialNetwork.data.model;

import java.io.Serializable;
import java.util.List;


public class CentreInteret implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;

	private String description;

	private String latitude;

	private String longitude;

	private String nom;

	private List<Commentaire> commentaires;

	public CentreInteret() {
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLatitude() {
		return this.latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return this.longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getNom() {
		return this.nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public List<Commentaire> getCommentaires() {
		return this.commentaires;
	}

	public void setCommentaires(List<Commentaire> commentaires) {
		this.commentaires = commentaires;
	}

	public Commentaire addCommentaire(Commentaire commentaire) {
		getCommentaires().add(commentaire);
		commentaire.setCentreInteretBean(this);

		return commentaire;
	}

	public Commentaire removeCommentaire(Commentaire commentaire) {
		getCommentaires().remove(commentaire);
		commentaire.setCentreInteretBean(null);

		return commentaire;
	}

  @Override
  public String toString()
  {
    return "CentreInteret [id=" + id + ", description=" + description + ", latitude=" + latitude + ", longitude=" + longitude + ", nom=" + nom + "]";
  }

}