package eu.telecom_bretagne.ambSocialNetwork.data.model;

import java.io.Serializable;
import java.util.List;


/**
 * The persistent class for the centre_interet database table.
 * 
 */
public class CentreInteretDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer       id;
  private String        nom;
	private String        description;
	private String        latitude;
	private String        longitude;
	private List<Integer> commentaires;

	public CentreInteretDTO() {
	}

	public Integer       getId()           { return this.id;           }
  public String        getNom()          { return this.nom;          }
  public String        getDescription()  { return this.description;  }
  public String        getLatitude()     { return this.latitude;     }
  public String        getLongitude()    { return this.longitude;    }
  public List<Integer> getCommentaires() { return this.commentaires; }

	public void setId(Integer id)                           { this.id           = id;           }
  public void setNom(String nom)                          { this.nom          = nom;          }
	public void setDescription(String description)          { this.description  = description;  }
	public void setLatitude(String latitude)                { this.latitude     = latitude;     }
	public void setLongitude(String longitude)              { this.longitude    = longitude;    }
	public void setCommentaires(List<Integer> commentaires) { this.commentaires = commentaires; }

  @Override
  public String toString()
  {
    return "CentreInteret [id=" + id + ", description=" + description + ", latitude=" + latitude + ", longitude=" + longitude + ", nom=" + nom + "]";
  }

}