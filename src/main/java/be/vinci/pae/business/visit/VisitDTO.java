package be.vinci.pae.business.visit;

import java.sql.Timestamp;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = VisitImpl.class)
public interface VisitDTO {

  int getIdVisit();

  int getIdUser();

  Timestamp getCreationDate();

  Timestamp getVisitDate();

  String getTimePeriod();

  String getStreetVisit();

  String getNumVisit();

  String getBoxVisit();

  int getPostalCodeVisit();

  String getMunicipalityVisit();

  String getCountryVisit();

  String getState();

  String getCancellationReason();

  void setIdVisit(int idVisit);

  void setIdUser(int idUser);

  void setCreationDate(Timestamp creationDate);

  void setVisitDate(Timestamp visitDate);

  void setTimePeriod(String timePeriod);

  void setStreetVisit(String streetVisit);

  void setNumVisit(String numVisit);

  void setBoxVisit(String boxVisit);

  void setPostalCodeVisit(int postalCodeVisit);

  void setMunicipalityVisit(String municipalityVisit);

  void setCountryVisit(String countryVisit);

  void setState(String state);

  void setCancellationReason(String cancellationReason);


}
