package be.vinci.pae.business.furniture;

import java.sql.Timestamp;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = FurnitureImpl.class)
public interface FurnitureDTO {

  int getIdFurniture();

  void setIdFurniture(int idFurniture);

  int getIdType();

  void setIdType(int idType);

  String getDescription();

  void setDescription(String description);

  double getPurchasePrice();

  void setPurchasePrice(double purchasePrice);

  double getSalesPrice();

  void setSalesPrice(double salesPrice);

  double getDealerPrice();

  void setDealerPrice(double dealerPrice);

  Timestamp getRecoveryDate();

  void setRecoveryDate(Timestamp recoveryDate);

  Timestamp getDepositDate();

  void setDepositDate(Timestamp depositDate);

  Timestamp getRemovalDate();

  void setRemovalDate(Timestamp removalDate);

  Timestamp getDeliveryDate();

  void setDeliveryDate(Timestamp deliveryDate);

  int getFavoritePicture();

  void setFavoritePicture(int favoritePicture);

  String getState();

  void setState(String state);

}
