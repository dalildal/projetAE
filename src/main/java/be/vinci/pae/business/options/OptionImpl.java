package be.vinci.pae.business.options;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class OptionImpl implements Option {

  private int idFurniture;
  private int idUser;
  private int time;
  private Timestamp dateOption;

  @Override
  public int getIdFurniture() {
    return idFurniture;
  }

  @Override
  public void setIdFurniture(int idFurniture) {
    this.idFurniture = idFurniture;
  }

  @Override
  public int getIdUser() {
    return idUser;
  }

  @Override
  public void setIdUser(int idUser) {
    this.idUser = idUser;
  }

  @Override
  public int getTime() {
    return time;
  }

  @Override
  public void setTime(int time) {
    this.time = time;
  }

  @Override
  public Timestamp getDateOption() {
    return dateOption;
  }

  @Override
  public void setDateOption(Timestamp dateOption) {
    this.dateOption = dateOption;
  }



}
