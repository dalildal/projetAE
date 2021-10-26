package be.vinci.pae.data.users;

import java.util.HashMap;
import java.util.List;
import be.vinci.pae.business.users.UserDTO;

public interface UserDAO {

  /**
   * This method return user if pseudo exist.
   * 
   * @param pseudo of user
   * @return userDTO or null
   */
  UserDTO findByPseudo(String pseudo);

  /**
   * This method return user if email exist.
   * 
   * @param email of user
   * @return userDTO or null
   */
  UserDTO findByEmail(String email);

  /**
   * This method return user if id exist.
   * 
   * @param id of user
   * @return userDTO or null
   */
  UserDTO findById(int id);

  /**
   * This method return true or false if user was well registered.
   * 
   * @param user to register
   * @return true or false
   */
  boolean insert(UserDTO user);

  /**
   * This method return the list of all users.
   * 
   * @param etat of user
   * @return listUser
   */
  List<UserDTO> selectAllUser(int etat);

  /**
   * This method update user statut.
   * 
   * @param id of user
   */
  void updateUserStatut(int id);

  /**
   * this method update user type.
   * 
   * @param id of user
   * @param type of user
   */
  void updateUserType(int id, String type);

  /**
   * this method delete user.
   * 
   * @param id of user
   */
  void deleteUser(int id);

  /**
   * This method return a list of users if they match the filter in params.
   * 
   * @param searchFilter filter needed to search the users
   * @return list of user according to the search filter
   */
  HashMap<Integer, List<String>> findUsers(String searchFilter);

  /**
   * This method return the user who bought the specified furniture.
   * 
   * @param id of the furniture
   * @return userDTO or null
   */
  UserDTO selectFurnitureBuyer(int id);

  /**
   * This method return the user who sell the specified furniture.
   * 
   * @param id of the furniture
   * @return userDTO or null
   */
  UserDTO selectFurnitureSeller(int id);

  /**
   * This method return the number of furniture sold to mr satcho.
   * 
   * @param idUser wgo sold the furnitures
   * @return nbr of sold furniture
   * 
   */
  int getNbrFurniture(int idUser);


}
