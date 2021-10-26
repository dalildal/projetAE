package be.vinci.pae.api.filters;

import java.io.IOException;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import com.auth0.jwt.interfaces.JWTVerifier;
import be.vinci.pae.utils.Config;
import jakarta.inject.Singleton;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.container.ContainerRequestContext;
import jakarta.ws.rs.container.ContainerRequestFilter;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.Response.Status;
import jakarta.ws.rs.ext.Provider;

@Singleton
@Provider
@AdminOnly
public class AdminOnlyRequestFilter implements ContainerRequestFilter {

  private final Algorithm jwtAlgorithm2 = Algorithm.HMAC256(Config.getProperty("JWTSecret"));
  private final JWTVerifier jwtVerifier2 =
      JWT.require(this.jwtAlgorithm2).withIssuer("auth0").build();

  @Override
  public void filter(ContainerRequestContext requestContext) throws IOException {
    String token2 = requestContext.getHeaderString("Authorization");
    if (token2 == null) {
      requestContext.abortWith(Response.status(Response.Status.UNAUTHORIZED)
          .entity("A token is needed to access this resource").build());
    } else {
      DecodedJWT decodedToken2 = null;
      try {
        decodedToken2 = this.jwtVerifier2.verify(token2);
      } catch (Exception e) {
        throw new WebApplicationException("Malformed token", e, Status.UNAUTHORIZED);
      }

      if (!decodedToken2.getClaim("type").asString().equals("admin")) {
        throw new WebApplicationException("Seul un admin peut accéder à cette ressource",
            Status.UNAUTHORIZED);
      }
    }
  }


}
