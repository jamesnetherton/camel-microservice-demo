package com.github.jamesnetherton.service.prefix.servlet;

import org.apache.camel.component.servlet.CamelHttpTransportServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "CamelServlet", urlPatterns = {"/api/*"})
public class CamelBootstrapServlet extends CamelHttpTransportServlet {
}
