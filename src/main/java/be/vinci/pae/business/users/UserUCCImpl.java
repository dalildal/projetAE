package be.vinci.pae.business.users;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import be.vinci.pae.data.DALServices;
import be.vinci.pae.data.users.UserDAO;
import be.vinci.pae.utils.BusinessException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

public class UserUCCImpl implements UserUCC {

  @Inject
  private UserDAO userDAO;

  @Inject
  private DALServices dalServices;

  @Override
  public UserDTO login(String pseudo, String password) {

    dalServices.startTransaction();

    UserDTO user = userDAO.findByPseudo(pseudo);
    User userCast = (User) user;

    if (user == null) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Pseudo ou mot de passe incorrect.", Status.NOT_ACCEPTABLE);
    }

    if (!userCast.checkPwd(password)) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Pseudo ou mot de passe incorrect.", Status.UNAUTHORIZED);
    }

    if (user.getStatut() == 0) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Vous n'êtes pas encore accepté par un Admin.",
          Status.UNAUTHORIZED);
    } else {
      dalServices.commitTransaction();
      return user;
    }
  }

  @Override
  public boolean register(UserDTO userDTO) {

    dalServices.startTransaction();

    User user = (User) userDTO;

    user.encryptPwd();

    if (userDAO.findByPseudo(user.getPseudo().toLowerCase()) != null) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Pseudo deja utilisé", Status.CONFLICT);
    }

    if (userDAO.findByEmail(user.getEmail()) != null) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Email deja utilisé", Status.CONFLICT);
    }

    boolean bool = userDAO.insert(user);
    dalServices.commitTransaction();
    return bool;
  }

  @Override
  public UserDTO getUser(int id) {
    dalServices.startTransaction();
    UserDTO user = null;
    try {
      user = userDAO.findById(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.CONFLICT);
    }
    return user;
  }

  @Override
  public UserDTO getUserByEmail(String email) {
    dalServices.startTransaction();
    UserDTO user = null;
    try {
      user = userDAO.findByEmail(email);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.CONFLICT);
    }
    return user;
  }

  @Override
  public List<UserDTO> getAllUser(int etat) {
    dalServices.startTransaction();
    List<UserDTO> list = null;
    try {
      list = userDAO.selectAllUser(etat);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.CONFLICT);
    }
    return list;
  }

  @Override
  public void putUserStatut(int id) {
    dalServices.startTransaction();

    try {
      userDAO.updateUserStatut(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public void putUserType(int id, String type) {
    dalServices.startTransaction();

    try {
      userDAO.updateUserType(id, type);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }
  }

  @Override
  public void removeUser(int id) {
    dalServices.startTransaction();

    try {
      userDAO.deleteUser(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

  }

  @Override
  public HashMap<Integer, List<String>> getUsers(String searchFilter) {
    dalServices.startTransaction();
    HashMap<Integer, List<String>> userMap;
    try {
      userMap = userDAO.findUsers(searchFilter);

      Iterator iterator = userMap.entrySet().iterator();
      while (iterator.hasNext()) {
        Map.Entry mapentry = (Map.Entry) iterator.next();
        int nbrFurn = userDAO.getNbrFurniture((int) mapentry.getKey());
        List<String> temp = (List<String>) mapentry.getValue();
        temp.add(String.valueOf(nbrFurn));
      }

      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.INTERNAL_SERVER_ERROR);
    }

    return userMap;
  }

  @Override
  public UserDTO getUserFurnitureBuyer(int id) {
    dalServices.startTransaction();
    UserDTO user = null;
    try {
      user = userDAO.selectFurnitureBuyer(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.CONFLICT);
    }
    return user;
  }

  @Override
  public UserDTO getUserFurnitureSeller(int id) {
    dalServices.startTransaction();
    UserDTO user = null;
    try {
      user = userDAO.selectFurnitureSeller(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.CONFLICT);
    }
    return user;
  }

}
