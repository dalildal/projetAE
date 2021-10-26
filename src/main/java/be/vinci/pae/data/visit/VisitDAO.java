package be.vinci.pae.data.visit;

import java.sql.Timestamp;
import java.util.List;
import be.vinci.pae.business.visit.VisitDTO;

public interface VisitDAO {

  /**
   * This method return id of the visit if the visit was well encoded.
   * 
   * @param visit to register
   * @return id of the visit
   */
  int insert(VisitDTO visit);

  /**
   * This method return true or false if the furniture was correctly linked.
   * 
   * @param idVisit to link
   * @param idFurniture to link
   * @return true or false
   */
  boolean linkVisit(int idVisit, int idFurniture);

  /**
   * This method return the list of all visits.
   * 
   * @param etat of visit
   * @return listVisit
   */
  List<VisitDTO> selectAllVisit(String etat);

  /**
   * This method return visit if id exist.
   * 
   * @param id of visit
   * @return visitDTO or null
   */
  VisitDTO findById(int id);

  /**
   * this method update the Visit.
   * 
   * @param id of visit
   * @param date of visit
   * @param raison of visit
   */
  void updateVisit(int id, Timestamp date, String raison);

  /**
   * this method return a list the furniture id.
   * 
   * @param id of visit
   * @return list the furniture id
   */
  List<Integer> selectListIdFurniture(int id);

  /**
   * This method return true or false if the furniture_visit was well insert.
   * 
   * @param idVisit of the visit
   * @param idFurniture of the furniture
   * @return true or false
   */
  boolean insertFurnitureVisit(int idVisit, int idFurniture);
}
