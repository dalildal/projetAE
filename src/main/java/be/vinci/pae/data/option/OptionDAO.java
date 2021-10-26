package be.vinci.pae.data.option;

import java.util.List;
import be.vinci.pae.business.options.OptionDTO;

public interface OptionDAO {

  /**
   * This method return true or false if option was well create.
   * 
   * @param option to create
   * @return true or false
   */
  boolean insert(OptionDTO option);

  /**
   * This method update status furniture.
   * 
   * @param idFurniture of furniture
   * @param status of furniture
   */
  void updateFurnitureStatus(int idFurniture, String status);

  /**
   * This method return option in relation to the furniture and the user.
   * 
   * @param idFurniture of furniture
   * @param idUser of user
   * @return OptionDTO
   */
  OptionDTO findOption(int idFurniture, int idUser);

  /**
   * This method update option time.
   * 
   * @param idFurniture of furniture
   * @param idUser of user
   * @param time of option
   */
  void updateOption(int idFurniture, int idUser, int time);

  /**
   * This method check if an option is valid.
   * 
   * @param idFurniture of furiture
   * @return userid or -1
   */
  int checkOptionValid(int idFurniture);

  /**
   * This method return the list of all options.
   * 
   * @param id of user
   * @return listOption
   */
  List<OptionDTO> selectAllOptionValid(int id);
}
