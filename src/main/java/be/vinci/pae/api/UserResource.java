package be.vinci.pae.api;

import java.util.HashMap;
import java.util.List;
import org.glassfish.jersey.server.ContainerRequest;
import org.jose4j.jwt.NumericDate;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import be.vinci.pae.api.filters.AdminOnly;
import be.vinci.pae.api.filters.Authorize;
import be.vinci.pae.api.utils.Json;
import be.vinci.pae.business.users.User;
import be.vinci.pae.business.users.UserDTO;
import be.vinci.pae.business.users.UserImpl;
import be.vinci.pae.business.users.UserUCC;
import be.vinci.pae.utils.Config;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import jakarta.ws.rs.Consumes;
import jakarta.ws.rs.DELETE;
import jakarta.ws.rs.DefaultValue;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.PUT;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.PathParam;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import views.Views;

@Singleton
@Path("/users")
public class UserResource {

  private final Algorithm jwtAlgorithm = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final ObjectMapper jsonMapper = new ObjectMapper();

  @Inject
  private UserUCC userUCC;

  /**
   * This method check if user parameter is "good".
   * 
   * @param json is json connection parameters of user
   * @return token of session
   */
  @POST
  @Path("login")
  @Consumes(MediaType.APPLICATION_JSON)
  public Response login(JsonNode json) {

    // Get and check credentials
    if (!json.hasNonNull("pseudo") || !json.hasNonNull("password")) {
      throw new WebApplicationException("Veuillez indiquer un pseudo et un mot de passe",
          Status.PRECONDITION_FAILED);
    }
    String pseudo = json.get("pseudo").asText();
    String password = json.get("password").asText();

    // Try to login
    UserDTO user = userUCC.login(pseudo, password);

    return createToken(user);

  }

  /**
   * This method is call by frontend when user want register.
   * 
   * @param userDTO parameters
   * @return Response status
   */
  @POST
  @Path("register")
  public Response register(UserDTO userDTO) {

    // Get and check credentials
    if (userDTO.getPseudo().isEmpty() || userDTO.getPwd().isEmpty()
        || userDTO.getLastName().isEmpty() || userDTO.getFirstName().isEmpty()
        || userDTO.getStreet().isEmpty() || userDTO.getNum().isEmpty()
        || userDTO.getMunicipality().isEmpty() || userDTO.getCountry().isEmpty()
        || userDTO.getEmail().isEmpty() || userDTO.getPostalCode() == 0) {
      throw new WebApplicationException("Veuillez remplire tous les champs",
          Status.PRECONDITION_FAILED);
    }

    userUCC.register(userDTO);

    return Response.ok("true").build();

  }

  /**
   * This method is call by frontend to each refresh or state change.
   * 
   * @param request with token and user id
   * @return user currently connected
   */
  @GET
  @Path("me")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  public User getUser(@Context ContainerRequest request) {
    User currentUser = (User) request.getProperty("user");
    return Json.filterPublicJsonView(currentUser, User.class); // probleme statut renvoyer
  }

  /**
   * This method is call by frontend to each refresh or state change.
   * 
   * @param filter needed to search user
   * @return user currently connected
   */
  @GET
  @Path("search/{filter}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public HashMap<Integer, List<String>> getUsers(
      @DefaultValue("") @PathParam("filter") String filter) {
    HashMap<Integer, List<String>> userMap = new HashMap<Integer, List<String>>();

    if (!filter.equals("") && filter != null) {
      userMap = userUCC.getUsers(filter);
    } else {
      throw new WebApplicationException("Veuillez remplire la barre de recherche",
          Status.PRECONDITION_FAILED);
    }
    return userMap;
  }

  /**
   * This method return user via its email address.
   * 
   * @param email of user
   * @return user
   */
  @GET
  @Path("/{email}")
  @Produces(MediaType.APPLICATION_JSON)
  @AdminOnly
  public User getUserByEmail(@PathParam("email") String email) {
    if (email == null) {
      throw new WebApplicationException("Veuillez introduire un email", Status.PRECONDITION_FAILED);
    }

    User user = (User) userUCC.getUserByEmail(email);
    return Json.filterPublicJsonView(user, User.class);
  }

  /**
   * This method return the list of all user.
   * 
   * @param etat of the user who want
   * @return listeUser
   */
  @GET
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public List<UserDTO> getAllUser(@DefaultValue("-1") @QueryParam("etat") int etat) {

    List<UserDTO> listeUser;

    if (etat >= -1 && etat <= 1) {
      listeUser = userUCC.getAllUser(etat);
    } else {
      throw new WebApplicationException("Cet etat n'est pas valide", Status.PRECONDITION_FAILED);
    }
    return listeUser;
  }

  /**
   * This method update statut of the user.
   * 
   * @param id of the user
   * @return UserDTO of user in id Path
   */
  @PUT
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public UserDTO updateUserStatut(@PathParam("id") int id) {

    User user = (User) userUCC.getUser(id);

    userUCC.putUserStatut(id);

    return Json.filterPublicJsonView(user, User.class); // probleme statut renvoyer

  }

  /**
   * This method update type of the user.
   * 
   * @param type of the user
   * @param id of the user
   * @return UserDTO of user in id Path
   */
  @PUT
  @Path("/{id}:{type}")
  @Produces(MediaType.APPLICATION_JSON)
  @Consumes(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public UserDTO updateUserType(@PathParam("type") String type, @PathParam("id") int id) {

    User user = (User) userUCC.getUser(id);

    userUCC.putUserType(id, type);

    return Json.filterPublicJsonView(user, User.class); // probleme statut renvoyer

  }

  /**
   * This method delete of db.
   * 
   * @param id of user
   * @return UserDTO who has been deleted
   */
  @DELETE
  @Path("/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @Authorize
  @AdminOnly
  public UserDTO deleteUser(@PathParam("id") int id) {

    User user = (User) userUCC.getUser(id);

    userUCC.removeUser(id);

    return Json.filterPublicJsonView(user, User.class); // probleme statut renvoyer

  }

  /**
   * This method return user who bought the furniture.
   * 
   * @param id of the furniture
   * @return user
   */
  @GET
  @Path("/userBuyer/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @AdminOnly
  public User getUserBuyerByIdFurniture(@PathParam("id") int id) {
    if (id < 1) {
      throw new WebApplicationException("Id du meuble incorrect", Status.PRECONDITION_FAILED);
    }
    User user = (User) userUCC.getUserFurnitureBuyer(id);
    if (user != null) {
      return Json.filterPublicJsonView(user, User.class);
    }
    return new UserImpl();
  }

  /**
   * This method return user who sell the furniture.
   * 
   * @param id of the furniture
   * @return user
   */
  @GET
  @Path("/userSeller/{id}")
  @Produces(MediaType.APPLICATION_JSON)
  @AdminOnly
  public User getUserSellerByIdFurniture(@PathParam("id") int id) {
    if (id < 1) {
      throw new WebApplicationException("Id du meuble incorrect", Status.PRECONDITION_FAILED);
    }
    User user = (User) userUCC.getUserFurnitureSeller(id);
    if (user != null) {
      return Json.filterPublicJsonView(user, User.class);
    }
    return new UserImpl();
  }

  /**
   * This method return response for token creation.
   * 
   * @param user for token
   * @return response of request
   */
  private Response createToken(UserDTO user) {
    // Create token
    String token;
    NumericDate time = NumericDate.now();
    time.addSeconds(Config.getIntProperty("TimeToken"));
    try {
      token = JWT.create().withIssuer("auth0").withClaim("user", user.getId())
          .withClaim("type", user.getType()).withClaim("exp", time.getValue())
          .sign(this.jwtAlgorithm);
    } catch (Exception e) {
      throw new WebApplicationException("Unable to create token", e, Status.INTERNAL_SERVER_ERROR);
    }
    // Build response

    // load the user data from a public JSON view to filter out the private info not
    // to be returned by the API (such as password)
    // UserDTO publicUser = Json.filterPublicJsonView(user, UserDTO.class);
    ObjectNode node = null;
    try {
      node = jsonMapper.createObjectNode().put("token", token).putPOJO("user",
          jsonMapper.writerWithView(Views.Public.class).writeValueAsString(user));
    } catch (JsonProcessingException e) {
      throw new WebApplicationException("Unable to create token", e, Status.INTERNAL_SERVER_ERROR);
    }
    return Response.ok(node, MediaType.APPLICATION_JSON).build();
  }
}
