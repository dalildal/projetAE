package be.vinci.pae.business.options;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import be.vinci.pae.data.DALServices;
import be.vinci.pae.data.option.OptionDAO;
import be.vinci.pae.utils.BusinessException;
import jakarta.inject.Inject;
import jakarta.ws.rs.core.Response.Status;

public class OptionUCCImpl implements OptionUCC {

  @Inject
  private OptionDAO optionDAO;

  @Inject
  private DALServices dalServices;

  @Override
  public boolean createOption(OptionDTO optionDTO) {

    dalServices.startTransaction();

    if (optionDAO.checkOptionValid(optionDTO.getIdFurniture()) != -1) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Ce meuble a deja une option", Status.UNAUTHORIZED);
    }

    OptionDTO option = optionDAO.findOption(optionDTO.getIdFurniture(), optionDTO.getIdUser());
    if (option != null) {

      LocalDate dateTime1 = option.getDateOption().toLocalDateTime().toLocalDate();
      int sumTime = option.getTime() + optionDTO.getTime();

      if (sumTime > 5
          || !LocalDate.now().plusDays(optionDTO.getTime()).isBefore(dateTime1.plusDays(5))
              && !LocalDate.now().plusDays(optionDTO.getTime()).isEqual(dateTime1.plusDays(5))) {
        dalServices.rollbackTransaction();
        throw new BusinessException(
            "Vous ne pouvez pas prendre plus de 5 jours d'options ouvrables", Status.UNAUTHORIZED);
      }
      optionDAO.updateOption(optionDTO.getIdFurniture(), optionDTO.getIdUser(), sumTime);
      optionDAO.updateFurnitureStatus(optionDTO.getIdFurniture(), "option");
      dalServices.commitTransaction();
    } else {
      optionDAO.insert(optionDTO);
      optionDAO.updateFurnitureStatus(optionDTO.getIdFurniture(), "option");
      dalServices.commitTransaction();
    }

    return true;

  }

  @Override
  public OptionDTO putOption(int idUser, int idFurniture) {

    dalServices.startTransaction();

    OptionDTO option = optionDAO.findOption(idFurniture, idUser);

    LocalDateTime date = option.getDateOption().toLocalDateTime();
    int daysBetween = (int) ChronoUnit.DAYS.between(date, LocalDateTime.now());

    try {
      optionDAO.updateOption(idFurniture, idUser, daysBetween);
      optionDAO.updateFurnitureStatus(idFurniture, "a vendre");
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }
    option.setTime(daysBetween);

    dalServices.commitTransaction();

    return option;
  }

  @Override
  public List<OptionDTO> getAllOptionValid(int id) {
    dalServices.startTransaction();
    List<OptionDTO> list = null;
    try {
      list = optionDAO.selectAllOptionValid(id);
      dalServices.commitTransaction();
    } catch (Exception e) {
      dalServices.rollbackTransaction();
      throw new BusinessException(e.getMessage(), Status.UNAUTHORIZED);
    }

    return list;
  }

  @Override
  public OptionDTO getOption(int idFurniture) {
    dalServices.startTransaction();

    int userId = optionDAO.checkOptionValid(idFurniture);

    if (userId == -1) {
      dalServices.rollbackTransaction();
      throw new BusinessException("Ce meuble n'a pas d'option", Status.UNAUTHORIZED);
    }

    return optionDAO.findOption(idFurniture, userId);

  }

}
