package eu.telecom_bretagne.ambSocialNetwork.data.model.dto;

import java.io.Serializable;
import java.util.List;


/**
 * The persistent class for the service database table.
 * 
 */
public class ServiceDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer       id;
  private String        type;
	private String        description;
	private String        latitude;
	private String        longitude;
	private List<Integer> commentaires;

	public ServiceDTO() {}

	public Integer       getId()           { return this.id;           }
  public String        getType()         { return this.type;          }
  public String        getDescription()  { return this.description;  }
  public String        getLatitude()     { return this.latitude;     }
  public String        getLongitude()    { return this.longitude;    }
  public List<Integer> getCommentaires() { return this.commentaires; }

	public void setId(Integer id)                           { this.id           = id;           }
  public void setType(String type)                        { this.type          = type;        }
	public void setDescription(String description)          { this.description  = description;  }
	public void setLatitude(String latitude)                { this.latitude     = latitude;     }
	public void setLongitude(String longitude)              { this.longitude    = longitude;    }
	public void setCommentaires(List<Integer> commentaires) { this.commentaires = commentaires; }

  @Override
  public String toString()
  {
    return "ServiceDTO [id=" + id + ", description=" + description + ", latitude=" + latitude + ", longitude=" + longitude + ", type=" + type + "]";
  }

}