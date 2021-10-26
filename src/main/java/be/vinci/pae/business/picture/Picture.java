package be.vinci.pae.business.picture;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(as = PictureImpl.class)
public interface Picture extends PictureDTO {

}
