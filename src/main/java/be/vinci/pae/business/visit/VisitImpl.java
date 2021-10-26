package be.vinci.pae.business.visit;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class VisitImpl implements Visit {
  private int idVisit;
  private int idUser;
  private Timestamp creationDate;
  private Timestamp visitDate;
  private String timePeriod;
  private String streetVisit;
  private String numVisit;
  private String boxVisit;
  private int postalCodeVisit;
  private String municipalityVisit;
  private String countryVisit;
  private String state;
  private String cancellationReason;

  @Override
  public int getIdVisit() {
    return idVisit;
  }

  @Override
  public void setIdVisit(int idVisit) {
    this.idVisit = idVisit;
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
  public Timestamp getCreationDate() {
    return creationDate;
  }

  @Override
  public void setCreationDate(Timestamp creationDate) {
    this.creationDate = creationDate;
  }

  @Override
  public Timestamp getVisitDate() {
    return visitDate;
  }

  @Override
  public void setVisitDate(Timestamp visitDate) {
    this.visitDate = visitDate;
  }

  @Override
  public String getTimePeriod() {
    return timePeriod;
  }

  @Override
  public void setTimePeriod(String timePeriod) {
    this.timePeriod = timePeriod;
  }

  @Override
  public String getStreetVisit() {
    return streetVisit;
  }

  @Override
  public void setStreetVisit(String streetVisit) {
    this.streetVisit = streetVisit;
  }

  @Override
  public String getNumVisit() {
    return numVisit;
  }

  @Override
  public void setNumVisit(String numVisit) {
    this.numVisit = numVisit;
  }

  @Override
  public String getBoxVisit() {
    return boxVisit;
  }

  @Override
  public void setBoxVisit(String boxVisit) {
    this.boxVisit = boxVisit;
  }

  @Override
  public int getPostalCodeVisit() {
    return postalCodeVisit;
  }

  @Override
  public void setPostalCodeVisit(int postalCodeVisit) {
    this.postalCodeVisit = postalCodeVisit;
  }

  @Override
  public String getMunicipalityVisit() {
    return municipalityVisit;
  }

  @Override
  public void setMunicipalityVisit(String municipalityVisit) {
    this.municipalityVisit = municipalityVisit;
  }

  @Override
  public String getCountryVisit() {
    return countryVisit;
  }

  @Override
  public void setCountryVisit(String countryVisit) {
    this.countryVisit = countryVisit;
  }

  @Override
  public String getState() {
    return state;
  }

  @Override
  public void setState(String state) {
    this.state = state;
  }

  @Override
  public String getCancellationReason() {
    return cancellationReason;
  }

  @Override
  public void setCancellationReason(String cancellationReason) {
    this.cancellationReason = cancellationReason;
  }

}
