package be.vinci.pae.data.visit;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import be.vinci.pae.business.visit.VisitDTO;
import be.vinci.pae.business.visit.VisitFactory;
import be.vinci.pae.data.DALBackendServices;
import be.vinci.pae.utils.FatalException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

public class VisitDAOImpl implements VisitDAO {
  @Inject
  private DALBackendServices dalServices;

  @Inject
  private VisitFactory visitFactory;

  @Override
  public int insert(VisitDTO visit) {
    String sql = "INSERT INTO antiquapp.visites VALUES "
        + "(DEFAULT,?,?,?,?,?,?,?,?,?,?,?,?) RETURNING id_visite;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    int idVisite = 0;

    try {
      ps.setInt(1, visit.getIdUser());
      ps.setTimestamp(2, Timestamp.valueOf(LocalDateTime.now()));
      ps.setTimestamp(3, visit.getVisitDate());
      ps.setString(4, visit.getTimePeriod());
      ps.setString(5, visit.getStreetVisit());
      ps.setString(6, visit.getNumVisit());
      ps.setString(7, visit.getBoxVisit());
      ps.setInt(8, visit.getPostalCodeVisit());
      ps.setString(9, visit.getMunicipalityVisit());
      ps.setString(10, visit.getCountryVisit());
      ps.setString(11, "attente");
      ps.setString(12, visit.getCancellationReason());

      ps.executeQuery();
      ResultSet rs = ps.getResultSet();

      while (rs.next()) {
        idVisite = rs.getInt(1);
      }

      ps.close();
      rs.close();
    } catch (Exception e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return idVisite;
  }

  @Override
  public boolean linkVisit(int idVisit, int idFurniture) {
    return false;
  }

  @Override
  public List<VisitDTO> selectAllVisit(String etat) {
    String sql = "";
    if (etat == "") {
      sql = "SELECT * FROM antiquapp.visites;";
    } else {
      sql = "SELECT * FROM antiquapp.visites WHERE etat = ?;";
    }
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<VisitDTO> listVisit = new ArrayList<VisitDTO>();

    try {
      if (etat != "") {
        ps.setString(1, etat);
      }

      ps.executeQuery();

      ResultSet rs = ps.getResultSet();
      while (rs.next()) {
        VisitDTO visit = this.visitFactory.getVisit();
        setVisitData(visit, rs);
        listVisit.add(visit);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return listVisit;
  }

  @Override
  public VisitDTO findById(int id) {
    String sql = "SELECT * FROM antiquapp.visites WHERE id_visite = ?;";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    VisitDTO visit = visitFactory.getVisit();

    try {
      ps.setInt(1, id);
      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        setVisitData(visit, rs);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return visit;
  }

  private void setVisitData(VisitDTO visit, ResultSet rs) throws SQLException {
    visit.setIdVisit(rs.getInt(1));
    visit.setIdUser(rs.getInt(2));
    visit.setCreationDate(rs.getTimestamp(3));
    visit.setVisitDate(rs.getTimestamp(4));
    visit.setTimePeriod(rs.getString(5));
    visit.setStreetVisit(rs.getString(6));
    visit.setNumVisit(rs.getString(7));
    visit.setBoxVisit(rs.getString(8));
    visit.setPostalCodeVisit(rs.getInt(9));
    visit.setMunicipalityVisit(rs.getString(10));
    visit.setCountryVisit(rs.getString(11));
    visit.setState(rs.getString(12));
    visit.setCancellationReason(rs.getString(13));
  }

  @Override
  public void updateVisit(int id, Timestamp date, String raison) {
    String sql = "";

    if (date != null) {
      sql = "UPDATE antiquapp.visites SET date_visite = ?, etat = 'confirmee' WHERE id_visite = ?;";
    } else if (!raison.equals("null")) {
      sql = "UPDATE antiquapp.visites SET raison_annulation = ?, etat = 'annulee' "
          + "WHERE id_visite = ?;";
    }

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      if (date != null) {
        ps.setTimestamp(1, date);
      } else if (!raison.equals("null")) {
        ps.setString(1, raison);
      }
      ps.setInt(2, id);

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public List<Integer> selectListIdFurniture(int id) {
    String sql = "SELECT * FROM antiquapp.meubles_visites WHERE id_visite = ?;";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<Integer> listIdFurniture = new ArrayList<Integer>();

    try {
      ps.setInt(1, id);
      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        listIdFurniture.add(rs.getInt(2));
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return listIdFurniture;
  }

  @Override
  public boolean insertFurnitureVisit(int idVisit, int idFurniture) {
    String sql = "INSERT INTO antiquapp.meubles_visites VALUES (?,?);";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {
      ps.setInt(1, idVisit);
      ps.setInt(2, idFurniture);

      ps.execute();
      ps.close();
    } catch (Exception e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return true;
  }

}
