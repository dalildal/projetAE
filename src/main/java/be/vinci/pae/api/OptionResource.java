package be.vinci.pae.api;

import java.util.List;
import com.fasterxml.jackson.databind.JsonNode;
import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.business.options.OptionDTO;
import be.vinci.pae.business.options.OptionUCC;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DefaultValue;
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
@Path("/options")
public class OptionResource {

  @Inject
  private OptionUCC optionUCC;

  /**
   * This method create an option.
   * 
   * @param optionDTO parameters
   * @return Response status
   */
  @POST
  @Path("/{state}")
  @Authorize
  public Response createOption(OptionDTO optionDTO,
      @DefaultValue("-1") @PathParam("state") String state) {

    // Get and check credentials
    if (optionDTO.getTime() == 0 || optionDTO.getTime() < 0 || optionDTO.getTime() > 5) {
      throw new WebApplicationException("Veuillez indiquer une durée a l'option",
          Status.PRECONDITION_FAILED);
    }

    if (optionDTO.getIdFurniture() == 0 || optionDTO.getIdUser() == 0) {
      throw new WebApplicationException("Veuillez indiquer id Utilisateur ou id meuble",
          Status.PRECONDITION_FAILED);
    }

    if (state.equals("vendu") || state.equals("emporte") || state.equals("livre")
        || state.equals("reserve")) {
      throw new WebApplicationException(
          "Vous ne pouvez pas prendre une option sur un meuble " + state,
          Status.PRECONDITION_FAILED);
    }

    optionUCC.createOption(optionDTO);

    return Response.ok("true").build();

  }

  /**
   * This method update option.
   * 
   * @param json option parameters
   * @return OptionDTO
   */
  @PUT
  @Consumes(MediaType.APPLICATION_JSON)
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public OptionDTO updateOption(JsonNode json) {

    // Get and check credentials
    if (!json.hasNonNull("idFurniture") || !json.hasNonNull("idUser")) {
      throw new WebApplicationException("Veuillez indiquer l'id du meuble ou de l'utilisateur",
          Status.PRECONDITION_FAILED);
    }

    int idFurniture = json.get("idFurniture").asInt();
    int idUser = json.get("idUser").asInt();

    OptionDTO option = optionUCC.putOption(idUser, idFurniture);

    return option;

  }

  /**
   * This method return the list of all option.
   * 
   * @param id of the user
   * @return listeOption
   */
  @GET
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public List<OptionDTO> getAllOptionValid(@DefaultValue("-1") @PathParam("id") int id) {

    if (id == -1) {
      throw new WebApplicationException("Veuillez indiquer l'id de l'utilisateur",
          Status.PRECONDITION_FAILED);
    }

    List<OptionDTO> listOption = optionUCC.getAllOptionValid(id);
    return listOption;
  }

  /**
   * This method return valid option of the id to param.
   * 
   * @param id of the furniture
   * @return OptionDTO
   */
  @GET
  @Path("/furniture/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public OptionDTO getOption(@DefaultValue("-1") @PathParam("id") int id) {

    if (id == -1) {
      throw new WebApplicationException("Veuillez indiquer l'id du meuble",
          Status.PRECONDITION_FAILED);
    }

    OptionDTO option = optionUCC.getOption(id);
    return option;
  }

}
