package org.dao;

import org.apache.log4j.Logger;
import org.db.ConnectionManager;
import org.model.AboutMessage;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by HP on 29.05.2015.
 */
public class MessageDaoImpl implements MessageDao {
    private Logger logger = Logger.getLogger(MessageDaoImpl.class.getName());
    private Lock lock = new ReentrantLock();
    @Override
    public void add(AboutMessage message) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            lock.lock();
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);
            System.out.println(message.getIdNumber());
            preparedStatement = connection.prepareStatement("INSERT INTO messages (id, text, name) VALUES (?, ?, ?)");
            preparedStatement.setInt(1, message.getIdNumber());
            preparedStatement.setString(2, message.getMessage());
            preparedStatement.setString(3, message.getUserName());
            preparedStatement.executeUpdate();
            connection.commit();
            lock.unlock();
        } catch (SQLException e) {
            connection.rollback();
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
    }

    @Override
    public void update(AboutMessage message) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        try {
            connection = ConnectionManager.getConnection();
            connection.setAutoCommit(false);
            preparedStatement = connection.prepareStatement("Update     messages SET text = ? WHERE id = ?");
            preparedStatement.setString(1, message.getMessage());
            preparedStatement.setInt(2, message.getIdNumber());
            preparedStatement.executeUpdate();
            connection.commit();
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (preparedStatement != null) {
                try {
                    preparedStatement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }

    }

    @Override
    public void delete(AboutMessage message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public AboutMessage selectById(AboutMessage message) {
        throw new UnsupportedOperationException();
    }

    @Override
    public List<AboutMessage> selectAll() {
        List<AboutMessage> messages = new ArrayList<>();
        Connection connection = null;
        Statement statement = null;
        ResultSet resultSet = null;

        try {
            connection = ConnectionManager.getConnection();
            statement = connection.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM messages");
            while (resultSet.next()) {
                int id = resultSet.getInt("id");
                String text = resultSet.getString("text");
                String name =  resultSet.getString("name");
                messages.add(new AboutMessage(id, name, text));
            }
        } catch (SQLException e) {
            logger.error(e);
        } finally {
            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (statement != null) {
                try {
                    statement.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    logger.error(e);
                }
            }
        }
        return messages;

    }
}
