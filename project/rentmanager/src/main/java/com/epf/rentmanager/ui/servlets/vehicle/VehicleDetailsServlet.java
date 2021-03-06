package com.epf.rentmanager.ui.servlets.vehicle;

import java.io.IOException;
import java.util.List;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.support.SpringBeanAutowiringSupport;

import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.service.ClientService;
import com.epf.rentmanager.service.ReservationService;
import com.epf.rentmanager.service.VehicleService;

@WebServlet("/cars/details")
public class VehicleDetailsServlet extends HttpServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7220114404005692057L;

	@Autowired
	private ClientService clientService;
	
	@Autowired
	private VehicleService vehicleService;
	
	@Autowired
	private ReservationService reservationService;
	
	@Override
	public void init() throws ServletException {
		super.init();
		SpringBeanAutowiringSupport.processInjectionBasedOnCurrentContext(this);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		final RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/vehicles/details.jsp");
		try {
			final int id = Integer.parseInt(request.getParameter("id"));
			final List<Reservation> reservations = reservationService.findByVehicle(id);
			final List<Client> users = clientService.findByVehicle(id);
			request.setAttribute("vehicle", vehicleService.findById(id));
			request.setAttribute("reservations", reservations);
			request.setAttribute("users", users);
			request.setAttribute("countr", reservations.size());
			request.setAttribute("countu", users.size());
		} catch (final Exception e) {
			System.out.println(e.getMessage());
		}
		dispatcher.forward(request, response);
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		doGet(request, response);
	}
}
