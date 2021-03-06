package com.epf.rentmanager.ui.servlets;

import java.io.IOException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.epf.rentmanager.dao.VehicleDao;
import com.epf.rentmanager.exception.DaoException;

@WebServlet("/home")
public class HomeServlet extends HttpServlet{
	/**
	 * 
	 */
	private static final long serialVersionUID = -9137624914315585800L;
	
	@Autowired
	private VehicleDao vehicleDao;
	
	@Override
	public void init() throws ServletException {
		super.init();
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/home.jsp");
		try {
			final int vehicleCount = vehicleDao.count();
			request.setAttribute("vehicleCount", vehicleCount);
		} catch (DaoException e) {
			e.printStackTrace();
		}
		dispatcher.forward(request, response);
	}
}
