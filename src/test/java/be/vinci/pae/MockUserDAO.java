package be.vinci.pae;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.mindrot.jbcrypt.BCrypt;
import be.vinci.pae.business.users.UserDTO;
import be.vinci.pae.business.users.UserFactory;
import be.vinci.pae.data.users.UserDAO;
import jakarta.inject.Inject;

public class MockUserDAO implements UserDAO {

  @Inject
  private UserFactory userFactory;

  @Override
  public UserDTO findByPseudo(String pseudo) {

    UserDTO[] userDBTest = getUserDB();

    for (UserDTO userDB : userDBTest) {
      if (userDB.getPseudo().equals(pseudo)) {
        return userDB;
      }
    }

    return null;
  }

  @Override
  public UserDTO findByEmail(String email) {

    UserDTO[] userDBTest = getUserDB();

    for (UserDTO userDB : userDBTest) {
      if (userDB.getEmail().equals(email)) {
        return userDB;
      }
    }

    return null;
  }

  @Override
  public UserDTO findById(int id) {

    UserDTO[] userDBTest = getUserDB();

    for (UserDTO userDB : userDBTest) {
      if (userDB.getId() == id) {
        return userDB;
      }
    }

    return null;
  }

  @Override
  public boolean insert(UserDTO user) {
    return true;
  }

  @Override
  public List<UserDTO> selectAllUser(int statut) {

    UserDTO[] userDBTest = getUserDB();
    List<UserDTO> listUser = new ArrayList<UserDTO>();

    if (statut == -1) {
      for (UserDTO userDB : userDBTest) {
        listUser.add(userDB);
      }
    } else {
      for (UserDTO userDB : userDBTest) {
        if (userDB.getStatut() == statut) {
          listUser.add(userDB);
        }
      }
    }

    return listUser;
  }

  @Override
  public void updateUserStatut(int id) {
    // Not necessary for test.
  }

  @Override
  public void deleteUser(int id) {
    // Not necessary for test.
  }

  @Override
  public void updateUserType(int id, String type) {
    // Not necessary for test.
  }

  private UserDTO[] getUserDB() {
    UserDTO[] userDBTest = {
        setUserData(0, "user1", "mdp", "User1", "User1", "Chaussée Léonard de Vinci", "1", null,
            1000, "Bruxelles", "Belgique", "user1@student.vinci.be", new Timestamp(2021 - 02 - 25),
            "admin", 1),
        setUserData(1, "user2", "mdp", "User2", "User2", "Chaussée Léonard de Vinci", "2", null,
            1000, "Bruxelles", "Belgique", "user2@student.vinci.be", new Timestamp(2021 - 02 - 26),
            "client", 0),
        setUserData(2, "user3", "mdp", "User3", "User3", "Chaussée Léonard de Vinci", "3", null,
            1000, "Bruxelles", "Belgique", "user3@student.vinci.be", new Timestamp(2021 - 02 - 27),
            "client", 0)};

    return userDBTest;
  }

  private UserDTO setUserData(int id, String pseudo, String mdp, String firstName, String lastName,
      String street, String num, String box, int postalCode, String municipality, String country,
      String email, Timestamp dateRegister, String type, int statut) {
    UserDTO user = this.userFactory.getUser();

    user.setId(id);
    user.setPseudo(pseudo);
    user.setPwd(BCrypt.hashpw(mdp, BCrypt.gensalt()));
    user.setFirstName(firstName);
    user.setLastName(lastName);
    user.setStreet(street);
    user.setNum(num);
    user.setBox(box);
    user.setPostalCode(postalCode);
    user.setMunicipality(municipality);
    user.setCountry(country);
    user.setEmail(email);
    user.setDateRegister(dateRegister);
    user.setType(type);
    user.setStatut(statut);

    return user;
  }

  @Override
  public HashMap<Integer, List<String>> findUsers(String searchFilter) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public UserDTO selectFurnitureBuyer(int id) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public int getNbrFurniture(int idUser) {
    // TODO Auto-generated method stub
    return 0;
  }

  @Override
  public UserDTO selectFurnitureSeller(int id) {
    // TODO Auto-generated method stub
    return null;
  }

}
