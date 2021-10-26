package be.vinci.pae.data.picture;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import be.vinci.pae.business.picture.PictureDTO;
import be.vinci.pae.business.picture.PictureFactory;
import be.vinci.pae.data.DALBackendServices;
import be.vinci.pae.utils.FatalException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

public class PictureDAOImpl implements PictureDAO {
  @Inject
  private DALBackendServices dalServices;

  @Inject
  private PictureFactory pictureFactory;

  @Override
  public PictureDTO findById(int id) {
    String sql = "SELECT * FROM antiquapp.photos WHERE id_photo = ?;";
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    PictureDTO picture = this.pictureFactory.getPicture();

    try {
      ps.setInt(1, id);
      ResultSet rs = ps.executeQuery();
      if (!rs.isBeforeFirst()) {
        return null;
      }
      while (rs.next()) {
        setPictureData(picture, rs);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return picture;
  }

  @Override
  public int insert(PictureDTO picture) {
    String sql = "INSERT INTO antiquapp.photos VALUES (DEFAULT,?,?,?) RETURNING id_photo;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    int id = 0;
    try {
      ps.setInt(1, picture.getIdFurniture());
      ps.setString(2, picture.getLink());
      ps.setInt(3, 0);

      ps.executeQuery();
      ResultSet rs = ps.getResultSet();

      while (rs.next()) {
        id = rs.getInt(1);
      }

      ps.close();
      rs.close();

    } catch (Exception e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return id;
  }

  @Override
  public List<PictureDTO> selectAllPicturesById(int id) {
    String sql = "SELECT * FROM antiquapp.photos WHERE id_meuble = ? ORDER BY id_photo;";

    return selectPicture(id, sql);
  }

  @Override
  public List<PictureDTO> selectAllVisiblePicturesById(int id) {
    String sql =
        "SELECT * FROM antiquapp.photos WHERE id_meuble = ? AND visibilite=1 ORDER BY id_photo;";

    return selectPicture(id, sql);
  }

  private List<PictureDTO> selectPicture(int id, String sql) {
    PreparedStatement ps = dalServices.getPreparedStatement(sql);
    List<PictureDTO> listPicture = new ArrayList<PictureDTO>();

    try {

      ps.setInt(1, id);

      ps.executeQuery();

      ResultSet rs = ps.getResultSet();
      while (rs.next()) {
        PictureDTO picture = this.pictureFactory.getPicture();

        setPictureData(picture, rs);

        listPicture.add(picture);
      }
      ps.close();
      rs.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
    return listPicture;
  }

  private void setPictureData(PictureDTO picture, ResultSet rs) throws SQLException {
    picture.setIdPicture(rs.getInt(1));
    picture.setIdFurniture(rs.getInt(2));
    picture.setLink(rs.getString(3));
    picture.setVisibility(rs.getInt(4));
  }

  @Override
  public void updatePicture(int id, int visibility) {
    String sql = "UPDATE antiquapp.photos SET visibilite = ?  WHERE id_photo = ?;";

    PreparedStatement ps = dalServices.getPreparedStatement(sql);

    try {

      ps.setInt(1, visibility);
      ps.setInt(2, id);

      ps.execute();
      ps.close();
    } catch (SQLException e) {
      throw new FatalException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }
}
