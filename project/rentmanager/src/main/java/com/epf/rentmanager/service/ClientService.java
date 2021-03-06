package com.epf.rentmanager.service;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.exception.ServiceException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.model.Reservation;
import com.epf.rentmanager.utils.FormatChecker;
import com.epf.rentmanager.dao.ClientDao;

@Service
public class ClientService {
	public static int minNameLength = 3;
	
	@Autowired
	private ClientDao clientDao;
	
	@Autowired
	private ReservationService reservationService;
	
	private ClientService(ClientDao clientDao) {
		this.clientDao = clientDao;
	}
	
	/**
	 * Créé un client dans la base de données
	 * @param client
	 * @return l'identifiant du client
	 * @throws DaoException
	 */
	public long create(Client client) throws ServiceException {
		try {
			isValid(client);
			client.setNom(client.getNom().toUpperCase());
			return clientDao.create(client);
		} catch (ServiceException | DaoException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * Met à jour un client dans la base de données
	 * @param client
	 * @return l'identifiant du client
	 * @throws DaoException
	 */
	public long update(Client client) throws ServiceException {
		try {
			isValid(client);
			client.setNom(client.getNom().toUpperCase());
			return clientDao.update(client);
		} catch (ServiceException | DaoException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * Supprime un client de la base de données
	 * @param l'identifiant du client
	 * @return
	 * @throws DaoException
	 */
	public long delete(int id) throws ServiceException {
		try {
			// Peut être davantage optimisé
			List<Reservation> reservations = reservationService.findByClient(id);
			for(Reservation reservation : reservations) {
				reservationService.delete(reservation.getId());
			}
			return clientDao.delete(id);
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * Recherche et renvoie un client à partir de son identifiant (s'il existe)
	 * @param l'identifiant du client
	 * @return client
	 * @throws DaoException
	 */
	public Client findById(long id) throws ServiceException {
		try {
			return clientDao.findById(id).get();
		} catch (RuntimeException | DaoException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * Recherche et renvoie les clients liés à un véhicule
	 * @param l'identifiant du véhicule
	 * @return la liste de clients
	 * @throws DaoException
	 */
	public List<Client> findByVehicle(long vehicle_id) throws ServiceException {
		try {
			return clientDao.findByVehicleId(vehicle_id);
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage());
		}
	}

	/**
	 * Recherche et renvoie tous les clients de la base de données
	 * @return la liste de clients
	 * @throws DaoException
	 */
	public List<Client> findAll() throws ServiceException {
		try {
			return clientDao.findAll();
		} catch (DaoException e) {
			throw new ServiceException(e.getMessage());
		}
	}
	
	/**
	 * Vérifie le respect des contraintes avant d'insérer le client dans la base de données
	 * @param client
	 * @throws ServiceException
	 */
	private void isValid(Client client) throws ServiceException {
		if (FormatChecker.isBlank(client.getNom()) || FormatChecker.isBlank(client.getPrenom()))
			throw new ServiceException("Nom / prénom vide");
		if (!hasValidEmail(client))
			throw new ServiceException("Format d'email invalide");
		if (!isMajor(client))
			throw new ServiceException("L'utilisateur n'est pas mejeur");
		if (!hasValidName(client))
			throw new ServiceException("Le nom et le prénom de l'utilsateur doivent avoir au moins " + minNameLength + " caractères");
	}
	
	/**
	 * Vérifie si le client est majeur
	 * @param client
	 * @return
	 */
	private boolean isMajor(Client client) {
		return client.getNaissance().until(LocalDate.now(), ChronoUnit.YEARS) >= 18;
	}
	
	/**
	 * Vérifie si le nom du client respecte les contraintes
	 * @param client
	 * @return
	 */
	private boolean hasValidName(Client client) {
		return client.getNom().length() >= minNameLength && client.getPrenom().length() >= minNameLength;
	}
	
	/**
	 * Vérifie si l'email du client respecte les contraintes
	 * @param client
	 * @return
	 */
	private boolean hasValidEmail(Client client) {
		final boolean validFormat = FormatChecker.isValidEmailAddress(client.getEmail());
		if (!validFormat) return false;
		// Peut être davantage optimisé
		try {
			List<Client> clients = this.findAll();
			for (Client iclient : clients) {
	            if (iclient.getEmail().equals(client.getEmail())) return false;
	        }
			return true;
		} catch (ServiceException e) {
			e.printStackTrace();
			return false;
		}
	}
}
