package be.vinci.pae.business.users;

import java.sql.Timestamp;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = UserImpl.class)
public interface UserDTO {

  int getId();

  String getPseudo();

  String getPwd();

  String getFirstName();

  String getLastName();

  String getStreet();

  String getNum();

  String getBox();

  int getPostalCode();

  String getMunicipality();

  String getCountry();

  String getEmail();

  Timestamp getDateRegister();

  String getType();

  int getStatut();

  void setId(int id);

  void setPseudo(String pseudo);

  void setPwd(String pwd);

  void setFirstName(String firstName);

  void setLastName(String lastName);

  void setStreet(String street);

  void setNum(String num);

  void setBox(String box);

  void setPostalCode(int postalCode);

  void setMunicipality(String municipality);

  void setCountry(String country);

  void setEmail(String email);

  void setDateRegister(Timestamp dateRegister);

  void setType(String type);

  void setStatut(int statut);

}
