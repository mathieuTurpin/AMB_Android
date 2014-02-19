package eu.telecom_bretagne.ambSocialNetwork.data.model;

import java.io.Serializable;
import java.util.List;

public class Point implements Serializable
{
  private static final long serialVersionUID = 1L;
  
  private Integer id;

  private String latitude;

  private String longitude;

  private List<Commentaire> commentaires;

  private Poi poi;

  private Service service;

  public Point() {}

  public Integer           getId()           { return this.id;           }
  public String            getLatitude()     { return this.latitude;     }
  public String            getLongitude()    { return this.longitude;    }
  public List<Commentaire> getCommentaires() { return this.commentaires; }
  public Poi               getPoi()          { return this.poi;          }
  public Service           getService()      { return this.service;      }

  public void setId(Integer id)                               { this.id           = id;           }
  public void setLatitude(String latitude)                    { this.latitude     = latitude;     }
  public void setLongitude(String longitude)                  { this.longitude    = longitude;    }
  public void setCommentaires(List<Commentaire> commentaires) { this.commentaires = commentaires; }
  public void setPoi(Poi poi)                                 { this.poi          = poi;          }
  public void setService(Service service)                     { this.service      = service;      }

  public Commentaire addCommentaire(Commentaire commentaire)
  {
    getCommentaires().add(commentaire);
    commentaire.setPointBean(this);

    return commentaire;
  }

  public Commentaire removeCommentaire(Commentaire commentaire)
  {
    getCommentaires().remove(commentaire);
    commentaire.setPointBean(null);

    return commentaire;
  }

}