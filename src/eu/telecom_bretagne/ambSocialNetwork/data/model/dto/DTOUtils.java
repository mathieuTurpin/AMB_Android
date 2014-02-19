package eu.telecom_bretagne.ambSocialNetwork.data.model.dto;

import java.util.ArrayList;
import java.util.List;

import eu.telecom_bretagne.ambSocialNetwork.data.model.Commentaire;
import eu.telecom_bretagne.ambSocialNetwork.data.model.Poi;
import eu.telecom_bretagne.ambSocialNetwork.data.model.Service;
import eu.telecom_bretagne.ambSocialNetwork.data.model.Utilisateur;

public class DTOUtils
{
  //-----------------------------------------------------------------------------
  public static UtilisateurDTO toDTO(Utilisateur utilisateur)
  {
    UtilisateurDTO uDTO = new UtilisateurDTO();
    uDTO.setId(utilisateur.getId());
    uDTO.setNom(utilisateur.getNom());
    uDTO.setPrenom(utilisateur.getPrenom());
    uDTO.setEmail(utilisateur.getEmail());
    uDTO.setMotDePasse(utilisateur.getMotDePasse());
    uDTO.setUrlAvatar(utilisateur.getUrlAvatar());
    uDTO.setDescription(utilisateur.getDescription());
    uDTO.setPartagePosition(utilisateur.getPartagePosition());
    uDTO.setPartagePositionPublic(utilisateur.getPartagePositionPublic());
    uDTO.setLatitude(utilisateur.getLatitude());
    uDTO.setLongitude(utilisateur.getLongitude());
    uDTO.setCap(utilisateur.getCap());
    uDTO.setVitesse(utilisateur.getVitesse());
    List<Integer> commentairesId = new ArrayList<Integer>();
    if(utilisateur.getCommentaires() != null)
    {
      for(Commentaire c : utilisateur.getCommentaires())
      {
        commentairesId.add(c.getId());
      }
    }
    uDTO.setCommentaires(commentairesId);
    List<Integer> utilisateursId = new ArrayList<Integer>();
    if(utilisateur.getUtilisateurs1() != null)
    {
      for(Utilisateur u : utilisateur.getUtilisateurs1())
      {
        utilisateursId.add(u.getId());
      }
    }
    uDTO.setDeclaresEtreMesAmis(utilisateursId);
    utilisateursId = new ArrayList<Integer>();
    if(utilisateur.getUtilisateurs2() != null)
    {
      for(Utilisateur u : utilisateur.getUtilisateurs2())
      {
        utilisateursId.add(u.getId());
      }
    }
    uDTO.setMesAmis(utilisateursId);
    
    return uDTO;
  }
  //-----------------------------------------------------------------------------
  public static CommentaireDTO toDTO(Commentaire commentaire)
  {
    CommentaireDTO cDTO = new CommentaireDTO();
    cDTO.setId(commentaire.getId());
    cDTO.setUtilisateurId(commentaire.getUtilisateurBean().getId());
    cDTO.setPointId(commentaire.getPointBean().getId());
    cDTO.setContenu(commentaire.getContenu());
    cDTO.setUrlPhoto(commentaire.getUrlPhoto());
    cDTO.setDatePublication(commentaire.getDatePublication());
    cDTO.setPartageCommentairePublic(commentaire.getPartageCommentairePublic());
    
    return cDTO;
  }
  //-----------------------------------------------------------------------------
  public static ServiceDTO toDTO(Service service)
  {
    ServiceDTO serviceDTO = new ServiceDTO();
    
    serviceDTO.setId(service.getId());
    serviceDTO.setType(service.getType());
    serviceDTO.setDescription(service.getDescription());
    serviceDTO.setLatitude(service.getPoint().getLatitude());
    serviceDTO.setLongitude(service.getPoint().getLongitude());
    List<Integer> commentairesId = new ArrayList<Integer>();
    if(service.getPoint().getCommentaires() != null)
    {
      for(Commentaire c : service.getPoint().getCommentaires())
      {
        commentairesId.add(c.getId());
      }
    }
    serviceDTO.setCommentaires(commentairesId);
    
    return serviceDTO;
  }
  //-----------------------------------------------------------------------------
  public static PoiDTO toDTO(Poi poi)
  {
    PoiDTO poiDTO = new PoiDTO();
    
    
    poiDTO.setId(poi.getId());
    poiDTO.setType(poi.getType());
    poiDTO.setLatitude(poi.getPoint().getLatitude());
    poiDTO.setLongitude(poi.getPoint().getLongitude());
    List<Integer> commentairesId = new ArrayList<Integer>();
    if(poi.getPoint().getCommentaires() != null)
    {
      for(Commentaire c : poi.getPoint().getCommentaires())
      {
        commentairesId.add(c.getId());
      }
    }
    poiDTO.setCommentaires(commentairesId);
    
    return poiDTO;
  }
  //-----------------------------------------------------------------------------
  public static List<UtilisateurDTO> toListeUtilisateurDTO(List<Utilisateur> utilisateurs)
  {
    List<UtilisateurDTO> resultat = new ArrayList<UtilisateurDTO>();
    for(Utilisateur u : utilisateurs)
    {
      resultat.add(DTOUtils.toDTO(u));
    }
    return resultat;
  }
  //-----------------------------------------------------------------------------
  public static List<ServiceDTO> toListeServicesDTO(List<Service> services)
  {
    List<ServiceDTO> resultat = new ArrayList<ServiceDTO>();
    for(Service service : services)
    {
      resultat.add(DTOUtils.toDTO(service));
    }
    return resultat;
  }
  //-----------------------------------------------------------------------------
  public static List<PoiDTO> toListePoisDTO(List<Poi> pois)
  {
    List<PoiDTO> resultat = new ArrayList<PoiDTO>();
    for(Poi poi : pois)
    {
      resultat.add(DTOUtils.toDTO(poi));
    }
    return resultat;
  }
  //-----------------------------------------------------------------------------
  public static List<CommentaireDTO> toListeCommentaireDTO(List<Commentaire> commentaires)
  {
    List<CommentaireDTO> resultat = new ArrayList<CommentaireDTO>();
    for(Commentaire c : commentaires)
    {
      resultat.add(DTOUtils.toDTO(c));
    }
    return resultat;
  }
  //-----------------------------------------------------------------------------
}
