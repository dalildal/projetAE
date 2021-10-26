package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import be.vinci.pae.business.users.UserDTO;
import be.vinci.pae.business.users.UserFactory;
import be.vinci.pae.business.users.UserUCC;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.Config;

public class UserTest {
  private UserUCC userUCC;
  private UserFactory userFactory;

  @BeforeEach
  void initAll() {
    Config.load("prod.properties");
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinderTest());
    this.userUCC = locator.getService(UserUCC.class);
    this.userFactory = locator.getService(UserFactory.class);
  }

  @DisplayName("UserTest 1 : Login with wrong pseudo")
  @Test
  public void userLoginTest1() {
    assertThrows(BusinessException.class, () -> {
      this.userUCC.login("user", "mdp");
    });
  }

  @DisplayName("UserTest 2 : Login with wrong password")
  @Test
  public void userLoginTest2() {
    assertThrows(BusinessException.class, () -> {
      this.userUCC.login("user1", "mdp1");
    });
  }

  @DisplayName("UserTest 3 : Login with wrong user statut (0)")
  @Test
  public void userLoginTest3() {
    assertThrows(BusinessException.class, () -> {
      this.userUCC.login("user2", "mdp");
    });
  }

  @DisplayName("UserTest 4 : Login with correclty pseudo and password")
  @Test
  public void userLoginTest4() {
    assertEquals("user1", this.userUCC.login("user1", "mdp").getPseudo());
  }

  @DisplayName("UserTest 5 : Register with already pseudo")
  @Test
  public void userRegisterTest1() {
    UserDTO userDTO = userFactory.getUser();
    userDTO.setPseudo("user3");
    userDTO.setEmail("user3bis@student.vinci.be");
    assertThrows(BusinessException.class, () -> {
      this.userUCC.register(userDTO);
    });
  }

  @DisplayName("UserTest 6 : Register with already email")
  @Test
  public void userRegisterTest2() {
    UserDTO userDTO = userFactory.getUser();
    userDTO.setPseudo("user3");
    userDTO.setEmail("als@student.vinci.be");
    assertThrows(BusinessException.class, () -> {
      this.userUCC.register(userDTO);
    });
  }

  @DisplayName("UserTest 7 : Register with news pseudo and email")
  @Test
  public void userRegisterTest3() {
    UserDTO userDTO = userFactory.getUser();
    userDTO.setPseudo("user4");
    userDTO.setEmail("user4@student.vinci.be");
    assertTrue(this.userUCC.register(userDTO));
  }

  // Doing test with already email and pseudo is not necessary.
  // Cf. UserUCCImpl class -> register() to understand why.

  @DisplayName("UserTest 8 : GetUser with correctly id")
  @Test
  public void userGetUserTest1() {
    assertEquals(0, userUCC.getUser(0).getId());
  }

  @DisplayName("UserTest 9 : GetUser with wrong id")
  @Test
  public void userGetUserTest2() {
    assertNull(userUCC.getUser(3));
  }

  @DisplayName("UserTest 10 : GetAllUser with etat -1")
  @Test
  public void userGetAllUserTest1() {
    assertEquals(3, this.userUCC.getAllUser(-1).size());
  }

  @DisplayName("UserTest 11 : GetAllUser with etat 0")
  @Test
  public void userGetAllUserTest2() {
    assertEquals(2, this.userUCC.getAllUser(0).size());
  }

  @DisplayName("UserTest 12 : GetAllUser with etat 1")
  @Test
  public void userGetAllUserTest3() {
    assertEquals(1, this.userUCC.getAllUser(1).size());
  }

  // Doing tests to putUserStatut is not necessary.
  // Cf. UserUCCImpl class to understand why (void method).

  // Doing tests to putUserType is not necessary.
  // Cf. UserUCCImpl class to understand why (void method).

  // Doing tests to removeUser is not necessary.
  // Cf. UserUCCImpl class to understand why (void method).

}
