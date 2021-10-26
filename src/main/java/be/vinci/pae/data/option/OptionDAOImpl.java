package be.vinci.pae.data.option;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import be.vinci.pae.business.options.OptionDTO;
import be.vinci.pae.business.options.OptionFactory;
import be.vinci.pae.data.DALBackendServices;
import be.vinci.pae.utils.FatalException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

public class OptionDAOImpl implements OptionDAO {

  @Inject
  private DALBackendServices dalServices;

  @Inject
  private OptionFactory optionFactory;

  @Override
  public boolean insert(OptionDTO option) {
    String sql = "INSERT INTO antiquapp.options VALUES (?,?,?,?);";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {
      ps.setInt(1, option.getIdUser());
      ps.setInt(2, option.getIdFurniture());
      ps.setInt(3, option.getTime());
      ps.setTimestamp(4, Timestamp.valueOf(LocalDateTime.now()));

      ps.execute();
      ps.close();

    } catch (Exception e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return true;
  }

  @Override
  public OptionDTO findOption(int idFurniture, int idUser) {
    String sql = "SELECT * FROM antiquapp.options WHERE id_meuble = ? AND id_utilisateur = ?;";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    OptionDTO option = this.optionFactory.getOption();

    try {
      ps.setInt(1, idFurniture);
      ps.setInt(2, idUser);
      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        setOptionData(option, rs);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return option;
  }

  @Override
  public int checkOptionValid(int idFurniture) {
    String sql = "SELECT * FROM antiquapp.options WHERE id_meuble = ?;";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {
      ps.setInt(1, idFurniture);
      ps.executeQuery();

      ResultSet rs = ps.getResultSet();
      while (rs.next()) {
        OptionDTO option = optionFactory.getOption();

        setOptionData(option, rs);

        LocalDateTime dateTimeDay = addDays(option);

        if (dateTimeDay.isAfter(LocalDateTime.now())) {
          return rs.getInt(1);
        }
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return -1;
  }

  private LocalDateTime addDays(OptionDTO option) {
    LocalDateTime dateTime = option.getDateOption().toLocalDateTime();
    LocalDateTime dateTimeDay = dateTime.plusDays(option.getTime());
    return dateTimeDay;
  }

  private void setOptionData(OptionDTO option, ResultSet rs) throws SQLException {
    option.setIdUser(rs.getInt(1));
    option.setIdFurniture(rs.getInt(2));
    option.setTime(rs.getInt(3));
    option.setDateOption(rs.getTimestamp(4));
  }

  @Override
  public void updateOption(int idFurniture, int idUser, int time) {
    String sql =
        "UPDATE antiquapp.options SET duree = ? WHERE id_meuble = ? AND id_utilisateur = ?;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      ps.setInt(1, time);
      ps.setInt(2, idFurniture);
      ps.setInt(3, idUser);

      ps.execute();
      ps.close();

    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public void updateFurnitureStatus(int idFurniture, String status) {
    String sql = "UPDATE antiquapp.meubles SET etat = ? WHERE id_meuble = ?;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      ps.setString(1, status);
      ps.setInt(2, idFurniture);

      ps.execute();
      ps.close();

    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public List<OptionDTO> selectAllOptionValid(int id) {
    String sql = "SELECT * FROM antiquapp.options WHERE id_utilisateur = ?;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<OptionDTO> listOption = new ArrayList<OptionDTO>();

    try {
      ps.setInt(1, id);

      ps.executeQuery();

      ResultSet rs = ps.getResultSet();
      while (rs.next()) {
        OptionDTO option = this.optionFactory.getOption();
        setOptionData(option, rs);

        LocalDateTime dateTimeDay = addDays(option);

        if (dateTimeDay.isAfter(LocalDateTime.now())) {
          listOption.add(option);
        }

      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return listOption;
  }

}
