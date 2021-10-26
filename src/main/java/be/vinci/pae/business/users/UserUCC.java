package be.vinci.pae.business.users;

import java.util.HashMap;
import java.util.List;


public interface UserUCC {

  /**
   * This method will check if pseudo and password is correct.
   * 
   * @param pseudo of user
   * @param password of user
   * @return userDTO with user information
   */
  UserDTO login(String pseudo, String password);

  /**
   * This method call findById from UserDAO.
   * 
   * @param id of user
   * @return userDTO with user information
   */
  UserDTO getUser(int id);

  /**
   * This method call findByEmail from UserDAO.
   * 
   * @param email of user
   * @return userDTO with user informatio n
   */
  UserDTO getUserByEmail(String email);

  /**
   * This method will register a new user.
   * 
   * @param user to register
   * @return true if user is well registered
   */
  boolean register(UserDTO user);


  /**
   * This method call selectAllUser from UserDAO.
   * 
   * @param etat of user
   * @return userDTO with user information
   */
  List<UserDTO> getAllUser(int etat);

  /**
   * this method call updateUserStatut from UserDAO.
   * 
   * @param id of user
   */
  void putUserStatut(int id);

  /**
   * this method call updateUserType from UserDAO.
   * 
   * @param id of user
   * @param type of user
   */
  void putUserType(int id, String type);

  /**
   * this method call deleteUser from UserDAO.
   * 
   * @param id of user
   */
  void removeUser(int id);

  /**
   * This method call findUsers from UserDAO.
   * 
   * @param searchFilter filter needed to search the users
   * @return list of user according to the search filter
   */
  HashMap<Integer, List<String>> getUsers(String searchFilter);

  /**
   * This method call selectFurnitureBuyer from UserDAO.
   * 
   * @param id of user
   * @return userDTO with user information
   */
  UserDTO getUserFurnitureBuyer(int id);

  /**
   * This method call selectFurnitureSeller from UserDAO.
   * 
   * @param id of user
   * @return userDTO with user information
   */
  UserDTO getUserFurnitureSeller(int id);

}
