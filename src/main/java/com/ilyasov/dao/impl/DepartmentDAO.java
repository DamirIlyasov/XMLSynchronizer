package com.ilyasov.dao.impl;

import com.ilyasov.dao.dao;
import com.ilyasov.model.Department;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.HashMap;
import java.util.Properties;

public class DepartmentDAO implements dao {
    final static Logger logger = Logger.getLogger(DepartmentDAO.class);
    Connection connection;
    String url;
    String username;
    String password;
    PreparedStatement preparedStatement;

    public DepartmentDAO() {

        try {
            InputStream inputStream = DepartmentDAO.class.getClassLoader().getResourceAsStream("persistence.properties");
            Properties properties = new Properties();
            properties.load(inputStream);
            url = properties.getProperty("url");
            username = properties.getProperty("user");
            password = properties.getProperty("password");
        } catch (IOException e) {
            System.out.println("Failed to load properties!");
            e.printStackTrace();
            System.exit(1);
        }

        try {
            connection = DriverManager.getConnection(url, username, password);
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    public void update(HashMap<Integer, Department> depFromXml) {
        logger.info("Updating database started.");
        HashMap<Integer, Department> localDepFromXml = depFromXml;
        HashMap<Integer, Department> depFromDatabase = read();
        try {
            //start of transaction
            connection.setAutoCommit(false);
            logger.info("Updating departments started.");
            //updating departments
            for (int i = 0; i < localDepFromXml.size(); i++) {
                Department departmentXml = localDepFromXml.get(i);
                for (int j = 0; j < depFromDatabase.size(); j++) {

                    Department departmentDatabase = depFromDatabase.get(j);
                    if (departmentXml.getDepCode().equals(departmentDatabase.getDepCode()) &&
                            departmentXml.getDepJob().equals(departmentDatabase.getDepJob()) &&
                            !(departmentXml.getDescription().equals(departmentDatabase.getDescription()))) {
                        preparedStatement = connection.prepareStatement("UPDATE department SET description = ? WHERE dep_code = ? AND dep_job = ?");
                        preparedStatement.setString(1, departmentXml.getDescription());
                        preparedStatement.setString(2, departmentXml.getDepCode());
                        preparedStatement.setString(3, departmentXml.getDepJob());
                        preparedStatement.executeUpdate();

                        localDepFromXml.remove(i);
                        break;
                    }
                }
            }
            logger.info("Updating departments finished.");
            logger.info("Adding new departments started.");
            //adding new departments
            for (int i = 0; i < localDepFromXml.size(); i++) {
                if (!depFromDatabase.containsValue(localDepFromXml.get(i))) {

                    Department department = localDepFromXml.get(i);
                    preparedStatement = connection.prepareStatement("INSERT INTO department (dep_code, dep_job, description) VALUES (?,?,?)");
                    preparedStatement.setString(1, department.getDepCode());
                    preparedStatement.setString(2, department.getDepJob());
                    preparedStatement.setString(3, department.getDescription());
                    preparedStatement.executeUpdate();

                }
            }
            logger.info("Adding new departments finished.");
            logger.info("Deleting outdated departments from database started.");
            //deleting outdated departments from database
            for (int i = 0; i < depFromDatabase.size(); i++) {
                if (!depFromXml.containsValue(depFromDatabase.get(i))) {

                    Department department = depFromDatabase.get(i);
                    preparedStatement = connection.prepareStatement("DELETE FROM department WHERE dep_code = ? AND dep_job = ? AND description = ?");
                    preparedStatement.setString(1, department.getDepCode());
                    preparedStatement.setString(2, department.getDepJob());
                    preparedStatement.setString(3, department.getDescription());
                    preparedStatement.executeUpdate();

                }
            }
            logger.info("Deleting outdated departments from database finished.");
            //end of transaction
            connection.commit();
            logger.info("Updating database completed successfully.");
        } catch (SQLException e) {
            logger.error("Error in updating database.");
            try {
                connection.rollback();
            } catch (SQLException e1) {
                logger.error("Error in connection.rollback.");
                e1.printStackTrace();
            }
            e.printStackTrace();
        }
    }

    public HashMap<Integer, Department> read() {
        logger.info("Reading database started.");
        HashMap<Integer, Department> departmentsFromDatabase = new HashMap<Integer, Department>();
        int counter = 0;
        try {
            PreparedStatement statement = connection.prepareStatement("SELECT id,dep_code,dep_job,description FROM department");
            ResultSet resultSet = statement.executeQuery();
            while (resultSet.next()) {
                Department department = new Department();
                department.setDepCode(resultSet.getString("dep_code"));
                department.setDepJob(resultSet.getString("dep_job"));
                department.setDescription(resultSet.getString("description"));
                department.setId(resultSet.getInt("id"));
                departmentsFromDatabase.put(counter++, department);
            }
            logger.info("Reading database completed successfully.");
            return departmentsFromDatabase;
        } catch (SQLException e) {
            logger.error("Error in reading database.");
            e.printStackTrace();
        }
        return null;
    }
}
