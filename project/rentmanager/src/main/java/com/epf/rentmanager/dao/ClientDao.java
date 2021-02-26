package com.epf.rentmanager.dao;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import com.epf.rentmanager.exception.DaoException;
import com.epf.rentmanager.model.Client;
import com.epf.rentmanager.persistence.ConnectionManager;

public class ClientDao {
	
	private static ClientDao instance = null;
	private ClientDao() {}
	public static ClientDao getInstance() {
		if(instance == null)
			instance = new ClientDao();
		return instance;
	}
	
	private static final String CREATE_CLIENT_QUERY = "INSERT INTO Client(nom, prenom, email, naissance) VALUES(?, ?, ?, ?);";
	private static final String DELETE_CLIENT_QUERY = "DELETE FROM Client WHERE id=?;";
	private static final String FIND_CLIENT_QUERY = "SELECT nom, prenom, email, naissance FROM Client WHERE id=?;";
	private static final String FIND_CLIENTS_QUERY = "SELECT id, nom, prenom, email, naissance FROM Client;";
	
	public long create(Client client) throws DaoException {
		try {
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(CREATE_CLIENT_QUERY);
			preparedStatement.setString(1, client.getNom());
			preparedStatement.setString(2, client.getPrenom());
			preparedStatement.setString(3, client.getEmail());
			preparedStatement.setDate(4, Date.valueOf(client.getNaissance()));			
			long id = preparedStatement.executeUpdate();
			preparedStatement.close();
			connection.close();
			return id;
		} catch (SQLException e) {
			 throw new DaoException(e.getMessage());
		}
	}
	
	public long delete(long id) throws DaoException {
		try (
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(DELETE_CLIENT_QUERY);
		) {
			preparedStatement.setLong(1, id);
			return preparedStatement.executeUpdate();
		} catch (SQLException e) {
			 throw new DaoException(e.getMessage());
		}
	}

	public Optional<Client> findById(long id) throws DaoException {
		try (
			Connection connection = ConnectionManager.getConnection();
			PreparedStatement preparedStatement = connection.prepareStatement(FIND_CLIENT_QUERY);
		) {
			preparedStatement.setLong(1, id);
			ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next())
				return Optional.ofNullable(instanceFromResult(resultSet));
			return Optional.empty();
		} catch (SQLException e) {
			 throw new DaoException(e.getMessage());
		}
	}

	public List<Client> findAll() throws DaoException {
		 List<Client> clients = new ArrayList<Client>();
		 try (
			 Connection connection = ConnectionManager.getConnection();
			 PreparedStatement preparedStatement = connection.prepareStatement(FIND_CLIENTS_QUERY);
			 ResultSet resultSet = preparedStatement.executeQuery();
		 ) {
			 while(resultSet.next())
				 clients.add(instanceFromResult(resultSet));
		 } catch (SQLException e) {
			 throw new DaoException(e.getMessage());
		 }		 
		 return clients;
	}
	
	private Client instanceFromResult(ResultSet resultSet) throws SQLException {
		return new Client(
			resultSet.getLong("id"),
			resultSet.getString("nom"),
			resultSet.getString("prenom"),
			resultSet.getString("email"),
			resultSet.getDate("naissance").toLocalDate()
		);
	}

}
