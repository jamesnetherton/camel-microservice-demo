package com.github.jamesnetherton.servlet;

import com.netflix.hystrix.contrib.metrics.eventstream.HystrixMetricsStreamServlet;

import javax.servlet.annotation.WebServlet;

@WebServlet(name = "HystrixMetricsStreamServlet", urlPatterns = {"/hystrix.stream"})
public class MetricsServlet extends HystrixMetricsStreamServlet {
}