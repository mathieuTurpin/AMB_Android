package eu.telecom_bretagne.ambSocialNetwork.data.model;

import java.io.Serializable;
import java.util.List;


public class UtilisateurDTO implements Serializable {
	private static final long serialVersionUID = 1L;

	private Integer id;
  private String  nom;
  private String  prenom;
  private String  email;
  private String  motDePasse;
  private String  urlAvatar;
  private String  description;
  private Boolean partagePosition;
  private Boolean partagePositionPublic;
	private float   latitude;
	private float   longitude;
  private Integer cap;
	private Integer vitesse;
	private List<Integer> commentaires;
	private List<Integer> utilisateurs1;
	private List<Integer> utilisateurs2;

	public UtilisateurDTO() {
	}

	public Integer       getId()                    { return this.id;                    }
  public String        getNom()                   { return this.nom;                   }
  public String        getPrenom()                { return this.prenom;                }
  public String        getEmail()                 { return this.email;                 }
  public String        getMotDePasse()            { return this.motDePasse;            }
  public String        getUrlAvatar()             { return this.urlAvatar;             }
  public String        getDescription()           { return this.description;           }
  public Boolean       getPartagePosition()       { return this.partagePosition;       }
  public Boolean       getPartagePositionPublic() { return this.partagePositionPublic; }
  public float         getLatitude()              { return this.latitude;              }
  public float         getLongitude()             { return this.longitude;             }
	public Integer       getCap()                   { return this.cap;                   }
  public Integer       getVitesse()               { return this.vitesse;               }
  public List<Integer> getCommentaires()          { return this.commentaires;          }
  public List<Integer> getUtilisateurs1()         { return this.utilisateurs1;         }
  public List<Integer> getUtilisateurs2()         { return this.utilisateurs2;         }

	public void setId(Integer id)                                       { this.id                    = id;                    }
  public void setNom(String nom)                                      { this.nom                   = nom;                   }
  public void setPrenom(String prenom)                                { this.prenom                = prenom;                }
  public void setEmail(String email)                                  { this.email                 = email;                 }
  public void setMotDePasse(String motDePasse)                        { this.motDePasse            = motDePasse;            }
  public void setUrlAvatar(String urlAvatar)                          { this.urlAvatar             = urlAvatar;             }
  public void setDescription(String description)                      { this.description           = description;           }
  public void setPartagePosition(Boolean partagePosition)             { this.partagePosition       = partagePosition;       }
  public void setPartagePositionPublic(Boolean partagePositionPublic) { this.partagePositionPublic = partagePositionPublic; }
  public void setLatitude(float latitude)                             { this.latitude              = latitude;              }
  public void setLongitude(float longitude)                           { this.longitude             = longitude;             }
	public void setCap(Integer cap)                                     { this.cap                   = cap;                   }
	public void setVitesse(Integer vitesse)                             { this.vitesse               = vitesse;               }
	public void setCommentaires(List<Integer> commentaires)             { this.commentaires          = commentaires;          }
	public void setUtilisateurs1(List<Integer> utilisateurs1)           { this.utilisateurs1         = utilisateurs1;         }
	public void setUtilisateurs2(List<Integer> utilisateurs2)           { this.utilisateurs2         = utilisateurs2;         }

  @Override
  public String toString()
  {
    return "UtilisateurDTO [id=" + id + ", nom=" + nom + ", prenom=" + prenom + ", email=" + email + ", motDePasse=" + motDePasse + "]";
  }

}