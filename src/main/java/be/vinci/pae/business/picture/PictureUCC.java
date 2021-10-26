package be.vinci.pae.business.picture;

import java.util.List;

public interface PictureUCC {

  /**
   * This method call findById from PictureDAO.
   * 
   * @param id of picture
   * @return PictureDTO with picture information
   */
  PictureDTO getPicture(int id);

  /**
   * This method call selectPicturesById from FurnitureDAO.
   * 
   * @param id of furniture
   * @return list pictureDTO
   */
  List<PictureDTO> getAllPicturesById(int id);

  /**
   * This method call selectAllVisiblePicturesById from FurnitureDAO.
   * 
   * @param id of furniture
   * @return list pictureDTO
   */
  List<PictureDTO> getAllVisiblePicturesById(int id);

  /**
   * This method will add a new picture.
   * 
   * @param picture to add
   * @return id
   */
  int add(PictureDTO picture);

  /**
   * this method call updatePicture from PictureDAO.
   * 
   * @param id of picture
   * @param visibility of picture
   */
  void putPicture(int id, int visibility);
}
