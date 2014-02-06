package eu.telecom_bretagne.ambSocialNetwork.data.model;

import java.util.ArrayList;
import java.util.List;

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
    for(Commentaire c : utilisateur.getCommentaires())
    {
      commentairesId.add(c.getId());
    }
    uDTO.setCommentaires(commentairesId);
    List<Integer> utilisateursId = new ArrayList<Integer>();
    for(Utilisateur u : utilisateur.getUtilisateurs1())
    {
      utilisateursId.add(u.getId());
    }
    uDTO.setUtilisateurs1(utilisateursId);
    utilisateursId = new ArrayList<Integer>();
    for(Utilisateur u : utilisateur.getUtilisateurs2())
    {
      utilisateursId.add(u.getId());
    }
    uDTO.setUtilisateurs2(utilisateursId);
    
    return uDTO;
  }
  //-----------------------------------------------------------------------------
  public static CommentaireDTO toDTO(Commentaire commentaire)
  {
    CommentaireDTO cDTO = new CommentaireDTO();
    cDTO.setId(commentaire.getId());
    cDTO.setUtilisateurId(commentaire.getUtilisateurBean().getId());
    cDTO.setCentreInteretId(commentaire.getCentreInteretBean().getId());
    cDTO.setContenu(commentaire.getContenu());
    cDTO.setUrlPhoto(commentaire.getUrlPhoto());
    cDTO.setDatePublication(commentaire.getDatePublication());
    cDTO.setPartageCommentairePublic(commentaire.getPartageCommentairePublic());
    
    return cDTO;
  }
  //-----------------------------------------------------------------------------
  public static CentreInteretDTO toDTO(CentreInteret centreInteret)
  {
    CentreInteretDTO ciDTO = new CentreInteretDTO();
    ciDTO.setId(centreInteret.getId());
    ciDTO.setNom(centreInteret.getNom());
    ciDTO.setDescription(centreInteret.getDescription());
    ciDTO.setLatitude(centreInteret.getLatitude());
    ciDTO.setLongitude(centreInteret.getLongitude());
    List<Integer> commentairesId = new ArrayList<Integer>();
    for(Commentaire c : centreInteret.getCommentaires())
    {
      commentairesId.add(c.getId());
    }
    ciDTO.setCommentaires(commentairesId);
    
    return ciDTO;
  }
  //-----------------------------------------------------------------------------
}
