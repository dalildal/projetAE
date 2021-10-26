package be.vinci.pae.data.users;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import be.vinci.pae.business.users.UserDTO;
import be.vinci.pae.business.users.UserFactory;
import be.vinci.pae.data.DALBackendServices;
import be.vinci.pae.utils.FatalException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;


public class UserDAOImpl implements UserDAO {

  @Inject
  private DALBackendServices dalServices;

  @Inject
  private UserFactory userFactory;

  @Override
  public UserDTO findByPseudo(String pseudo) {
    String sql = "SELECT * FROM antiquapp.utilisateurs WHERE LOWER(pseudo) = LOWER(?);";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    UserDTO user = this.userFactory.getUser();

    try {
      ps.setString(1, pseudo);
      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        setUserData(user, rs);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return (UserDTO) user;
  }

  @Override
  public HashMap<Integer, List<String>> findUsers(String searchFilter) {
    String sql = null;
    boolean isNumeric = isNumeric(searchFilter);

    if (!isNumeric) {
      sql =
          "SELECT u.id_utilisateur, u.pseudo, u.nom, " + " u.prenom, u.commune, count(v.id_meuble) "
              + " FROM antiquapp.utilisateurs u LEFT OUTER JOIN "
              + " antiquapp.ventes v ON u.id_utilisateur = v.id_utilisateur "
              + " WHERE nom LIKE ? OR commune LIKE ? "
              + " GROUP BY u.id_utilisateur, v.id_utilisateur; ";

    } else {
      sql = "SELECT u.id_utilisateur, u.pseudo, u.nom, u.prenom, u.commune, count(v.id_meuble) "
          + "FROM antiquapp.utilisateurs u LEFT OUTER JOIN antiquapp.ventes v ON "
          + "u.id_utilisateur = v.id_utilisateur WHERE code_postal = ? "
          + "GROUP BY u.id_utilisateur;";
    }

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<String> userValues = new ArrayList<String>();
    HashMap<Integer, List<String>> userMap = new HashMap<Integer, List<String>>();

    try {
      if (!isNumeric) {
        ps.setString(1, "%" + searchFilter + "%");
        ps.setString(2, "%" + searchFilter + "%");
      } else {
        ps.setInt(1, Integer.parseInt(searchFilter));
      }

      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        userValues.add(rs.getString(2));
        userValues.add(rs.getString(3));
        userValues.add(rs.getString(4));
        userValues.add(rs.getString(5));
        userValues.add(String.valueOf(rs.getInt(6)));
        List<String> temp = new ArrayList<String>();
        temp.addAll(userValues);
        userMap.put(rs.getInt(1), temp);
        userValues.clear();
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return userMap;
  }

  @Override
  public int getNbrFurniture(int idUser) {

    String sql = null;

    sql = "SELECT count(mv.id_meuble) FROM antiquapp.meubles m, antiquapp.visites v, "
        + "antiquapp.meubles_visites mv WHERE m.id_meuble = mv.id_meuble "
        + "AND mv.id_visite = v.id_visite AND v.etat = 'confirmee' AND v.id_utilisateur = ? "
        + "AND  m.etat != 'inadequat';";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    int nbrFurniture = -1;

    try {
      ps.setInt(1, idUser);
      ResultSet rs = ps.executeQuery();

      if (!rs.isBeforeFirst()) {
        return -1;
      }

      if (rs.next()) {
        nbrFurniture = rs.getInt(1);

      }

      ps.close();
      rs.close();
    } catch (Exception e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return nbrFurniture;
  }

  /**
   * Source : https://www.baeldung.com/java-check-string-number.
   * 
   * @param strNum checked if numeric or not
   * @return true or false
   */
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
  public UserDTO findByEmail(String email) {
    String sql = "SELECT * FROM antiquapp.utilisateurs WHERE email = ?;";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    UserDTO user = this.userFactory.getUser();

    try {
      ps.setString(1, email);
      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        setUserData(user, rs);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return (UserDTO) user;
  }

  @Override
  public UserDTO findById(int id) {
    String sql = "SELECT * FROM antiquapp.utilisateurs WHERE id_utilisateur = ?;";
    return setIdUser(id, sql);
  }

  @Override
  public boolean insert(UserDTO user) {

    String sql = "INSERT INTO antiquapp.utilisateurs VALUES "
        + "(DEFAULT,?,?,?,?,?,?,?,?,?,?,?,?,DEFAULT,DEFAULT);";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {
      ps.setString(1, user.getPseudo());
      ps.setString(2, user.getPwd());
      ps.setString(3, user.getLastName());
      ps.setString(4, user.getFirstName());
      ps.setString(5, user.getStreet());
      ps.setString(6, user.getNum());
      ps.setString(7, user.getBox());
      ps.setInt(8, user.getPostalCode());
      ps.setString(9, user.getMunicipality());
      ps.setString(10, user.getCountry());
      ps.setString(11, user.getEmail());
      ps.setTimestamp(12, Timestamp.valueOf(LocalDateTime.now()));

      ps.execute();
      ps.close();

    } catch (Exception e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return true;
  }

  @Override
  public List<UserDTO> selectAllUser(int etat) {
    String sql = "";
    if (etat == -1) {
      sql = "SELECT * FROM antiquapp.utilisateurs;";
    } else {
      sql = "SELECT * FROM antiquapp.utilisateurs WHERE etat = ?;";
    }
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<UserDTO> listUser = new ArrayList<UserDTO>();

    try {
      if (etat != -1) {
        ps.setInt(1, etat);
      }

      ps.executeQuery();

      ResultSet rs = ps.getResultSet();
      while (rs.next()) {
        UserDTO user = this.userFactory.getUser();
        setUserData(user, rs);
        listUser.add(user);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return listUser;
  }

  @Override
  public void updateUserStatut(int id) {
    String sql = "UPDATE antiquapp.utilisateurs SET etat = 1 WHERE id_utilisateur = ?;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      ps.setInt(1, id);

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void updateUserType(int id, String type) {
    String sql = "UPDATE antiquapp.utilisateurs SET type = ? WHERE id_utilisateur = ?;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      ps.setString(1, type);
      ps.setInt(2, id);

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void deleteUser(int id) {
    String sql = "DELETE FROM antiquapp.utilisateurs WHERE id_utilisateur = ?;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      ps.setInt(1, id);

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public UserDTO selectFurnitureBuyer(int id) {
    String sql = "SELECT u.* FROM antiquapp.utilisateurs u, antiquapp.ventes v "
        + "WHERE u.id_utilisateur = v.id_utilisateur AND v.id_meuble = ?";
    return setIdUser(id, sql);
  }

  @Override
  public UserDTO selectFurnitureSeller(int id) {
    String sql = "SELECT u.* FROM antiquapp.utilisateurs u, antiquapp.visites v, "
        + "antiquapp.meubles_visites mv "
        + "WHERE u.id_utilisateur = v.id_utilisateur AND v.id_visite = mv.id_visite "
        + "AND v.etat='confirmee' " + "AND mv.id_meuble = ?";
    return setIdUser(id, sql);
  }

  private void setUserData(UserDTO user, ResultSet rs) throws SQLException {
    user.setId(rs.getInt(1));
    user.setPseudo(rs.getString(2));
    user.setPwd(rs.getString(3));
    user.setLastName(rs.getString(4));
    user.setFirstName(rs.getString(5));
    user.setStreet(rs.getString(6));
    user.setNum(rs.getString(7));
    user.setBox(rs.getString(8));
    user.setPostalCode(rs.getInt(9));
    user.setMunicipality(rs.getString(10));
    user.setCountry(rs.getString(11));
    user.setEmail(rs.getString(12));
    user.setDateRegister(rs.getTimestamp(13));
    user.setType(rs.getString(14));
    user.setStatut(rs.getInt(15));
  }

  private UserDTO setIdUser(int id, String sql) {
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    UserDTO user = this.userFactory.getUser();

    try {
      ps.setInt(1, id);
      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        setUserData(user, rs);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return (UserDTO) user;
  }
}
