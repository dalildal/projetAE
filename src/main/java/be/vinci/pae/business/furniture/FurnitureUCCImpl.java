package be.vinci.pae.business.furniture;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import be.vinci.pae.data.DALServices;
import be.vinci.pae.data.furniture.FurnitureDAO;
import be.vinci.pae.data.users.UserDAO;
import be.vinci.pae.utils.BusinessException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

public class FurnitureUCCImpl implements FurnitureUCC {

  @Inject
  private FurnitureDAO furnitureDAO;

  @Inject
  private DALServices dalServices;

  @Inject
  private UserDAO userDAO;

  @Override
  public List<FurnitureDTO> getAllFurniture(String type, List<String> etat) {
    dalServices.startTransaction();
    List<FurnitureDTO> listFurniture = null;
    try {
      listFurniture = furnitureDAO.selectAllFurniture(type, etat);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return listFurniture;
  }

  @Override
  public FurnitureDTO getFurniture(int id) {
    dalServices.startTransaction();
    FurnitureDTO furniture = null;
    try {
      furniture = furnitureDAO.findById(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return furniture;
  }

  @Override
  public void putFurnitureState(int id, String state, String date, double price, double dealerPrice,
      double purchasePrice, String recoveryDate, String description) {

    dalServices.startTransaction();

    if (state.equals("a vendre") || state.equals("vendu")) {
      furnitureDAO.updateFurnitureToSell(id, state, price, dealerPrice, description);
      dalServices.commitTransaction();
    } else if (state.equals("disponible") || state.equals("livre") || state.equals("emporte")) {
      Timestamp timestamp = parseDate(date);
      furnitureDAO.updateFurnitureSoldState(id, state, timestamp);
      dalServices.commitTransaction();

    } else if (state.equals("achete")) {
      Timestamp timestamp = parseDate(recoveryDate);
      furnitureDAO.updateFurnitureToPurchase(id, state, purchasePrice, timestamp, description);
      dalServices.commitTransaction();
    } else {
      furnitureDAO.updateFurnitureState(id, state);
      dalServices.commitTransaction();
    }
  }

  private Timestamp parseDate(String date) {
    Timestamp timestamp = null;
    try {
      SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
      Date parsedDate = dateFormat.parse(date);
      timestamp = new Timestamp(parsedDate.getTime());
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return timestamp;
  }

  @Override
  public List<String> getAllFurnitureType() {
    dalServices.startTransaction();
    List<String> list = null;
    try {
      list = furnitureDAO.selectAllFurnitureType();
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return list;
  }

  @Override
  public String getFurnitureType(int idType) {
    dalServices.startTransaction();
    String type = null;
    try {
      type = furnitureDAO.selectFurnitureType(idType);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return type;
  }

  @Override
  public void putFurniture(int id, String description, String type, double price,
      int idFavoritePicture) {
    dalServices.startTransaction();
    int idType = 0;
    if (type != "") {
      idType = furnitureDAO.selectIdFurnitureType(type);
    }
    try {
      furnitureDAO.updateFurniture(id, description, idType, price, idFavoritePicture);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void putAllFurnitureFromVisit(int idVisit) {
    dalServices.startTransaction();
    try {
      furnitureDAO.updateAllFurnitureFromVisit(idVisit);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public int add(FurnitureDTO furniture) {
    dalServices.startTransaction();
    int idFurniture;
    try {
      furniture.setState("demande");
      idFurniture = furnitureDAO.insert(furniture);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return idFurniture;
  }

  @Override
  public void addSales(int furnitureId, int userId) {
    dalServices.startTransaction();
    try {
      LocalDateTime now = LocalDateTime.now();
      Timestamp date = Timestamp.valueOf(now);
      furnitureDAO.insertSales(furnitureId, userId, date);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public List<FurnitureDTO> getAllFurnitureById(List<Integer> idList, String etat) {
    dalServices.startTransaction();
    List<FurnitureDTO> list = furnitureDAO.getAllFurnitureById(idList, etat);
    if (list == null) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Il n'y a pas de meuble a gérer", Status.INTERNAL_SERVER_ERROR);
    }
    dalServices.commitTransaction();
    return list;
  }

  @Override
  public HashMap<Integer, List<String>> getFurnitureByFilters(String searchFilter) {
    dalServices.startTransaction();
    HashMap<Integer, List<String>> map = furnitureDAO.getFurnitureByFilters(searchFilter);
    if (map == null) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Aucun meuble ne correspond � votre recherche !",
          Status.INTERNAL_SERVER_ERROR);
    }
    dalServices.commitTransaction();
    return map;
  }

  @Override
  public List<FurnitureDTO> getAllPurchaseFurnitureOfUser(int idUser) {
    dalServices.startTransaction();
    List<FurnitureDTO> list = furnitureDAO.selectAllPurchaseFurnitureOfUser(idUser);
    if (list == null || userDAO.findById(idUser) == null) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Aucun meuble a été acheté à ce client !",
          Status.INTERNAL_SERVER_ERROR);
    }
    dalServices.commitTransaction();
    return list;
  }

  @Override
  public List<FurnitureDTO> getAllSellFurnitureOfUser(int idUser) {
    dalServices.startTransaction();
    List<FurnitureDTO> list = furnitureDAO.selectAllSellFurnitureOfUser(idUser);
    if (list == null || userDAO.findById(idUser) == null) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Aucun meuble a été vendu à ce client !",
          Status.INTERNAL_SERVER_ERROR);
    }
    dalServices.commitTransaction();
    return list;
  }

  @Override
  public List<FurnitureDTO> selectSearchSellFurnitureOfUser(int idUser, String search) {
    dalServices.startTransaction();
    List<FurnitureDTO> list = furnitureDAO.selectSearchSellFurnitureOfUser(idUser, search);
    if (list == null || userDAO.findById(idUser) == null) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Aucun meuble a été vendu à ce client !",
          Status.INTERNAL_SERVER_ERROR);
    }
    dalServices.commitTransaction();
    return list;
  }

}
