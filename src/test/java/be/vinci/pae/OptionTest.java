package be.vinci.pae;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import org.glassfish.hk2.api.ServiceLocator;
import org.glassfish.hk2.utilities.ServiceLocatorUtilities;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import be.vinci.pae.business.options.OptionDTO;
import be.vinci.pae.business.options.OptionFactory;
import be.vinci.pae.business.options.OptionUCC;
import be.vinci.pae.utils.BusinessException;
import be.vinci.pae.utils.Config;

class OptionTest {
  private OptionUCC optionUCC;
  private OptionFactory optionFactory;

  @BeforeEach
  void initAll() {
    Config.load("prod.properties");
    ServiceLocator locator = ServiceLocatorUtilities.bind(new ApplicationBinderTest());
    this.optionUCC = locator.getService(OptionUCC.class);
    this.optionFactory = locator.getService(OptionFactory.class);
  }

  @DisplayName("OptionTest 1 : CreateOption with correctly furniture and user")
  @Test
  public void optionFindOptionTest1() {
    OptionDTO optionDTO = this.optionFactory.getOption();
    optionDTO.setIdFurniture(4);
    optionDTO.setIdUser(0);
    assertTrue(this.optionUCC.createOption(optionDTO));
  }

  @DisplayName("OptionTest 2 : CreateOption on furniture that already has an option")
  @Test
  public void optionCreateOptionTest2() {
    OptionDTO optionDTO = this.optionFactory.getOption();
    optionDTO.setIdFurniture(1);
    optionDTO.setIdUser(1);
    optionDTO.setDateOption(Timestamp.valueOf(LocalDateTime.of(2021, 03, 30, 14, 00, 00)));
    optionDTO.setTime(1);
    assertThrows(BusinessException.class, () -> {
      this.optionUCC.createOption(optionDTO);
    });
  }

  @DisplayName("OptionTest 3 : CreateOption which user has already past option under 5 days")
  @Test
  public void optionCreateOptionTest3() {
    OptionDTO optionDTO = this.optionFactory.getOption();
    optionDTO.setIdFurniture(2);
    optionDTO.setIdUser(1);
    optionDTO.setDateOption(Timestamp.valueOf(LocalDateTime.of(2021, 04, 01, 14, 00, 00)));
    optionDTO.setTime(3);
    assertTrue(this.optionUCC.createOption(optionDTO));
  }

  @DisplayName("OptionTest 4 : PutOption with correctly idUser and idFurniture")
  @Test
  public void optionPutOptionTest() {
    assertEquals(2, this.optionUCC.putOption(1, 2).getIdFurniture());
  }

  @DisplayName("OptionTest 5 : GetAllOptionValid return correct number of valid options")
  @Test
  public void optionGetAllOptionValidTest1() {
    assertEquals(2, this.optionUCC.getAllOptionValid(0).size());
  }

  @DisplayName("OptionTest 6 : GetAllOptionValid of user who don't has option")
  @Test
  public void optionGetAllOptionValidTest2() {
    assertEquals(0, this.optionUCC.getAllOptionValid(3).size());
  }
}
