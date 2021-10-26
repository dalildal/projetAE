package be.vinci.pae;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import be.vinci.pae.business.furniture.FurnitureDTO;
import be.vinci.pae.business.furniture.FurnitureFactory;
import be.vinci.pae.data.furniture.FurnitureDAO;
import be.vinci.pae.utils.FatalException;
import jakarta.inject.Inject;

public class MockFurnitureDAO implements FurnitureDAO {

  @Inject
  FurnitureFactory furnitureFactory;

  @Override
  public void updateFurnitureState(int id, String state) {
    // Not necessary for test.
  }

  @Override
  public FurnitureDTO findById(int id) {

    List<FurnitureDTO> furnitureList = new ArrayList<FurnitureDTO>(Arrays.asList(getFurnitureDB()));

    for (FurnitureDTO furnitureDB : furnitureList) {
      if (furnitureDB.getIdFurniture() == id) {
        return furnitureDB;
      }
    }

    return null;
  }

  @Override
  public void updateFurnitureToSell(int id, String state, double price, double dealerPrice,
      String description) {
    // Not necessary for test.
  }

  @Override
  public void updateFurnitureSoldState(int id, String state, Timestamp date) {
    // Not necessary for test.
  }

  @Override
  public List<String> selectAllFurnitureType() {
    List<String> furnitureList = new ArrayList<String>();
    furnitureList.add("armoire"); // id 0
    furnitureList.add("lit"); // id 1
    furnitureList.add("bureau"); // id 2
    return furnitureList;
  }

  @Override
  public List<FurnitureDTO> selectAllFurniture(String type, List<String> state) {
    List<String> typeList = selectAllFurnitureType();

    List<String> stateList = getStateData();

    if (!typeList.contains(type) && type != "") {
      throw new FatalException();
    }
    if (!state.isEmpty() && !stateList.contains(state.get(0))) {
      throw new FatalException();
    }

    List<FurnitureDTO> furnitureList = new ArrayList<FurnitureDTO>(Arrays.asList(getFurnitureDB()));

    if (type == "" && state.isEmpty()) {
      return furnitureList;
    }

    int idType = typeList.indexOf(type);

    List<FurnitureDTO> response = new ArrayList<FurnitureDTO>();
    for (FurnitureDTO furniture : furnitureList) {
      if (furniture.getIdType() == idType && furniture.getState().equals(state.get(0))) {
        response.add(furniture);
      }
    }

    return response;
  }

  private FurnitureDTO[] getFurnitureDB() {
    FurnitureDTO[] furnitureDBTest =
        {setFurnitureData(0, 0, "Meuble 1", 0, 0, 0, null, null, null, null, 0, "demande"),
            setFurnitureData(1, 1, "Meuble 2", 10.0, 0, 0, new Timestamp(2021 - 03 - 25), null,
                null, null, 0, "disponible"),
            setFurnitureData(2, 2, "Meuble 3", 15.0, 50.0, 50.0, null, null, null, null, 0,
                "a vendre")};

    return furnitureDBTest;
  }

  private FurnitureDTO setFurnitureData(int id, int idType, String description,
      double purchasePrice, double salesPrice, double dealerPrice, Timestamp recoveryDate,
      Timestamp depositDate, Timestamp removalDate, Timestamp deliveryDate, int favoritePicture,
      String state) {

    FurnitureDTO furniture = this.furnitureFactory.getFurniture();

    furniture.setIdFurniture(id);
    furniture.setIdType(idType);
    furniture.setDescription(description);
    furniture.setPurchasePrice(purchasePrice);
    furniture.setSalesPrice(salesPrice);
    furniture.setDealerPrice(dealerPrice);
    furniture.setRecoveryDate(recoveryDate);
    furniture.setDepositDate(depositDate);
    furniture.setRemovalDate(removalDate);
    furniture.setDeliveryDate(deliveryDate);
    furniture.setFavoritePicture(favoritePicture);
    furniture.setState(state);

    return furniture;
  }

  private List<String> getStateData() {

    List<String> stateList = new ArrayList<String>();
    stateList.add("demande");
    stateList.add("disponible");
    stateList.add("a vendre");

    return stateList;
  }

  @Override
  public void updateFurniture(int id, String description, int idType, double price,
      int idFavoritePicture) {
    // Not necessary for test.
  }

  @Override
  public String selectFurnitureType(int idType) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int selectIdFurnitureType(String type) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public int insert(FurnitureDTO furniture) {
    // TODO Auto-generated method stub
    return 0;
  }

  public void updateFurnitureToPurchase(int id, String state, double purchasePrice,
      Timestamp recoveryDate, String description) {
    // Not necessary for test.

  }

  @Override
  public List<FurnitureDTO> getAllFurnitureById(List<Integer> idList, String etat) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void insertSales(int furnitureId, int userId, Timestamp date) {
    // Not necessary for test.
  }

  @Override
  public HashMap<Integer, List<String>> getFurnitureByFilters(String searchFilter) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<FurnitureDTO> selectAllPurchaseFurnitureOfUser(int idUser) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<FurnitureDTO> selectAllSellFurnitureOfUser(int idUser) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public List<FurnitureDTO> selectSearchSellFurnitureOfUser(int idUser, String search) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void updateAllFurnitureFromVisit(int idVisit) {
    // TODO Auto-generated method stub

  }

}
