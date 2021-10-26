package be.vinci.pae.business.options;

import java.sql.Timestamp;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = OptionImpl.class)
public interface OptionDTO {

  int getIdFurniture();

  void setIdFurniture(int idFurniture);

  int getIdUser();

  void setIdUser(int idUser);

  void setDateOption(Timestamp dateOption);

  Timestamp getDateOption();

  void setTime(int time);

  int getTime();

}
