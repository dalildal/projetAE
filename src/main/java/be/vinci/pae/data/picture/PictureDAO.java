package be.vinci.pae.data.picture;

import java.util.List;
import be.vinci.pae.business.picture.PictureDTO;

public interface PictureDAO {

  /**
   * This method return picture if id exist.
   * 
   * @param id of picture
   * @return PictureDTO or null
   */
  PictureDTO findById(int id);

  /**
   * This method return list picture.
   * 
   * @param id of furniture
   * @return list pictureDTO
   */
  List<PictureDTO> selectAllPicturesById(int id);

  /**
   * This method return a list of visible furniture picture.
   * 
   * @param id of furniture
   * @return list pictureDTO
   */
  List<PictureDTO> selectAllVisiblePicturesById(int id);

  /**
   * This method return the id of the picture if the picture was well encoded.
   * 
   * @param picture to insert
   * @return id
   */
  int insert(PictureDTO picture);

  /**
   * this method update picture.
   * 
   * @param id of picture
   * @param visibility of picture
   */
  void updatePicture(int id, int visibility);
}
