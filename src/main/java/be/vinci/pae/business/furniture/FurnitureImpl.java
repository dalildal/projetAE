package be.vinci.pae.business.furniture;

import java.sql.Timestamp;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonView;
import views.Views;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class FurnitureImpl implements Furniture {

  private int idFurniture;
  private int idType;
  private String description;
  @JsonView(Views.Internal.class)
  private double purchasePrice;
  private double salesPrice;
  private double dealerPrice;
  private Timestamp recoveryDate;
  private Timestamp depositDate;
  private Timestamp removalDate;
  private Timestamp deliveryDate;
  private int favoritePicture;
  @JsonView(Views.Internal.class)
  private String state;

  @Override
  public int getIdFurniture() {
    return idFurniture;
  }

  @Override
  public void setIdFurniture(int idFurniture) {
    this.idFurniture = idFurniture;
  }

  @Override
  public int getIdType() {
    return idType;
  }

  @Override
  public void setIdType(int idType) {
    this.idType = idType;
  }

  @Override
  public String getDescription() {
    return description;
  }

  @Override
  public void setDescription(String description) {
    this.description = description;
  }

  @Override
  public double getPurchasePrice() {
    return purchasePrice;
  }

  @Override
  public void setPurchasePrice(double purchasePrice) {
    this.purchasePrice = purchasePrice;
  }

  @Override
  public double getSalesPrice() {
    return salesPrice;
  }

  @Override
  public void setSalesPrice(double salesPrice) {
    this.salesPrice = salesPrice;
  }

  @Override
  public double getDealerPrice() {
    return dealerPrice;
  }

  @Override
  public void setDealerPrice(double dealerPrice) {
    this.dealerPrice = dealerPrice;
  }

  @Override
  public Timestamp getRecoveryDate() {
    return recoveryDate;
  }

  @Override
  public void setRecoveryDate(Timestamp recoveryDate) {
    this.recoveryDate = recoveryDate;
  }

  @Override
  public Timestamp getDepositDate() {
    return depositDate;
  }

  @Override
  public void setDepositDate(Timestamp depositDate) {
    this.depositDate = depositDate;
  }

  @Override
  public Timestamp getRemovalDate() {
    return removalDate;
  }

  @Override
  public void setRemovalDate(Timestamp removalDate) {
    this.removalDate = removalDate;
  }

  @Override
  public Timestamp getDeliveryDate() {
    return deliveryDate;
  }

  @Override
  public void setDeliveryDate(Timestamp deliveryDate) {
    this.deliveryDate = deliveryDate;
  }

  @Override
  public int getFavoritePicture() {
    return favoritePicture;
  }

  @Override
  public void setFavoritePicture(int favoritePicture) {
    this.favoritePicture = favoritePicture;
  }

  @Override
  public String getState() {
    return state;
  }

  @Override
  public void setState(String state) {
    this.state = state;
  }


}
