package org.dao;

import org.model.AboutMessage;

import java.sql.SQLException;
import java.util.List;

/**
 * Created by HP on 29.05.2015.
 */
public interface MessageDao {
    void add(AboutMessage message) throws SQLException;
    void update(AboutMessage message);
    void delete(AboutMessage message);
    AboutMessage selectById(AboutMessage message);
    List<AboutMessage> selectAll();
}
