package org.example;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.servlets.DefaultServlet;
import org.apache.catalina.startup.Tomcat;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class Main {

    public static void main(String[] args) throws LifecycleException {
        Tomcat tomcat = new Tomcat();
        tomcat.setPort(8080);

        Context context = tomcat.addContext("", null);

        Tomcat.addServlet(context, "helloServlet", new HttpServlet() {
            @Override
            protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
                resp.getWriter().println("Hello, this is the Embed Tomcat!");
            }
        });
        context.addServletMappingDecoded("/hello", "helloServlet");

        Tomcat.addServlet(context, "default", new DefaultServlet());
        context.addServletMappingDecoded("/", "default");

        tomcat.start();
        tomcat.getServer().await();
    }
}