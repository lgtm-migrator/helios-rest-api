package com.apploidxxx.api;

import com.apploidxxx.api.model.ErrorMessage;
import com.apploidxxx.api.util.Password;
import com.apploidxxx.entity.User;
import com.apploidxxx.entity.dao.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.ws.rs.DELETE;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

/**
 * @author Arthur Kupriyanov
 */
@Path("/api/register")
public class RegisterApi {
    @POST
    public Response register(@Valid@NotNull@QueryParam("username") String username,
                             @Valid@NotNull@QueryParam("password") String password,
                             @Valid@NotNull@QueryParam("first_name") String firstName,
                             @Valid@NotNull@QueryParam("last_name") String lastName,
                             @Valid@NotNull@QueryParam("email") String email){

        if (UserService.findByName(username)==null){
            UserService.saveUser(new User(username, Password.hash(password), firstName, lastName, email));
            return Response.ok().build();
        }
        else {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("invalid_username","This username already is taken"))
                    .build();
        }
    }

    @DELETE
    public Response deleteUser(@Valid@NotNull@QueryParam("username") String username,
                               @Valid@NotNull@QueryParam("password") String password)
    {
        User user = UserService.findByName(username);
        if (user!=null && Password.isEqual(password, user.getPassword())) {
            UserService.deleteUser(user);
            return Response.ok().build();
        } else {
            return Response
                    .status(Response.Status.BAD_REQUEST)
                    .entity(new ErrorMessage("invalid_credentials", "invalid username or password"))
                    .build();
        }
    }
}
