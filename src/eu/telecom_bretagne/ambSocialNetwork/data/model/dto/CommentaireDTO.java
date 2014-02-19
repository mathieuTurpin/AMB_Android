package eu.telecom_bretagne.ambSocialNetwork.data.model.dto;

import java.io.Serializable;
import java.sql.Timestamp;


public class CommentaireDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer   id;
	private String    contenu;
	private Timestamp datePublication;
	private Boolean   partageCommentairePublic;
	private String    urlPhoto;
	private Integer   pointId;
	private Integer   utilisateurId;

	public CommentaireDTO() {
	}

	public Integer   getId()                       { return this.id;                       }
  public Integer   getUtilisateurId()            { return this.utilisateurId;            }
  public Integer   getPointId()                  { return this.pointId;                  }
  public String    getContenu()                  { return this.contenu;                  }
  public String    getUrlPhoto()                 { return this.urlPhoto;                 }
  public Timestamp getDatePublication()          { return this.datePublication;          }
  public Boolean   getPartageCommentairePublic() { return this.partageCommentairePublic; }

	public void setId(Integer id)                                             { this.id                       = id;                       }
  public void setUtilisateurId(Integer utilisateurId)                       { this.utilisateurId            = utilisateurId;            }
  public void setPointId(Integer pointId)                                   { this.pointId                  = pointId;                  }
  public void setContenu(String contenu)                                    { this.contenu                  = contenu;                  }
  public void setUrlPhoto(String urlPhoto)                                  { this.urlPhoto                 = urlPhoto;                 }
	public void setDatePublication(Timestamp datePublication)                 { this.datePublication          = datePublication;          }
	public void setPartageCommentairePublic(Boolean partageCommentairePublic) { this.partageCommentairePublic = partageCommentairePublic; }

  @Override
  public String toString()
  {
    return "CommentaireDTO [id=" + id + ", contenu=" + contenu + ", datePublication=" + datePublication + ", partageCommentairePublic=" + partageCommentairePublic + ", centreInteretId=" + pointId + ", utilisateurId=" + utilisateurId + "]";
  }

}