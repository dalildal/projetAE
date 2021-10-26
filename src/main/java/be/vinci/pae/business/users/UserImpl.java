package be.vinci.pae.business.users;

import java.sql.Timestamp;
import org.mindrot.jbcrypt.BCrypt;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import views.Views;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserImpl implements User {

  private int id;
  private String pseudo;
  @JsonView(Views.Internal.class)
  private String pwd;
  private String firstName;
  private String lastName;
  private String street;
  private String num;
  private String box;
  private int postalCode;
  private String municipality;
  private String country;
  private String email;
  private Timestamp dateRegister;
  private String type;
  @JsonView(Views.Internal.class)
  private int statut;

  @Override
  public int getId() {
    return id;
  }

  @Override
  public void setId(int id) {
    this.id = id;
  }

  @Override
  public String getPseudo() {
    return pseudo;
  }

  @Override
  public void setPseudo(String pseudo) {
    this.pseudo = pseudo;
  }

  @Override
  public String getPwd() {
    return pwd;
  }

  @Override
  public void setPwd(String pwd) {
    this.pwd = pwd;
  }

  @Override
  public String getFirstName() {
    return firstName;
  }

  @Override
  public void setFirstName(String firstName) {
    this.firstName = firstName;
  }

  @Override
  public String getLastName() {
    return lastName;
  }

  @Override
  public void setLastName(String lastName) {
    this.lastName = lastName;
  }

  @Override
  public String getStreet() {
    return street;
  }

  @Override
  public void setStreet(String street) {
    this.street = street;
  }

  @Override
  public String getNum() {
    return num;
  }

  @Override
  public void setNum(String num) {
    this.num = num;
  }

  @Override
  public String getBox() {
    return box;
  }

  @Override
  public void setBox(String box) {
    this.box = box;
  }

  @Override
  public int getPostalCode() {
    return postalCode;
  }

  @Override
  public void setPostalCode(int postalCode) {
    this.postalCode = postalCode;
  }

  @Override
  public String getMunicipality() {
    return municipality;
  }

  @Override
  public void setMunicipality(String municipality) {
    this.municipality = municipality;
  }

  @Override
  public String getCountry() {
    return country;
  }

  @Override
  public void setCountry(String country) {
    this.country = country;
  }

  @Override
  public String getEmail() {
    return email;
  }

  @Override
  public void setEmail(String email) {
    this.email = email;
  }

  @Override
  public Timestamp getDateRegister() {
    return dateRegister;
  }

  @Override
  public void setDateRegister(Timestamp dateRegister) {
    this.dateRegister = dateRegister;
  }

  @Override
  public String getType() {
    return type;
  }

  @Override
  public void setType(String type) {
    this.type = type;
  }

  @Override
  public int getStatut() {
    return statut;
  }

  @Override
  public void setStatut(int statut) {
    this.statut = statut;
  }

  @Override
  public void encryptPwd() {
    this.setPwd(BCrypt.hashpw(this.pwd, BCrypt.gensalt()));
  }

  @Override
  public boolean checkPwd(String password) {
    return BCrypt.checkpw(password, this.getPwd());
  }

  @Override
  public boolean checkCanBeAdmin() {
    return false;
  }

  @Override
  public boolean changeToAdmin() {
    return false;
  }

}
