package be.vinci.pae.api;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import be.vinci.pae.api.filters.AdminOnly;
import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.business.picture.PictureDTO;
import be.vinci.pae.business.picture.PictureUCC;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;

@Singleton
@Path("/pictures")
public class PictureResource {

  @Inject
  private PictureUCC pictureUCC;

  /**
   * This method return the picture.
   * 
   * @param id of picture
   * @return pictureDTO
   */
  @GET
  @Path("/id/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public PictureDTO getPicture(@PathParam("id") int id) {
    return pictureUCC.getPicture(id);
  }

  /**
   * This method return the list of all pictures by id furniture.
   * 
   * @param id of furniture
   * @return listPicture
   */
  @GET
  @AdminOnly
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<PictureDTO> getAllPicturesById(@PathParam("id") int id) {
    List<PictureDTO> listePicture = pictureUCC.getAllPicturesById(id);
    return listePicture;
  }

  /**
   * This method return the list of all visible pictures by id furniture.
   * 
   * @param id of furniture
   * @return listPicture
   */
  @GET
  @Authorize
  @Path("/visible/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  public List<PictureDTO> getAllVisiblePicturesById(@PathParam("id") int id) {

    List<PictureDTO> listePicture = pictureUCC.getAllVisiblePicturesById(id);

    return listePicture;
  }

  /**
   * This method is call by frontend when user want request a visit.
   * 
   * @param pictureDTO parameters
   * @return Response status
   */
  @POST
  @Authorize
  @Path("add")
  public Response add(PictureDTO pictureDTO) {

    // Get and check credentials
    if (pictureDTO.getLink().equals("") || pictureDTO.getIdFurniture() <= 0) {
      throw new WebApplicationException("Veuillez remplire tous les champs",
          Status.PRECONDITION_FAILED);
    }

    int id = pictureUCC.add(pictureDTO);

    return Response.ok(id).build();

  }

  /**
   * This method update state of the furniture.
   * 
   * @param id of the furniture
   * @param json contains data for furniture
   * @return FurnitureDTO of furniture in id Path
   */
  @PUT
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public PictureDTO updatePicture(@PathParam("id") int id, JsonNode json) {

    if (!json.hasNonNull("visibility")) {
      throw new WebApplicationException("Veuillez donner une visibilité",
          Status.PRECONDITION_FAILED);
    }

    int visibility = json.get("visibility").asInt();

    pictureUCC.putPicture(id, visibility);

    return pictureUCC.getPicture(id);
  }
}
