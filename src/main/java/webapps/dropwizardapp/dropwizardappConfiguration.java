package webapps.dropwizardapp;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import org.hibernate.validator.constraints.NotEmpty;

import com.bendb.dropwizard.redis.JedisFactory;
import com.fasterxml.jackson.annotation.JsonProperty;

import io.dropwizard.Configuration;
import io.dropwizard.db.DataSourceFactory;

public class dropwizardappConfiguration extends Configuration {
	@NotEmpty
    private String template;

    @NotEmpty
    private String defaultName = "Stranger";
    
    @Valid
    @NotNull
    private DataSourceFactory database = new DataSourceFactory();
    
    @NotNull
    @JsonProperty
    private JedisFactory redis;

    public JedisFactory getJedisFactory() {
    	return redis;
    }

    public void setJedisFactory(JedisFactory jedisFactory) {
    	this.redis = jedisFactory;
    }
    
    @JsonProperty
	public String getTemplate() {
		return template;
	}
    
    @JsonProperty
	public void setTemplate(String template) {
		this.template = template;
	}

    @JsonProperty
	public String getDefaultName() {
		return defaultName;
	}

    @JsonProperty
	public void setDefaultName(String defaultName) {
		this.defaultName = defaultName;
	}
    
    @JsonProperty("database")
    public DataSourceFactory getDataSourceFactory() {
        return database;
    }
    
    @JsonProperty("database")
    public void setDataSourceFactory(DataSourceFactory dataSourceFactory) {
        this.database = dataSourceFactory;
    }
    
    
}
