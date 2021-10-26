package be.vinci.pae.business.options;

import java.util.List;

public interface OptionUCC {

  /**
   * This method will create a new option.
   * 
   * @param optionDTO to create
   * @return true if option is well create
   */
  boolean createOption(OptionDTO optionDTO);

  /**
   * This method call updateOption from OptionDAO.
   * 
   * @param idUser of option
   * @param idFurniture of option
   * @return OptionDTO
   */
  OptionDTO putOption(int idUser, int idFurniture);

  /**
   * This method call selectAllOption from OptionDAO.
   * 
   * @param id of user
   * @return listOption
   */
  List<OptionDTO> getAllOptionValid(int id);

  /**
   * This method return valid option.
   * 
   * @param idFurniture of furniture
   * @return OptionDTO
   */
  OptionDTO getOption(int idFurniture);

}
