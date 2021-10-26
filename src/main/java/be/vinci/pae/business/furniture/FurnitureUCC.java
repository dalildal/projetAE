package be.vinci.pae.business.furniture;

import java.util.HashMap;
import java.util.List;

public interface FurnitureUCC {
  /**
   * This method call selectAllFurniture from FurnitureDAO.
   * 
   * @param type of furniture
   * @param etat list of furniture's states
   * @return List of FurnitureDTO objects
   */
  List<FurnitureDTO> getAllFurniture(String type, List<String> etat);

  /**
   * This method call findById from FurnitureDAO.
   * 
   * @param id of furniture
   * @return FurnitureDTO with furniture information
   */
  FurnitureDTO getFurniture(int id);

  /**
   * this method call updateFurnitureState from FurnitureDAO.
   * 
   * @param id of furniture
   * @param state of furniture
   * @param date of furniture
   * @param price of furniture
   * @param dealerPrice of furniture
   * @param purchasePrice of furniture
   * @param recoveryDate of furniture
   * @param description of furniture
   */
  void putFurnitureState(int id, String state, String date, double price, double dealerPrice,
      double purchasePrice, String recoveryDate, String description);

  /**
   * this method call updateFurniture from FurnitureDAO.
   * 
   * @param id of furniture
   * @param description of furniture
   * @param type of furniture
   * @param price of furniture
   * @param idFavoritePicture of picture
   */
  void putFurniture(int id, String description, String type, double price, int idFavoritePicture);

  /**
   * this method call updateAllFurnitureFromVisit from FurnitureDAO.
   * 
   * @param idVisit of furniture
   */
  void putAllFurnitureFromVisit(int idVisit);

  /**
   * this method call selectAllFurnitureType from FurnitureDAO.
   * 
   * @return a list of furniture by type
   */
  List<String> getAllFurnitureType();

  /**
   * this methode call selectFurnitureType from FurnitureDAO.
   * 
   * @param idType of type
   * @return libelle of type
   */
  String getFurnitureType(int idType);

  /**
   * This method will add a new furniture.
   * 
   * @param furniture to add
   * @return id of the furniture
   */
  int add(FurnitureDTO furniture);

  /**
   * This method call insertSales from FurnitureDAO.
   * 
   * @param furnitureId of furniture
   * @param userId of user
   */
  void addSales(int furnitureId, int userId);

  /**
   * this method call selectAllFurnitureById from FurnitureDAO.
   * 
   * @param idList list furniture id
   * @param etat of furniture
   * @return list furnitureDTO
   */
  List<FurnitureDTO> getAllFurnitureById(List<Integer> idList, String etat);

  /**
   * this method call getFurnitureByFilters from FurnitureDAO.
   * 
   * @param searchFilter used to find the returned furnitures
   * @return list of furnitureDTO
   */
  HashMap<Integer, List<String>> getFurnitureByFilters(String searchFilter);

  /**
   * This method call selectAllPurchaseFurnitureOfUser from FurnitureDAO.
   * 
   * @param idUser of user
   * @return list of furnitureDTO
   */
  List<FurnitureDTO> getAllPurchaseFurnitureOfUser(int idUser);

  /**
   * This method call selectAllSellFurnitureOfUser from FurnitureDAO.
   * 
   * @param idUser of user
   * @return list of furnitureDTO
   */
  List<FurnitureDTO> getAllSellFurnitureOfUser(int idUser);

  /**
   * This method call selectAllSellFurnitureOfUser from FurnitureDAO.
   * 
   * @param idUser of the user
   * @param search filter use to find furniture
   * @return list of furnitureDTO
   */
  List<FurnitureDTO> selectSearchSellFurnitureOfUser(int idUser, String search);
}
