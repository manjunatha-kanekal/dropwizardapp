package webapps.dropwizardapp;

import io.dropwizard.Application;
import io.dropwizard.db.DataSourceFactory;
import io.dropwizard.hibernate.HibernateBundle;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import webapps.dropwizardapp.core.Users;
import webapps.dropwizardapp.db.UsersDAO;
import webapps.dropwizardapp.resources.HelloWorldResource;
import webapps.dropwizardapp.resources.UsersResource;

public class dropwizardappApplication extends Application<dropwizardappConfiguration> {

	public static void main(final String[] args) throws Exception {
		new dropwizardappApplication().run(args);
	}
	
	private final HibernateBundle<dropwizardappConfiguration> hibernate = new HibernateBundle<dropwizardappConfiguration>(Users.class) {
	    @Override
	    public DataSourceFactory getDataSourceFactory(dropwizardappConfiguration configuration) {
	        return configuration.getDataSourceFactory();
	    }
	};

	@Override
	public String getName() {
		return "dropwizardapp";
	}

	@Override
	public void initialize(final Bootstrap<dropwizardappConfiguration> bootstrap) {
		bootstrap.addBundle(hibernate);
	}

	@Override
	public void run(final dropwizardappConfiguration configuration, final Environment environment) {
		final UsersDAO dao = new UsersDAO(hibernate.getSessionFactory());
	    environment.jersey().register(new UsersResource(dao));
		
		final HelloWorldResource resource = new HelloWorldResource(configuration.getTemplate(), configuration.getDefaultName());
		environment.jersey().register(resource);
	}

}
