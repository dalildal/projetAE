package be.vinci.pae.business.visit;

import java.util.List;

public interface VisitUCC {

  /**
   * This method will add a new visit.
   * 
   * @param visit to add
   * @return id of the visit
   */
  int add(VisitDTO visit);

  /**
   * This method call selectAllVisit from VisitDAO.
   * 
   * @param etat of visit
   * @return visitDTO with visit information
   */
  List<VisitDTO> getAllVisit(String etat);

  /**
   * This method call findById from VisitDAO.
   * 
   * @param id of Visit
   * @return VisitDTO
   */
  VisitDTO getVisit(int id);

  /**
   * this method call updateVisit from VisitDAO.
   * 
   * @param id of visit
   * @param date of visit
   * @param raison of visit
   */
  void putVisit(int id, String date, String raison);

  /**
   * this method call selectListIdFurniture from VisitDAO.
   * 
   * @param id of visit
   * @return list the furniture id
   */
  List<Integer> getListIdFurniture(int id);


  /**
   * This method will add a new furniture_visit.
   * 
   * @param idVisit of the visit
   * @param idFurniture of the furniture
   * @return true or false
   */
  boolean addFurnitureVisit(int idVisit, int idFurniture);

}
