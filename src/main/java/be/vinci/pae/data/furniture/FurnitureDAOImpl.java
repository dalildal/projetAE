package be.vinci.pae.data.furniture;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import be.vinci.pae.business.furniture.FurnitureDTO;
import be.vinci.pae.business.furniture.FurnitureFactory;
import be.vinci.pae.data.DALBackendServices;
import be.vinci.pae.utils.FatalException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

public class FurnitureDAOImpl implements FurnitureDAO {

  @Inject
  private DALBackendServices dalServices;

  @Inject
  private FurnitureFactory furnitureFactory;

  @Override
  public List<FurnitureDTO> selectAllFurniture(String type, List<String> etat) {
    String sql;

    if (type.equals("") && etat.isEmpty()) {
      sql = "SELECT * FROM antiquapp.meubles ORDER BY etat;";
    } else if (!type.equals("") && etat.isEmpty()) {
      sql = "SELECT * FROM antiquapp.meubles m, antiquapp.types t "
          + "WHERE t.id_type=m.id_type AND t.libelle = ?;";
    } else if (type.equals("") && !etat.isEmpty()) {
      sql = "SELECT * FROM antiquapp.meubles m WHERE ";
      for (int i = 0; i < etat.size(); i++) {
        if (i == etat.size() - 1) {
          sql += "m.etat = ?;";
        } else {
          sql += "m.etat = ? OR ";
        }
      }
    } else {
      sql = "SELECT * FROM antiquapp.meubles m, antiquapp.types t "
          + "WHERE t.id_type=m.id_type AND t.libelle = ? AND (";
      for (int i = 0; i < etat.size(); i++) {
        if (i == etat.size() - 1) {
          sql += "m.etat = ?);";
        } else {
          sql += "m.etat = ? OR ";
        }
      }
    }

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<FurnitureDTO> listFurniture = new ArrayList<FurnitureDTO>();

    try {

      if (!type.equals("") && etat.isEmpty()) {
        ps.setString(1, type);
      } else if (type.equals("") && !etat.isEmpty()) {
        for (int i = 0; i < etat.size(); i++) {
          ps.setString(i + 1, etat.get(i));
        }
      } else if (!type.equals("") && !etat.isEmpty()) {
        ps.setString(1, type);
        for (int i = 0; i < etat.size(); i++) {
          ps.setString(i + 2, etat.get(i));
        }
      }

      ps.executeQuery();

      ResultSet rs = ps.getResultSet();
      while (rs.next()) {
        FurnitureDTO furniture = this.furnitureFactory.getFurniture();

        setFurnitureData(furniture, rs);

        listFurniture.add(furniture);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return listFurniture;
  }

  @Override
  public FurnitureDTO findById(int id) {
    String sql = "SELECT * FROM antiquapp.meubles WHERE id_meuble = ?;";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    FurnitureDTO furniture = this.furnitureFactory.getFurniture();

    try {
      ps.setInt(1, id);
      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        setFurnitureData(furniture, rs);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return furniture;
  }

  private void setFurnitureData(FurnitureDTO furniture, ResultSet rs) throws SQLException {
    furniture.setIdFurniture(rs.getInt(1));
    furniture.setIdType(rs.getInt(2));
    furniture.setDescription(rs.getString(3));
    furniture.setPurchasePrice(rs.getDouble(4));
    furniture.setSalesPrice(rs.getDouble(5));
    furniture.setDealerPrice(rs.getDouble(6));
    furniture.setRecoveryDate(rs.getTimestamp(7));
    furniture.setDepositDate(rs.getTimestamp(8));
    furniture.setRemovalDate(rs.getTimestamp(9));
    furniture.setDeliveryDate(rs.getTimestamp(10));
    furniture.setFavoritePicture(rs.getInt(11));
    furniture.setState(rs.getString(12));
  }

  @Override
  public void updateFurnitureState(int id, String state) {
    String sql = "UPDATE antiquapp.meubles SET etat = ? WHERE id_meuble = ?;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      ps.setString(1, state);
      ps.setInt(2, id);

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public void updateFurnitureToSell(int id, String state, double price, double dealerPrice,
      String description) {
    String sql;
    if (state.equals("a vendre")) {
      sql = "UPDATE antiquapp.meubles SET etat = ?, prix_vente = ?, description = ? "
          + "WHERE id_meuble = ?;";
    } else {
      sql = "UPDATE antiquapp.meubles SET etat = ?, prix_antiquaire = ? WHERE id_meuble = ?;";
    }

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      if (state.equals("a vendre")) {
        ps.setString(1, state);
        ps.setDouble(2, price);
        ps.setString(3, description);
        ps.setInt(4, id);
      } else {
        ps.setString(1, state);
        ps.setDouble(2, dealerPrice);
        ps.setInt(3, id);
      }

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public void updateFurnitureSoldState(int id, String state, Timestamp date) {
    String sql = "";

    if (state.equals("disponible")) {
      sql = "UPDATE antiquapp.meubles SET etat = ?, date_depot = ? WHERE id_meuble = ?;";
    } else if (state.equals("livre")) {
      sql = "UPDATE antiquapp.meubles SET etat = ?, date_livraison = ? WHERE id_meuble = ?;";
    } else {
      sql = "UPDATE antiquapp.meubles SET etat = ?, date_retrait = ? WHERE id_meuble = ?;";
    }

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      ps.setString(1, state);
      ps.setTimestamp(2, date);
      ps.setInt(3, id);

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public void updateFurnitureToPurchase(int id, String state, double purchasePrice,
      Timestamp recoveryDate, String description) {
    String sql = null;
    if (!description.isEmpty()) {
      sql = "UPDATE antiquapp.meubles SET etat = ?, prix_achat = ?, date_recuperation = ?, "
          + "description = ? WHERE id_meuble = ?;";
    } else {
      sql = "UPDATE antiquapp.meubles SET etat = ?, prix_achat = ?, date_recuperation = ? "
          + "WHERE id_meuble = ?;";
    }


    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      if (!description.isEmpty()) {
        ps.setString(1, state);
        ps.setDouble(2, purchasePrice);
        ps.setTimestamp(3, recoveryDate);
        ps.setString(4, description);
        ps.setInt(5, id);
      } else {
        ps.setString(1, state);
        ps.setDouble(2, purchasePrice);
        ps.setTimestamp(3, recoveryDate);
        ps.setInt(4, id);
      }

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public void updateFurniture(int id, String description, int idType, double price,
      int idFavoritePicture) {
    String sql = null;

    if (description != "null") {
      sql = "UPDATE antiquapp.meubles SET description = ? WHERE id_meuble = ?;";
    } else if (idType != 0) {
      sql = "UPDATE antiquapp.meubles SET id_type = ? WHERE id_meuble = ?;";
    } else if (price != 0) {
      sql = "UPDATE antiquapp.meubles SET prix_vente = ? WHERE id_meuble = ?;";
    } else if (idFavoritePicture != -1) {
      sql = "UPDATE antiquapp.meubles SET photo_prefere = ? WHERE id_meuble = ?;";
    }

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      if (description != "null") {
        ps.setString(1, description);
      } else if (idType != 0) {
        ps.setInt(1, idType);
      } else if (price != 0) {
        ps.setDouble(1, price);
      } else if (idFavoritePicture != -1) {
        ps.setInt(1, idFavoritePicture);
      }
      ps.setInt(2, id);

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public void updateAllFurnitureFromVisit(int idVisit) {
    String sql =
        "UPDATE antiquapp.meubles m " + "SET etat = 'annule' " + "FROM antiquapp.meubles_visites v "
            + "WHERE m.id_meuble = v.id_meuble " + "AND v.id_visite = ?;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {
      ps.setInt(1, idVisit);

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public List<String> selectAllFurnitureType() {
    String sql = "SELECT t.libelle FROM antiquapp.types t";
    List<String> types = new ArrayList<String>();
    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {
      ps.executeQuery();
      ResultSet rs = ps.getResultSet();
      while (rs.next()) {
        types.add(rs.getString(1));
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return types;
  }

  @Override
  public String selectFurnitureType(int idType) {
    String sql = "SELECT t.libelle FROM antiquapp.types t WHERE id_type = ?";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    String type = null;
    try {
      ps.setInt(1, idType);
      ps.executeQuery();
      ResultSet rs = ps.getResultSet();

      while (rs.next()) {
        type = rs.getString(1);
      }

      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return type;
  }

  @Override
  public int selectIdFurnitureType(String type) {
    String sql = "SELECT id_type FROM antiquapp.types WHERE libelle = ?";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    int idType = 0;
    try {
      ps.setString(1, type);
      ps.executeQuery();
      ResultSet rs = ps.getResultSet();

      while (rs.next()) {
        idType = rs.getInt(1);
      }

      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return idType;
  }

  @Override
  public int insert(FurnitureDTO furniture) {
    String sql =
        "INSERT INTO antiquapp.meubles VALUES (DEFAULT,?,?,?,?,?,?,?,?,?,?,?) RETURNING id_meuble;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    int idFurniture = 0;

    try {
      ps.setInt(1, furniture.getIdType());
      ps.setString(2, furniture.getDescription());
      ps.setDouble(3, furniture.getPurchasePrice());
      ps.setDouble(4, furniture.getSalesPrice());
      ps.setDouble(5, furniture.getDealerPrice());
      ps.setTimestamp(6, furniture.getRecoveryDate());
      ps.setTimestamp(7, furniture.getDepositDate());
      ps.setTimestamp(8, furniture.getRemovalDate());
      ps.setTimestamp(9, furniture.getDeliveryDate());
      ps.setInt(10, furniture.getFavoritePicture());
      ps.setString(11, furniture.getState());

      ps.executeQuery();
      ResultSet rs = ps.getResultSet();

      while (rs.next()) {
        idFurniture = rs.getInt(1);
      }

      ps.close();
      rs.close();
    } catch (Exception e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return idFurniture;
  }

  @Override
  public void insertSales(int furnitureId, int userId, Timestamp date) {
    String sql;
    if (userId != 0) {
      sql = "INSERT INTO antiquapp.ventes VALUES (DEFAULT,?,?,?);";
    } else {
      sql = "INSERT INTO antiquapp.ventes VALUES (DEFAULT,NULL,?,?);";
    }



    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {
      if (userId != 0) {
        ps.setInt(1, userId);
        ps.setInt(2, furnitureId);
        ps.setTimestamp(3, date);
      } else {
        ps.setInt(1, furnitureId);
        ps.setTimestamp(2, date);
      }

      ps.execute();

      ps.close();
    } catch (Exception e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public List<FurnitureDTO> getAllFurnitureById(List<Integer> idList, String etat) {
    String sql = "SELECT * FROM antiquapp.meubles WHERE etat = ? AND (";
    for (int i = 0; i < idList.size(); i++) {
      if (i == idList.size() - 1) {
        sql += "id_meuble = ?);";
      } else {
        sql += "id_meuble = ? OR ";
      }
    }

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<FurnitureDTO> listFurniture = new ArrayList<FurnitureDTO>();

    try {

      ps.setString(1, etat);
      for (int i = 0; i < idList.size(); i++) {
        ps.setInt(i + 2, idList.get(i));
      }

      ps.executeQuery();

      ResultSet rs = ps.getResultSet();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        FurnitureDTO furniture = this.furnitureFactory.getFurniture();

        setFurnitureData(furniture, rs);

        listFurniture.add(furniture);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return listFurniture;
  }

  @Override
  public HashMap<Integer, List<String>> getFurnitureByFilters(String searchFilter) {
    String sql = null;
    boolean isNumeric = isNumeric(searchFilter);

    if (!isNumeric) {
      sql = "SELECT m.id_meuble, m.description, m.etat, m.photo_prefere, p.lien "
          + "FROM antiquapp.meubles m, antiquapp.types t, "
          + "antiquapp.meubles_visites mv, antiquapp.visites v, "
          + "antiquapp.utilisateurs u, antiquapp.photos p " + "WHERE m.id_meuble = mv.id_meuble "
          + "AND mv.id_visite = v.id_visite " + "AND t.id_type = m.id_type "
          + "AND v.id_utilisateur = u.id_utilisateur " + "AND m.photo_prefere = p.id_photo "
          + "AND m.etat <> 'inadequat'"
          + "AND (u.nom LIKE ? OR u.prenom LIKE ? OR t.libelle LIKE ?) ";
    } else {
      sql = "SELECT m.id_meuble, m.description, m.etat, m.photo_prefere, p.lien "
          + "FROM antiquapp.meubles m, antiquapp.photos p "
          + "WHERE  m.photo_prefere = p.id_photo AND m.prix_vente >= ? AND m.prix_vente <= ? "
          + "AND m.etat <> 'inadequat'";
    }

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<String> furnitureValues = new ArrayList<String>();
    HashMap<Integer, List<String>> furnitureMap = new HashMap<Integer, List<String>>();

    try {
      if (!isNumeric) {
        ps.setString(1, "%" + searchFilter + "%");
        ps.setString(2, "%" + searchFilter + "%");
        ps.setString(3, "%" + searchFilter + "%");
      } else {
        double price = Double.parseDouble(searchFilter);
        double minus = price - 50;
        double plus = price + 50;
        ps.setDouble(1, minus);
        ps.setDouble(2, plus);
      }

      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        furnitureValues.add(String.valueOf(rs.getInt(1)));
        furnitureValues.add(rs.getString(2));
        furnitureValues.add(rs.getString(3));
        furnitureValues.add(String.valueOf(rs.getInt(4)));
        furnitureValues.add(rs.getString(5));
        List<String> temp = new ArrayList<String>();
        temp.addAll(furnitureValues);
        furnitureMap.put(rs.getInt(1), temp);
        furnitureValues.clear();
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return furnitureMap;
  }

  private static boolean isNumeric(String strNum) {
    if (strNum == null) {
      return false;
    }
    try {
      Double.parseDouble(strNum);
    } catch (NumberFormatException nfe) {
      return false;
    }
    return true;
  }

  @Override
  public List<FurnitureDTO> selectAllPurchaseFurnitureOfUser(int idUser) {
    String sql = "SELECT m.* FROM antiquapp.meubles m, antiquapp.visites v, "
        + "antiquapp.meubles_visites mv WHERE m.id_meuble = mv.id_meuble "
        + "AND mv.id_visite = v.id_visite AND v.etat = 'confirmee' AND v.id_utilisateur = ? "
        + "AND  m.etat != 'inadequat'";

    return extracted(idUser, sql);
  }

  @Override
  public List<FurnitureDTO> selectAllSellFurnitureOfUser(int idUser) {
    String sql = "SELECT DISTINCT m.* FROM antiquapp.ventes v, antiquapp.meubles m "
        + "WHERE m.id_meuble = v.id_meuble AND v.id_utilisateur = ?";

    return extracted(idUser, sql);
  }

  @Override
  public List<FurnitureDTO> selectSearchSellFurnitureOfUser(int idUser, String search) {
    String sql =
        "SELECT DISTINCT m.* FROM antiquapp.ventes v, antiquapp.meubles m, antiquapp.types t "
            + "WHERE t.id_type = m.id_type AND m.id_meuble = v.id_meuble AND v.id_utilisateur = ? "
            + "AND (t.libelle LIKE ? OR m.description LIKE ?) ;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<FurnitureDTO> listFurniture = new ArrayList<FurnitureDTO>();

    try {
      ps.setInt(1, idUser);
      ps.setString(2, "%" + search + "%");
      ps.setString(3, "%" + search + "%");

      ps.executeQuery();
      ResultSet rs = ps.getResultSet();

      while (rs.next()) {
        FurnitureDTO furniture = this.furnitureFactory.getFurniture();
        setFurnitureData(furniture, rs);
        listFurniture.add(furniture);
      }
      ps.close();
      rs.close();
    } catch (Exception e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return listFurniture;
  }

  private List<FurnitureDTO> extracted(int idUser, String sql) {
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<FurnitureDTO> listFurniture = new ArrayList<FurnitureDTO>();
    try {
      ps.setInt(1, idUser);
      ps.executeQuery();
      ResultSet rs = ps.getResultSet();

      while (rs.next()) {
        FurnitureDTO furniture = this.furnitureFactory.getFurniture();
        setFurnitureData(furniture, rs);
        listFurniture.add(furniture);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return listFurniture;
  }

}
