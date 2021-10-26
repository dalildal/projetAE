package be.vinci.pae;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import be.vinci.pae.business.options.OptionDTO;
import be.vinci.pae.business.options.OptionFactory;
import be.vinci.pae.data.option.OptionDAO;
import jakarta.inject.Inject;

public class MockOptionDAO implements OptionDAO {
  @Inject
  private OptionFactory optionFactory;

  @Override
  public boolean insert(OptionDTO option) {
    return true;
  }

  @Override
  public OptionDTO findOption(int idFurniture, int idUser) {

    OptionDTO[] options = getOptionDB();

    for (OptionDTO optionDB : options) {
      if (optionDB.getIdFurniture() == idFurniture && optionDB.getIdUser() == idUser) {
        return optionDB;
      }
    }

    return null;
  }

  @Override
  public void updateOption(int idFurniture, int idUser, int time) {

    OptionDTO[] options = getOptionDB();

    for (OptionDTO optionDB : options) {
      if (optionDB.getIdFurniture() == idFurniture && optionDB.getIdUser() == idUser) {
        optionDB.setTime(time);
      }
    }
  }

  @Override
  public int checkOptionValid(int idFurniture) {

    OptionDTO[] options = getOptionDB();

    for (OptionDTO optionDB : options) {
      if (optionDB.getIdFurniture() == idFurniture) {
        LocalDateTime dateTimeDay = addDays(optionDB);
        if (dateTimeDay.isAfter(LocalDateTime.now())) {
          return optionDB.getIdUser();
        }
      }
    }

    return -1;
  }

  @Override
  public List<OptionDTO> selectAllOptionValid(int id) {

    List<OptionDTO> optionList = new ArrayList<OptionDTO>();
    OptionDTO[] options = getOptionDB();

    for (OptionDTO optionDB : options) {
      if (optionDB.getIdUser() == id) {
        LocalDateTime dateTimeDay = addDays(optionDB);
        if (dateTimeDay.isAfter(LocalDateTime.now())) {
          optionList.add(optionDB);
        }
      }
    }

    return optionList;
  }

  private LocalDateTime addDays(OptionDTO option) {
    LocalDateTime dateTime = option.getDateOption().toLocalDateTime();
    LocalDateTime dateTimeDay = dateTime.plusDays(option.getTime());
    return dateTimeDay;
  }

  private OptionDTO[] getOptionDB() {

    OptionDTO[] optionDBTest = {
        // idFurniture 0, idUser 0, time 3d
        setOptionData(0, 0, 3, Timestamp.valueOf(LocalDateTime.now().minusDays(1))),
        // idFurniture 1, idUser 0, time 2d
        setOptionData(1, 0, 4, Timestamp.valueOf(LocalDateTime.now().minusDays(1))),
        // idFurniture 2, idUser 1, time 1d
        setOptionData(2, 1, 1, Timestamp.valueOf(LocalDateTime.now().minusDays(1))),
        // idFurniture 3, idUser 0, time 5d
        setOptionData(3, 0, 5, Timestamp.valueOf(LocalDateTime.now().minusMonths(1))) };
    return optionDBTest;
  }

  private OptionDTO setOptionData(int idFurniture, int idUser, int time, Timestamp dateOption) {
    OptionDTO option = this.optionFactory.getOption();

    option.setIdFurniture(idFurniture);
    option.setIdUser(idUser);
    option.setTime(time);
    option.setDateOption(dateOption);

    return option;
  }

  @Override
  public void updateFurnitureStatus(int idFurniture, String status) {
    // TODO
  }
}
