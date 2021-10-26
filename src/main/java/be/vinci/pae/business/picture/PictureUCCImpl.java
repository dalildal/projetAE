package be.vinci.pae.business.picture;

import java.util.List;
import be.vinci.pae.data.DALServices;
import be.vinci.pae.data.picture.PictureDAO;
import be.vinci.pae.utils.BusinessException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

public class PictureUCCImpl implements PictureUCC {

  @Inject
  private PictureDAO pictureDAO;
  @Inject
  private DALServices dalServices;


  @Override
  public PictureDTO getPicture(int id) {
    dalServices.startTransaction();
    PictureDTO picture = null;
    try {
      picture = pictureDAO.findById(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }

    return picture;
  }

  @Override
  public List<PictureDTO> getAllPicturesById(int id) {
    dalServices.startTransaction();
    List<PictureDTO> list = null;
    try {
      list = pictureDAO.selectAllPicturesById(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }
    return list;
  }

  @Override
  public List<PictureDTO> getAllVisiblePicturesById(int id) {
    dalServices.startTransaction();
    List<PictureDTO> list = null;
    try {
      list = pictureDAO.selectAllVisiblePicturesById(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }
    return list;
  }

  @Override
  public int add(PictureDTO picture) {
    dalServices.startTransaction();
    int id = 0;
    try {
      id = pictureDAO.insert(picture);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }
    return id;
  }

  @Override
  public void putPicture(int id, int visibility) {
    dalServices.startTransaction();
    try {
      pictureDAO.updatePicture(id, visibility);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }
  }
}
