package be.vinci.pae.business.picture;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class PictureImpl implements Picture {

  private int idPicture;
  private int idFurniture;
  private String link;
  private int visibility;

  @Override
  public int getIdPicture() {
    return idPicture;
  }

  @Override
  public void setIdPicture(int idPicture) {
    this.idPicture = idPicture;
  }

  @Override
  public int getIdFurniture() {
    return idFurniture;
  }

  @Override
  public void setIdFurniture(int idFurniture) {
    this.idFurniture = idFurniture;
  }

  @Override
  public String getLink() {
    return link;
  }

  @Override
  public void setLink(String link) {
    this.link = link;
  }

  @Override
  public int getVisibility() {
    return visibility;
  }

  @Override
  public void setVisibility(int visibility) {
    this.visibility = visibility;
  }



}
