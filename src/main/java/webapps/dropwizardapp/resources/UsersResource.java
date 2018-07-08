package webapps.dropwizardapp.resources;

import java.util.List;
import java.util.Optional;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
import redis.clients.jedis.Jedis;
import webapps.dropwizardapp.core.LoginResponse;
import webapps.dropwizardapp.core.Users;
import webapps.dropwizardapp.db.UsersDAO;

@Path("/")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource {
	private static final Logger LOGGER = LoggerFactory.getLogger(UsersResource.class);
	private final UsersDAO userDAO;

    public UsersResource(UsersDAO userDAO) {
        this.userDAO = userDAO;
    }

    @POST
    @Timed
    @Path("/register")
    @UnitOfWork
    public Users createUser(Users user) {
        return userDAO.create(user);
    }
    
    @GET
    @Timed
    @Path("/find")
    @UnitOfWork
    public List<Users> findUser(@QueryParam("phone") Optional<String> phone) {
    	if (phone.isPresent()) {
            return userDAO.findByPhone(phone.get());
        } /* else {
            return userDAO.findAll();
        }*/
    	
    	return null;
    }

    @GET
    @Timed
    @Path("/all")
    @UnitOfWork
    public List<Users> listUsers() {
        return userDAO.findAll();
    }
    
    @POST
    @Timed
    @Path("/login")
    @UnitOfWork
    public LoginResponse login(Users user, @Context Jedis jedis) {
		LoginResponse e = userDAO.login(user.getPhone());
		if(jedis.hget("user#token", user.getPhone()) != null) {
			jedis.del(jedis.hget("user#token", user.getPhone()));
		}
		jedis.set(e.getToken(), user.getPhone());
		jedis.expire(e.getToken(), 300);
		jedis.hset("user#token", user.getPhone(), e.getToken());
    	LOGGER.info("redis token: " + jedis.get(e.getToken()));
    	return e;
    }
    
    @POST
    @Timed
    @Path("/topup")
    @UnitOfWork
    public LoginResponse topUp(Users user) {
        return userDAO.topUp(user);
    }

}
