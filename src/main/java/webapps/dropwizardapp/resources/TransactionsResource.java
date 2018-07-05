package webapps.dropwizardapp.resources;

import java.util.List;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.codahale.metrics.annotation.Timed;

import io.dropwizard.hibernate.UnitOfWork;
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
    public TransactionsResponse spend(Transactions txn) {
        
    	TransactionsResponse resp = new TransactionsResponse();
		
		List<Users> userList = userDAO.findByPhone(txn.getPhone());
		if(userList != null && userList.size() > 0) {
			Users u = userList.get(0);
			double credit = u.getCredit();
			if(credit >= txn.getAmount()) {
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
