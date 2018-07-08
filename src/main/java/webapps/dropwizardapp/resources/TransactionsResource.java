package webapps.dropwizardapp.resources;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
import redis.clients.jedis.Jedis;
import webapps.dropwizardapp.core.Transactions;
import webapps.dropwizardapp.core.TransactionsResponse;
import webapps.dropwizardapp.core.Users;
import webapps.dropwizardapp.db.TransactionsDAO;
import webapps.dropwizardapp.db.UsersDAO;

@Path("/txn/")
@Produces(MediaType.APPLICATION_JSON)
public class TransactionsResource {
	private final TransactionsDAO txnDAO;
	private final UsersDAO userDAO;

    public TransactionsResource(TransactionsDAO txnDAO, UsersDAO userDAO) {
        this.txnDAO = txnDAO;
        this.userDAO = userDAO;
    }
    
    @POST
    @Timed
    @Path("/spend")
    @UnitOfWork
    public TransactionsResponse spend(Transactions txn, @Context HttpServletRequest req, @Context Jedis jedis) {
        
    	TransactionsResponse resp = new TransactionsResponse();
		
    	/*String token = req.getHeader(HttpHeaders.AUTHORIZATION);
		List<Users> userList = userDAO.findByToken(token);*/
    	
    	String token = req.getHeader(HttpHeaders.AUTHORIZATION);
    	String phone = jedis.get(token);
		List<Users> userList = userDAO.findByPhone(phone);
		if(userList != null && userList.size() > 0) {
			Users u = userList.get(0);
			double credit = u.getCredit();
			if(credit >= txn.getAmount()) {
				txn.setPhone(u.getPhone());
				txnDAO.create(txn);
				u.setCredit(credit - txn.getAmount());
				userDAO.create(u);
				resp.setTransactionStatus("Transaction Successful!");
				resp.setMessage("Credit balance updated");
				resp.setCredit(u.getCredit());
			} else {
				resp.setTransactionStatus("Transaction Failed!");
				resp.setMessage("Insufficient credit balance. Consider topup.");
				resp.setCredit(u.getCredit());
			}
		} else {
			resp.setTransactionStatus("Transaction Failed!");
			resp.setMessage("Phone number doesn't exists! Please register first");
		}
		
		return resp;
    }
}
