package be.vinci.pae.data.furniture;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import be.vinci.pae.business.furniture.FurnitureDTO;

public interface FurnitureDAO {

  /**
   * This method return furniture by type.
   * 
   * @param type of showed furniture
   * @param etat list of furniture's states
   * @return list of furniture filtered by type
   */
  List<FurnitureDTO> selectAllFurniture(String type, List<String> etat);

  /**
   * this method update furniture's state.
   * 
   * @param id of furniture
   * @param state of furniture
   */
  void updateFurnitureState(int id, String state);

  /**
   * this method update furniture to sell.
   * 
   * @param id of furniture
   * @param state of furniture
   * @param price of furniture
   * @param dealerPrice of furniture
   * @param description of furniture
   */
  void updateFurnitureToSell(int id, String state, double price, double dealerPrice,
      String description);

  /**
   * this method update furniture with date.
   * 
   * @param id of furniture
   * @param state of furniture
   * @param date is deleveryDate or sellDate or recoveryDate
   */
  void updateFurnitureSoldState(int id, String state, Timestamp date);

  /**
   * this method update furniture.
   * 
   * @param id of furniture
   * @param description of furniture
   * @param idType of furniture
   * @param price of furniture
   * @param idFavoritePicture of picture
   */
  void updateFurniture(int id, String description, int idType, double price, int idFavoritePicture);

  /**
   * this method update furniture to purchase.
   * 
   * @param id of furniture
   * @param state of furniture
   * @param purchasePrice of furniture
   * @param recoveryDate of furniture
   * @param description of furniture
   */
  void updateFurnitureToPurchase(int id, String state, double purchasePrice, Timestamp recoveryDate,
      String description);

  /**
   * This method update all furniture from visit.
   * 
   * @param idVisit of the furniture
   */
  void updateAllFurnitureFromVisit(int idVisit);

  /**
   * This method return furniture if id exist.
   * 
   * @param id of furniture
   * @return FurnitureDTO or null
   */
  FurnitureDTO findById(int id);

  /**
   * This method get all types in a list of strings.
   * 
   * @return a list of string
   */
  List<String> selectAllFurnitureType();

  /**
   * This method get type of furniture.
   * 
   * @param idType of Type
   * @return libelle of Type
   */
  String selectFurnitureType(int idType);

  /**
   * This method get id of type.
   * 
   * @param type name of type
   * @return id of type
   */
  int selectIdFurnitureType(String type);

  /**
   * This method return the id of the furniture if the furniture was well encoded.
   * 
   * @param furniture to encode
   * @return id of the furniture
   */
  int insert(FurnitureDTO furniture);

  /**
   * This method add an sales in table.
   * 
   * @param furnitureId of furniture
   * @param userId of user
   * @param date today
   */
  void insertSales(int furnitureId, int userId, Timestamp date);

  /**
   * This method return list furniture.
   * 
   * @param idList list furniture id
   * @param etat of furniture
   * @return list furnitureDTO
   */
  List<FurnitureDTO> getAllFurnitureById(List<Integer> idList, String etat);


  /**
   * This method return a list of furnitures matching with the filters.
   *
   * @param searchFilter used to find the returned furnitures
   * @return list of furnitureDTO
   */
  HashMap<Integer, List<String>> getFurnitureByFilters(String searchFilter);


  /**
   * This method return a list of furniture bought by Satcho at a user.
   * 
   * @param idUser of the user
   * @return list of furnitureDTO
   */
  List<FurnitureDTO> selectAllPurchaseFurnitureOfUser(int idUser);

  /**
   * This method return a list of furniture sell by Satcho at a user.
   * 
   * @param idUser of the user
   * @return list of furnitureDTO
   */
  List<FurnitureDTO> selectAllSellFurnitureOfUser(int idUser);

  /**
   * This method return list of furniture sell by Satcho to a user using the filter in param.
   * 
   * @param idUser of the user
   * @param search filter use to find furniture
   * @return list of furnitureDTO
   */
  List<FurnitureDTO> selectSearchSellFurnitureOfUser(int idUser, String search);
}
