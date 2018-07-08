package webapps.dropwizardapp.core;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.eclipse.jetty.http.HttpStatus;

import io.dropwizard.hibernate.UnitOfWork;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;

public class AuthorizationFilter implements javax.servlet.Filter {
	
	/*private final UsersDAO userDAO;
	public AuthorizationFilter(UsersDAO userDAO) {
		this.userDAO = userDAO;
	}*/
	
	private final JedisPool jedisPool;
	public AuthorizationFilter(JedisPool jedisPool) {
		this.jedisPool = jedisPool;
	}

	@Override
	public void destroy() {}

	@Override
	@UnitOfWork
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
		
		String accessToken = ((HttpServletRequest) request).getHeader(HttpHeaders.AUTHORIZATION);
		boolean validToken = false;
		
		if(accessToken != null && !"".equals(accessToken.trim())) {

			/*List<Users> userList = userDAO.findByToken(accessToken);
			if(userList != null && userList.size() > 0) {
				validToken = true;
				chain.doFilter(request, response);
			}*/
			
			/* Replacing with redis cache */
			try (Jedis jedis = jedisPool.getResource()) {
			    if(jedis.get(accessToken) != null) {
			    	jedis.expire(accessToken, 300);
			    	validToken = true;
					chain.doFilter(request, response);
			    }
			}
			
		} 
		
		if(!validToken) {
			HttpServletResponse httpResponse = (HttpServletResponse) response;
            httpResponse.setStatus(HttpStatus.UNAUTHORIZED_401);
            httpResponse.getWriter().print("Unauthorized");
		}
	}

	@Override
	public void init(FilterConfig arg0) throws ServletException {}

}
