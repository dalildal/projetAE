package be.vinci.pae.business.picture;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = PictureImpl.class)
public interface PictureDTO {

  int getIdPicture();

  int getIdFurniture();

  String getLink();

  int getVisibility();

  void setIdPicture(int idPicture);

  void setIdFurniture(int idFurniture);

  void setLink(String link);

  void setVisibility(int visibility);
}
