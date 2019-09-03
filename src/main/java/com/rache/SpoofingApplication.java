package com.rache;

import com.rache.resources.CallSpoofResource;
import com.rache.resources.TextSpoofResource;
import io.dropwizard.Application;
import io.dropwizard.Configuration;
import io.dropwizard.setup.Environment;

public class SpoofingApplication extends Application<Configuration> {

    public static void main(final String[] args) throws Exception {
        new SpoofingApplication().run(args);
    }

    @Override
    public String getName() {
        return "Innovation Fest Spoofing Server";
    }

    public void run(Configuration configuration, Environment environment) throws Exception {
        environment.jersey().register(new TextSpoofResource());
        environment.jersey().register(new CallSpoofResource());
    }
}
