package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.ArrayList;
import java.util.List;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import be.vinci.pae.business.furniture.FurnitureUCC;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.Config;

public class FurnitureTest {

  private FurnitureUCC furnitureUCC;

  @BeforeEach
  void initAll() {
    Config.load("prod.properties");
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinderTest());
    this.furnitureUCC = locator.getService(FurnitureUCC.class);
  }

  @DisplayName("FurnitureTest 1 : GetAllFurniture with wrong type")
  @Test
  public void furnitureGetAllFurnitureTest1() {
    List<String> state = new ArrayList<String>();
    state.add("a vendre");
    assertThrows(BusinessException.class, () -> {
      this.furnitureUCC.getAllFurniture("commode", state);
    });
  }

  @DisplayName("FurnitureTest 2 : GetAllFurniture with wrong state")
  @Test
  public void furnitureGetAllFurnitureTest2() {
    List<String> state = new ArrayList<String>();
    state.add("a-vendre");
    assertThrows(BusinessException.class, () -> {
      this.furnitureUCC.getAllFurniture("armoire", state);
    });
  }

  @DisplayName("FurnitureTest 3 : GetAllFurniture with no type and no state")
  @Test
  public void furnitureGetAllFurnitureTest3() {
    List<String> state = new ArrayList<String>();
    assertEquals(3, this.furnitureUCC.getAllFurniture("", state).size());
  }

  @DisplayName("FurnitureTest 4 : GetAllFurniture with correctly type and state")
  @Test
  public void furnitureGetAllFurnitureTest4() {
    List<String> state = new ArrayList<String>();
    state.add("a vendre");
    assertEquals(1, this.furnitureUCC.getAllFurniture("bureau", state).size());
  }

  @DisplayName("FurnitureTest 5 : GetFurniture with wrong id")
  @Test
  public void furnitureGetFurnitureTest1() {
    assertNull(this.furnitureUCC.getFurniture(5));
  }

  @DisplayName("FurnitureTest 6 : GetFurniture with correctly id")
  @Test
  public void furnitureGetFurnitureTest2() {
    assertEquals("Meuble 2", this.furnitureUCC.getFurniture(1).getDescription());
  }

  // Doing tests to putFurnitureState is not necessary.
  // Cf. FurnitureUCCImpl class to understand why (void method).

  @DisplayName("FurnitureTest 7 : GetAllFurnitureType return correct list")
  @Test
  public void furnitureGetAllFurnitureTypeTest() {
    assertEquals(3, this.furnitureUCC.getAllFurnitureType().size());
  }
}
